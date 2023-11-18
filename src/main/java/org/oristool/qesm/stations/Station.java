package org.oristool.qesm.stations;

import java.math.BigDecimal;

import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;

public abstract class Station {
    private PetriNet pn;
    private Marking m;
    private double[] times;

    public Station() {
        this.pn = new PetriNet();
        this.m = new Marking();

        build(pn, m);
    }

    public abstract void updateWaitingTime(double[] times);
    protected abstract void build(PetriNet net, Marking marking);
    
    public double[] exec() {
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal("5"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.02")).build();

        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(pn, m);
        
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

    public void showAnalysisGraph() {
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal("5"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.02")).build();

        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(pn, m);

        new TransientSolutionViewer(solution);
    }

    public double[] getTimes() {
        return times;
    }

    public void setTimes(double[] times) {
        this.times = times;
    }
}