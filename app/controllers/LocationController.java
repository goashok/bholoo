package controllers;

import play.cache.Cache;
import play.mvc.*;
import util.CityParser;

public class LocationController extends Controller {

	
	@Before
	public static void resolveLocation() {
		String ip = request.remoteAddress;
		if(Cache.get(ip) == null) {
			try {
				System.out.println("Cannot find ip in cache for ip: " + request.remoteAddress);
				models.City city = City.findByIp(ip);
				Cache.set(ip, city, "8h");
			}catch(Exception e) {
				models.City nyc = CityParser.cityByNameState.get("New York"+"NY"); //default to NYC
				Cache.set(ip, nyc, "8h");
			}
		}
	}
	
	public static models.City getLocation() {
		return (models.City) Cache.get(request.remoteAddress);
	}

}
