package de.cryxy.owntracks.recorder.gpx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;

/**
 * 
 * 
 *
 */
public class LocationDtoToGpxMapper {

	private static Logger LOG = Logger.getLogger(LocationDtoToGpxMapper.class.getName());

	public static OutputStream map(List<LocationDto> locationDtos) throws IOException {

		LOG.info("Map LocationDtos to GPX.");

		List<WayPoint> waypoints = locationDtos.stream().map(locationDto -> {

			return WayPoint.builder().desc(locationDto.toString()).lat(locationDto.getLat()).lon(locationDto.getLon())
					.time(locationDto.getTimestamp().atZone(ZoneId.of("Etc/UTC")).toInstant()).build();
		}).collect(Collectors.toList());

		final GPX gpx = GPX.builder().addTrack(track -> track.addSegment(segment -> segment.points(waypoints))).build();

		OutputStream output = new ByteArrayOutputStream();

		GPX.write(gpx, output);

		return output;
	}

}
