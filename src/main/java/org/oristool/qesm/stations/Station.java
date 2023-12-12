package org.oristool.qesm.stations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.MarkingCondition;
import org.oristool.petrinet.PetriNet;
import org.oristool.qesm.approximations.TruncatedExponentialApproximation;

public abstract class Station {
    protected final BigDecimal upTime = new BigDecimal("5");
    protected final BigDecimal timeStep = this.upTime.divide(new BigDecimal("100"));

    protected PetriNet net;
    protected Marking marking;
    protected double[] times;

    public Station() {
        build();
    }

    public abstract void updateWaitingTime(List<double[]> times);
    protected abstract void build();
    
    public double[] exec() {
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(this.upTime, this.upTime.divide(new BigDecimal("1000")))
                .timeStep(this.timeStep)
                .markingFilter(MarkingCondition.fromString("p16 == 1"))
                .build();

        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(net, marking);
        TransientSolution<DeterministicEnablingState, RewardRate> rewardSolution = TransientSolution.computeRewards(false, solution, RewardRate.fromString("p16 == 1"));

        double[] CDF = new double[rewardSolution.getSolution().length];

        for(int i = 0; i < CDF.length; i++) {
            CDF[i] = rewardSolution.getSolution()[i][0][0];
        }

        return CDF;
    }

    public boolean isTimesDiffInThreshold(double[] newTimes, double threshold) {
        if (times.length != newTimes.length) {
            return false;
        }

        for(int i = 0; i < times.length; i++) {
            if (Math.abs(times[i] - newTimes[i]) > threshold) {
                return false;
            }
        }

        return true;
    }

    public StochasticTransitionFeature approxTimes(double[] times) {
        TruncatedExponentialApproximation approx = new TruncatedExponentialApproximation();
        return approx.getApproximatedStochasticTransitionFeature(times, 0, this.upTime.doubleValue(), this.timeStep);

        // return StochasticTransitionFeature.newDeterministicInstance(new BigDecimal(0), MarkingExpr.from("1", net));
    }

    public void showAnalysisGraph() {
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(this.upTime, this.upTime.divide(new BigDecimal("1000")))
                .timeStep(this.timeStep).build();

        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(net, marking);

        new TransientSolutionViewer(solution);
    }

    public double[] getTimes() {
        return times;
    }

    public void setTimes(double[] times) {
        this.times = times;
    }
}