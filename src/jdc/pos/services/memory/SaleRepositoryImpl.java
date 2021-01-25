package jdc.pos.services.memory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jdc.pos.entities.Category;
import jdc.pos.entities.Item;
import jdc.pos.entities.OrderDetail;
import jdc.pos.entities.Voucher;
import jdc.pos.services.SaleRepository;
import jdc.pos.util.MiniPosException;

public class SaleRepositoryImpl implements SaleRepository {

	private static SaleRepository INSTANCE = new SaleRepositoryImpl();
	
	private List<Voucher> vouchers;
	
	protected SaleRepositoryImpl() {
		if(vouchers == null)
			vouchers = new ArrayList<Voucher>();
	}
	
	public static SaleRepository getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void paid(Voucher voucher) {
		if(null == voucher)
			throw new MiniPosException("Voucher can not be empty !!!");
		
		voucher.setSaleDate(LocalDate.now());
		voucher.setSaleTime(LocalTime.now());
		voucher.setId(voucher.getSaleTime().toSecondOfDay());
		
		voucher.getDetails().forEach(order -> order.setVoucher(voucher));
		vouchers.add(voucher);
	}
	
	@Override
	public List<OrderDetail> search(Category c, Item item, 
			LocalDate dateFrom, LocalDate dateTo) {
		
		Predicate<OrderDetail> condition = a -> true;
		
		if(null != c) {
			condition = condition.and(order -> order.getItem().getCategory() == c);
		}
		
		if(null != item) {
			condition = condition.and(order -> order.getItem().equals(item));
		}
		
		if(null != dateFrom) {
			condition = condition.and(order -> order.getVoucher()
					.getSaleDate().compareTo(dateFrom) >= 0);
		}
		
		if(null != dateTo) {
			condition = condition.and(order -> order.getVoucher()
					.getSaleDate().compareTo(dateTo) <= 0);
		}
		
		return vouchers.stream()
				.flatMap(voucher -> voucher.getDetails().stream())
				.filter(condition)
				.collect(Collectors.toList());
	}

}













