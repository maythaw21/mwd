package jdc.pos.views;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.ToIntFunction;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import jdc.pos.entities.Category;
import jdc.pos.entities.Item;
import jdc.pos.entities.OrderDetail;
import jdc.pos.entities.Voucher;
import jdc.pos.services.ItemRepository;
import jdc.pos.services.SaleRepository;
import jdc.pos.util.MessageHandler;
import jdc.pos.util.MiniPosException;

public class PosHome implements Initializable{

    @FXML
    private ComboBox<Category> category;
    @FXML
    private TextField idName;
    @FXML
    private Label totalHeader;
    @FXML
    private TableView<Item> itemTable;
    @FXML
    private TableView<OrderDetail> cartTable;
    @FXML
    private Label subTotal;
    @FXML
    private Label total;
    @FXML
    private Label tax;
    @FXML
    private TableColumn<OrderDetail, Integer> count;
    
    private SaleRepository saleRepo;
    private ItemRepository itemRepo;

    @FXML
    void clear() {
    	category.setValue(null);
    	idName.clear();
    }

    @FXML
    void clearCart() {
    	cartTable.getItems().clear();
    	showCash();
    }

    @FXML
    void delete() {
    	try {
    		OrderDetail order = cartTable.getSelectionModel().getSelectedItem();
    		
    		if(null == order)
    			throw new MiniPosException("Please select order in cart table !!!");
    		
    		cartTable.getItems().remove(order);
    		showCash();
    		
    	} catch (Exception e) {
    		MessageHandler.show(e);
    		MessageHandler.toFront();
    	}
    }

    @FXML
    void paid() {
    	try {
			if(cartTable.getItems().isEmpty()) 
				throw new MiniPosException("Please add order into cart table !!!");
			
			Voucher voucher = new Voucher();
			voucher.getDetails().addAll(cartTable.getItems());
			
    		saleRepo.paid(voucher);
    		clearCart();
    		
		} catch (Exception e) {
			MessageHandler.show(e);
			MessageHandler.toFront();
		}
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		itemRepo = ItemRepository.getInstance();
		saleRepo = SaleRepository.getInstance();
		
		category.getItems().addAll(Category.values());
		
		category.valueProperty().addListener( (a,b,c) -> search());
		idName.textProperty().addListener( (a,b,c) -> search());
		
		search();
		
		itemTable.setOnMouseClicked(event -> {
			
			if(event.getClickCount() == 2) {
				
				Item item = itemTable.getSelectionModel().getSelectedItem();
				
				// check cartTable for add existing item
				OrderDetail order = cartTable.getItems().stream()
					.filter(od -> od.getItem().getId() == item.getId())
					.findAny().orElse(null);
				
				if(order == null) {
					order = new OrderDetail();
					order.setItem(item);
					cartTable.getItems().add(order);
				}
				
				order.setCount(order.getCount() + 1);
				order.calculatePrice();
				
				cartTable.refresh();
				
				showCash();
			}
			
		});
		
		totalHeader.textProperty().bind(total.textProperty());
		
		cartTable.setEditable(true);
		
		count.setCellFactory(TextFieldTableCell.forTableColumn(
				new StringConverter<Integer>() {

			@Override
			public String toString(Integer object) {
				if(null != object) {
					return object.toString();
				}
				return null;
			}

			@Override
			public Integer fromString(String string) {
				try {
					if(null != string && !string.isEmpty()) {
						return Integer.parseInt(string);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}));
		
		count.setOnEditCommit(event -> {
			OrderDetail order = event.getRowValue();
			order.setCount(event.getNewValue());
			order.calculatePrice();
			
			cartTable.refresh();
			showCash();
		});
		
		MenuItem delete = new MenuItem("DELETE");
		delete.setOnAction(event -> delete());
		
		ContextMenu menu = new ContextMenu(delete);
		cartTable.setContextMenu(menu);
		
	}
	
	private Integer convertSum(ToIntFunction<OrderDetail> mapper) {
		return cartTable.getItems().stream().mapToInt(mapper).sum();
	}

	private void showCash() {
		subTotal.setText(convertSum(order -> order.getSubTotal()).toString());
		tax.setText(convertSum(order -> order.getTax()).toString());
		total.setText(convertSum(order -> order.getTotal()).toString());
	}

	private void search() {
		
		List<Item> items = itemRepo.search(category.getValue(), idName.getText());
		
		itemTable.getItems().clear();
		itemTable.getItems().addAll(items);
		PosFrame.setMessage("Size: " + itemTable.getItems().size());
	}

}
