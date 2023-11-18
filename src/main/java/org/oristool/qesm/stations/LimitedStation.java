package org.oristool.qesm.stations;

import org.oristool.qesm.PollingSystem;

public class LimitedStation extends Station {

   public LimitedStation() {
      super();
      PollingSystem.buildLimitedStation(pn, m);
   }
}
