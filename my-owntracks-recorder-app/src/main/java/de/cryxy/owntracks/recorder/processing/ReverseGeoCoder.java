package de.cryxy.owntracks.recorder.processing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import de.cryxy.owntracks.recorder.config.Config;
import geocode.GeoName;
import geocode.ReverseGeoCode;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ReverseGeoCoder {

	private final Logger LOG = Logger.getLogger(getClass().getName());

	@Inject
	private Config config;
	private ReverseGeoCode coder;

	@PostConstruct
	public void init() {

		coder = new ReverseGeoCode();
		LOG.info("Reading GeonamesZip from " + config.getGeonamesZip());
		String[] geonameZips = config.getGeonamesZip().split(",");

		Boolean geonamesOnlyMajorCities = config.getGeonamesOnlyMajorCities();
		LOG.info("Only major cities? " + geonamesOnlyMajorCities);

		for (String geonameZip : geonameZips) {
			FileInputStream targetStream = null;
			try {
				LOG.info("Processing " + geonameZip);
				File zipFile = new File(geonameZip);
				targetStream = new FileInputStream(zipFile);
				ZipInputStream zippedPlacednames = new ZipInputStream(targetStream);
				coder.addZipInputStream(zippedPlacednames, false);
			} catch (Exception e) {
				LOG.warning("Error while reading geonames ...");
				e.printStackTrace();
			} finally {
				try {
					targetStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		LOG.info("Build KD-tree");
		coder.build();

	}

	public LocationDto processDto(LocationDto dto) {

		GeoName geoName = coder.nearestPlace(dto.getLat(), dto.getLon());

		dto.setPlacename(geoName.name);
		dto.setCountry(geoName.country);

		LOG.fine("Result of reverse geocoding: " + dto);

		return dto;
	}

}
