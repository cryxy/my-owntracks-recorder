package de.cryxy.owntracks.recorder.mqtt;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.weld.environment.se.events.ContainerInitialized;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeforeShutdown;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class OwntracksMqttClient {

	private static Logger LOG = Logger.getLogger(OwntracksMqttClient.class.getName());

	@Inject
	private de.cryxy.owntracks.recorder.config.Config config;

	@Inject
	private Event<LocationDto> event;

	private final String clientId = "cryxy-owntrack-recorder";

	private Mqtt3AsyncClient client;

	public OwntracksMqttClient() {
		LOG.info("Creating client ...");
	}

	public void connect() {
		connect(config.getMqttServerUri(), config.getMqttUsername(), config.getMqttPassword(), config.getMqttTopic());
	}

	public void connect(String serverURI, String userName, String password, String topicFilter) {

		URI uri = URI.create(serverURI);

		Mqtt3ClientBuilder builder = MqttClient.builder().useMqttVersion3().identifier(clientId)
				.serverHost(uri.getHost()).serverPort(uri.getPort()).automaticReconnectWithDefaultConfig();

		switch (uri.getScheme()) {
		case "ssl":
			builder.sslWithDefaultConfig();
			break;
		case "ws":
			builder.webSocketWithDefaultConfig();
			break;
		case "wss":
			builder.sslWithDefaultConfig().webSocketWithDefaultConfig();
		default:
			break;
		}

		client = builder.buildAsync();

		LOG.info("Connecting to " + uri.toString());

		/*
		 * TODO: Log disconnect
		 */
		client.connectWith().simpleAuth().username(userName).password(password.getBytes()).applySimpleAuth().send()
				.whenComplete((connAck, throwable) -> {
					if (throwable != null) {
						LOG.log(Level.WARNING, "Connecting failed!", throwable);
					} else {
						LOG.info("Successful connected to " + uri.toString());
					}
				});

		client.subscribeWith().topicFilter(topicFilter).callback(publish -> {
			String topic = publish.getTopic().toString();
			byte[] payload = publish.getPayloadAsBytes();
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.FINE, topic + "-" + new String(payload));
			LocationDto locationDto;
			try {
				locationDto = OwntrackJsonParser.createFrom(topic, payload);
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Error parsing mqtt payload: " + new String(payload), e);
				return;
			}
			if (locationDto != null) {
				LOG.fine("Fire event with locationDto=" + locationDto);
				event.fireAsync(locationDto);
			}
		}).send().whenComplete((subAck, throwable) -> {
			if (throwable != null) {
				LOG.log(Level.WARNING, "Subscribing failed!", throwable);
			} else {
				LOG.info("Subscribed to ... " + topicFilter);
			}
		});

	}

	public void onContainerInitializedEvent(@Observes ContainerInitialized event) {
		try {
			connect();
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error connection to MQTT Broker.", e);
		}
	}

	@PreDestroy
	public void disconnect() {
		LOG.info("[MQTT] Disconnecting from MQTT Server");
		try {
			LOG.info("Disconnecting ...");
			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void observeEvent(@Observes BeforeShutdown event) throws RuntimeException {
		disconnect();
	}

}
