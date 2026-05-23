package WebBookStore.member;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberService;

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/login.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(String username, String password, HttpSession session) {
		MemberVO loginUser = memberService.getLoginUser(username, password);
		if (loginUser != null) {
			session.setAttribute("loginUser", loginUser.getUsername());
			return "redirect:/book/list";
		}
		return "redirect:/member/login?error=true";
	}

	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/register.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String register(MemberVO member, RedirectAttributes ra) {
		if (memberService.registerMember(member)) {
			ra.addFlashAttribute("authMessage", "회원가입이 완료되었습니다. 이제 로그인하시면 바로 이용할 수 있어요.");
			return "redirect:/member/login";
		}
		ra.addFlashAttribute("authError", "회원가입에 실패했습니다. 입력값을 다시 확인해주세요.");
		return "redirect:/member/register";
	}

	@RequestMapping(value = "profile", method = RequestMethod.GET)
	public String profile(HttpSession session, Model model, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			ra.addFlashAttribute("authError", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		model.addAttribute("member", memberService.getMember(loginUser));
		model.addAttribute("contentPage", "/WEB-INF/views/member/profile.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "profile/update", method = RequestMethod.POST)
	public String updateProfile(MemberVO member, HttpSession session, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
		}
		member.setUsername(loginUser);
		if (memberService.updateProfile(member)) {
			ra.addFlashAttribute("profileMessage", "회원 정보가 수정되었습니다.");
		} else {
			ra.addFlashAttribute("profileError", "회원 정보 수정에 실패했습니다.");
		}
		return "redirect:/member/profile";
	}

	@RequestMapping(value = "password", method = RequestMethod.GET)
	public String passwordPage(HttpSession session, Model model) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
		}
		model.addAttribute("contentPage", "/WEB-INF/views/member/password.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "password/update", method = RequestMethod.POST)
	public String updatePassword(String currentPassword, String newPassword, String confirmPassword, HttpSession session,
			RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
		}
		if (newPassword == null || !newPassword.equals(confirmPassword)) {
			ra.addFlashAttribute("passwordError", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
			return "redirect:/member/password";
		}
		if (memberService.updatePassword(loginUser, currentPassword, newPassword)) {
			ra.addFlashAttribute("passwordMessage", "비밀번호가 변경되었습니다.");
			return "redirect:/member/profile";
		}
		ra.addFlashAttribute("passwordError", "현재 비밀번호를 다시 확인해주세요.");
		return "redirect:/member/password";
	}

	@RequestMapping(value = "delete", method = RequestMethod.GET)
	public String deletePage(HttpSession session, Model model) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
		}
		model.addAttribute("contentPage", "/WEB-INF/views/member/delete.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String deleteMember(String password, HttpSession session, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
		}
		MemberVO member = memberService.getLoginUser(loginUser, password);
		if (member == null) {
			ra.addFlashAttribute("deleteError", "비밀번호가 올바르지 않습니다.");
			return "redirect:/member/delete";
		}
		memberService.deleteMember(loginUser);
		session.invalidate();
		ra.addFlashAttribute("authMessage", "회원 탈퇴가 완료되었습니다.");
		return "redirect:/member/login";
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/member/login";
	}
}
