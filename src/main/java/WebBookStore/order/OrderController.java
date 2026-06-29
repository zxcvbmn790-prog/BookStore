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
import org.springframework.web.bind.annotation.RequestParam;

import WebBookStore.cart.CartService;
import WebBookStore.cart.CartVO;

import WebBookStore.member.MemberService;
import WebBookStore.member.MemberVO;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private MemberService memberService;

	@RequestMapping(value = "/checkout2", method = RequestMethod.GET)
	public String checkout(HttpSession session, HttpServletRequest request, Model model) {
	    // 1. 로그인한 회원 아이디를 가져온다.
	    String loginUser = (String) session.getAttribute("loginUser");

	    // 2. 장바구니 조회에 사용할 userid를 만든다.
	    // 회원이면 loginUser를 사용하고, 비회원이면 쿠키의 guestId를 사용한다.
	    String userid = loginUser;

	    // 3. 현재 사용자가 회원인지 확인한다.
	    boolean isMember = loginUser != null;

	    // 4. 로그인 회원이 아니면 비회원 쿠키 id를 가져온다.
	    if (userid == null) {
	        userid = getGuestId(request);
	    }

	    // 5. 회원도 아니고 비회원 쿠키도 없으면 장바구니로 돌려보낸다.
	    if (userid == null) {
	        return "redirect:/cart/list";
	    }

	    // 6. 현재 사용자의 장바구니 목록을 가져온다.
	    List<CartVO> cartList = cartService.getCartList(userid);

	    // 7. 장바구니가 비어 있으면 주문 페이지로 갈 수 없다.
	    if (cartList == null || cartList.isEmpty()) {
	        return "redirect:/cart/list";
	    }

	    // 8. 장바구니 총 금액 계산
	    int sumMoney = 0;
	    for (CartVO cart : cartList) {
	        sumMoney += cart.getTotalPrice();
	    }

	    // 9. 회원이면 회원 정보를 조회한다.
	    // 비회원이면 member는 null이다.
	    MemberVO member = null;
	    int mileageRate = 0;

	    if (isMember) {
	        member = memberService.getMember(loginUser);

	        if (member != null) {
	            mileageRate = memberService.getMileageRate(member.getGrade());
	        }
	    }

	    // 10. checkout.jsp에서 사용할 데이터 전달
	    model.addAttribute("cartList", cartList);
	    model.addAttribute("sumMoney", sumMoney);
	    model.addAttribute("member", member);
	    model.addAttribute("isMember", isMember);
	    model.addAttribute("mileageRate", mileageRate);

	    model.addAttribute("contentPage", "/WEB-INF/views/order/checkout.jsp");
	    return "layout/layout";
	}

	@RequestMapping(value = "/pay2", method = RequestMethod.POST)
	public String pay(String receiver, String phone, String address, Integer useMileage,
	        HttpSession session, HttpServletRequest request, HttpServletResponse response) {

	    // 1. 로그인 회원 아이디 확인
	    String loginUser = (String) session.getAttribute("loginUser");

	    // 2. 주문에 사용할 userid
	    String userid = loginUser;

	    // 3. 비회원 여부
	    boolean isGuest = false;

	    // 4. 로그인하지 않은 경우 비회원 쿠키 id 사용
	    if (userid == null) {
	        userid = getGuestId(request);
	        isGuest = true;
	    }

	    // 5. 회원도 아니고 비회원 쿠키도 없으면 장바구니로 이동
	    if (userid == null) {
	        return "redirect:/cart/list";
	    }

	    // 6. 장바구니 목록 조회
	    List<CartVO> cartList = cartService.getCartList(userid);

	    // 7. 장바구니가 비어 있으면 주문 불가
	    if (cartList == null || cartList.isEmpty()) {
	        return "redirect:/cart/list";
	    }

	    // 8. 배송정보 검증
	    if (receiver == null || receiver.trim().isEmpty()
	            || phone == null || phone.trim().isEmpty()
	            || address == null || address.trim().isEmpty()) {
	        return "redirect:/order/checkout?error=true";
	    }

	    // 9. 마일리지 값 보정
	    // 비회원은 마일리지를 사용할 수 없으므로 0 처리
	    if (useMileage == null || isGuest) {
	        useMileage = 0;
	    }

	    // 10. 주문 처리
	    int result = orderService.placeOrder(userid, cartList, receiver, phone, address, useMileage);

	    // 11. 주문 성공
	    if (result > 0) {
	        return "redirect:/order/complete";
	    }

	    // 12. 주문 실패
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
	
	@RequestMapping(value = "/trackingDetail", method = RequestMethod.GET)
	public String trackingDetail(@RequestParam("orderId") int orderId, Model model) {
	    
	    // 특정 주문번호 1건에 대한 정보를 가져오는 서비스 메서드가 필요합니다.
	    // 기존에 만약 1건 조회가 없다면 OrderDAO에 단건조회 로직을 선언해주셔야 합니다.
	    OrderVO order = orderService.getOrderDetail(orderId); 
	    
	    model.addAttribute("order", order);
	    
	    // 레이아웃 전체 템플릿이 아닌 팝업창 전용 단독 페이지로 열기 위해 직접 리턴합니다.
	    return "order/trackingDetail"; 
	}
}