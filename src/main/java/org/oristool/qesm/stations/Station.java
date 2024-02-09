package org.oristool.qesm.stations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.qesm.approximations.TruncatedExponentialApproximation;

public abstract class Station {
   private BigDecimal upTime = new BigDecimal("15");

   protected PetriNet net;
   protected Marking marking;
   private double[] CDF;
   public TruncatedExponentialApproximation approximation;
   public StochasticTransitionFeature feature;

   public abstract void updatePNWithOtherStations(List<Station> stations);

   protected abstract void buildStation();

   public Station() {
      buildStation();
      this.approximation = new TruncatedExponentialApproximation();
   }

   public String report(int i) {
      if (approximation.getDistribution() == null) {
         return "- Station " + i + " completed: distribution is null";
      }
      return "- Station " + i + " completed: " + "BodyLambda: " + approximation.getDistribution().getBodyLambda()
            + ", Delta: " + approximation.getDistribution().getDelta() + ", Upp: "
            + approximation.getDistribution().getUpp();
   }

   public double[] exec(BigDecimal additionalUpTime) {
      String cond = "pEnd > 0";
      this.upTime = additionalUpTime.doubleValue() > 0 ? additionalUpTime : this.upTime;
      System.out.println(this.upTime);

      RegTransient analysis = RegTransient.builder()
            .localEvaluationPeriod(1)
            .globalEvaluationPeriod(1)
            .greedyPolicy(this.upTime, new BigDecimal(0))
            .timeStep(this.upTime.divide(new BigDecimal("100")))
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

   public StochasticTransitionFeature approxCDF(double[] CDF) {
      System.out.println(this.getClass().getSimpleName() + " - [" + CDF[0] + ", " + CDF[1] + ", " + CDF[2] + ", ..., "
            + CDF[CDF.length - 3] + ", " + CDF[CDF.length - 2] + ", " + CDF[CDF.length - 1] + "]");

      ArrayList<Double> subCDF = new ArrayList<>();
      int count = 0;
      double epsilon = 1E-3;
      for (int i = 0; i < CDF.length; i++) {
         if (CDF[i] > 0) {
            count++;
            subCDF.add(CDF[i]);
            if (CDF[i] > 1 - epsilon) {
               break;
            }
         }
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