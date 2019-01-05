package de.cryxy.owntracks.commons.dtos;

import java.time.LocalDateTime;

import de.cryxy.owntracks.commons.enums.InternetConnectivity;
import de.cryxy.owntracks.commons.enums.ReportTrigger;

public class LocationDto {

	/**
	 * timestamp of the location fix
	 */
	private LocalDateTime timestamp;

	private String userName;
	private String deviceName;

	/**
	 * A TID is a tracker ID, a two-character identifier of your chosing, your
	 * initials.
	 */
	private String tid;

	private Double lat;
	private Double lon;

	/**
	 * Device battery level (iOS,Android/integer/percent/optional)
	 */
	private Integer battLevel;

	/**
	 * Accuracy of the reported location in meters without unit
	 * (iOS/integer/meters/optional)
	 */
	private Integer accuracy;

	private ReportTrigger trigger;
	private InternetConnectivity connectivity;

	/**
	 * https://en.wikipedia.org/wiki/Geohash
	 */
	private String geoHash;

	/**
	 * Obtained via offline reverse geocoding.
	 * 
	 * @see https://github.com/AReallyGoodName/OfflineReverseGeocode
	 */
	private String placename;

	private String country;

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
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

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Integer getBattLevel() {
		return battLevel;
	}

	public void setBattLevel(Integer battLevel) {
		this.battLevel = battLevel;
	}

	public Integer getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}

	public ReportTrigger getTrigger() {
		return trigger;
	}

	public void setTrigger(ReportTrigger trigger) {
		this.trigger = trigger;
	}

	public InternetConnectivity getConnectivity() {
		return connectivity;
	}

	public void setConnectivity(InternetConnectivity connectivity) {
		this.connectivity = connectivity;
	}

	public String getGeoHash() {
		return geoHash;
	}

	public void setGeoHash(String geoHash) {
		this.geoHash = geoHash;
	}

	public String getPlacename() {
		return placename;
	}

	public void setPlacename(String placename) {
		this.placename = placename;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "LocationDto [timestamp=" + timestamp + ", userName=" + userName + ", deviceName=" + deviceName
				+ ", tid=" + tid + ", lat=" + lat + ", lon=" + lon + ", battLevel=" + battLevel + ", accuracy="
				+ accuracy + ", trigger=" + trigger + ", connectivity=" + connectivity + ", geoHash=" + geoHash
				+ ", placename=" + placename + ", country=" + country + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accuracy == null) ? 0 : accuracy.hashCode());
		result = prime * result + ((battLevel == null) ? 0 : battLevel.hashCode());
		result = prime * result + ((connectivity == null) ? 0 : connectivity.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((deviceName == null) ? 0 : deviceName.hashCode());
		result = prime * result + ((geoHash == null) ? 0 : geoHash.hashCode());
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lon == null) ? 0 : lon.hashCode());
		result = prime * result + ((placename == null) ? 0 : placename.hashCode());
		result = prime * result + ((tid == null) ? 0 : tid.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((trigger == null) ? 0 : trigger.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationDto other = (LocationDto) obj;
		if (accuracy == null) {
			if (other.accuracy != null)
				return false;
		} else if (!accuracy.equals(other.accuracy))
			return false;
		if (battLevel == null) {
			if (other.battLevel != null)
				return false;
		} else if (!battLevel.equals(other.battLevel))
			return false;
		if (connectivity != other.connectivity)
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (deviceName == null) {
			if (other.deviceName != null)
				return false;
		} else if (!deviceName.equals(other.deviceName))
			return false;
		if (geoHash == null) {
			if (other.geoHash != null)
				return false;
		} else if (!geoHash.equals(other.geoHash))
			return false;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (lon == null) {
			if (other.lon != null)
				return false;
		} else if (!lon.equals(other.lon))
			return false;
		if (placename == null) {
			if (other.placename != null)
				return false;
		} else if (!placename.equals(other.placename))
			return false;
		if (tid == null) {
			if (other.tid != null)
				return false;
		} else if (!tid.equals(other.tid))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (trigger != other.trigger)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

}
