package jdc.pos.views;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import jdc.pos.entities.Category;
import jdc.pos.entities.Item;
import jdc.pos.entities.OrderDetail;
import jdc.pos.services.ItemRepository;
import jdc.pos.services.SaleRepository;

public class PosReport implements Initializable{

    @FXML
    private ComboBox<Category> category;
    @FXML
    private ComboBox<Item> item;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private TableView<OrderDetail> table;
    
    private ItemRepository itemRepo;
    private SaleRepository saleRepo;
    
    @FXML
    void clear() {
    	category.setValue(null);
    	item.setValue(null);
    	dateFrom.setValue(null);
    	dateTo.setValue(null);
    }

    @FXML
    void search() {
    	table.getItems().clear();
    	List<OrderDetail> list = saleRepo.search(category.getValue(), 
    			item.getValue(), dateFrom.getValue(), dateTo.getValue());
    	table.getItems().addAll(list);
    	PosFrame.setMessage("Size: " + list.size());
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		itemRepo = ItemRepository.getInstance();
		saleRepo = SaleRepository.getInstance();
		
		category.getItems().addAll(Category.values());
		
		category.valueProperty().addListener((a,b,c) -> {
			
			item.getItems().clear();
			Category cat = category.getValue();
			
			if(null != cat) {
				List<Item> items = itemRepo.search(cat, null);
				item.getItems().addAll(items);
			}
			
			search();
		});
		
		item.valueProperty().addListener((a,b,c) -> search());
		dateFrom.valueProperty().addListener((a,b,c) -> search());
		dateTo.valueProperty().addListener((a,b,c) -> search());
		
		search();
	}

}
