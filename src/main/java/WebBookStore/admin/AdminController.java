package WebBookStore.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
}