package pers.mmc.bookmarket.controller.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pers.mmc.bookmarket.entity.Book;
import pers.mmc.bookmarket.entity.GoodsItem;
import pers.mmc.bookmarket.entity.OrderItem;
import pers.mmc.bookmarket.entity.ShoppingCart;
import pers.mmc.bookmarket.service.BookService;
import pers.mmc.bookmarket.service.OrderService;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = { "/addToCart.do",
		"/viewProductDetails.do", "/deleteItem.do", "/showShoppingCart.do",
		"/clearShoppingCart.do" })
public class ShoppingCartServlet extends HttpServlet {

	private static final long serialVersionUID = 5504970214885302771L;
	BookService bookService = new BookService();
	OrderService cartService = new OrderService();

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		if ("/viewProductDetails.do".equals(path)) {
			showProductDetails(request, response);
		} else if ("/deleteItem.do".equals(path)) {
			deleteItem(request, response);
		} else if ("/showShoppingCart.do".equals(path)) {
			showBooks(request, response);
		} else if ("/clearShoppingCart.do".equals(path)) {
			clearShoppingCart(request, response);
		}
	}

	private void clearShoppingCart(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		boolean result=true;
		HttpSession session = request.getSession();
		try{
			ShoppingCart cart=(ShoppingCart) session.getAttribute("cart");
			List<OrderItem> orderItems = cartService.queryItemsByUserId((int) session.getAttribute("userId"));
			List<Integer> idList=new ArrayList<>();
			for (OrderItem order: orderItems) {
				idList.add(order.getId());
			}
			cartService.upDateOrderPayState(idList);
			cart.clear();
		}catch(Exception e){
			result=false;
		}
		response.getWriter().write(result?"????????????":"????????????");
	}

	private void showBooks(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Integer id = (Integer) session.getAttribute("userId");
		List<OrderItem> items = cartService.queryItemsByUserId(id);
		ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
		if (cart == null) {
			cart = new ShoppingCart();
			session.setAttribute("cart", cart);
		}
		for (OrderItem cartItem : items) {
			cart.add(new GoodsItem(bookService.queryBookById(cartItem
					.getBookId()), cartItem.getQuantity()));
		}
		response.sendRedirect("client/jsp/shoppingCart.jsp");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ??????????????????????????????
		int bookId = 0;
		int quantity = 0;
		String message = "????????????";
		try {
			bookId = Integer.parseInt(request.getParameter("id"));
			quantity = Integer.parseInt(request.getParameter("quantity"));
		} catch (NumberFormatException e) {
			System.out.println(e);
		}
		Book product = bookService.queryBookById(bookId);
		if (product != null) {
			HttpSession session = request.getSession();
			ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
			GoodsItem goodsItem = new GoodsItem(product, quantity);
			if (cart == null) {
				cart = new ShoppingCart();
				session.setAttribute("cart", cart);
			}
			if (cart.add(goodsItem)) {
				message = "????????????";
			} else {
				message = "????????????";
			}
		}
		response.getWriter().write(message);
	}

	// ??????????????????????????????????????????
	private void showProductDetails(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		int bookId = 0;
		try {
			bookId = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException e) {
			System.out.println(bookId);
			System.out.println(e);
		}
		// ?????????????????????????????????
		Book product = bookService.queryBookById(bookId);
		if (product != null) {
			HttpSession session = request.getSession();
			session.setAttribute("product", product);
			response.sendRedirect("showProduct.jsp");
		} else {
			// out.println("No product found");
		}
	}

	// ?????????????????????????????????
	private void deleteItem(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
		try {
			int id = Integer.parseInt(request.getParameter("id"));
			GoodsItem item = null;
			for (GoodsItem shopItem : cart.getItems()) {
				if (shopItem.getBook().getId() == id) {
					item = shopItem;
					break;
				}
			}
			cart.remove(item.getBook().getId());
		} catch (NumberFormatException e) {
			System.out.println("???????????????" + e.getMessage());
		}
		response.sendRedirect("client/jsp/shoppingCart.jsp");
	}

}
