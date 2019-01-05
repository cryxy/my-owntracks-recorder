package de.cryxy.owntracks.commons.enums;

/**
 * Internet connectivity status (route to host) when the message is created.
 * @author fabian
 *
 */
public enum InternetConnectivity {
	
	NO,WIFI,MOBILE_DATA;
	
	public static InternetConnectivity fromString(String str) {
		switch (str) {
		case "w":
			return WIFI;
		case "m":
			return MOBILE_DATA;

		default:
			return NO;
		}
	}

}
