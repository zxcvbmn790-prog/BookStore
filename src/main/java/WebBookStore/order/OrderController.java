package WebBookStore.order;

import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	public String checkout(HttpSession session, HttpServletRequest request, Model model) {
		String userid = (String) session.getAttribute("loginUser");

		if (userid == null) {
			userid = getGuestId(request);
		}

		if (userid == null) {
			return "redirect:/cart/list";
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
			HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String userid = (String) session.getAttribute("loginUser");
		boolean isGuest = false;

		if (userid == null) {
			userid = getGuestId(request);
			isGuest = true; 
		}

		if (userid == null) {
			return "redirect:/cart/list";
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
			// [선택 사항] 비회원 주문 성공 시, 브라우저의 비회원 쿠키를 삭제하고 싶다면 아래 주석을 해제하세요.
			/*
			if (isGuest) {
				Cookie cookie = new Cookie("guestId", null);
				cookie.setPath("/");
				cookie.setMaxAge(0); // 유효기간 0으로 만들어 즉시 삭제
				response.addCookie(cookie);
			}
			*/
			return "redirect:/order/complete";
		}

		return "redirect:/order/checkout?error=true";
	}

	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public String complete(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/order/complete.jsp");
		return "layout/layout";
	}

	// [수정] 주문 내역 목록 (HttpServletRequest 추가)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(HttpSession session, HttpServletRequest request, Model model) {
		String userid = (String) session.getAttribute("loginUser");

		if (userid == null) {
			userid = getGuestId(request);
		}

		if (userid == null) {
			return "redirect:/member/login";
		}

		List<OrderVO> orderList = orderService.getOrderList(userid);
		model.addAttribute("orderList", orderList);
		model.addAttribute("contentPage", "/WEB-INF/views/order/list.jsp");
		return "layout/layout";
	}
	
	private String getGuestId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("guestId".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}