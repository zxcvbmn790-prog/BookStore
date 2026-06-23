package WebBookStore.member;

import java.security.SecureRandom;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
public class MemberController {

	private static final long OTP_VALIDITY_MS = 3 * 60 * 1000;

	@Autowired
	private MemberService memberService;

	@Autowired
	private KakaoLoginService kakaoLoginService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private EmailService emailService;

	// ==================== 로그인 ====================

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/login.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody MemberVO member, HttpServletRequest request, HttpSession loginsession) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword())
			);

			SecurityContextHolder.getContext().setAuthentication(authentication);

			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			session.setAttribute("loginUser", authentication.getName());
			session.setAttribute("loginNickname", member.getNickname());

			return ResponseEntity.ok(
					Map.of("result", "success", "message", "로그인 성공", "username", authentication.getName())
			);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					Map.of("result", "fail", "message", "로그인 실패: " + e.getMessage())
			);
		}
	}

	// ==================== 카카오 로그인 ====================

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

			org.springframework.security.core.userdetails.UserDetails userDetails =
					memberService.loadUserByUsername(member.getUsername());
			Authentication authentication = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

			return "redirect:/book/list";
		} catch (Exception e) {
			e.printStackTrace();
			ra.addFlashAttribute("authError", "카카오 로그인 처리 중 오류가 발생했습니다.");
			return "redirect:/member/login";
		}
	}

	// ==================== 회원가입 ====================

	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/member/register.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String register(MemberVO member, HttpSession session, RedirectAttributes ra) {
		Boolean verified = (Boolean) session.getAttribute("pendingEmailVerified");
		String verifiedEmail = (String) session.getAttribute("pendingEmail");

		if (verified == null || !verified
				|| verifiedEmail == null || !verifiedEmail.equals(member.getEmail())) {
			ra.addFlashAttribute("authError", "이메일 인증을 먼저 완료해주세요.");
			return "redirect:/member/register";
		}

		try {
			memberService.registerMember(member);
			clearPendingSession(session);
			ra.addFlashAttribute("authMessage", "회원가입이 완료되었습니다. 로그인 후 이용해주세요.");
			return "redirect:/member/login";
		} catch (Exception e) {
			ra.addFlashAttribute("authError", "회원가입 실패: " + e.getMessage());
			return "redirect:/member/register";
		}
	}

	// ==================== OTP 발송 / 검증 (AJAX) ====================

	@RequestMapping(value = "sendOtp", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> sendOtp(@RequestParam("email") String email, HttpSession session) {
		if (email == null || email.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("result", "fail", "message", "이메일을 입력해주세요."));
		}

		String otp = generateOtp();
		long expiry = System.currentTimeMillis() + OTP_VALIDITY_MS;

		session.setAttribute("pendingOtp", otp);
		session.setAttribute("pendingOtpExpiry", expiry);
		session.setAttribute("pendingEmail", email.trim());
		session.setAttribute("pendingEmailVerified", false);

		try {
			emailService.sendOtpEmail(email.trim(), null, otp);
			return ResponseEntity.ok(
					Map.of("result", "success", "message", "인증번호가 발송되었습니다.", "remainingSec", 180)
			);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					Map.of("result", "fail", "message", e.getMessage())
			);
		}
	}

	@RequestMapping(value = "verifyOtp", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> verifyOtp(@RequestParam("otp") String inputOtp, HttpSession session) {
		String savedOtp = (String) session.getAttribute("pendingOtp");
		Long expiry = (Long) session.getAttribute("pendingOtpExpiry");

		if (savedOtp == null || expiry == null) {
			return ResponseEntity.badRequest().body(
					Map.of("result", "expired", "message", "인증 세션이 만료되었습니다. 인증번호를 다시 발송해주세요.")
			);
		}
		if (System.currentTimeMillis() > expiry) {
			return ResponseEntity.badRequest().body(
					Map.of("result", "expired", "message", "인증번호가 만료되었습니다. 다시 발송해주세요.")
			);
		}
		if (!savedOtp.equals(inputOtp.trim())) {
			return ResponseEntity.badRequest().body(
					Map.of("result", "mismatch", "message", "인증번호가 일치하지 않습니다.")
			);
		}

		session.setAttribute("pendingEmailVerified", true);
		return ResponseEntity.ok(Map.of("result", "success", "message", "이메일 인증이 완료되었습니다."));
	}

	// ==================== 프로필 / 탈퇴 / 로그아웃 ====================

	@RequestMapping(value = "profile", method = RequestMethod.GET)
	public String profile(HttpSession session, Model model, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			ra.addFlashAttribute("authError", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}

		MemberVO member = memberService.getMember(loginUser);
		model.addAttribute("member", member);
		model.addAttribute("contentPage", "/WEB-INF/views/member/profile.jsp");
		return "layout/layout";
	}
	
	@RequestMapping(value = "profile/update", method = RequestMethod.POST)
	public String updateProfile(MemberVO member, HttpSession session, RedirectAttributes ra) {
	    String loginUser = (String) session.getAttribute("loginUser");
	    if (loginUser == null || "admin".equals(loginUser)) {
	        ra.addFlashAttribute("authError", "로그인 후 이용해주세요.");
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

	@RequestMapping(value = "checkId", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkId(String username) {
		if (username == null || username.trim().isEmpty()) {
			return false;
		}
		try {
			return memberService.getMember(username) == null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ==================== 헬퍼 ====================

	private String generateOtp() {
		return String.format("%06d", new SecureRandom().nextInt(1000000));
	}

	private void clearPendingSession(HttpSession session) {
		session.removeAttribute("pendingOtp");
		session.removeAttribute("pendingOtpExpiry");
		session.removeAttribute("pendingEmail");
		session.removeAttribute("pendingEmailVerified");
	}
}
