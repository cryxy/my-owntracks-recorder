package de.cryxy.owntracks.recorder;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.cryxy.owntracks.recorder.api.resources.LocationDtoResource;

public class Application {

	private static final URI BASE_URI = URI.create("http://localhost:8080/api/");

	public static void main(String[] args) throws MqttException, IOException {

//		SeContainerInitializer initializer = SeContainerInitializer.newInstance();
//
//		SeContainer container = initializer.initialize();
		{

//			final OwntracksMqttClient client = container.select(OwntracksMqttClient.class).get();
//			// client.connect();
//
//			final InfluxDbConnector influxDbConnector = container.select(InfluxDbConnector.class).get();
//			LocationQuery query = new LocationQuery();
//			query.setStartDate(LocalDateTime.now().minusHours(96));
//			query.setEndDate(LocalDateTime.now().minusHours(24));
//			// query.setDeviceName("g5");
//
//			List<LocationDto> locations = influxDbConnector.readLocations(query);
//			System.out.println(locations);

			final ResourceConfig resourceConfig = createJaxRsApp();

			final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
			server.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					System.out.println("Shutdown");
					server.shutdownNow();
//					client.disconnect();
//					influxDbConnector.cleanUp();
//					Event<Object> event = container.getBeanManager().getEvent();
//					event.fire(new ShutdownEvent());

				}
			});

		}

	}

	/**
	 * JAX-RS application defined as a CDI bean.
	 */
	@ManagedBean
	public static class JaxRsApplication extends javax.ws.rs.core.Application {

		@Context
		UriInfo uInfo;

		static final Set<Class<?>> appClasses = new HashSet<>();

		static {
			appClasses.add(LocationDtoResource.class);
		}

		@Override
		public Set<Class<?>> getClasses() {
			return appClasses;
		}
	}

	/**
	 * Create JAX-RS application. The same one is used also in the tests.
	 *
	 * @return Jersey's resource configuration of the Weld application.
	 */
	public static ResourceConfig createJaxRsApp() {
		return ResourceConfig.forApplicationClass(JaxRsApplication.class);
	}

}
