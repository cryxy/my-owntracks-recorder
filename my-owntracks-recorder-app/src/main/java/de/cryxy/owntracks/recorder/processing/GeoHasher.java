package de.cryxy.owntracks.recorder.processing;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.hsr.geohash.GeoHash;
import de.cryxy.owntracks.commons.dtos.LocationDto;
import de.cryxy.owntracks.recorder.config.Config;

@Singleton
public class GeoHasher {
	
	@Inject
	private Config config;
	
	private final Logger LOG = Logger.getLogger(getClass().getName());
	
	public LocationDto processDto(LocationDto dto) {
		String geoHash = GeoHash.geoHashStringWithCharacterPrecision(dto.getLat(), dto.getLon(), config.getCharacterPrecision());
		dto.setGeoHash(geoHash);
		LOG.fine("Calculated geohash: " + dto );
		return dto;
	}

}
