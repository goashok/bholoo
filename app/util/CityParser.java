package util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import play.vfs.VirtualFile;

import models.City;

public class CityParser {

	public static List<City> cities = new LinkedList<City>();
	public static Map<String, City> cityByNameState = new HashMap<String, City>();
	public static Map<String, City> cityByZip = new HashMap<String, City>();
	

	public static void parseCities() throws IOException {
		VirtualFile vFile = VirtualFile.fromRelativePath("conf/data/location/cities_extended.sql");
		System.out.println(vFile.getRealFile().getAbsolutePath());
		BufferedReader bufReader = new BufferedReader(new FileReader(vFile.getRealFile()));
		String line;
		while ((line = bufReader.readLine()) != null) {
			StringTokenizer tok = new StringTokenizer(line, "|");
			while (tok.hasMoreTokens()) {
				String name = tok.nextToken();
				String state = tok.nextToken();
				String zip = tok.nextToken();
				double latitude = Double.valueOf(tok.nextToken());
				double longitude = Double.valueOf(tok.nextToken());
				String county = tok.nextToken();
				City city = new City(name, state, zip, latitude, longitude,
						county);
				cities.add(city);
				cityByNameState.put(name + state, city);
				cityByZip.put(zip, city);
			}
		}

	}
	
	public static List<City> findCitiesMatchingName(String term) {
		String termUpperCase = term.toUpperCase();
		List<City> matches = new LinkedList<City>();
		for(City city: cityByNameState.values()) {
			if(city.nameUpperCase.contains(termUpperCase)) {				
				matches.add(city);
			}
		}
		return matches;
	}

	public static void main(String[] args) throws Exception {
		parseCities();
	}
}