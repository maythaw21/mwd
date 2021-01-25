package jdc.pos.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class MessageHandler {

	private static Alert alert = new Alert(null);
	
	static {
		alert.setResizable(true);
	}
	
	public static void show(String message) {
		alert.setAlertType(AlertType.INFORMATION);
		alert.setHeaderText("Message from MiniPOS");
		alert.setContentText(message);
		alert.setTitle("Application Message");
		alert.show();
	}
	
	public static void show(Exception e) {
		
		AlertType type = e instanceof MiniPosException ? 
				AlertType.WARNING : AlertType.ERROR;
		
		if(null == e.getMessage() || e.getMessage().isEmpty())
			alert.setContentText("Please contact to Developer !!!");
		else 
			alert.setContentText(e.getMessage());
		
		alert.setAlertType(type);
		alert.setTitle("Error in Application");
		alert.show();
	}
	
	public static void toFront() {
		Stage window = (Stage) alert.getDialogPane().getScene().getWindow();
		window.setAlwaysOnTop(true);
	}
	
}
