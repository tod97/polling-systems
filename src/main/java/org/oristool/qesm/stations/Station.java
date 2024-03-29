package org.oristool.qesm.stations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.SteadyStateSolution;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.steady.RegSteadyState;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.MarkingCondition;
import org.oristool.petrinet.PetriNet;
import org.oristool.qesm.approximations.ExponentialAvgApproximation;
import org.oristool.qesm.distributions.ExpolynomialDistribution;

public abstract class Station {
   private BigDecimal upTime = new BigDecimal("15");

   protected PetriNet net;
   protected Marking marking;
   private double[] CDF;
   public ExponentialAvgApproximation approximation;
   public StochasticTransitionFeature feature;

   public abstract void updatePNWithOtherStations(List<Station> stations);

   protected abstract void buildStation();

   public Station() {
      buildStation();
      this.approximation = new ExponentialAvgApproximation();
      this.feature = new ExpolynomialDistribution(2.4, 1, 10).getStochasticTransitionFeature();
   }

   public String report(int i) {
      if (approximation.getDistribution() == null) {
         return "- Station " + i + " completed: distribution is null";
      }
      return "- Station " + i + " completed: " + approximation.getDistribution();
   }

   private Map<Marking, BigDecimal> getSteadyStateMap() {
      RegSteadyState analysis = RegSteadyState.builder()
            .stopOn(MarkingCondition.fromString("p0==0"))
            .build();

      SteadyStateSolution<Marking> solution = analysis.compute(this.net, this.marking);
      Map<Marking, BigDecimal> m = solution.getSteadyState();

      // Filter that removes non-p0 keys (it can be done better with sirio)
      m.entrySet().removeIf(entry -> entry.getKey().toString().indexOf("p0") == -1);

      return m;
   }

   private double[] getTransientCDF(PetriNet net, Marking marking) {
      String cond = "p0==0";
      RegTransient analysis = RegTransient.builder()
            .localEvaluationPeriod(1)
            .globalEvaluationPeriod(1)
            .greedyPolicy(this.upTime, new BigDecimal(0))
            .timeStep(this.upTime.divide(new BigDecimal("100")))
            .stopOn(MarkingCondition.fromString(cond))
            .build();

      TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(net, marking);
      TransientSolution<DeterministicEnablingState, RewardRate> rewardSolution = TransientSolution.computeRewards(false,
            solution,
            RewardRate.fromString(cond));

      double[] CDF = new double[rewardSolution.getSolution().length];

      for (int i = 0; i < CDF.length; i++) {
         CDF[i] = rewardSolution.getSolution()[i][0][0];
      }

      return CDF;
   }

   public double[] exec(BigDecimal additionalUpTime) {
      Map<Marking, BigDecimal> m = this.getSteadyStateMap();
      ArrayList<Double> resultCDF = new ArrayList<>();
      this.upTime = additionalUpTime.doubleValue() > 0 ? additionalUpTime : this.upTime;

      for (Map.Entry<Marking, BigDecimal> entry : m.entrySet()) {
         double[] CDF = this.getTransientCDF(this.net, entry.getKey());

         for (int i = 0; i < CDF.length; i++) {
            if (resultCDF.size() <= i) {
               resultCDF.add(CDF[i] * entry.getValue().doubleValue());
            } else {
               resultCDF.set(i, resultCDF.get(i) + CDF[i] * entry.getValue().doubleValue());
            }
         }
      }

      return resultCDF.stream().mapToDouble(Double::doubleValue).toArray();
   }

   public StochasticTransitionFeature approxCDF(double[] CDF) {
      System.out.println(this.getClass().getSimpleName() + " - [" + CDF[0] + ", " + CDF[1] + ", " + CDF[2] + ", ..., "
            + CDF[CDF.length - 3] + ", " + CDF[CDF.length - 2] + ", " + CDF[CDF.length - 1] + "]");

      ArrayList<Double> subCDF = new ArrayList<>();
      int count = 0;
      double epsilon = 1E-3;
      for (int i = 0; i < CDF.length; i++) {
         //if (CDF[i] > 0) {
         count++;
         subCDF.add(CDF[i]);
         if (CDF[i] > 1 - epsilon) {
            break;
         }
         //}
      }
      double[] newCDF = subCDF.stream().mapToDouble(Double::doubleValue).toArray();
      double step = this.upTime.doubleValue() / (CDF.length - 1);
      double newUp = count * step;
      //double newUp = count * (this.upTime.doubleValue() / CDF.length);
      //BigDecimal step = new BigDecimal(newUp / (CDF.length - 1));
      this.upTime = new BigDecimal(newUp);

      this.feature = this.approximation.getApproximatedStochasticTransitionFeature(newCDF, 0, newUp,
            BigDecimal.valueOf(step));
      return this.feature;
   }

   public double[] getCDF() {
      return CDF;
   }

   public void setCDF(double[] CDF) {
      this.CDF = CDF;
   }

   public BigDecimal getUpTime() {
      return upTime;
   }

   public void setUpTime(BigDecimal upTime) {
      this.upTime = upTime;
   }
}