package controllers;

import play.mvc.Controller;

public class EntityStats extends Controller{

	public static void all() {
		renderXml(models.EntityStats.findAll());
	}
	
	public static void hits() {
		renderXml(models.EntityStats.find("hits > 0").fetch());
	}
	
	public static void likes() {
		renderXml(models.EntityStats.find("likes > 0").fetch());
	}
	
	public static void spams() {
		renderXml(models.EntityStats.find("spams >0 ").fetch());
	}
	
	public static void abuses() {
		renderXml(models.EntityStats.find("abuses > 0").fetch());
	}
}
