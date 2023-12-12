package org.oristool.qesm.approximations;

import java.math.BigDecimal;

import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.qesm.distributions.Distribution;

public abstract class Approximation {

   public abstract Distribution getDistribution();

   public abstract StochasticTransitionFeature getApproximatedStochasticTransitionFeature(double[] cdf, double low,
         double upp, BigDecimal step);
}