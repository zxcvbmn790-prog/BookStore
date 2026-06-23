package WebBookStore.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import WebBookStore.order.OrderService;
import WebBookStore.order.OrderVO;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@RequestMapping("/insertform")
	public String insertform(Model model) {
		// 수정 폼과 똑같이 생겼으므로 updateform.jsp를 재활용합니다!
		model.addAttribute("contentPage", "/WEB-INF/views/admin/updateform.jsp");
		return "layout/layout";
	}

	@RequestMapping("/insert")
	public String insert(AdminVO admin) {
		adminService.insertBook(admin);
		return "redirect:/book/list"; // 등록 후 전체 도서 목록으로 이동
	}

	@RequestMapping("/updateform")
	public String updateform(@RequestParam("isbn") int isbn, Model model) {
		model.addAttribute("admin", adminService.getBookById(isbn));
		model.addAttribute("contentPage", "/WEB-INF/views/admin/updateform.jsp");
		return "layout/layout";
	}

	@RequestMapping("/update")
	public String update(AdminVO admin, RedirectAttributes ra) {
		if (adminService.updateBook(admin)) {
			ra.addFlashAttribute("message", "success");
		} else {
			ra.addFlashAttribute("message", "fail");
		}
		return "redirect:/book/view?isbn=" + admin.getIsbn();
	}

	@RequestMapping("/delete")
	public String delete(@RequestParam("isbn") int isbn) { // id 대신 isbn으로 명시
		adminService.deleteBook(isbn);
		return "redirect:/book/list";
	}
	
	@RequestMapping("/sales")
	public String sales(@RequestParam(value = "period", defaultValue = "day") String period, HttpSession session, Model model) {
	    String loginUser = (String) session.getAttribute("loginUser");

	    if (!"admin".equals(loginUser)) {
	        return "redirect:/book/list";
	    }

	    model.addAttribute("summary", adminService.getSalesSummary());
	    model.addAttribute("dailySalesList", adminService.getDailySalesList(period));
	    model.addAttribute("topBookList", adminService.getTopBookSalesList());
	    model.addAttribute("period", period);
	    model.addAttribute("contentPage", "/WEB-INF/views/admin/sales_stats.jsp");

	    return "layout/layout";
	}

	@RequestMapping("/members")
	public String members(HttpSession session, Model model) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (!"admin".equals(loginUser)) {
			return "redirect:/book/list";
		}
		model.addAttribute("memberList", adminService.getMemberList());
		model.addAttribute("contentPage", "/WEB-INF/views/admin/member_manage.jsp");
		return "layout/layout";
	}

	@RequestMapping("/member/delete")
	public String deleteMember(@RequestParam("username") String username, HttpSession session, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (!"admin".equals(loginUser)) {
			return "redirect:/book/list";
		}
		if ("admin".equals(username)) {
			ra.addFlashAttribute("message", "관리자 계정은 삭제할 수 없습니다.");
		} else if (adminService.deleteMember(username)) {
			ra.addFlashAttribute("message", "회원이 삭제되었습니다.");
		} else {
			ra.addFlashAttribute("message", "회원 삭제에 실패했습니다.");
		}
		return "redirect:/admin/members";
	}
	
	
	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/traking", method = RequestMethod.GET)
	public String trackingList(Principal principal, Model model) {
	    
	    // 1. 스프링 시큐리티를 통해 로그인한 유저의 ID(username)를 가져옵니다.
	    if (principal == null) {
	        // 로그인이 안 되어 있다면 로그인 페이지로 이동
	        return "redirect:/member/login"; 
	    }
	    
	    String userid = principal.getName(); // 로그인한 아이디 (예: tracking)

	    // 2. 권한 검증 (아이디가 'tracking'이거나 권한이 맞는지 확인)
	    // 시큐리티 설정(XML)에서 이미 ROLE_TRAKING만 들어오도록 막았기 때문에 이 조건은 생략해도 안전합니다.
	    if (!"tracking".equals(userid) && !"admin".equals(userid)) {
	        return "redirect:/member/login";
	    }

	    // 3. 전체 주문 내역 가져오기
	    List<OrderVO> deliveryList = orderService.getAllOrderList(); 
	    
	    model.addAttribute("deliveryList", deliveryList);
	    model.addAttribute("contentPage", "/WEB-INF/views/admin/traking.jsp");
	    
	    return "layout/layout";
	}
	
	@RequestMapping(value = "/updateTracking", method = RequestMethod.POST)
	public String updateTracking(
	        @RequestParam("orderId") int orderId, 
	        @RequestParam("trakingstatus") String trakingstatus) {
	    
	    try {
	        // 1. 서비스 레이어를 호출하여 DB의 배송 상태를 업데이트합니다.
	        // (파라미터로 받은 주문번호(orderId)와 변경할 상태(trakingstatus)를 전달)
	        orderService.updateTrackingStatus(orderId, trakingstatus);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        // 에러 발생 시 로그를 남기거나 예외 처리를 진행합니다.
	    }
	    
	    // 2. 처리가 완료되면 다시 배송 관리 리스트 페이지로 새로고침(리다이렉트) 합니다.
	    // ※ 현재 사용 중인 배송 관리 목록 매핑 주소가 "/admin/traking"이 맞는지 확인해 주세요!
	    return "redirect:/admin/traking";
	}
	
}