package org.oristool.qesm;

import java.util.ArrayList;
import java.util.List;

import org.oristool.qesm.stations.GatedStation;
import org.oristool.qesm.stations.LimitedStation;
import org.oristool.qesm.stations.Station;

public class Main {
    public static void main(String[] args) {
        List<Station> stations = new ArrayList<Station>();
        stations.add(new GatedStation());
        stations.add(new LimitedStation());

        int nStationCompleted = 0;
        
        //while (nStationCompleted < stations.size()) {
        for(int j = 0; j < 10; j++) {
            nStationCompleted = 0;
            for (int i = 0; i < stations.size(); i++) {
                Station station = stations.get(i);

                if (station.getTimes() == null) {
                    double[] newTimes = station.exec();
                    station.setTimes(newTimes);
                } else {
                    double[] otherTimes = getStationsTimesButIndex(stations, i);
                    station.updateWaitingTime(otherTimes);
                    
                    double[] newTimes = station.exec();
                    if(station.isTimesDiffInThreshold(newTimes, 0.01)) {
                        nStationCompleted++;
                    } else {
                        station.setTimes(newTimes);
                    }
                }
            }
        }
    }

    private static double[] getStationsTimesButIndex(List<Station> stations, int index) {
        if (stations.size() == 0) {
            return null;
        }

        double[] times = new double[stations.get(0).getTimes().length];

        for(int i = 0; i < stations.size(); i++) {
            if (i != index) {
                for(int j = 0; j < times.length; j++) {
                    times[j] += stations.get(i).getTimes()[j];
                }
            }
        }

        return times;
    }
}