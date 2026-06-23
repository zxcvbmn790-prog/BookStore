package WebBookStore.order;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import WebBookStore.cart.CartService;
import WebBookStore.cart.CartVO;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/checkout", method = RequestMethod.GET)
	public String checkout(HttpSession session, Model model) {
		String userid = (String) session.getAttribute("loginUser");

		if (userid == null) {
			return "redirect:/member/login";
		}

		List<CartVO> cartList = cartService.getCartList(userid);

		if (cartList == null || cartList.isEmpty()) {
			return "redirect:/cart/list";
		}

		int sumMoney = 0;
		for (CartVO cart : cartList) {
			sumMoney += cart.getTotalPrice();
		}

		model.addAttribute("cartList", cartList);
		model.addAttribute("sumMoney", sumMoney);
		model.addAttribute("contentPage", "/WEB-INF/views/order/checkout.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "/pay", method = RequestMethod.POST)
	public String pay(String receiver, String phone, String address,
			HttpSession session) {

		String userid = (String) session.getAttribute("loginUser");

		if (userid == null) {
			return "redirect:/member/login";
		}

		List<CartVO> cartList = cartService.getCartList(userid);

		if (cartList == null || cartList.isEmpty()) {
			return "redirect:/cart/list";
		}

		if (receiver == null || receiver.trim().isEmpty()
				|| phone == null || phone.trim().isEmpty()
				|| address == null || address.trim().isEmpty()) {
			return "redirect:/order/checkout?error=true";
		}

		int result = orderService.placeOrder(userid, cartList, receiver, phone, address);

		if (result > 0) {
			return "redirect:/order/complete";
		}

		return "redirect:/order/checkout?error=true";
	}

	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public String complete(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/order/complete.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(HttpSession session, Model model) {
		String userid = (String) session.getAttribute("loginUser");

		if (userid == null) {
			return "redirect:/member/login";
		}

		List<OrderVO> orderList = orderService.getOrderList(userid);
		model.addAttribute("orderList", orderList);
		model.addAttribute("contentPage", "/WEB-INF/views/order/list.jsp");
		return "layout/layout";
	}
	
	
}