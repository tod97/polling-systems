package org.oristool.qesm.distributions;

import java.math.BigDecimal;

import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class UniformDistribution implements IDistribution {

   BigDecimal eft;
   BigDecimal lft;

   UniformDistribution(BigDecimal eft, BigDecimal lft) {
      this.eft = eft;
      this.lft = lft;
   }

   @Override
   public StochasticTransitionFeature getStochasticTransitionFeature() {
      return StochasticTransitionFeature.newUniformInstance(eft, lft);
   }
}