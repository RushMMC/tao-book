package pers.mmc.bookmarket.entity;

import java.util.*;

public class ShoppingCart {
	// 这里Map的键是商品号
	HashMap<Integer, GoodsItem> items = null;

	public ShoppingCart() { // 购物车的构造方法
		items = new HashMap<Integer, GoodsItem>();
	}

	// 向购物车中添加商品方法
	public boolean add(GoodsItem goodsItem) {
		// 返回添加的商品号
		Book book = goodsItem.getBook();
		int productid = book.getId();
		// 如果购物车中包含指定的商品，返回该商品并增加数量
		if (items.containsKey(productid)) {
			GoodsItem scitem = (GoodsItem) items.get(productid);
			int quantity=scitem.getQuantity() + goodsItem.getQuantity();
			if(book.getQuantity()>=quantity){
				// 修改该商品的数量
				scitem.setQuantity(quantity);
			}else{
				return false;
			}
		} else {
			// 否则将该商品添加到购物车中
			items.put(productid, goodsItem);
		}
		return true;
	}

	// 从购物车中删除一件商品
	public void remove(Integer productid) {
		if (items.containsKey(productid)) {
			GoodsItem scitem = (GoodsItem) items.get(productid);
			scitem.setQuantity(scitem.getQuantity() - 1);
			if (scitem.getQuantity() <= 0)
				items.remove(productid);
		}
	}

	// 返回购物车中GoodsItem的集合
	public Collection<GoodsItem> getItems() {
		return items.values();
	}
	
	public GoodsItem getItemById(int id){
		return items.get(id);
	}

	// 计算购物车所有商品价格
	public double getTotal() {
		double amount = 0.0;
		for (Iterator<GoodsItem> i = getItems().iterator(); i.hasNext();) {
			GoodsItem item = (GoodsItem) i.next();
			Book book = (Book) item.getBook();
			amount += item.getQuantity() * book.getPrice();
		}
		return roundOff(amount);
	}

	// 对数值进行四舍五入并保留两位小数
	private double roundOff(double x) {
		long val = Math.round(x * 100); // cents
		return val / 100.0;
	}

	// 清空购物车方法
	public void clear() {
		items.clear();
	}
}
