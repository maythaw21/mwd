package jdc.pos.util;

import java.io.FileInputStream;
import java.util.Properties;

public class PosSetting {

	private static Properties properties;
	
	static {
		
		try {
			properties = new Properties();
			properties.load(new FileInputStream("application.properties"));
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static String getValue(String key) {
		return properties.getProperty(key);
	}
	
//	public static void main(String[] args) {
//		System.out.println(getValue("pos.login.id"));
//		System.out.println(getValue("pos.login.pass"));
//	}
}
