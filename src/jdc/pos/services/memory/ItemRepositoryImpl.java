package jdc.pos.services.memory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jdc.pos.entities.Category;
import jdc.pos.entities.Item;
import jdc.pos.services.ItemRepository;
import jdc.pos.util.MiniPosException;

public class ItemRepositoryImpl implements ItemRepository {

	private List<Item> items;
	private static ItemRepository INSTANCE = new ItemRepositoryImpl();
	
	
	protected ItemRepositoryImpl() {
		items = new ArrayList<Item>();
	}
	
	public static ItemRepository getInstance() {
		return INSTANCE;
	}
	
	public int size() {
		return items.size();
	}
	
	@Override
	public List<Item> search(Category c, String idName) {
		
		Predicate<Item> condition = a -> true;
		
		if(null != c) {
			condition = condition.and(item -> item.getCategory() == c);
		}
		
		if(null != idName && !idName.isEmpty()) {
			
			Predicate<Item> id = item -> String.valueOf(item.getId())
					.equals(idName);
			
			Predicate<Item> itemName = item -> item.getName()
					.toLowerCase().startsWith(idName.toLowerCase());
			
			condition = condition.and(id.or(itemName));
			
		}
		
		return items.stream().filter(condition).collect(Collectors.toList());
	}

	@Override
	public void add(Item item) {
		if(null == item)
			throw new MiniPosException("Please enter item data");
		
		items.add(item);
	}

	@Override
	public void add(String path) {
		try {
			items = Files.lines(Paths.get(path))
					.skip(1)
					.map(line -> line.split("\t"))
					.map(array -> new Item(array))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
