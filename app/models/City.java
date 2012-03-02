package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import com.maxmind.geoip.Location;

import java.util.*;

@Entity
@Table(name="tb_city")
public class City extends Model {
    
	public String name;
	public String nameUpperCase;
	public String state;
	public String stateUpperCase;
	public String zip;
	public double latitude;
	public double longitude;
	public String county;
	
	
	public static final City NOT_FOUND = new City("No City Found", "", "", 0, 0, "");
	
	public City(String name, String state, String zip, double latitude,
			double longitude, String county) {
		this.name = name;
		this.nameUpperCase = name.toUpperCase();
		this.state = state;
		this.stateUpperCase = state.toUpperCase();
		this.zip = zip;
		this.latitude = latitude;
		this.longitude = longitude;
		this.county = county;
		
	}
	
	
	public double distance(City city) {
		Location thisLoc = new Location(Double.valueOf(latitude).floatValue(), Double.valueOf(longitude).floatValue());
		Location otherLoc = new Location(Double.valueOf(city.latitude).floatValue(), Double.valueOf(city.longitude).floatValue());
		return thisLoc.distance(otherLoc);
	}
		
	

	@Override
	public String toString() {
		return "City [city=" + name + ", state=" + state + ", zip="
				+ zip + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", county=" + county + "]";
	}
	
	
}
