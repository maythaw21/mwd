package jdc.pos.util;

public class LoginSetting {
	
	public static void checkLogin(String loginId, String pass) {
		
		// empty login text
		if(loginId == null || loginId.isEmpty()) {
			throw new MiniPosException("Login ID should not be empty !");
		}
		
		// empty password text
		if(pass == null || pass.isEmpty()) {
			throw new MiniPosException("Password should not be empty !");
		}
		
		// wrong login ID
		if(!loginId.equals(PosSetting.getValue("pos.login.id"))) {
			throw new MiniPosException("Please check Login ID !");
		}
		
		// wrong password
		if(!pass.equals(PosSetting.getValue("pos.login.pass"))) {
			throw new MiniPosException("Please check password !");
		}
	}
	
}
