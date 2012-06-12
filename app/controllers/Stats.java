package controllers;

import play.mvc.Controller;

public class Stats extends Controller{

	public static void all() {
		renderXml(models.Stats.findAll());
	}
	
	public static void hits() {
		renderXml(models.Stats.find("hits > 0").fetch());
	}
	
	public static void likes() {
		renderXml(models.Stats.find("likes > 0").fetch());
	}
	
	public static void spams() {
		renderXml(models.Stats.find("spams >0 ").fetch());
	}
	
	public static void abuses() {
		renderXml(models.Stats.find("abuses > 0").fetch());
	}
	
	public static void ratings() {
		renderXml(models.Stats.find("cumulativeRatings > 0").fetch());
	}
}
