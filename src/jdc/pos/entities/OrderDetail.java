package jdc.pos.entities;

import java.time.LocalDate;
import java.time.LocalTime;

// DTO (Data Transfer Object)
// Entity
public class OrderDetail {

	private int id;
	private int count;
	private int subTotal;
	private int total;
	private int tax;

	private Item item;
	private Voucher voucher;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

	public int getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(int subTotal) {
		this.subTotal = subTotal;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTax() {
		return tax;
	}

	public void setTax(int tax) {
		this.tax = tax;
	}
	
	// TODO: custom methods
	public int getUnitPrice() {
		return null == item ? 0 : item.getPrice();
	}
	
	// category
	public Category getCategory() {
		return item.getCategory();
	}
	
	// saleDate
	public LocalDate getSaleDate() {
		return voucher.getSaleDate();
	}
	
	public LocalTime getSaleTime() {
		return voucher.getSaleTime();
	}

	public void calculatePrice() {
		subTotal = getUnitPrice() * count;
		Double value = subTotal * 0.05;
		tax = value.intValue();
		total = tax + subTotal;
	}
}
















