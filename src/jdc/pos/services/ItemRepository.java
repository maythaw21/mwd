package jdc.pos.services;

import java.util.List;

import jdc.pos.entities.Category;
import jdc.pos.entities.Item;
import jdc.pos.services.db.ItemRepositoryImpl;

public interface ItemRepository {

	public static ItemRepository getInstance() {
		return ItemRepositoryImpl.getInstance();
	}
	
	List<Item> search(Category c, String idName);
	
	void add(Item item);
	
	void add(String path);
	
}
