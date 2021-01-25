package jdc.pos.views;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jdc.pos.services.ItemRepository;
import jdc.pos.util.MiniPosException;

public class PosFrame implements Initializable {

    @FXML
    private Label title;
    @FXML
    private SVGPath icon;
    @FXML
    private StackPane container;
    @FXML
    private Label message;
    @FXML
    private MenuItem upload;
    @FXML
    private HBox box;
    
    private static Label MESSAGE;
    
    private ItemRepository itemRepo;
    private FileChooser chooser;
    
    private static final String POS = "M14.286 34.286c0 1.563-1.295 2.857-2.857 2.857s-2.857-1.295-2.857-2.857 1.295-2.857 2.857-2.857 2.857 1.295 2.857 2.857zM34.286 34.286c0 1.563-1.295 2.857-2.857 2.857s-2.857-1.295-2.857-2.857 1.295-2.857 2.857-2.857 2.857 1.295 2.857 2.857zM37.143 10v11.429c0 0.714-0.558 1.339-1.272 1.429l-23.304 2.723c0.112 0.513 0.29 1.027 0.29 1.563 0 0.513-0.313 0.982-0.536 1.429h20.536c0.781 0 1.429 0.647 1.429 1.429s-0.647 1.429-1.429 1.429h-22.857c-0.781 0-1.429-0.647-1.429-1.429 0-0.692 1.004-2.366 1.362-3.058l-3.951-18.371h-4.554c-0.781 0-1.429-0.647-1.429-1.429s0.647-1.429 1.429-1.429h5.714c1.496 0 1.54 1.786 1.763 2.857h26.808c0.781 0 1.429 0.647 1.429 1.429z";
    private static final String REPORT = "M35.852 8.948c-0.868-1.183-2.077-2.566-3.405-3.895s-2.711-2.538-3.895-3.405c-2.015-1.477-2.992-1.648-3.552-1.648h-19.375c-1.723 0-3.125 1.402-3.125 3.125v33.75c0 1.723 1.402 3.125 3.125 3.125h28.75c1.723 0 3.125-1.402 3.125-3.125v-24.375c0-0.56-0.171-1.537-1.648-3.552zM30.679 6.821c1.199 1.199 2.141 2.281 2.835 3.179h-6.014v-6.014c0.898 0.695 1.98 1.636 3.179 2.835zM35 36.875c0 0.339-0.286 0.625-0.625 0.625h-28.75c-0.339 0-0.625-0.286-0.625-0.625v-33.75c0-0.339 0.286-0.625 0.625-0.625 0 0 19.373-0 19.375 0v8.75c0 0.69 0.56 1.25 1.25 1.25h8.75v24.375z";
    
    private static EventHandler<MouseEvent> eventPos;
    private static EventHandler<MouseEvent> eventReport;
    
    @FXML
    void close() {
    	Platform.exit();
    }
    
    @FXML
    void upload() {
    	
    	try {
    		File file = chooser.showOpenDialog(null);
    		
    		if(null == file)
    			throw new MiniPosException("Please select a file to upload !!!");
    		
    		itemRepo.add(file.getAbsolutePath());
    		
    		loadFXML("PosHome.fxml");
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void loadFXML(String fxml) {
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource(fxml));
			container.getChildren().clear();
			container.getChildren().add(root);
		} catch (Exception e) {
			message.setText(e.getMessage());
			e.printStackTrace();
		}
	}

	private void fileChooserSetting() {
    	chooser = new FileChooser();
    	
    	File defaultPath = new File(System.getProperty("user.home"), "Desktop");
    	chooser.setInitialDirectory(defaultPath);
    	chooser.setTitle("Select a text file to upload !");
    	
    	ExtensionFilter extension = 
    			new FileChooser.ExtensionFilter("Text File Only (*.txt)", "*.txt");
    	
    	chooser.setSelectedExtensionFilter(extension);
    	chooser.getExtensionFilters().add(extension);
    }
    
    public static void show() {

    	try {
			// load fxml from file
			Parent root = FXMLLoader.load(PosFrame.class.getResource("PosFrame.fxml"));
			
			// set scene object
			Scene scene = new Scene(root);

			// set window (stage)
			Stage window = new Stage();
			window.setScene(scene);
			
			// show
			window.setTitle("JDC Mini POS");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		itemRepo = ItemRepository.getInstance();
		fileChooserSetting();
		
		MESSAGE = message;
		
		eventPos = event -> {
			loadFXML("PosHome.fxml");
			icon.setContent(POS);
			box.setOnMouseClicked(eventReport);
			upload.setVisible(true);
			title.setText("POS Home");
		};
		
		eventReport = event -> {
			loadFXML("PosReport.fxml");
			icon.setContent(REPORT);
			box.setOnMouseClicked(eventPos);
			upload.setVisible(false);
			title.setText("Sale History");
		};
		
		// default event 
		box.setOnMouseClicked(eventReport);
		
		loadFXML("PosHome.fxml");
	}
	
	public static void clearMessage() {
		MESSAGE.setText("");
	}
	
	public static void setMessage(String message) {
		MESSAGE.setText(message);
	}
    
}
