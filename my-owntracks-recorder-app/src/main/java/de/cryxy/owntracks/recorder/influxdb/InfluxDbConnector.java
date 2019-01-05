package de.cryxy.owntracks.recorder.influxdb;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.LogLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.BoundParameterQuery.QueryBuilder;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.influxdb.impl.TimeUtil;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import de.cryxy.owntracks.commons.enums.InternetConnectivity;
import de.cryxy.owntracks.recorder.config.Config;
import okhttp3.OkHttpClient;

@Singleton
public class InfluxDbConnector {

	private Logger LOG = Logger.getLogger(getClass().getName());

	/*
	 * table column names
	 */
	protected static final String TAG_USER_NAME = "userName";

	protected static final String TAG_GEO_HASH = "geoHash";

	protected static final String FIELD_LON = "lon";

	protected static final String FIELD_LAT = "lat";

	protected static final String NAME_MEASUREMENT = "locations";

	protected static final String TAG_TID = "tid";

	protected static final String TAG_DEVICE_NAME = "deviceName";

	protected static final String TAG_COUNTRY = "country";

	protected static final String TAG_PLACENAME = "placename";

	protected static final String FIELD_CONNECTIVITY = "connectivity";

	private static final String FIELD_BATT_LEVEL = "battLevel";

	protected static final String FIELD_ACCURACY = "accuracy";

	/*
	 * Binding params for the query
	 */
	private static final String BINDING_PARAM_DEVICE_NAME = TAG_DEVICE_NAME;

	private static final String BINDING_PARAM_USER_NAME = TAG_USER_NAME;

	private static final String BINDING_PARAM_TID = TAG_TID;

	private static final String BINDING_PARAM_END_TIME = "endTime";

	private static final String BINDING_PARAM_ACCURACY = "minAccuracy";

	private final String BINDING_PARAM_START_TIME = "startTime";

	@Inject
	private Config config;
	private InfluxDB influxDB;

	@PostConstruct
	public synchronized void init() {
		LOG.info("About to connect to " + config.getInfluxDbUrl());

		OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
		httpClientBuilder.readTimeout(2000, TimeUnit.MILLISECONDS);

		influxDB = InfluxDBFactory.connect(config.getInfluxDbUrl(), config.getInfluxDbUsername(),
				config.getInfluxDbPassword(), httpClientBuilder);

		LOG.info("Check for db existence " + config.getInfluxDbName());
		try {
			boolean databaseExists = influxDB.databaseExists(config.getInfluxDbName());
			if (!databaseExists) {
				LOG.warning("Database not found!");
				throw new IllegalStateException("Database not found!");
			}
		} catch (Exception e) {
			LOG.warning("Error connecting to database ...");
			e.printStackTrace();
			throw e;
		}

		influxDB.setDatabase(config.getInfluxDbName());

		influxDB.enableBatch(2000, 1000, TimeUnit.MILLISECONDS, Executors.defaultThreadFactory(),
				(failedPoints, throwable) -> {
					LOG.warning("Asynchronous Error: " + throwable.getMessage());
				});

		LOG.info("Init completed ... ");

	}

	@PreDestroy
	public void cleanUp() {
		System.out.println("[InfluxDB] About to close connection to " + config.getInfluxDbUrl());
		influxDB.flush();
		influxDB.close();
	}

	/**
	 * Write a new measurement from the given {@link LocationDto}.
	 */
	public void writeLocation(LocationDto dto) {
		// required fields and tags
		Builder pointBuilder = Point.measurement(NAME_MEASUREMENT)
				.time(dto.getTimestamp().atZone(ZoneId.systemDefault()).toEpochSecond(), TimeUnit.SECONDS)
				.addField(FIELD_LAT, dto.getLat()).addField(FIELD_LON, dto.getLon()).tag(TAG_GEO_HASH, dto.getGeoHash())
				.tag(TAG_USER_NAME, dto.getUserName());

		/*
		 * Optional fields and tags
		 */
		if (dto.getAccuracy() != null) {
			pointBuilder.addField(FIELD_ACCURACY, dto.getAccuracy());
		}
		if (dto.getBattLevel() != null) {
			pointBuilder.addField(FIELD_BATT_LEVEL, dto.getBattLevel());
		}
		if (dto.getConnectivity() != null) {
			pointBuilder.addField(FIELD_CONNECTIVITY, dto.getConnectivity().name());
		}
		if (dto.getPlacename() != null) {
			pointBuilder.tag(TAG_PLACENAME, dto.getPlacename());
		}
		if (dto.getCountry() != null) {
			pointBuilder.tag(TAG_COUNTRY, dto.getCountry());
		}
		if (dto.getDeviceName() != null) {
			pointBuilder.tag(TAG_DEVICE_NAME, dto.getDeviceName());
		}
		if (dto.getTid() != null) {
			pointBuilder.tag(TAG_TID, dto.getTid());
		}

		/*
		 * Write ...
		 */
		Point point = pointBuilder.build();
		LOG.fine("About to write Point=" + point);
		influxDB.write(point);

	}

