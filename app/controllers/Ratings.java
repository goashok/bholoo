package controllers;

import play.Logger;
import play.mvc.Controller;

public class Ratings extends Controller{

	
	public static void businessRating(String businessId, String rating) {
		Logger.info("Rated Business %s with rating of %s start", businessId, rating);
	}
	
}
