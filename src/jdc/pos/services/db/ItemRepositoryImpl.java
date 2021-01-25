package jdc.pos.services.db;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdc.pos.entities.Category;
import jdc.pos.entities.Item;
import jdc.pos.services.ItemRepository;
import jdc.pos.util.ConnectionManager;

public class ItemRepositoryImpl implements ItemRepository {

	private static final ItemRepository INSTANCE = new ItemRepositoryImpl();
	private static final String SELECT = "select id, name, category, price from item where 1 = 1";
	private static final String INSERT = "insert into item values (?,?,?,?)";

	ItemRepositoryImpl() {
	}

	public static ItemRepository getInstance() {
		return INSTANCE;
	}

	@Override
	public List<Item> search(Category c, String idName) {

		StringBuilder query = new StringBuilder(SELECT);

		List<Object> params = new ArrayList<Object>();
		List<Item> items = new ArrayList<Item>();

		if (null != c) {
			query.append(" and category = ?");
			params.add(c.toString());
		}

		if (null != idName && !idName.isEmpty()) {

			if (checkDigit(idName)) {
				query.append(" and id = ?");
				params.add(idName);
			} else {
				query.append(" and name like ?");
				params.add(idName.concat("%"));
			}

		}

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement prep = con.prepareStatement(query.toString())) {

			// set parameters
			for (int i = 0; i < params.size(); i++) {
				prep.setObject(i + 1, params.get(i));
			}

			ResultSet rs = prep.executeQuery();

			while (rs.next()) {
				items.add(getItem(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	private Item getItem(ResultSet rs) throws SQLException {
		Item item = new Item();
		item.setId(rs.getInt("id"));
		item.setName(rs.getString("name"));
		item.setCategory(Category.valueOf(rs.getString("category")));
		item.setPrice(rs.getInt("price"));
		return item;
	}

	private boolean checkDigit(String idName) {
		try {
			Integer.parseInt(idName);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public void add(Item item) {

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement prep = con.prepareStatement(INSERT)){
			
			prep.setInt(1, item.getId());
			prep.setString(2, item.getName());
			prep.setString(3, item.getCategory().toString());
			prep.setInt(4, item.getPrice());
			prep.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add(String path) {
		try {
			Files.lines(Paths.get(path)).skip(1)
				.map(line -> line.split("\t"))
				.map(Item::new)
				.forEach(item -> add(item));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
