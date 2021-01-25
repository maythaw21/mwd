package jdc.pos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	private static final String URL = "jdbc:mysql://localhost:3306/jdc_mini_pos?useSSL=false";
	private static final String USR = "root";
	private static final String PASS = "admin";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USR, PASS);
	}
}
