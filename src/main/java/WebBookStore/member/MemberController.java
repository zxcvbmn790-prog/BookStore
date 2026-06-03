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

	// Spring Security 인증을 위한 매니저 주입
	@Autowired
	private AuthenticationManager authenticationManager;

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/login.jsp");
		return "layout/layout";
	}
	
	/*
	/**
	 * REST API 로그인 처리 (POST)
	 * 기존 세션 리다이렉트 방식에서 스프링 시큐리티 인증 및 JSON 반환 방식으로 전환합니다.
	 
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody MemberVO member) {
		try {
			// 1. 입력받은 아이디와 비밀번호로 시큐리티 인증 토큰 생성 및 인증 시도
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							member.getUsername(),
							member.getPassword()
					)
			);

			// 2. 인증 성공 시 결과 및 인증된 사용자명을 JSON(Map)으로 반환
			return ResponseEntity.ok(
					Map.of(
							"result", "success",
							"message", "로그인 성공",
							"username", authentication.getName()
					)
			);

		} catch (Exception e) {
			// 3. 인증 실패 시(비밀번호 불일치, 자격 증명 실패 등) 에러 메시지와 함께 400 Bad Request 반환
			return ResponseEntity.badRequest().body(
					Map.of(
							"result", "fail",
							"message", "로그인 실패: " + e.getMessage()
					)
			);
		}
	}
	*/
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody MemberVO member, HttpServletRequest request) { // ◀ HttpServletRequest 추가
		try {
			// 1. 시큐리티 인증 시도
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							member.getUsername(),
							member.getPassword()
					)
			);

			// 2. [핵심 추가] 인증 객체를 SecurityContextHolder에 세팅
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			// 3. [핵심 추가] 스프링 시큐리티가 이 인증 정보를 세션에 유지하도록 수동으로 넣어줍니다.
			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			
			// 4. [기존 JSP 호환용] 기존 JSP에서 ${sessionScope.loginUser} 혹은 ${loginUser}를 쓰고 있다면 같이 세팅해 줍니다.
			session.setAttribute("loginUser", authentication.getName());

			return ResponseEntity.ok(
					Map.of(
							"result", "success",
							"message", "로그인 성공",
							"username", authentication.getName()
					)
			);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					Map.of(
							"result", "fail",
							"message", "로그인 실패: " + e.getMessage()
					)
			);
		}
	}
	
	
	
	
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/register.jsp");
		return "layout/layout";
	}
	
	// REST API 회원가입 처리
	// 전통적인 Spring MVC 방식의 회원가입 처리
		@RequestMapping(value = "register", method = RequestMethod.POST)
		public String register(MemberVO member, RedirectAttributes ra) {
			try {
				// MemberService의 회원가입 로직 호출 (비밀번호 암호화 및 DB 저장)
				memberService.registerMember(member);

				// 리다이렉트 후 화면에서 딱 한 번만 사용할 수 있는 일회성 메시지 전달
				ra.addFlashAttribute("authMessage", "회원가입이 완료되었습니다. 로그인 후 이용해주세요.");
				
				// 회원가입 성공 시 로그인 페이지로 강제 이동 (URL 변경)
				return "redirect:/member/login";

			} catch (Exception e) {
				// 예외 발생 시(예: 아이디 중복 등) 에러 메시지를 가시고 가입 폼으로 리다이렉트
				ra.addFlashAttribute("authError", "회원가입 실패: " + e.getMessage());
				
				return "redirect:/member/register";
			}
		}

	// 기존 일반 세션 기반 코드 유지 (Spring Security 권한 체크 방식에 맞추어 추후 SecurityContextHolder 사용 권장)
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
		
		// 암호화 로그인 유저 가져오기로 수정 필요할 수 있음
		// 임시 가이드: 기존 세션 삭제 및 탈퇴 기능 유지
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
	
	
	/**
	 * 아이디 중복 확인 API (GET)
	 * @return 사용 가능하면 true, 중복이면 false 반환
	 */
	@RequestMapping(value = "checkId", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkId(String username) {
		try {
			// 시큐리티를 거치지 않고 DAO를 통해 DB 유저를 직접 조회합니다.
			MemberVO existingUser = memberService.getMemberByUsernames(username);
			
			// 콘솔 로그로 데이터가 실제로 어떻게 찍히는지 추적합니다.
			System.out.println("중복 체크 대상 아이디: " + username);
			System.out.println("조회된 유저 결과 객체: " + existingUser);

			// 객체가 null이면 -> 가입된 적 없음 (사용 가능 true)
			// 객체가 존재하면 -> 이미 존재함 (사용 불가 false)
			return existingUser == null;
			
		} catch (Exception e) {
			// 혹시 서비스단에서 유저가 없을 때 예외를 던진다면, 유저가 없다는 뜻이므로 사용 가능(true) 처리
			System.out.println("조회 중 예외 발생(유저 없음 기인): " + e.getMessage());
			return true; 
		}
	}
	
}