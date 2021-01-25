package jdc.pos.services;

import java.time.LocalDate;
import java.util.List;

import jdc.pos.entities.Category;
import jdc.pos.entities.Item;
import jdc.pos.entities.OrderDetail;
import jdc.pos.entities.Voucher;
import jdc.pos.services.db.SaleRepositoryImpl;

public interface SaleRepository {

	public static SaleRepository getInstance() {
		return SaleRepositoryImpl.getInstance();
	}
	
	void paid(Voucher voucher);
	
	List<OrderDetail> search(Category c, Item item, 
			LocalDate dateFrom, LocalDate dateTo);
	
}
