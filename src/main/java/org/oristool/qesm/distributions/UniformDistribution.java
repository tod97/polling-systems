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
}