package controllers;

import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;
import util.LocationPref;

@With({LocationController.class})
public class User extends Controller {

	
	public static void register(@Valid models.User user,  @Required(message="Please type the code") String code, String randomID) throws Throwable {
		System.out.println("Register called with code=" + code + ", randomID="+randomID+", user="+user);
		validation.equals(user.password, user.passwordCheck).message("The passwords do not match");
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			Cache.delete(randomID);
			create();
		}
		
		System.out.println("Finding existing user");
		user.email = user.email.toLowerCase();
		models.User existingUser = models.User.findByEmail(user.email);
		System.out.println("Found existing user " + (existingUser != null ? existingUser.fullname : null));
		Boolean userExists = !(existingUser != null);
		validation.isTrue(userExists).message("User with this email already exists");
		
		System.out.println("Checking validation code");
		validation.equals(
	            code, Cache.get(randomID)
	        ).message("Invalid code. Please type it again");
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			Cache.delete(randomID);
			create();
		}
		Cache.delete(randomID);
		System.out.println("Saving user " + user.fullname);
		user.save();
		flash.success("Thanks for registering. Your login is %s, Please login now...", user.email);
		Secure.login();
	}
	
	public static void create() {		
		String randomID = Codec.UUID();
		LocationPref locationPref = LocationController.getLocation();
		System.out.println("LocationPref=" + locationPref);
		render(randomID, locationPref);
	}
	
	
}
