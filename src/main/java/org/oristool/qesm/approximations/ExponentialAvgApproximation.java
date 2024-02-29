package org.oristool.qesm.approximations;

import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.qesm.distributions.ExponentialDistribution;

import java.math.BigDecimal;
import java.util.stream.IntStream;

public class ExponentialAvgApproximation extends Approximation {

   ExponentialDistribution distribution;

   @Override
   public ExponentialDistribution getDistribution() {
      return distribution;
   }

   @Override
   public StochasticTransitionFeature getApproximatedStochasticTransitionFeature(double[] cdf, double low, double upp,
         BigDecimal step) {
      StochasticTransitionFeature feature;

      if (cdf.length < (int) (upp - low) / step.doubleValue()) {
         throw new RuntimeException(
               "cdf has not enough samples with respect to provided support and time step value");
      }

      double[] pdf = IntStream.range(1, cdf.length).mapToDouble(i -> (cdf[i] - cdf[i - 1]) / step.doubleValue())
            .toArray();
      double mean = 0;
      for (int i = 0; i < pdf.length; i++) {
         mean += pdf[i] * step.doubleValue() * (step.doubleValue() * i);
      }

      double bodyLambda = 1 / mean;

      this.distribution = new ExponentialDistribution(new BigDecimal(bodyLambda));
      feature = this.distribution.getStochasticTransitionFeature();

      return feature;
   }
}