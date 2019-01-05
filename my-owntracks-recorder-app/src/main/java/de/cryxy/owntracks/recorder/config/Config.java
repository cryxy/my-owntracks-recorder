package de.cryxy.owntracks.recorder.config;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class Config {

	private final Logger LOG = Logger.getLogger(getClass().getName());

	/*
	 * MQTT
	 */
	private final String PROP_MQTT_USERNAME = "de.cryxy.owntracks.recorder.mqtt.server.user.name";
	private final String PROP_MQTT_PASSWD = "de.cryxy.owntracks.recorder.mqtt.server.user.password";
	private final String PROP_MQTT_TOPIC = "de.cryxy.owntracks.recorder.mqtt.server.topic";
	private final String PROP_MQTT_SERVER_URI = "de.cryxy.owntracks.recorder.mqtt.server.uri";

	/*
	 * Geoprocessing
	 */
	private final String PROP_GEOHASH_CHARACTER_PRECISION = "de.cryxy.owntracks.recorder.processing.geohashCharacterPrecision";
	private final String PROP_GEOCODING_GEONAMES_ZIP = "de.cryxy.owntracks.recorder.processing.geonames.zips";
	private final String PROP_GEOCODING_GEONAMES_ONLY_MAJOR = "de.cryxy.owntracks.recorder.processing.geonames.onlyMajorCities";

	private String pathToConfig = "config/config.properties";

	/*
	 * InfluxDB
	 */
	private final String PROP_INFLUX_DB_NAME = "de.cryxy.owntracks.recorder.influxdb.dbName";
	private final String PROP_INFLUX_DB_URL = "de.cryxy.owntracks.recorder.influxdb.url";
	private final String PROP_INFLUX_DB_USERNAME = "de.cryxy.owntracks.recorder.influxdb.user.name";
	private final String PROP_INFLUX_DB_PASSWD = "de.cryxy.owntracks.recorder.influxdb.user.password";

	/*
	 * API
	 */
	private final String PROP_API_USERNAME = "de.cryxy.owntracks.recorder.api.user.name";
	private final String PROP_API_PASSWD = "de.cryxy.owntracks.recorder.api.user.password";

	private Properties config;

	public Config() {

	}

	public Config(String pathToConfig) {

		this.pathToConfig = pathToConfig;

	}

	@PostConstruct
	public void init() {

		try {

			String recorderEnvironmentConfig = System.getenv("RECORDER_CONFIG");
			if (recorderEnvironmentConfig != null) {
				pathToConfig = recorderEnvironmentConfig;
			}

			LOG.log(Level.INFO, "Startup, read config from: " + pathToConfig);

			config = new Properties();
			FileInputStream in = new FileInputStream(pathToConfig);
			config.load(in);
			in.close();
			LOG.log(Level.FINE, "Configuration readed.");
		} catch (Exception e) {
			throw new RuntimeException("Error reading config.", e);
		}

	}

	public String getMqttUsername() {
		return config.getProperty(PROP_MQTT_USERNAME);
	}

	public String getMqttPassword() {
		return config.getProperty(PROP_MQTT_PASSWD);
	}

	public String getMqttTopic() {
		return config.getProperty(PROP_MQTT_TOPIC);
	}

	public String getMqttServerUri() {
		return config.getProperty(PROP_MQTT_SERVER_URI);
	}

	/**
	 * Value between 1 and 12. e.g. 6 -> ±0.61 km.
	 * 
	 * @see https://gis.stackexchange.com/questions/115280/what-is-the-precision-of-a-geohash
	 */
	public Integer getCharacterPrecision() {
		return Integer.valueOf(config.getProperty(PROP_GEOHASH_CHARACTER_PRECISION));
	}

	/**
	 * 
	 * @see http://download.geonames.org/export/dump/
	 */
	public String getGeonamesZip() {
		return config.getProperty(PROP_GEOCODING_GEONAMES_ZIP);
	}

	public Boolean getGeonamesOnlyMajorCities() {
		return Boolean.valueOf(config.getProperty(PROP_GEOCODING_GEONAMES_ONLY_MAJOR, "false"));
	}

	public String getInfluxDbUsername() {
		String property = config.getProperty(PROP_INFLUX_DB_USERNAME);
		return property;
	}

	public String getInfluxDbPassword() {
		return config.getProperty(PROP_INFLUX_DB_PASSWD);
	}

	public String getInfluxDbUrl() {
		return config.getProperty(PROP_INFLUX_DB_URL);
	}

	public String getInfluxDbName() {
		return config.getProperty(PROP_INFLUX_DB_NAME);
	}

	public String getApiUsername() {
		String property = config.getProperty(PROP_API_USERNAME);
		return property;
	}

	public String getApiPassword() {
		return config.getProperty(PROP_API_PASSWD);
	}

}
