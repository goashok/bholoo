package controllers;

import play.Logger;
import play.cache.Cache;
import play.mvc.*;
import util.CityParser;
import util.IPUtil;
import util.LocationPref;

public class LocationController extends Controller {

	
	@Before(unless={"changeLocationPreference"})
	public static void resolveLocation() {
		String clientIp = IPUtil.clientIp(request);
		System.out.println("Resolving lcoation for clientIp " + clientIp);
		if(Cache.get(clientIp) == null) {
			LocationPref pref;
			try {
				System.out.println("Cannot find clientIp in cache for clientIp: " + clientIp);
				models.City city = City.findByIp(clientIp);
				pref = new LocationPref(city.name, city.state, city.zip);
			}catch(Exception e) {
				e.printStackTrace();
				models.City nyc = CityParser.cityByNameState.get("New York"+"NY"); //default to NYC
				pref = new LocationPref(nyc.name, nyc.state, nyc.zip);
			}
			Cache.set(clientIp, pref, "8h");
		}else {
			System.out.println("ip exists with LocationPref " + ((LocationPref)Cache.get(clientIp)).city);
		}
		renderArgs.put("locationPref", getLocation());
	}
	
	public static LocationPref getLocation() {
		return (LocationPref) Cache.get(IPUtil.clientIp(request));
	}
	
	public static void changeLocationPref(String cityPreference, int radius) {
		Logger.info("new city pref" + cityPreference);
		String clientIp = IPUtil.clientIp(request);
		cityPreference = cityPreference.replaceAll(", ", "");
		System.out.println("Finding location for " + cityPreference);
		models.City newCity = CityParser.cityByNameState.get(cityPreference);
		LocationPref locationPref = new LocationPref(newCity.name, newCity.state, newCity.zip, radius);		
		Cache.delete(clientIp);
		Logger.info("Deleted cache entry for clientIp " + clientIp);
		Cache.add(clientIp, locationPref, "8h");
		Application.index();
	}

}
