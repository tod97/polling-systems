package org.oristool.qesm;

import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.oristool.qesm.distributions.ExpolynomialDistribution;
import org.oristool.qesm.printers.LineChartPrinter;
import org.oristool.qesm.stations.GatedStation;
import org.oristool.qesm.stations.Station;

public class Main {
   public static void main(String[] args) {
      runApproximator();
   }

   public static void runApproximator() {
      List<Station> stations = new ArrayList<Station>();
      List<XYSeries> series = new ArrayList<XYSeries>();

      stations.add(new GatedStation());
      stations.add(new GatedStation());
      stations.add(new GatedStation());

      int nStationCompleted = 0;

      for (int i = 0; i < stations.size(); i++) {
         series.add(new XYSeries("Station " + (i + 1)));
      }

      // EXECUTE APPROXIMATION
      for (int count = 0; count < 1000; count++) {
         nStationCompleted = 0;
         for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);

            List<Station> otherStations = new ArrayList<Station>();
            for (int k = 0; k < stations.size(); k++) {
               if (k != i && stations.get(k).getTimes() != null) {
                  otherStations.add(stations.get(k));
               }
            }

            ExpolynomialDistribution oldDistribution = station.approximation.getDistribution();
            station.updatePNWithOtherStations(otherStations);
            double[] newTimes = station.exec();
            station.approxTimes(newTimes);
            station.setTimes(newTimes);
            ExpolynomialDistribution newDistribution = station.approximation.getDistribution();

            if (oldDistribution != null && newDistribution != null) {
               double difference = Math
                     .abs(newDistribution.getBodyLambda() - oldDistribution.getBodyLambda());
               if (difference < 10E-2) {
                  nStationCompleted++;
               }
            }

            System.out.println(station);
            series.get(i).add(count + 1, station.approximation.getDistribution().getBodyLambda());
         }

         System.out.println();
         System.out.println("--------------------------------------------------");
         System.out.println("Completed Stations: " + nStationCompleted);
         System.out.println("--------------------------------------------------");
         System.out.println();
         if (nStationCompleted == stations.size()) {
            break;
         }
      }

      printSeries(series);
   }

   public static void printExample() {
      var series = new XYSeries("2016");
      series.add(1, 567);
      series.add(2, 612);
      series.add(3, 800);
      series.add(4, 980);
      series.add(5, 1410);
      series.add(6, 2350);

      printSeries(List.of(series));
   }

   private static void printSeries(List<XYSeries> series) {
      var dataset = new XYSeriesCollection();
      for (XYSeries s : series) {
         dataset.addSeries(s);
      }

      var ex = new LineChartPrinter(dataset, "Execution time every iteration", "Iteration", "bodyLambda");
      ex.setVisible(true);
   }
}