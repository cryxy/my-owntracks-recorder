package de.cryxy.owntracks.recorder.api.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import de.cryxy.owntracks.recorder.gpx.LocationDtoToGpxMapper;
import de.cryxy.owntracks.recorder.influxdb.InfluxDbConnector;
import de.cryxy.owntracks.recorder.influxdb.LocationQuery;

@Path("locations")
public class LocationDtoResource {

	private Logger LOG = Logger.getLogger(getClass().getName());

	@Inject
	private InfluxDbConnector influxDbConnector;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/gpx+xml")
	@Path("/query")
	public Response readLocations(@NotNull LocationQuery query) throws IOException {

		// no bean validation available
		if (query == null) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		List<LocationDto> locations;
		try {
			locations = influxDbConnector.readLocations(query);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error reading locations from influxdb or mapping.", e);
			throw new WebApplicationException("Error reading locations.",
					Response.serverError().entity(e.getMessage()).build());
		}

		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) LocationDtoToGpxMapper.map(locations);

		ResponseBuilder responseBuilder = Response.ok(outputStream.toString());
		responseBuilder.header("Content-Disposition", "attachment; filename=" + "locations.gpx");

		return responseBuilder.build();

	}

}
