package org.oristool.qesm.distributions;

import java.math.BigDecimal;

import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class ExponentialDistribution extends Distribution {

   BigDecimal expRate;
   MarkingExpr clockRate;

   public ExponentialDistribution(BigDecimal expRate, MarkingExpr clockRate) {
      this.expRate = expRate;
      this.clockRate = clockRate;
   }

   @Override
   public StochasticTransitionFeature getStochasticTransitionFeature() {
      return StochasticTransitionFeature.newExponentialInstance(expRate, clockRate);
   }

   @Override
   public double getSignificantThreshold(Distribution other) {
      if (other instanceof ExponentialDistribution) {
         return this.getExpRate().doubleValue() - ((ExponentialDistribution) other).getExpRate().doubleValue();
      }
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public String toString() {
      return "ExponentialDistribution [expRate=" + expRate + ", clockRate=" + clockRate + "]";
   }

   public BigDecimal getExpRate() {
      return expRate;
   }

   public void setExpRate(BigDecimal expRate) {
      this.expRate = expRate;
   }

   public MarkingExpr getClockRate() {
      return clockRate;
   }

   public void setClockRate(MarkingExpr clockRate) {
      this.clockRate = clockRate;
   }
}