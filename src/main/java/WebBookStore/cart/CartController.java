package WebBookStore.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	// [수정] 장바구니 담기 (HttpServletRequest, HttpServletResponse 파라미터 추가)
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public String insert(@RequestParam("isbn") int isbn,
						 @RequestParam("amount") int amount,
						 HttpSession session,
						 HttpServletRequest request,
						 HttpServletResponse response) {

		String userid = (String) session.getAttribute("loginUser");

		// 비회원일 경우 쿠키에서 ID를 가져오거나 새로 생성합니다.
		if (userid == null) {
			userid = getOrCreateGuestId(request, response);
		}

		if (amount < 1) {
			amount = 1;
		}

		CartVO cartVO = new CartVO();
		cartVO.setUserid(userid); // 회원ID 또는 GUEST_xxxx가 들어감
		cartVO.setIsbn(isbn);
		cartVO.setAmount(amount);

		cartService.insertOrUpdateCart(cartVO);

		return "redirect:/cart/list";
	}

	// [수정] 장바구니 목록 보기
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(HttpSession session, HttpServletRequest request, Model model) {
		String userid = (String) session.getAttribute("loginUser");

		// 비회원일 경우 쿠키에서 ID를 조회합니다.
		if (userid == null) {
			userid = getGuestId(request);
		}

		List<CartVO> cartList = new ArrayList<>();
		// 회원ID가 있거나 비회원 쿠키 ID가 존재하는 경우에만 조회
		if (userid != null) {
			cartList = cartService.getCartList(userid);
		}
		
		model.addAttribute("cartList", cartList);

		int sumMoney = 0;
		for (CartVO cart : cartList) {
			sumMoney += cart.getTotalPrice();
		}
		model.addAttribute("sumMoney", sumMoney);

		model.addAttribute("contentPage", "/WEB-INF/views/cart/list.jsp");
		return "layout/layout";
	}
	
	// [수정] 수량 변경
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(@RequestParam("isbn") int isbn,
						 @RequestParam("amount") int amount,
						 HttpSession session,
						 HttpServletRequest request) {

		String userid = (String) session.getAttribute("loginUser");

		if (userid == null) {
			userid = getGuestId(request);
		}

		// 유효하지 않은 접근 차단
		if (userid == null) {
			return "redirect:/book/list";
		}

		if (amount < 1) {
			amount = 1;
		}

		CartVO cartVO = new CartVO();
		cartVO.setUserid(userid);
		cartVO.setIsbn(isbn);
		cartVO.setAmount(amount);

		cartService.updateAmount(cartVO);

		return "redirect:/cart/list";
	}
	
	// [수정] 삭제
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delete(@RequestParam("isbn") int isbn, HttpSession session, HttpServletRequest request) {
		String userid = (String) session.getAttribute("loginUser");
		
		if (userid == null) {
			userid = getGuestId(request);
		}

		if (userid != null) {
			cartService.deleteBook(userid, isbn);
		}
		return "redirect:/cart/list";
	}

	// === 비회원용 쿠키 제어 헬퍼 메서드 추가 ===
	
	// 1. 기존 비회원 쿠키가 있는지 단순히 조회만 하는 메서드
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

	private String getOrCreateGuestId(HttpServletRequest request, HttpServletResponse response) {
		String guestId = getGuestId(request);
		
		if (guestId == null) {
			guestId = "GUEST_" + UUID.randomUUID().toString().substring(0, 8);
			Cookie cookie = new Cookie("guestId", guestId);
			cookie.setPath("/");          // 웹사이트 전체에서 쿠키 접근 가능하도록 설정
			cookie.setMaxAge(60 * 60 * 24 * 7); // 쿠키 유효기간: 7일
			response.addCookie(cookie);   // 사용자 브라우저에 쿠키 저장
		}
		return guestId;
	}
}