package controllers;

import java.util.HashMap;
import java.util.Map;

import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;
import util.KeyGen;
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
		user.save();
		String uuid = Codec.UUID();
		Cache.set(KeyGen.userActivation(uuid), user, "10mn");
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("uuid", uuid);
		String activateUrl =  Router.getFullUrl("User.activate", paramsMap);
		flash.success("Thanks for registering. Your login is %s. Please check your email and click the verify link to activate your account", user.email);
		System.out.println("Saving user " + user.fullname);
		MailSender.welcome(user, activateUrl);
		Secure.login();
	}
	
	public static void create() {		
		String randomID = Codec.UUID();
		render(randomID);
	}
	
	public static void activate(String uuid) throws Throwable {
		String key = KeyGen.userActivation(uuid);
		models.User user = (models.User) Cache.get(key);
		if(user != null) {
			models.User dbUser = models.User.findById(user.id);
			dbUser.isActive = true;
			dbUser.save();
			flash.success("Thanks for verifying your account. Your account is now active. Your login id is %s " , user.email);
		}else {
			flash.error("The activation url is no longer valid, Pls click on Resend Activation on login screen to get a new activation");
		}
		Secure.login();
	}
	
	
}
