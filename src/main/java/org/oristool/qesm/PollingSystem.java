package org.oristool.qesm;

import java.math.BigDecimal;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Precondition;
import org.oristool.petrinet.Transition;

public class PollingSystem {

  public static int numSystems = 0;

  public static void addClient(
    PetriNet net,
    Marking marking,
    StochasticTransitionFeature arriveFeature,
    StochasticTransitionFeature emptyFeature,
    StochasticTransitionFeature serveFeature,
    StochasticTransitionFeature startServiceFeature,
    StochasticTransitionFeature timeoutFeature
  ) {
    numSystems++;
    
    Place AtService = net.addPlace("AtService"+numSystems);
    Place Idle = net.addPlace("Idle"+numSystems);
    Place Vacant = net.addPlace("Vacant"+numSystems);
    Place Waiting = net.addPlace("Waiting"+numSystems);
    Transition arrive = net.addTransition("arrive"+numSystems);
    Transition empty = net.addTransition("empty"+numSystems);
    Transition serve = net.addTransition("serve"+numSystems);
    Transition startService = net.addTransition("startService"+numSystems);
    Transition timeout = net.addTransition("timeout"+numSystems);

    //Generating Connectors
    net.addInhibitorArc(Vacant, serve);
    net.addInhibitorArc(Waiting, empty);
    net.addPostcondition(arrive, Waiting);
    net.addPostcondition(empty, Vacant);
    net.addPrecondition(Vacant, startService);
    net.addPrecondition(Idle, arrive);
    net.addPostcondition(startService, AtService);
    net.addPrecondition(AtService, timeout);
    net.addPostcondition(timeout, Vacant);
    net.addPrecondition(AtService, empty);
    net.addPrecondition(Waiting, serve);
    net.addPostcondition(serve, Idle);

    //Generating Properties
    marking.setTokens(AtService, 0);
    marking.setTokens(Idle, 2);
    marking.setTokens(Vacant, 1);
    marking.setTokens(Waiting, 0);
    arrive.addFeature(arriveFeature);
    empty.addFeature(emptyFeature);
    empty.addFeature(new Priority(0));
    serve.addFeature(serveFeature);
    startService.addFeature(startServiceFeature);
    timeout.addFeature(timeoutFeature);
    timeout.addFeature(new Priority(0));

    if (numSystems == 2) {
      Place Polling = net.addPlace("Polling"+(numSystems-1));
      net.addPostcondition(net.getTransition("timeout"+(numSystems-1)), Polling);
      net.addPostcondition(net.getTransition("empty"+(numSystems-1)), Polling);
      net.addPrecondition(Polling, net.getTransition("startService"+(numSystems)));
    }
    if (numSystems >= 2) {
      Place PollingN = net.addPlace("Polling"+numSystems);
      net.addPostcondition(net.getTransition("timeout"+numSystems), PollingN);
      net.addPostcondition(net.getTransition("empty"+numSystems), PollingN);

      Precondition pre = net.getPrecondition(net.getPlace("Polling"+(numSystems-1)), net.getTransition("startService1"));
      if (pre != null) net.removePrecondition(pre);
      net.addPrecondition(PollingN, net.getTransition("startService1"));
      marking.setTokens(PollingN, 1);
    }
    
    for(int i = 1; i < numSystems; i++) {
      Place Polling = net.getPlace("Polling"+i);
      if (Polling != null) {
        marking.setTokens(Polling, 0);
      }
    }

  }

