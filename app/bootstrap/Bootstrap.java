package bootstrap;

import play.*;
import play.jobs.*;
import play.test.*;
import util.CityParser;

import models.*;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		try {
			CityParser.parseCities();
		} catch (Exception e) {
			throw new RuntimeException("Unable to load cities", e);
		}
	}

}