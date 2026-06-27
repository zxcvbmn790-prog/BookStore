package WebBookStore.member;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberService;

	// feature_kakaoLogin: 카카오 로그인 서비스 주입
	@Autowired
	private KakaoLoginService kakaoLoginService;

	// dev: Spring Security 인증을 위한 매니저 주입
	@Autowired
	private AuthenticationManager authenticationManager;

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/login.jsp");
		return "layout/layout";
	}

	// dev 브랜치 채택: Spring Security가 적용된 REST API 방식의 로그인 처리
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody MemberVO member, HttpServletRequest request) {
		try {
			// 1. 시큐리티 인증 시도
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							member.getUsername(),
							member.getPassword()
					)
			);

			// 2. 인증 객체를 SecurityContextHolder에 세팅
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			// 3. 스프링 시큐리티가 이 인증 정보를 세션에 유지하도록 수동으로 설정
			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			
			// 4. [기존 JSP 호환용] 기존 세션 방식 유지를 위해 속성 추가
			session.setAttribute("loginUser", authentication.getName());

			Map<String, String> response = new HashMap<>();
			response.put("result", "success");
			response.put("message", "로그인 성공");
			response.put("username", authentication.getName());

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("result", "fail");
			response.put("message", "로그인 실패: " + e.getMessage());

			return ResponseEntity.badRequest().body(response);
		}
	}

	// feature_kakaoLogin 브랜치 채택: 카카오 로그인 시작점
	@RequestMapping(value = "kakaoStart", method = RequestMethod.GET)
	public String kakaoStart() {
		return "redirect:" + kakaoLoginService.getAuthorizeUrl();
	}

	// feature_kakaoLogin 브랜치 채택: 카카오 로그인 콜백
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
	
	// dev 브랜치 채택: 전통적인 Spring MVC 방식의 회원가입 처리
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String register(MemberVO member, RedirectAttributes ra) {
		try {
			// MemberService의 회원가입 로직 호출 (비밀번호 암호화 및 DB 저장)
			memberService.registerMember(member);

			ra.addFlashAttribute("authMessage", "회원가입이 완료되었습니다. 로그인 후 이용해주세요.");
			return "redirect:/member/login";

		} catch (Exception e) {
			ra.addFlashAttribute("authError", "회원가입 실패: " + e.getMessage());
			return "redirect:/member/register";
		}
	}

	// dev 브랜치 채택: 프로필 
	@RequestMapping(value = "profile", method = RequestMethod.GET)
	public String profile(HttpSession session, Model model, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			ra.addFlashAttribute("authError", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		
		// 충돌로 인해 반환 구문이 유실되었을 가능성이 있어 기본 레이아웃 형태를 추가해 두었습니다.
		model.addAttribute("contentPage", "/WEB-INF/views/member/profile.jsp");
		return "layout/layout";
	}

	// dev 브랜치 채택: 회원 탈퇴
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String deleteMember(String password, HttpSession session, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
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
	
	// dev 브랜치 채택: 아이디 중복 확인 API
	@RequestMapping(value = "checkId", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkId(String username) {
		try {
			// ⚠️ 주의: 이전 DAO 충돌에서 언급했던 'findByUsernames'와 연관된 것으로 보입니다.
			// 서비스단 메서드명이 정확히 'getMemberByUsernames'인지 확인해 주세요.
			MemberVO existingUser = memberService.getMemberByUsernames(username);
			
			System.out.println("중복 체크 대상 아이디: " + username);
			System.out.println("조회된 유저 결과 객체: " + existingUser);

			return existingUser == null;
			
		} catch (Exception e) {
			System.out.println("조회 중 예외 발생(유저 없음 기인): " + e.getMessage());
			return true; 
		}
	}
}