package org.oristool.qesm;

import java.util.ArrayList;
import java.util.List;

import org.oristool.qesm.distributions.ExpolynomialDistribution;
import org.oristool.qesm.stations.GatedStation;
import org.oristool.qesm.stations.Station;

public class Main {
   public static void main(String[] args) {
      List<Station> stations = new ArrayList<Station>();
      stations.add(new GatedStation());
      stations.add(new GatedStation());
      stations.add(new GatedStation());

      int nStationCompleted = 0;
      // INIT STATIONS
      for (int i = 0; i < stations.size(); i++) {
         Station station = stations.get(i);

         double[] newTimes = station.exec();
         station.setTimes(newTimes);
      }

      // EXECUTE APPROXIMATION
      while (true) {
         nStationCompleted = 0;
         for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);

            List<double[]> otherTimes = new ArrayList<double[]>();
            for (int k = 0; k < stations.size(); k++) {
               if (k != i) {
                  otherTimes.add(stations.get(k).getTimes());
               }
            }

            ExpolynomialDistribution oldDistribution = station.approximation.getDistribution();
            station.updatePNWithApproxTimes(otherTimes);

            if (oldDistribution != null) {
               double difference = Math
                     .abs(station.approximation.getDistribution().getDelta() - oldDistribution.getDelta());
               if (difference < 10E-9) {
                  nStationCompleted++;
               }
            } else {
               double[] newTimes = station.exec();
               station.setTimes(newTimes);
            }
         }

         System.out.println("nStationCompleted: " + nStationCompleted);
         if (nStationCompleted == stations.size()) {
            break;
         }
      }
   }
}