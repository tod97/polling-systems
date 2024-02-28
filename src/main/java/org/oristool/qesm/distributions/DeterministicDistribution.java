package org.oristool.qesm.distributions;

import java.math.BigDecimal;

import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class DeterministicDistribution extends Distribution {

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

   @Override
   public double getSignificantThreshold(Distribution other) {
      if (other instanceof DeterministicDistribution) {
         return this.value.doubleValue() - ((DeterministicDistribution) other).getValue().doubleValue();
      }
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public String toString() {
      return "DeterministicDistribution [value=" + value + ", weight=" + weight + "]";
   }

   public BigDecimal getValue() {
      return value;
   }

   public void setValue(BigDecimal value) {
      this.value = value;
   }

   public MarkingExpr getWeight() {
      return weight;
   }

   public void setWeight(MarkingExpr weight) {
      this.weight = weight;
   }
}