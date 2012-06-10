package controllers;

import java.util.UUID;

import models.User;
import play.libs.Codec;
import play.mvc.Mailer;

public class MailSender extends Mailer {

	public static void welcome(User user, String activateUrl) {
		setSubject("Welcome %s", user.fullname);
	    addRecipient(user.email);
		setFrom("noreply@gmail.com");
		send(user, activateUrl);
	}
	 
	public static void replyTo(String postedBy, String title, String replyTo, String message) {
		setSubject(title);
		addRecipient(postedBy);
		setFrom(replyTo);
		send(message);
	}
	
}
