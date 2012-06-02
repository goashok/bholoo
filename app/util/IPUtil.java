package util;

import play.mvc.Http.Header;
import play.mvc.Http.Request;

public class IPUtil {

	
	public static String clientIp(Request request) {
		String clientIp;
		Header xForwadedForHeader = request.headers.get("x-forwarded-for"); 
		if(xForwadedForHeader != null) {
			clientIp =  xForwadedForHeader.value();
		}else {
			clientIp = request.remoteAddress;
		}
		return clientIp;
	}
}
