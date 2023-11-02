package org.oristool.qesm.distributions;

import java.math.BigDecimal;

import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class DeterministicDistribution implements IDistribution {

   BigDecimal value;
   MarkingExpr weight;

   public DeterministicDistribution(BigDecimal value, MarkingExpr weight) {
      this.value = value;
      this.weight = weight;
   }

   @Override
   public StochasticTransitionFeature getStochasticTransitionFeature() {
      return StochasticTransitionFeature.newDeterministicInstance(value, weight);
   }
}