	/**
	 * Read all measurements identified by the given {@link LocationQuery}.
	 */
	public List<LocationDto> readLocations(LocationQuery locationQuery) {

		List<List<Object>> bindingParams = new ArrayList<>();

		String queryCommand = String.format("SELECT * FROM %s", NAME_MEASUREMENT);
		StringBuilder queryCommandBuilder = new StringBuilder(queryCommand);

		if (locationQuery.getStartDate() == null)
			throw new IllegalArgumentException("startDate is required");

		queryCommandBuilder.append(String.format(" WHERE TIME >= $%s", BINDING_PARAM_START_TIME));
		String startTimeValue = TimeUtil.toInfluxDBTimeFormat(
				locationQuery.getStartDate().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
		LOG.info(String.format("Using startTime=%s.", startTimeValue));
		bindingParams.add(Arrays.asList(BINDING_PARAM_START_TIME, startTimeValue));

		if (locationQuery.getEndDate() != null) {
			queryCommandBuilder.append(String.format(" AND TIME <= $%s", BINDING_PARAM_END_TIME));
			String endTimeValue = TimeUtil.toInfluxDBTimeFormat(
					locationQuery.getEndDate().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
			bindingParams.add(Arrays.asList(BINDING_PARAM_END_TIME, endTimeValue));
		}

		if (locationQuery.getDeviceName() != null) {
			queryCommandBuilder.append(String.format(" AND %s=$%s", TAG_DEVICE_NAME, BINDING_PARAM_DEVICE_NAME));
			bindingParams.add(Arrays.asList(BINDING_PARAM_DEVICE_NAME, locationQuery.getDeviceName()));
		}

		if (locationQuery.getUserName() != null) {
			queryCommandBuilder.append(String.format(" AND %s=$%s", TAG_USER_NAME, BINDING_PARAM_USER_NAME));
			bindingParams.add(Arrays.asList(BINDING_PARAM_USER_NAME, locationQuery.getUserName()));
		}

		if (locationQuery.getTid() != null) {
			queryCommandBuilder.append(String.format(" AND %s=$%s", TAG_TID, BINDING_PARAM_TID));
			bindingParams.add(Arrays.asList(BINDING_PARAM_TID, locationQuery.getTid()));
		}

		if (locationQuery.getMinAccuracy() != null) {
			queryCommandBuilder.append(String.format(" AND %s=$%s", FIELD_ACCURACY, BINDING_PARAM_ACCURACY));
			bindingParams.add(Arrays.asList(BINDING_PARAM_ACCURACY, locationQuery.getMinAccuracy()));
		}

		queryCommandBuilder.append(" ORDER BY time");

		/*
		 * build query
		 */
		QueryBuilder queryBuilder = QueryBuilder.newQuery(queryCommandBuilder.toString())
				.forDatabase(config.getInfluxDbName());
		for (List<Object> bindingParam : bindingParams) {
			queryBuilder.bind((String) bindingParam.get(0), bindingParam.get(1));
		}

		BoundParameterQuery query = queryBuilder.create();
		LOG.info(String.format("Running the following query=%s and parameter=%s", query.getCommand(),
				query.getParameterJsonWithUrlEncoded()));
		QueryResult queryResult = influxDB.setLogLevel(LogLevel.BASIC).query(queryBuilder.create(), TimeUnit.SECONDS);
		List<Result> results = queryResult.getResults();
		if (results.isEmpty()) {
			LOG.info("Empty result.");
			return null;
		}

		List<Series> serieses = results.get(0).getSeries();
		if (serieses == null || serieses.isEmpty()) {
			LOG.info("Empty result.");
			return null;
		}

		Series series = serieses.get(0);
		if (!series.getName().equals(NAME_MEASUREMENT)) {
			LOG.info("Empty result.");
			return null;
		}

		// System.out.println(queryResult);
		LOG.info(String.format("Loaded %d results.", series.getValues().size()));

		List<LocationDto> mappedResult = mapSeries(series);

		return mappedResult;

	}

	/**
	 * Map series to {@link LocationDto}.
	 */
	protected List<LocationDto> mapSeries(Series series) {

		LOG.info("Map InfluxDB results to LocationDtos");

		List<LocationDto> locationDtos = series.getValues().stream().map(measurement -> {
			LocationDto locationDto = new LocationDto();
			locationDto.setTimestamp(
					LocalDateTime.ofEpochSecond(((Double) measurement.get(0)).longValue(), 1000, ZoneOffset.UTC));
			locationDto.setGeoHash((String) measurement.get(getIndex(TAG_GEO_HASH, series)));
			locationDto.setLat((Double) measurement.get(getIndex(FIELD_LAT, series)));
			locationDto.setLon((Double) measurement.get(getIndex(FIELD_LON, series)));
			locationDto.setUserName((String) measurement.get(getIndex(TAG_USER_NAME, series)));

			/*
			 * optional values
			 */

			Double accuracy = (Double) measurement.get(getIndex(FIELD_ACCURACY, series));
			if (accuracy != null)
				locationDto.setAccuracy(accuracy.intValue());

			Double battLevel = (Double) measurement.get(getIndex(FIELD_BATT_LEVEL, series));
			if (battLevel != null)
				locationDto.setBattLevel(battLevel.intValue());

			String connectivityStr = (String) measurement.get(getIndex(FIELD_CONNECTIVITY, series));
			if (connectivityStr != null)
				locationDto.setConnectivity(InternetConnectivity.valueOf(connectivityStr));
			locationDto.setCountry((String) measurement.get(getIndex(TAG_COUNTRY, series)));
			locationDto.setDeviceName((String) measurement.get(getIndex(TAG_DEVICE_NAME, series)));
			locationDto.setPlacename((String) measurement.get(getIndex(TAG_PLACENAME, series)));
			locationDto.setTid((String) measurement.get(getIndex(TAG_TID, series)));

			return locationDto;
		}).collect(Collectors.toList());

		return locationDtos;
	}

	protected int getIndex(String name, Series series) {
		// TODO improve
		List<String> columns = series.getColumns();

		return columns.indexOf(name);
	}

}
