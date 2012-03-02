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
				models.City city = City.findByIp(ip);
				Cache.set(ip, city, "30mn");
			}catch(Exception e) {
				models.City nyc = CityParser.cityByNameState.get("New York"+"NY"); //default to NYC
				Cache.set(ip, nyc, "30mn");
			}
		}
	}
	
	public static models.City getLocation() {
		return (models.City) Cache.get(request.remoteAddress);
	}

}
