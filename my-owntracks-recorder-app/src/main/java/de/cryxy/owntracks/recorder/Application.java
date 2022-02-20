package de.cryxy.owntracks.recorder;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.cryxy.owntracks.recorder.api.resources.LocationDtoResource;
import jakarta.annotation.ManagedBean;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

public class Application {

	private static final URI BASE_URI = URI.create("http://0.0.0.0:8080/api/");

	public static void main(String[] args) throws MqttException, IOException {

		{

			final ResourceConfig resourceConfig = createJaxRsApp();

			final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
			server.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					System.out.println("Shutdown");
					server.shutdownNow();

				}
			});

		}

	}

	/**
	 * JAX-RS application defined as a CDI bean.
	 */
	@ManagedBean
	public static class JaxRsApplication extends jakarta.ws.rs.core.Application {

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
