package org.oristool.qesm;

import java.util.ArrayList;
import java.util.List;

import org.oristool.qesm.stations.GatedStation;
import org.oristool.qesm.stations.Station;

public class Main {
   public static void main(String[] args) {
      List<Station> stations = new ArrayList<Station>();
      stations.add(new GatedStation());
      stations.add(new GatedStation());
      stations.add(new GatedStation());

      int nStationCompleted = 0;

      for (int j = 0; j < 100; j++) {
         nStationCompleted = 0;
         for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);

            if (station.getTimes() == null) {
               double[] newTimes = station.exec();
               station.setTimes(newTimes);
            } else {
               List<double[]> otherTimes = new ArrayList<double[]>();
               for (int k = 0; k < stations.size(); k++) {
                  if (k != i) {
                     otherTimes.add(stations.get(k).getTimes());
                  }
               }
               station.updatePNWithApproxTimes(otherTimes);

               double[] newTimes = station.exec();
               if (station.isTimesDiffInThreshold(newTimes, 10E-9)) {
                  nStationCompleted++;
               } else {
                  station.setTimes(newTimes);
               }
            }
         }

         System.out.println("nStationCompleted: " + nStationCompleted);
         if (nStationCompleted == stations.size()) {
            break;
         }
      }
   }
}