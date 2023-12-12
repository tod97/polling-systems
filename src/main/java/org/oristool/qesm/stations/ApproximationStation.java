package org.oristool.qesm.stations;

import java.math.BigDecimal;
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

   public void updateWaitingTime(List<double[]> times) {
      if (times.size() > 0) {
         build();

         for (int i = 0; i < times.size(); i++) {
            Transition transition = net.addTransition("det_t"+i);
            transition.addFeature(approxTimes(times.get(i)));
            Place place = net.addPlace("det_p"+i);

            if (i == 0) {
               net.addPrecondition(net.getPlace("p0"), transition);
            } else {
               net.addPrecondition(net.getPlace("det_p"+(i-1)), transition);
            }
            net.addPostcondition(transition, place);
         }

         Transition immTransition = net.addTransition("imm_t");
         immTransition.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
         net.addPrecondition(net.getPlace("det_p"+(times.size()-1)), immTransition);
         net.addPostcondition(immTransition, net.getPlace("pEnd"));
      }
   }

   protected void build() {
      net = new PetriNet();
      marking = new Marking();
      Place p0 = net.addPlace("p0");
      marking.setTokens(p0, 1);

      net.addPlace("pEnd");
   }
}
