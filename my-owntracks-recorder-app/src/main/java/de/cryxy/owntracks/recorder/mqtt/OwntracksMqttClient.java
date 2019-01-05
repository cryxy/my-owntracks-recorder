package de.cryxy.owntracks.recorder.mqtt;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import de.cryxy.owntracks.commons.dtos.LocationDto;

public class OwntracksMqttClient {

	private static Logger LOG = Logger.getLogger(OwntracksMqttClient.class.getName());

	@Inject
	private de.cryxy.owntracks.recorder.config.Config config;

	@Inject
	private Event<LocationDto> event;

	private final String clientId = "cryxy-owntrack-recorder";

	private MqttConnectOptions connOptions;

	private MqttClient client;

	public OwntracksMqttClient() {
		LOG.info("Creating client ...");
	}

	public void connect() throws MqttException {
		connect(config.getMqttServerUri(), config.getMqttUsername(), config.getMqttPassword(), config.getMqttTopic());
	}

	public void connect(String serverURI, String userName, String password, String topicFilter) throws MqttException {

		client = new MqttClient(serverURI, clientId);

		client.setCallback(new MqttCallbackExtended() {

			public void connectionLost(Throwable throwable) {
				LOG.log(Level.WARNING, "Connection lost! Try automatic reconnect ...", throwable);
			}

			public void deliveryComplete(IMqttDeliveryToken arg0) {

			}

			public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
				if (LOG.isLoggable(Level.FINE))
					LOG.log(Level.FINE, topic + "-" + new String(mqttMessage.getPayload()));
				LocationDto locationDto = OwntrackJsonParser.createFrom(topic, mqttMessage.getPayload());
				if (locationDto != null) {
					LOG.fine("Fire event with locationDto=" + locationDto);
					event.fireAsync(locationDto);
				}

			}

			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				LOG.info("Connected to mqtt server ... " + serverURI);

				try {
					LOG.info("Subscribe to ... " + topicFilter);
					client.subscribe(topicFilter);
				} catch (MqttException e) {
					LOG.log(Level.WARNING, "Subscribing failed!", e);
				}

			}

		});

		connOptions = new MqttConnectOptions();
		connOptions.setUserName(userName);
		connOptions.setPassword(password.toCharArray());
		connOptions.setAutomaticReconnect(true);

		LOG.info("Connect to mqtt server ... " + client.getServerURI());
		client.connect(connOptions);
	}

	public void onContainerInitializedEvent(@Observes ContainerInitialized event) {
		try {
			connect();
		} catch (MqttException e) {
			LOG.log(Level.WARNING, "Error connection to MQTT Broker.");
		}
	}

	@PreDestroy
	public void disconnect() {
		System.out.println("[MQTT] Disconnecting from MQTT Server");
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
