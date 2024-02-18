package org.oristool.qesm.stations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.oristool.math.OmegaBigDecimal;
import org.oristool.math.domain.DBMZone;
import org.oristool.math.expression.Expolynomial;
import org.oristool.math.expression.Variable;
import org.oristool.math.function.GEN;
import org.oristool.math.function.PartitionedGEN;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

public class ExhaustiveStation extends Station {

   public ExhaustiveStation() {
      super();
   }

   public void updatePNWithOtherStations(List<Station> stations) {
      buildStation();

      if (stations.size() > 0) {
         // REMOVE t0
         net.removePostcondition(net.getPostcondition(net.getTransition("t0"), net.getPlace("p1")));
         net.removePrecondition(net.getPrecondition(net.getPlace("p0"), net.getTransition("t0")));
         net.removeTransition(net.getTransition("t0"));

         for (int i = 0; i < stations.size(); i++) {
            Transition transition = net.addTransition("at" + i);
            transition.addFeature(stations.get(i).feature);

            Place place = net.addPlace("ap" + (i + 1));
            if (i == 0) {
               net.addPrecondition(net.getPlace("p0"), transition);
            } else {
               net.addPrecondition(net.getPlace("ap" + i), transition);
            }
            net.addPostcondition(transition, place);
         }

         Transition immTransition = net.addTransition("imm_t");
         immTransition.addFeature(
               StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
         net.addPrecondition(net.getPlace("ap" + stations.size()), immTransition);
         net.addPostcondition(immTransition, net.getPlace("p1"));
      }
   }

   protected void buildStation() {
      net = new PetriNet();
      marking = new Marking();

      //Generating Nodes
      Place p0 = net.addPlace("p0");
      Place p1 = net.addPlace("p1");
      Place p16 = net.addPlace("p16");
      Place p17 = net.addPlace("p17");
      Transition t0 = net.addTransition("t0");
      Transition t2 = net.addTransition("t2");
      Transition t3 = net.addTransition("t3");
      Transition t4 = net.addTransition("t4");

      //Generating Connectors
      net.addInhibitorArc(p17, t2);
      net.addInhibitorArc(p0, t3);
      net.addPrecondition(p17, t3);
      net.addPostcondition(t3, p16);
      net.addPrecondition(p1, t2);
      net.addPostcondition(t0, p1);
      net.addPrecondition(p0, t0);
      net.addPrecondition(p16, t4);
      net.addPostcondition(t4, p17);

      //Generating Properties
      marking.setTokens(p0, 1);
      marking.setTokens(p1, 0);
      marking.setTokens(p16, 5);
      marking.setTokens(p17, 0);
      List<GEN> t0_gens = new ArrayList<>();

      DBMZone t0_d_0 = new DBMZone(new Variable("x"));
      Expolynomial t0_e_0 = Expolynomial.fromString("3 * Exp[-4 x] + x^1 * Exp[-2 x]");
      //Normalization
      t0_e_0.multiply(new BigDecimal(8010.219010916156));
      t0_d_0.setCoefficient(new Variable("x"), new Variable("t*"), new OmegaBigDecimal("10"));
      t0_d_0.setCoefficient(new Variable("t*"), new Variable("x"), new OmegaBigDecimal("-5"));
      GEN t0_gen_0 = new GEN(t0_d_0, t0_e_0);
      t0_gens.add(t0_gen_0);

      PartitionedGEN t0_pFunction = new PartitionedGEN(t0_gens);
      StochasticTransitionFeature t0_feature = StochasticTransitionFeature.of(t0_pFunction);
      t0.addFeature(t0_feature);

      t2.addFeature(
            StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
      t2.addFeature(new Priority(0));
      t3.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("1")));
      t4.addFeature(
            StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("1", net)));
   }
}
