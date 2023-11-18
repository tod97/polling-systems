package org.oristool.qesm.stations;

import java.lang.reflect.Array;
import java.math.BigDecimal;

import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;

public abstract class Station {
    PetriNet pn;
    Marking m;
    double[] times;

    public Station() {
        this.pn = new PetriNet();
        this.m = new Marking();
    }
    
    public void exec() {
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal("5"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.02")).build();

        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(pn, m);
    }

    public void analysisGraph() {
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal("5"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.02")).build();

        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(pn, m);

        new TransientSolutionViewer(solution);
    }
}