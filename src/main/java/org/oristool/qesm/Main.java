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
      List<double[]> cumulativeTimes = new ArrayList<double[]>();
      // INIT STATIONS
      for (int i = 0; i < stations.size(); i++) {
         Station station = stations.get(i);

         double[] stationTimes = station.exec();
         station.setTimes(stationTimes);
         station.updatePNWithApproxTimes(cumulativeTimes);
         stationTimes = station.exec();
         station.setTimes(stationTimes);
         cumulativeTimes.add(stationTimes);
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
               if (difference < 10E-6) {
                  nStationCompleted++;
               } else {
                  double[] newTimes = station.exec();
                  station.setTimes(newTimes);
               }
            } else {
               double[] newTimes = station.exec();
               station.setTimes(newTimes);
            }
            System.out.println("- Station " + i);
            System.out.println("Delta: " + station.approximation.getDistribution().getDelta());
            System.out.println();
         }

         System.out.println("--------------------------------------------------");
         System.out.println("SUMMARY: ");
         System.out.println("Completed Stations: " + nStationCompleted);
         System.out.println("--------------------------------------------------");
         if (nStationCompleted == stations.size()) {
            break;
         }
      }
   }
}