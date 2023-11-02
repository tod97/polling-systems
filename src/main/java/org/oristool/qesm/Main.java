package org.oristool.qesm;

import java.math.BigDecimal;

import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;

public class Main {
    public static void main(String[] args) {
        PetriNet pn = new PetriNet();
        Marking m = new Marking();
        
        PollingSystem.buildExhaustiveExample(pn, m);

        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal("5"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.02")).build();

        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(pn, m);

        new TransientSolutionViewer(solution);
    }

}