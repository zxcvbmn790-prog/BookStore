package WebBookStore.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import WebBookStore.member.MemberService;
import WebBookStore.order.OrderService;
import WebBookStore.order.OrderVO;

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpSession;

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
	public String updateform(@RequestParam("isbn") long isbn, Model model) {
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
	public String delete(@RequestParam("isbn") long isbn) {
		adminService.deleteBook(isbn);
		return "redirect:/book/list";
	}
	
	@RequestMapping("/sales")
	public String sales(@RequestParam(value = "period", defaultValue = "day") String period, Model model) {
	    // Spring Security를 이용한 권한 체크
	    org.springframework.security.core.Authentication auth = 
	            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
	    
	    boolean isAdmin = auth != null && auth.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

	    if (!isAdmin) {
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
	public String members(Model model) {
	    org.springframework.security.core.Authentication auth = 
	            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
	    
	    boolean isAdmin = auth != null && auth.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			return "redirect:/book/list";
		}
		model.addAttribute("memberList", adminService.getMemberList());
		model.addAttribute("contentPage", "/WEB-INF/views/admin/member_manage.jsp");
		return "layout/layout";
	}

	@RequestMapping("/member/delete")
	public String deleteMember(@RequestParam("username") String username, RedirectAttributes ra) {
	    org.springframework.security.core.Authentication auth =
	            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth != null && auth.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
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

	@RequestMapping(value = "/books", method = RequestMethod.GET)
	public String bookManage(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
	    org.springframework.security.core.Authentication auth =
	            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
	    boolean isAdmin = auth != null && auth.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	    if (!isAdmin) {
	        return "redirect:/book/list";
	    }

	    List<AdminVO> bookList = adminService.searchBooks(keyword);
	    model.addAttribute("bookList", bookList);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("contentPage", "/WEB-INF/views/admin/book_manage.jsp");
	    return "layout/layout";
	}

	@RequestMapping(value = "/updateDiscount", method = RequestMethod.POST)
	@org.springframework.web.bind.annotation.ResponseBody
	public java.util.Map<String, Object> updateDiscount(
	        @RequestParam("isbn") long isbn,
	        @RequestParam("discountRate") int discountRate) {
	    java.util.Map<String, Object> result = new java.util.HashMap<>();
	    if (discountRate < 0) discountRate = 0;
	    if (discountRate > 99) discountRate = 99;
	    boolean success = adminService.updateDiscountRate(isbn, discountRate);
	    result.put("success", success);
	    return result;
	}

	@RequestMapping(value = "/updateAd", method = RequestMethod.POST)
	@org.springframework.web.bind.annotation.ResponseBody
	public java.util.Map<String, Object> updateAd(
	        @RequestParam("isbn") long isbn,
	        @RequestParam("isAd") boolean isAd) {
	    java.util.Map<String, Object> result = new java.util.HashMap<>();
	    boolean success = adminService.updateAdStatus(isbn, isAd);
	    result.put("success", success);
	    return result;
	}

	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/traking", method = RequestMethod.GET)
	public String trackingList(Principal principal, Model model) {
		if (principal == null) {
			return "redirect:/member/login";
		}

		String userid = principal.getName();

		if (!"ROLE_TRAKING".equals(userid) && !"ROLE_ADMIN".equals(userid)) {
			//return "redirect:/member/login";
		}

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
			orderService.updateTrackingStatus(orderId, trakingstatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/traking";
	}
	
	
	// 고객관리 변경
	@Autowired
	private MemberService memberService;
	
	@RequestMapping(value = "/member/updateRole", method = RequestMethod.GET)
    public String updateRole(
            @RequestParam("username") String username,
            @RequestParam("role") String role,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 1. 비즈니스 로직 수행 (DB의 회원 권한 업데이트)
            // memberService.updateMemberRole(username, role);
            
            // 2. 성공 메시지 설정 (JSP의 ${message} 부분에 표시됨)
            redirectAttributes.addFlashAttribute("message", username + " 회원의 권한이 성공적으로 변경되었습니다.");
            memberService.upRole(username, role);
            
        } catch (Exception e) {
            // 에러 발생 시 처리
            redirectAttributes.addFlashAttribute("message", "권한 변경 중 오류가 발생했습니다.");
        }
        
        // 3. 권한 변경 후 지정하신 admin/members 페이지로 리다이렉트(갱신)
        return "redirect:/admin/members";
    }
}