package util;

import java.io.Serializable;

public class LocationPref implements Serializable {
	
	private static final int DEFAULT_RADIUS = 50;
	public String city;
	public String state;
	public String zip;
	public int radius = DEFAULT_RADIUS;	
	
	public LocationPref(String city, String state, String zip) {
		this.city = city;
		this.state = state;
		this.zip = zip;
	}
	
	public LocationPref(String city, String state, String zip, int radius) {
		this(city, state, zip);
		this.radius = radius;
	}

}
