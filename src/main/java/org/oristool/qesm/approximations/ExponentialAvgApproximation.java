package org.oristool.qesm.approximations;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.PetriNet;
import org.oristool.qesm.distributions.ExponentialDistribution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
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

      // TODO
      this.distribution = new ExponentialDistribution(new BigDecimal(0), MarkingExpr.from("1", new PetriNet()));
      feature = this.distribution.getStochasticTransitionFeature();

      return feature;
   }
}