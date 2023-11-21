package org.oristool.qesm.stations;

import java.math.BigDecimal;
import java.util.List;

import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;

public abstract class Station {
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
                .greedyPolicy(new BigDecimal("5"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.1")).build();

        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(net, marking);
        
        double[] CDF = new double[solution.getSolution().length];

        for(int i = 0; i < CDF.length; i++) {
            CDF[i] = solution.getSolution()[i][0][0];
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

    public BigDecimal approxTimes(double[] times) {
        // TODO 
        return new BigDecimal(0);
    }

    public void showAnalysisGraph() {
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal("5"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.02")).build();

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