package org.oristool.qesm.distributions;

import org.oristool.math.OmegaBigDecimal;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class ExpolynomialDistribution extends Distribution {

   double bodyLambda;
   double delta;
   double upp;

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
}