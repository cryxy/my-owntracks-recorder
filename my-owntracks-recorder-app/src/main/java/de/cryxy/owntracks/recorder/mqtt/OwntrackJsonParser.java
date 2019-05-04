package de.cryxy.owntracks.recorder.mqtt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import de.cryxy.owntracks.commons.dtos.LocationDto;
import de.cryxy.owntracks.commons.enums.InternetConnectivity;

public class OwntrackJsonParser {

	public static LocationDto createFrom(String topicPath, byte[] payload) {

		ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);

		JsonReader reader = Json.createReader(inputStream);

		JsonObject locationObject = reader.readObject();
		String type = locationObject.getString("_type");
		if (type.equals("location")) {
			LocationDto dto = new LocationDto();

			// user and device name
			List<String> topicParts = Arrays.asList(topicPath.split("/"));
			dto.setUserName(topicParts.get(1));
			dto.setDeviceName(topicParts.get(2));

			// timestamp
			JsonNumber timestampNumber = locationObject.getJsonNumber("tst");
			if (timestampNumber != null)
				dto.setTimestamp(Instant.ofEpochSecond(timestampNumber.longValue()).atZone(ZoneId.systemDefault())
						.toLocalDateTime());

			// lat
			JsonNumber latNumber = locationObject.getJsonNumber("lat");
			if (latNumber == null)
				// zumindest die Koordinaten sollten gesetzt sein
				return null;
			dto.setLat(latNumber.doubleValue());

			// lon
			JsonNumber lonNumber = locationObject.getJsonNumber("lon");
			if (lonNumber == null)
				return null;
			dto.setLon(lonNumber.doubleValue());

			// battery
			JsonNumber battNumber = locationObject.getJsonNumber("batt");
			if (battNumber != null)
				dto.setBattLevel(battNumber.intValue());

			// acc
			JsonNumber accNumber = locationObject.getJsonNumber("acc");
			if (accNumber != null)
				dto.setAccuracy(accNumber.intValue());

			// alt
			JsonNumber altNumber = locationObject.getJsonNumber("alt");
			if (altNumber != null)
				dto.setAlt(altNumber.intValue());

			// vel
			JsonNumber velNumber = locationObject.getJsonNumber("vel");
			if (velNumber != null)
				dto.setVel(velNumber.intValue());

			// heart rate
			JsonNumber heartRateNumber = locationObject.getJsonNumber("hr");
			if (heartRateNumber != null)
				dto.setVel(heartRateNumber.intValue());

			// tid
			JsonString tidString = locationObject.getJsonString("tid");
			if (tidString != null) {
				dto.setTid(tidString.getString());
			}

			// conn
			JsonString connString = locationObject.getJsonString("conn");
			if (connString != null) {
				dto.setConnectivity(InternetConnectivity.fromString(connString.getString()));
			}

			return dto;
		}

		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

}
