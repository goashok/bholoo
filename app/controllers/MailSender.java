package controllers;

import java.util.UUID;

import models.User;
import play.libs.Codec;
import play.mvc.Mailer;

public class MailSender extends Mailer {

	public static void welcome(User user, String activateUrl) {
		setSubject("Welcome %s", user.fullname);
	    addRecipient(user.email);
		setFrom("goashok@gmail.com");
		send(user, activateUrl);
	}
}
