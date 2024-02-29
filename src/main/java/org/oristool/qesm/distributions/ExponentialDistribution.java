package org.oristool.qesm.distributions;

import java.math.BigDecimal;

import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class ExponentialDistribution extends Distribution {

   BigDecimal expRate;

   public ExponentialDistribution(BigDecimal expRate) {
      this.expRate = expRate;
   }

   @Override
   public StochasticTransitionFeature getStochasticTransitionFeature() {
      return StochasticTransitionFeature.newExponentialInstance(expRate);
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
      return "ExponentialDistribution [expRate=" + expRate + "]";
   }

   public BigDecimal getExpRate() {
      return expRate;
   }

   public void setExpRate(BigDecimal expRate) {
      this.expRate = expRate;
   }
}