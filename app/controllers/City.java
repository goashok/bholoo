package controllers;




import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;
import com.maxmind.geoip.timeZone;

import play.Logger;
import play.cache.Cache;
import play.mvc.*;
import play.vfs.VirtualFile;
import util.CityParser;
import util.LocationPref;

public class City extends Controller {

	
	private static LookupService lookupService; 
	
	public City() throws Exception {
		
	}
	public static void index() {
		render();
	}

	public static void listMatches(String term) {
		renderJSON(CityParser.findCitiesMatchingName(term));
	}

	public static models.City findByIp(String ip) throws IOException {
		if(lookupService == null) {			
			VirtualFile vFile = VirtualFile.fromRelativePath("conf/data/ip/GeoLiteCity.dat");
			lookupService = new LookupService(vFile.getRealFile().getAbsolutePath(),
					LookupService.GEOIP_MEMORY_CACHE);
		}
		Location ipLocation = lookupService.getLocation(ip);
		if(ipLocation == null) {
			Logger.warn("Unable to find location for ip %s, Defaulting to New York City" , ip);
			ipLocation = lookupService.getLocation("159.53.78.143"); //NYC location
		}
		if(Logger.isDebugEnabled()) {
			Logger.debug("findByIp(%s): countryCode %s, region %s, city %s, postalCode %s, latitude %s longitude %s, timeZone %s", ip,
					ipLocation.countryCode, ipLocation.region, ipLocation.city, ipLocation.postalCode, ipLocation.latitude, ipLocation.longitude, 
					timeZone.timeZoneByCountryAndRegion(ipLocation.countryCode,
							ipLocation.region));
		}
			return CityParser.cityByNameState.get(ipLocation.city
					+ ipLocation.region);

	}
	
	public static Set<models.City> neighborsByName(String name, String state, int radius) throws Exception {
		models.City city = CityParser.cityByNameState.get(name+state);
	    return withinRadius(city, radius);

}

	public static Set<models.City> neighborsByZip(String zip, int radius) throws Exception {
		models.City city = CityParser.cityByZip.get(zip);
		return withinRadius(city, radius);
		
	}
		
	private static Set<models.City> withinRadius(models.City city, double radius) {
		Set<models.City> matches = new HashSet<models.City>();
		if(city == null) {
			return matches;
		}
		double latitude1 = city.latitude;
		double longitude1 = city.longitude;

		//Upper reaches of possible boundaries
		double upperLatBound = latitude1 + radius/40.0;
		double lowerLatBound = latitude1 -radius/40.0;

		double upperLongBound = longitude1 + radius/40.0;
		double lowerLongBound = longitude1 - radius/40.0;

		//pull back possible matches
		matches = matchingCities(lowerLatBound, upperLatBound, lowerLongBound, upperLongBound);
		Set<models.City> acceptSet = new HashSet<models.City>();
		for(models.City match : matches)
		{
			double matchLatitude = match.latitude;
			double matchLongitude = match.longitude;
			double d = 3963.0 * Math.acos(Math.sin(latitude1 * Math.PI/180) * Math.sin(matchLatitude * Math.PI/180) + Math.cos(latitude1 * Math.PI/180) * Math.cos(matchLatitude * Math.PI/180) *  Math.cos(matchLongitude*Math.PI/180 -longitude1 * Math.PI/180));
		    if(d <radius)
		    {
		      acceptSet.add(match);  
		    }
		}
		//renderJSON(acceptList);
		return acceptSet;
	}
	
		public static Set<models.City> matchingCities( double lowerLatBound, double upperLatBound, double lowerLongBound, double upperLongBound) {
			Set<models.City> matched = new HashSet<models.City>();
			for(models.City city : CityParser.cities) {
				if(city.latitude >= lowerLatBound && city.latitude <= upperLatBound && city.longitude >= lowerLongBound && city.longitude <= upperLongBound) {
					matched.add(city);
				}
			}
			return matched;
		}
	
}
