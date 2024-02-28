package org.oristool.qesm.distributions;

import org.oristool.math.OmegaBigDecimal;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class ExpolynomialDistribution extends Distribution {

   protected double bodyLambda;
   protected double delta;
   protected double upp;

   public ExpolynomialDistribution(double bodyLambda, double delta, double upp) {
      this.bodyLambda = bodyLambda;
      this.delta = delta;
      this.upp = upp;
   }

   @Override
   public StochasticTransitionFeature getStochasticTransitionFeature() {
      return StochasticTransitionFeature.newExpolynomial(
            bodyLambda * Math.exp(bodyLambda * delta) / (1 - Math.exp(-bodyLambda * (upp - delta))) + " * Exp["
                  + (-bodyLambda) + " x]",
            new OmegaBigDecimal(String.valueOf(delta)),
            new OmegaBigDecimal(String.valueOf(upp)));
   }

   @Override
   public double getSignificantThreshold(Distribution other) {
      if (other instanceof ExpolynomialDistribution) {
         return this.getBodyLambda() - ((ExpolynomialDistribution) other).getBodyLambda();
      }
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public String toString() {
      return "ExpolynomialDistribution [bodyLambda=" + bodyLambda + ", delta=" + delta + ", upp=" + upp + "]";
   }

   public double getBodyLambda() {
      return bodyLambda;
   }

   public void setBodyLambda(double bodyLambda) {
      this.bodyLambda = bodyLambda;
   }

   public double getDelta() {
      return delta;
   }

   public void setDelta(double delta) {
      this.delta = delta;
   }

   public double getUpp() {
      return upp;
   }

   public void setUpp(double upp) {
      this.upp = upp;
   }
}