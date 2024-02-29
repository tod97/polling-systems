package org.oristool.qesm;

import java.math.BigDecimal;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.oristool.qesm.distributions.Distribution;
import org.oristool.qesm.distributions.ExpolynomialDistribution;
import org.oristool.qesm.distributions.ExponentialDistribution;
import org.oristool.qesm.printers.LineChartPrinter;
import org.oristool.qesm.stations.ExhaustiveStation;
import org.oristool.qesm.stations.Station;

public class Main {
   private static final int nStations = 3;
   private static final int nIterations = 1000;

   public static void main(String[] args) {
      runApproximator();
   }

   public static void runApproximator() {
      List<Station> stations = new ArrayList<Station>();
      List<XYSeries> bodyLamdaSeries = new ArrayList<XYSeries>();
      List<XYSeries> deltaSeries = new ArrayList<XYSeries>();
      List<XYSeries> uppSeries = new ArrayList<XYSeries>();

      for (int i = 0; i < nStations; i++) {
         stations.add(new ExhaustiveStation());
      }

      int nStationCompleted = 0;

      for (int i = 0; i < stations.size(); i++) {
         bodyLamdaSeries.add(new XYSeries("Station " + (i + 1)));
         deltaSeries.add(new XYSeries("Station " + (i + 1)));
         uppSeries.add(new XYSeries("Station " + (i + 1)));
      }

      // EXECUTE APPROXIMATION
      for (int count = 0; count < nIterations; count++) {
         nStationCompleted = 0;
         for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);

            BigDecimal additionalUpTime = new BigDecimal(0);
            List<Station> otherStations = new ArrayList<Station>();
            for (int k = 0; k < stations.size(); k++) {
               if (k != i && stations.get(k).getCDF() != null) {
                  otherStations.add(stations.get(k));
                  additionalUpTime = new BigDecimal(
                        additionalUpTime.doubleValue() + stations.get(k).getUpTime().doubleValue());
               }
            }

            Distribution oldDistribution = station.approximation.getDistribution();
            station.updatePNWithOtherStations(otherStations);
            /* station.setUpTime(
                  station.getUpTime().add(station.getUpTime().multiply(BigDecimal.valueOf(count + 1)))); */
            //double[] newCDF = station.exec(additionalUpTime);
            double[] newCDF = station.exec(BigDecimal.ZERO);
            station.approxCDF(newCDF);
            station.setCDF(newCDF);
            Distribution newDistribution = station.approximation.getDistribution();

            if (oldDistribution != null && newDistribution != null) {
               double difference = Math
                     .abs(newDistribution.getSignificantThreshold(oldDistribution));
               if (difference < 10E-2) {
                  nStationCompleted++;
               }
            }

            System.out.println(newDistribution);
            if (newDistribution instanceof ExpolynomialDistribution) {
               ExpolynomialDistribution expDist = (ExpolynomialDistribution) newDistribution;
               bodyLamdaSeries.get(i).add(count + 1, expDist.getBodyLambda());
               deltaSeries.get(i).add(count + 1, expDist.getDelta());
               uppSeries.get(i).add(count + 1, expDist.getUpp());
            }
            if (newDistribution instanceof ExponentialDistribution) {
               ExponentialDistribution expDist = (ExponentialDistribution) newDistribution;
               bodyLamdaSeries.get(i).add(count + 1, expDist.getExpRate().doubleValue());
               //deltaSeries.get(i).add(count+1, expDist.getClockRate().doubleValue());
            }

         }

         System.out.println("--------------------------------------------------");
         System.out.println("Completed Stations: " + nStationCompleted);
         System.out.println("--------------------------------------------------");
         System.out.println("--------------------------------------------------");
         if (nStationCompleted == stations.size()) {
            break;
         }
      }

      printSeries(bodyLamdaSeries, "", "bodyLambda", new Point(0, 0));
      printSeries(deltaSeries, "", "delta", new Point(600, 0));
      printSeries(uppSeries, "", "upp", new Point(1200, 0));
   }

   private static void printSeries(List<XYSeries> series, String title, String yAxis, Point point) {
      var dataset = new XYSeriesCollection();
      for (XYSeries s : series) {
         dataset.addSeries(s);
      }

      var ex = new LineChartPrinter(dataset, title, "Iteration", yAxis);
      ex.setVisible(true);
      ex.setLocation(point);
   }
}