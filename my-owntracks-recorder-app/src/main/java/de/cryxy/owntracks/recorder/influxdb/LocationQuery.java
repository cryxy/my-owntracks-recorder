package de.cryxy.owntracks.recorder.influxdb;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.cryxy.owntracks.recorder.api.resources.LocalDateTimeAdapter;

/**
 * Query a location measurement.
 * 
 * @author fabian
 *
 */
public class LocationQuery {

	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String tid;
	private String userName;
	private String deviceName;
	private Integer minAccuracy;

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getStartDate() {
		return startDate;
	}

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getEndDate() {
		return endDate;
	}

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @return the accuracy
	 */
	public Integer getMinAccuracy() {
		return minAccuracy;
	}

	/**
	 * @param accuracy the accuracy to set
	 */
	public void setMinAccuracy(Integer accuracy) {
		this.minAccuracy = accuracy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LocationQuery [startDate=" + startDate + ", endDate=" + endDate + ", tid=" + tid + ", userName="
				+ userName + ", deviceName=" + deviceName + ", accuracy=" + minAccuracy + "]";
	}

}
