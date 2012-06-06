package controllers;

import models.User;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password)  {
		User user = User.connect(username, password);
		if(user == null) {
			return false;
		}else {
			//check if is active
			if(!user.isActive) {
				flash.error("Your account seems to be inactive. Pls click on Resend Verification to verify your account");
				try {
					Secure.login();
				}catch(Throwable e) {
				 //
				}
				return false;
			}else {
				return true;
			}
		}
	}
		

}
