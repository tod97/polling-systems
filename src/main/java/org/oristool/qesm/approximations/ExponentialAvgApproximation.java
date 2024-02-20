package org.oristool.qesm.approximations;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.qesm.distributions.ExpolynomialDistribution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.IntStream;

public class ExponentialAvgApproximation extends Approximation {

   ExpolynomialDistribution distribution;

   @Override
   public ExpolynomialDistribution getDistribution() {
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

      // Ricorda che la cdf è data da 0 a upp; low si usa se serve sapere il supporto
      // reale.
      NewtonRaphsonSolver zeroSolver = new NewtonRaphsonSolver();

      for (int i = 0; i < cdf.length - 1; i++) {
         cdf[i] = BigDecimal.valueOf(cdf[i]).doubleValue();
      }
      double timeTick = step.doubleValue();

      double[] pdf = new double[cdf.length];
      double[] x = new double[cdf.length];
      for (int i = 0; i < cdf.length - 1; i++) {
         pdf[i + 1] = BigDecimal.valueOf((cdf[i + 1] - cdf[i]) / timeTick).setScale(3, RoundingMode.HALF_DOWN)
               .doubleValue();
         x[i] = /* low + */ i * timeTick;
      }

      double pdfMax = Arrays.stream(pdf, 0, pdf.length).max().getAsDouble();
      int xMaxIndex = IntStream.range(0, pdf.length)
            .filter(i -> pdf[i] == pdfMax)
            .findFirst() // first occurrence
            .orElse(-1);
      // .reduce((first, second) -> second).orElse(-1);

      double xMax = /* low + */ timeTick * xMaxIndex;
      double cdfMax = cdf[xMaxIndex];

      double delta = BigDecimal.valueOf((pdfMax * xMax - cdfMax) / pdfMax).doubleValue();

      int deltaIndex = IntStream.range(0, pdf.length)
            .filter(i -> x[i] >= delta)
            .findFirst() // first occurrence
            .orElse(-1);

      // Body
      double bodyLambda = Double.MAX_VALUE;

      for (int i = deltaIndex; i < pdf.length; i++) {
         // if(cdf[i] > 0 && cdf[i] < 1){
         try {
            bodyLambda = Math.min(
                  bodyLambda,
                  zeroSolver.solve(10000, new UnivariateDifferentiableFunction() {
                     private double delta;
                     private double b;
                     private double time;
                     private double histogram;

                     @Override
                     public DerivativeStructure value(DerivativeStructure t) throws DimensionMismatchException {
                        // t should be our lambda
                        DerivativeStructure p = t.reciprocal();
                        return p.subtract(histogram);
                     }

                     @Override
                     public double value(double x) {
                        // Here x is the lambda of the function
                        return (1 / x) - histogram;
                     }

                     public UnivariateDifferentiableFunction init(double delta, double b, double time,
                           double histogram) {
                        this.delta = delta;
                        this.b = b;
                        this.time = time;
                        this.histogram = histogram;
                        return this;
                     }
                  }.init(delta, upp, x[i], cdf[i]), 0.0001));
         } catch (Exception e) {
            // System.err.println("Exception: " + e.getMessage());
         }
         // }
      }

      bodyLambda = BigDecimal.valueOf(bodyLambda).setScale(3, RoundingMode.HALF_UP).doubleValue();

      this.distribution = new ExpolynomialDistribution(bodyLambda, delta, upp);
      feature = this.distribution.getStochasticTransitionFeature();

      return feature;
   }
}