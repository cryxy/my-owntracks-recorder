package de.cryxy.owntracks.recorder.influxdb;

import java.util.logging.Logger;

import javax.enterprise.event.ObservesAsync;

import de.cryxy.owntracks.commons.dtos.LocationDto;

public class InfluxDbLocationDtoObserver {

	private static Logger LOG = Logger.getLogger(InfluxDbLocationDtoObserver.class.getName());

	public void observeEvent(@ObservesAsync LocationDto event) {

		LOG.fine("###" + event);

	}

}
