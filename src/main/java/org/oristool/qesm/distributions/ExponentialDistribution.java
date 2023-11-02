package org.oristool.qesm.distributions;

import java.math.BigDecimal;

import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class ExponentialDistribution implements IDistribution {

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
}