  public static void addClient(
    PetriNet net,
    Marking marking,
    String arriveExpr,
    String emptyExpr,
    String timeoutExpr
  ) {
    StochasticTransitionFeature arriveFeature = StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from(arriveExpr, net));
    StochasticTransitionFeature emptyFeature = StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from(emptyExpr, net));
    StochasticTransitionFeature serveFeature = StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("1"));
    StochasticTransitionFeature startServiceFeature = StochasticTransitionFeature.newUniformInstance(new BigDecimal("1"), new BigDecimal("2"));
    StochasticTransitionFeature timeoutFeature = StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("3"), MarkingExpr.from(timeoutExpr, net));

    addClient(net, marking, arriveFeature, emptyFeature, serveFeature, startServiceFeature, timeoutFeature);
  }

  public static void addClient(
    PetriNet net,
    Marking marking
  ) {
    StochasticTransitionFeature arriveFeature = StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.05", net));
    StochasticTransitionFeature emptyFeature = StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net));
    StochasticTransitionFeature serveFeature = StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("1"));
    StochasticTransitionFeature startServiceFeature = StochasticTransitionFeature.newUniformInstance(new BigDecimal("1"), new BigDecimal("2"));
    StochasticTransitionFeature timeoutFeature = StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("3"), MarkingExpr.from("1", net));

    addClient(net, marking, arriveFeature, emptyFeature, serveFeature, startServiceFeature, timeoutFeature);
  }

  public static void buildExample(PetriNet net, Marking marking) {

    //Generating Nodes
    Place AtService1 = net.addPlace("AtService1");
    Place AtService2 = net.addPlace("AtService2");
    Place AtService3 = net.addPlace("AtService3");
    Place Idle1 = net.addPlace("Idle1");
    Place Idle2 = net.addPlace("Idle2");
    Place Idle3 = net.addPlace("Idle3");
    Place Polling1 = net.addPlace("Polling1");
    Place Polling2 = net.addPlace("Polling2");
    Place Polling3 = net.addPlace("Polling3");
    Place Vacant1 = net.addPlace("Vacant1");
    Place Vacant2 = net.addPlace("Vacant2");
    Place Vacant3 = net.addPlace("Vacant3");
    Place Waiting1 = net.addPlace("Waiting1");
    Place Waiting2 = net.addPlace("Waiting2");
    Place Waiting3 = net.addPlace("Waiting3");
    Transition arrive1 = net.addTransition("arrive1");
    Transition arrive2 = net.addTransition("arrive2");
    Transition arrive3 = net.addTransition("arrive3");
    Transition empty1 = net.addTransition("empty1");
    Transition empty2 = net.addTransition("empty2");
    Transition empty3 = net.addTransition("empty3");
    Transition serve1 = net.addTransition("serve1");
    Transition serve2 = net.addTransition("serve2");
    Transition serve3 = net.addTransition("serve3");
    Transition startService1 = net.addTransition("startService1");
    Transition startService2 = net.addTransition("startService2");
    Transition startService3 = net.addTransition("startService3");
    Transition timeout1 = net.addTransition("timeout1");
    Transition timeout2 = net.addTransition("timeout2");
    Transition timeout3 = net.addTransition("timeout3");

    //Generating Connectors
    net.addInhibitorArc(Waiting2, empty2);
    net.addInhibitorArc(Vacant3, serve3);
    net.addInhibitorArc(Waiting1, empty1);
    net.addInhibitorArc(Vacant2, serve2);
    net.addInhibitorArc(Vacant1, serve1);
    net.addInhibitorArc(Waiting3, empty3);
    net.addPostcondition(arrive3, Waiting3);
    net.addPrecondition(Waiting2, serve2);
    net.addPostcondition(empty3, Vacant3);
    net.addPostcondition(timeout2, Polling3);
    net.addPostcondition(timeout1, Vacant1);
    net.addPrecondition(Vacant3, startService3);
    net.addPrecondition(AtService2, empty2);
    net.addPostcondition(arrive1, Waiting1);
    net.addPrecondition(Idle2, arrive2);
    net.addPostcondition(startService2, AtService2);
    net.addPrecondition(Vacant2, startService2);
    net.addPrecondition(Idle3, arrive3);
    net.addPostcondition(empty1, Polling2);
    net.addPrecondition(Polling1, startService1);
    net.addPostcondition(serve1, Idle1);
    net.addPostcondition(serve2, Idle2);
    net.addPrecondition(Polling3, startService3);
    net.addPrecondition(AtService1, timeout1);
    net.addPostcondition(startService1, AtService1);
    net.addPrecondition(AtService2, timeout2);
    net.addPostcondition(startService3, AtService3);
    net.addPrecondition(AtService1, empty1);
    net.addPrecondition(AtService3, timeout3);
    net.addPostcondition(timeout3, Polling1);
    net.addPostcondition(empty2, Polling3);
    net.addPostcondition(empty3, Polling1);
    net.addPostcondition(timeout3, Vacant3);
    net.addPrecondition(Vacant1, startService1);
    net.addPrecondition(Polling2, startService2);
    net.addPostcondition(empty1, Vacant1);
    net.addPostcondition(timeout1, Polling2);
    net.addPrecondition(Idle1, arrive1);
    net.addPrecondition(Waiting1, serve1);
    net.addPrecondition(AtService3, empty3);
    net.addPostcondition(arrive2, Waiting2);
    net.addPrecondition(Waiting3, serve3);
    net.addPostcondition(serve3, Idle3);
    net.addPostcondition(empty2, Vacant2);
    net.addPostcondition(timeout2, Vacant2);

    //Generating Properties
    marking.setTokens(AtService1, 0);
    marking.setTokens(AtService2, 0);
    marking.setTokens(AtService3, 0);
    marking.setTokens(Idle1, 2);
    marking.setTokens(Idle2, 2);
    marking.setTokens(Idle3, 2);
    marking.setTokens(Polling1, 1);
    marking.setTokens(Polling2, 0);
    marking.setTokens(Polling3, 0);
    marking.setTokens(Vacant1, 1);
    marking.setTokens(Vacant2, 1);
    marking.setTokens(Vacant3, 1);
    marking.setTokens(Waiting1, 0);
    marking.setTokens(Waiting2, 0);
    marking.setTokens(Waiting3, 0);
    arrive1.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.05", net)));
    arrive2.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.05", net)));
    arrive3.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.05", net)));
    empty1.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
    empty1.addFeature(new Priority(0));
    empty2.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
    empty2.addFeature(new Priority(0));
    empty3.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
    empty3.addFeature(new Priority(0));
    serve1.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("1", net)));
    serve2.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("1"), new BigDecimal("4")));
    serve3.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("0"), new BigDecimal("1")));
    startService1.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("1"), new BigDecimal("2")));
    startService2.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("1"), new BigDecimal("2")));
    startService3.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal("1"), new BigDecimal("2")));
    timeout1.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("3"), MarkingExpr.from("1", net)));
    timeout1.addFeature(new Priority(0));
    timeout2.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("3"), MarkingExpr.from("1", net)));
    timeout2.addFeature(new Priority(0));
    timeout3.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("3"), MarkingExpr.from("1", net)));
    timeout3.addFeature(new Priority(0));
  }

}
