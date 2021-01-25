package jdc.pos.views;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import jdc.pos.util.Encryptor;
import jdc.pos.util.LoginSetting;
import jdc.pos.util.MiniPosException;

public class Login implements Initializable{

    @FXML
    private Label message;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    @FXML
    void cancel() {
    	message.getScene().getWindow().hide();
    }

    @FXML
    void login() {

    	try {
    		LoginSetting.checkLogin(login.getText(), Encryptor.encrypt(password.getText()));

    		PosFrame.show();
    		
    		cancel();
    		
		} catch (Exception e) {
			if(e instanceof MiniPosException) {
				message.setText(e.getMessage());
				System.out.println("MiniPOs Exception ...");
			} else {
				message.setText(e.getMessage());
			}
		}
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		message.setText("");
	}
}
