package org.oristool.qesm.stations;

import org.oristool.qesm.PollingSystem;

public class GatedStation extends Station {

   public GatedStation() {
      super();
      PollingSystem.buildGatedStation(pn, m);
   }
}
