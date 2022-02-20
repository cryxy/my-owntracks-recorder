package de.cryxy.owntracks.recorder.influxdb;

import java.util.logging.Logger;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import jakarta.enterprise.event.ObservesAsync;

public class InfluxDbLocationDtoObserver {

	private static Logger LOG = Logger.getLogger(InfluxDbLocationDtoObserver.class.getName());

	public void observeEvent(@ObservesAsync LocationDto event) {

		LOG.fine("###" + event);

	}

}
