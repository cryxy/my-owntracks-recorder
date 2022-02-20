/**
 * 
 */
package de.cryxy.owntracks.recorder.api.resources;

import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author fabian
 *
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

	@Override
	public LocalDateTime unmarshal(String s) throws Exception {
		return LocalDateTime.parse(s);
	}

	@Override
	public String marshal(LocalDateTime dateTime) throws Exception {
		return dateTime.toString();
	}

}
