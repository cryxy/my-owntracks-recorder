package de.cryxy.owntracks.recorder.api.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import de.cryxy.owntracks.recorder.gpx.LocationDtoToGpxMapper;
import de.cryxy.owntracks.recorder.influxdb.InfluxDbConnector;
import de.cryxy.owntracks.recorder.influxdb.LocationQuery;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

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
