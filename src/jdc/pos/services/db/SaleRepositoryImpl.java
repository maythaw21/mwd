package jdc.pos.services.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jdc.pos.entities.Category;
import jdc.pos.entities.Item;
import jdc.pos.entities.OrderDetail;
import jdc.pos.entities.Voucher;
import jdc.pos.services.SaleRepository;
import jdc.pos.util.ConnectionManager;

public class SaleRepositoryImpl implements SaleRepository {
	
	private static final SaleRepository INSTANCE = new SaleRepositoryImpl();
	private static final String INSERT_VOUCHER = "insert into voucher (sale_date, sale_time) value (?,?)";
	private static final String INSERT_DETAIL = "insert into order_detail "
			+ "(item_id, voucher_id, count) value (?,?,?)";
	private static final String SELECT = "select o.id orderId, v.id voucherId, i.id itemId, "
			+ "v.sale_date, v.sale_time, i.name, i.category, i.price, "
			+ "o.count "
			+ "from order_detail o "
			+ "join voucher v "
			+ "on o.voucher_id = v.id "
			+ "join item i "
			+ "on o.item_id = i.id where 1 = 1";
	
	SaleRepositoryImpl() {}

	public static SaleRepository getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void paid(Voucher voucher) {
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement prepVoucher = con.prepareStatement(INSERT_VOUCHER, 
						Statement.RETURN_GENERATED_KEYS);
				PreparedStatement prepOrder = con.prepareStatement(INSERT_DETAIL)){
			
			prepVoucher.setDate(1, Date.valueOf(LocalDate.now()));
			prepVoucher.setTime(2, Time.valueOf(LocalTime.now()));
			prepVoucher.executeUpdate();
			
			ResultSet rs = prepVoucher.getGeneratedKeys();
			
			while(rs.next()) {
				
				int voucherId = rs.getInt(1);
				
				for(OrderDetail order : voucher.getDetails()) {
					prepOrder.setInt(1, order.getItem().getId());
					prepOrder.setInt(2, voucherId);
					prepOrder.setInt(3, order.getCount());
					prepOrder.executeUpdate();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<OrderDetail> search(Category c, Item item, LocalDate dateFrom, LocalDate dateTo) {
		
		List<OrderDetail> list = new ArrayList<OrderDetail>();
		
		StringBuilder query = new StringBuilder(SELECT);
		List<Object> params = new ArrayList<Object>();
		
		if(null != c) {
			query.append(" and i.category = ?");
			params.add(c.toString());
		}
		
		if(null != item) {
			query.append(" and i.name like ?");
			params.add(item.getName().concat("%"));
		}
		
		if(null != dateFrom) {
			query.append(" and v.sale_date >= ?");
			params.add(Date.valueOf(dateFrom));
		}
		
		if(null != dateTo) {
			query.append(" and v.sale_date <= ?");
			params.add(Date.valueOf(dateTo));
		}
		
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement prep = conn.prepareStatement(query.toString())){
			
			for(int i = 0; i < params.size(); i++) {
				prep.setObject(i+1, params.get(i));
			}
			
			ResultSet rs = prep.executeQuery();
			
			while(rs.next()) {
				
				Item i = new Item();
				
				i.setId(rs.getInt("itemId"));
				i.setName(rs.getString("name"));
				i.setCategory(Category.valueOf(rs.getString("category")));
				i.setPrice(rs.getInt("price"));
				
				Voucher v = new Voucher();
				
				v.setId(rs.getInt("voucherId"));
				v.setSaleDate(rs.getDate("sale_date").toLocalDate());
				v.setSaleTime(rs.getTime("sale_time").toLocalTime());
				
				OrderDetail order = new OrderDetail();
				order.setId(rs.getInt("orderId"));
				order.setCount(rs.getInt("count"));
				
				order.setItem(i);
				order.setVoucher(v);
				order.calculatePrice();
				
				list.add(order);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
