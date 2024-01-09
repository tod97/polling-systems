package org.oristool.qesm.stations;

import java.math.BigDecimal;
import java.util.List;

import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trans.TreeTransient;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.MarkingCondition;
import org.oristool.petrinet.PetriNet;
import org.oristool.qesm.approximations.TruncatedExponentialApproximation;

public abstract class Station {
   private BigDecimal upTime = new BigDecimal("12");

   protected PetriNet net;
   protected Marking marking;
   private double[] CDF;
   public TruncatedExponentialApproximation approximation;

   public abstract void updatePNWithOtherStations(List<Station> stations);

   protected abstract void buildStation();

   public Station() {
      buildStation();
      this.approximation = new TruncatedExponentialApproximation();
   }

   @Override
   public String toString() {
      if (approximation.getDistribution() == null) {
         return "- Station " + "\n" +
               "BodyLambda: " + "null" + "\n" +
               "Delta: " + "null" + "\n" +
               "Upp: " + "null";
      }
      return "- Station " + "\n" +
            "BodyLambda: " + approximation.getDistribution().getBodyLambda() + "\n" +
            "Delta: " + approximation.getDistribution().getDelta() + "\n" +
            "Upp: " + approximation.getDistribution().getUpp();
   }

   public double[] exec() {
      String cond = "pEnd > 0";

      TreeTransient analysis = TreeTransient.builder()
            .greedyPolicy(this.upTime, new BigDecimal(0))
            .timeStep(this.upTime.divide(new BigDecimal("100")))
            .markingFilter(MarkingCondition.fromString(cond))
            .build();

      TransientSolution<Marking, Marking> solution = analysis.compute(net, marking);
      TransientSolution<Marking, RewardRate> rewardSolution = TransientSolution.computeRewards(false,
            solution,
            RewardRate.fromString(cond));

      double[] CDF = new double[rewardSolution.getSolution().length];

      for (int i = 0; i < CDF.length; i++) {
         CDF[i] = rewardSolution.getSolution()[i][0][0];
      }

      return CDF;
   }

   public StochasticTransitionFeature approxCDF(double[] CDF) {
      return this.approximation.getApproximatedStochasticTransitionFeature(CDF, 0, this.upTime.doubleValue(),
            this.upTime.divide(new BigDecimal("100")));
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