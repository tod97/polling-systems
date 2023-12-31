package org.oristool.qesm.stations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

public class ApproximationStation extends Station {

   public ApproximationStation() {
      super();
   }

   /* public void updatePNWithOtherStations(List<Station> stations) {
      if (stations.size() > 0) {
         buildStation();
   
         for (int i = 0; i < stations.size(); i++) {
            Transition transition = net.addTransition("t" + i);
            transition.addFeature(stations.get(i).approxTimes(stations.get(i).getTimes()));
   
            Place place = net.addPlace("p" + (i + 1));
            net.addPrecondition(net.getPlace("p" + i), transition);
            net.addPostcondition(transition, place);
         }
   
         Transition immTransition = net.addTransition("imm_t");
         immTransition.addFeature(
               StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
         net.addPrecondition(net.getPlace("p" + stations.size()), immTransition);
         net.addPostcondition(immTransition, net.getPlace("pEnd"));
      }
   } */

   public void updatePNWithOtherStations(List<Station> stations) {
      if (stations.size() > 0) {
         buildStation();

         // sum all times into one double[]
         List<double[]> times = new ArrayList<double[]>();
         times.add(stations.get(0).getTimes());
         for (int i = 0; i < times.get(0).length; i++) {
            for (int k = 1; k < stations.size(); k++) {
               times.get(0)[i] += stations.get(k).getTimes()[i];
            }
         }

         for (int i = 0; i < times.size(); i++) {
            Transition transition = net.addTransition("t" + i);
            transition.addFeature(approxTimes(times.get(i)));

            Place place = net.addPlace("p" + (i + 1));
            net.addPrecondition(net.getPlace("p" + i), transition);
            net.addPostcondition(transition, place);
         }

         Transition immTransition = net.addTransition("imm_t");
         immTransition.addFeature(
               StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
         net.addPrecondition(net.getPlace("p" + times.size()), immTransition);
         net.addPostcondition(immTransition, net.getPlace("pEnd"));
      }
   }

   protected void buildStation() {
      net = new PetriNet();
      marking = new Marking();
      Place p0 = net.addPlace("p0");
      marking.setTokens(p0, 1);

      net.addPlace("pEnd");
   }
}
