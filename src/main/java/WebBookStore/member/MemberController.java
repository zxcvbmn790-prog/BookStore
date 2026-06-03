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

	@Autowired
	private KakaoLoginService kakaoLoginService;

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
			session.setAttribute("loginNickname", loginUser.getNickname());
			return "redirect:/book/list";
		}
		return "redirect:/member/login?error=true";
	}

	@RequestMapping(value = "kakaoStart", method = RequestMethod.GET)
	public String kakaoStart() {
		return "redirect:" + kakaoLoginService.getAuthorizeUrl();
	}

	@RequestMapping(value = "kakaoLogin", method = RequestMethod.GET)
	public String kakaoLogin(String code, String error, HttpSession session, RedirectAttributes ra) {
		if (error != null) {
			ra.addFlashAttribute("authError", "카카오 로그인이 취소되었거나 실패했습니다.");
			return "redirect:/member/login";
		}

		if (code == null || code.trim().isEmpty()) {
			ra.addFlashAttribute("authError", "카카오 인증 코드가 없습니다.");
			return "redirect:/member/login";
		}

		try {
			String accessToken = kakaoLoginService.getAccessToken(code);
			KakaoUserInfo kakaoUserInfo = kakaoLoginService.getUserInfo(accessToken);
			MemberVO member = memberService.getOrRegisterKakaoMember(kakaoUserInfo);

			session.setAttribute("loginUser", member.getUsername());
			session.setAttribute("loginNickname", member.getNickname());
			session.setAttribute("loginType", "KAKAO");

			return "redirect:/book/list";
		} catch (Exception e) {
			e.printStackTrace();
			ra.addFlashAttribute("authError", "카카오 로그인 처리 중 오류가 발생했습니다.");
			return "redirect:/member/login";
		}
	}


	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/register.jsp");
		return "layout/layout";
	}


	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/member/login";
	}
}