package org.oristool.qesm.distributions;

import java.math.BigDecimal;

import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class UniformDistribution extends Distribution {

   BigDecimal eft;
   BigDecimal lft;

   public UniformDistribution(BigDecimal eft, BigDecimal lft) {
      this.eft = eft;
      this.lft = lft;
   }

   @Override
   public StochasticTransitionFeature getStochasticTransitionFeature() {
      return StochasticTransitionFeature.newUniformInstance(eft, lft);
   }

   @Override
   public double getSignificantThreshold(Distribution other) {
      if (other instanceof UniformDistribution) {
         return this.getEft().doubleValue() - ((UniformDistribution) other).getEft().doubleValue();
      }
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public String toString() {
      return "UniformDistribution [eft=" + eft + ", lft=" + lft + "]";
   }

   public BigDecimal getEft() {
      return eft;
   }

   public void setEft(BigDecimal eft) {
      this.eft = eft;
   }

   public BigDecimal getLft() {
      return lft;
   }

   public void setLft(BigDecimal lft) {
      this.lft = lft;
   }
}