package controllers;

import models.EntityType;
import models.Reply;
import play.data.validation.Required;
import play.mvc.*;
import util.UserUtil;

@With({LocationController.class,Secure.class})
public class ReplyTo extends Controller {

	
    public static void display(EntityType entityType, String postedBy, long entityId, String subject) {
        render(entityType, postedBy, entityId,subject);
    }
    
    public static void send(@Required(message="EntityType is required") EntityType entityType, @Required(message="postedBy is required") String postedBy, @Required(message="entityId is required") long entityId, @Required(message="Subject is required") String subject, @Required(message="Message cannot be empty")String message) {
    	if(validation.hasErrors())
        {
    		params.flash("message");
        	validation.keep();
        	display(entityType, postedBy, entityId, subject);
        }
    	MailSender.replyTo(postedBy, subject, UserUtil.loggedUser(session), message);
    	flash.success("Your message has been sent");
    	Classifieds.index();
    }

}
