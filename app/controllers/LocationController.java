package controllers;

import play.cache.Cache;
import play.mvc.*;
import util.CityParser;
import util.LocationPref;

public class LocationController extends Controller {

	
	@Before(unless={"changeLocationPreference"})
	public static void resolveLocation() {
		String ip = request.remoteAddress;
		System.out.println("Resolving lcoation for ip " + ip);
		if(Cache.get(ip) == null) {
			LocationPref pref;
			try {
				System.out.println("Cannot find ip in cache for ip: " + request.remoteAddress);
				models.City city = City.findByIp(ip);
				pref = new LocationPref(city.name, city.state, city.zip);
			}catch(Exception e) {
				e.printStackTrace();
				models.City nyc = CityParser.cityByNameState.get("New York"+"NY"); //default to NYC
				pref = new LocationPref(nyc.name, nyc.state, nyc.zip);
			}
			Cache.set(ip, pref, "8h");
		}else {
			System.out.println("ip exists with LocationPref " + ((LocationPref)Cache.get(ip)).city);
		}
		
	}
	
	public static LocationPref getLocation() {
		return (LocationPref) Cache.get(request.remoteAddress);
	}
	
	public static void changeLocationPref(String cityPreference, int radius) {
		System.out.println("new city pref" + cityPreference);
		cityPreference = cityPreference.replaceAll(", ", "");
		//cityPreference = cityPreference.replaceAll(" ", "");
		System.out.println("Finding location for " + cityPreference);
		models.City newCity = CityParser.cityByNameState.get(cityPreference);
		LocationPref locationPref = new LocationPref(newCity.name, newCity.state, newCity.zip, radius);
		//Cache.replace(request.remoteAddress, location, "8h");
		
		Cache.delete(request.remoteAddress);
		System.out.println("Deleted cache entry for ip " + request.remoteAddress);
		Cache.add(request.remoteAddress, locationPref, "8h");
		//System.out.println("added cache entry for ip " + request.remoteAddress + " as " + location.name);
		Application.index();
	}

}
