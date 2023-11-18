package org.oristool.qesm;

import org.oristool.qesm.stations.GatedStation;
import org.oristool.qesm.stations.Station;

public class Main {
    public static void main(String[] args) {
        Station station = new GatedStation();
        
        station.exec();
    }
}