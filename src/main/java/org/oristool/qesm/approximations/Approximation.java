package org.oristool.qesm.approximations;

import java.math.BigDecimal;

import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public abstract class Approximation {
   public abstract StochasticTransitionFeature getApproximatedStochasticTransitionFeature(double[] cdf, double low,
         double upp, BigDecimal step);
}