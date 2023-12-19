package org.oristool.qesm.stations;

import java.math.BigDecimal;
import java.util.List;

import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.MarkingCondition;
import org.oristool.petrinet.PetriNet;
import org.oristool.qesm.approximations.TruncatedExponentialApproximation;

public abstract class Station {
   protected BigDecimal upTime = new BigDecimal("5");

   protected PetriNet net;
   protected Marking marking;
   protected double[] times;
   public TruncatedExponentialApproximation approximation;

   public abstract void updatePNWithApproxTimes(List<double[]> times);

   protected abstract void buildStation();

   public Station() {
      buildStation();
      this.approximation = new TruncatedExponentialApproximation();
   }

   public double[] exec() {
      String cond = "pEnd > 0";

      RegTransient analysis = RegTransient.builder()
            .greedyPolicy(this.upTime, this.upTime.divide(new BigDecimal("1000")))
            .timeStep(this.upTime.divide(new BigDecimal("100")))
            .markingFilter(MarkingCondition.fromString(cond))
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

   public boolean isTimesDiffInThreshold(double[] newTimes, double threshold) {
      if (times.length != newTimes.length) {
         return false;
      }

      for (int i = 0; i < times.length; i++) {
         if (Math.abs(times[i] - newTimes[i]) > threshold) {
            return false;
         }
      }

      return true;
   }

   public StochasticTransitionFeature approxTimes(double[] times) {
      return this.approximation.getApproximatedStochasticTransitionFeature(times, 0, this.upTime.doubleValue(),
            this.upTime.divide(new BigDecimal("100")));
   }

   public double[] getTimes() {
      return times;
   }

   public void setTimes(double[] times) {
      this.times = times;
   }

   public BigDecimal getUpTime() {
      return upTime;
   }

   public void setUpTime(BigDecimal upTime) {
      this.upTime = upTime;
   }
}