package util;

import play.mvc.Http.Request;
import play.mvc.Scope.Session;

public class UserUtil {

	
	public static String userNameOrIp(Request request, Session session) {
		return session.get("username") != null ? session.get("username") : IPUtil.clientIp(request);
	}
	
	public static String loggedUser(Session session) throws NoValidLoginException {
		if(session.get("username") != null) {
			return session.get("username");
		}
		throw new NoValidLoginException("No logged in user in session.");
	}
	
	public static boolean isLogged(Session session) {
		return session.get("username") != null;
	}
}
