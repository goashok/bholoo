package controllers;

import static models.EntityType.YellowPages;
import static models.StatsType.Ratings;
import jobs.StatsJob;
import models.EntityType;
import models.StatsType;
import play.Logger;
import play.mvc.Controller;
import util.UserUtil;

public class Ratings extends Controller{

	
	public static void businessRating(long businessId, EntityType entityType, double rating) {
		Logger.info("User %s rated %s %s with rating of %s start", UserUtil.userNameOrIp(request, session), entityType, businessId, rating);
		String ratingsContributor =  UserUtil.userNameOrIp(request, session);
    	new StatsJob(Ratings, YellowPages, businessId , ratingsContributor, rating).now();  
    	//TODO like should be ajax call without page refesh.
    	flash.success("Thanks for liking");
		
	}
	
	private static double round(double cumulativeRatings, long numRatingsContribution) {
		double rating = (double) cumulativeRatings/numRatingsContribution;
		return Math.round(rating *2)/2d; 
	}
}
