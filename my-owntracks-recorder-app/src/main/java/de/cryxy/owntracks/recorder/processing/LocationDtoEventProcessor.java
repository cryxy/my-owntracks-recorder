package de.cryxy.owntracks.recorder.processing;

import java.util.logging.Logger;

import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import de.cryxy.owntracks.recorder.influxdb.InfluxDbConnector;

public class LocationDtoEventProcessor {
	
	private final Logger LOG = Logger.getLogger(getClass().getName());
	
	@Inject
	private GeoHasher geoHasher;
	
	@Inject
	private ReverseGeoCoder reverseGeoCoder;
	
	@Inject
	private InfluxDbConnector influxDbConnector;

	public void observeEvent(@ObservesAsync LocationDto event) {
		
		// processing
		LocationDto locationDto = geoHasher.processDto(event);
		reverseGeoCoder.processDto(locationDto);
		
		LOG.fine("Processed Dto about to write=" + locationDto.toString());
		
		// write to influxDb
		influxDbConnector.writeLocation(locationDto);

	}

}
