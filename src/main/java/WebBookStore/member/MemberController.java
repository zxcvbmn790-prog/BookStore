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

	        HttpSession session = request.getSession();
	        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
	        
	        // 🌟 DB에서 최신 회원 정보를 먼저 가져옵니다.
	        MemberVO dbMember = memberService.getMember(authentication.getName());
	        
	        // 🌟 DB에서 가져온 데이터(dbMember)로 세션을 채워야 권한(Role)이 정확히 반영됩니다!
	        session.setAttribute("loginUser", dbMember != null ? dbMember.getUsername() : authentication.getName());
	        session.setAttribute("loginNickname", dbMember != null ? dbMember.getNickname() : authentication.getName());
	        session.setAttribute("loginRole", dbMember != null ? dbMember.getRole() : "ROLE_USER"); 
	        
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
	        session.setAttribute("loginRole", member.getRole()); // 🌟 카카오 로그인 시에도 Role 저장 추가
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
		String checkedUsername = (String) session.getAttribute("checkedUsername");
		if (checkedUsername == null || !checkedUsername.equals(member.getUsername())) {
			ra.addFlashAttribute("authError", "아이디 중복 확인을 먼저 완료해주세요.");
			return "redirect:/member/register";
		}

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

	// ==================== 비밀번호 변경 ====================

	@RequestMapping(value = "password", method = RequestMethod.GET)
	public String password(HttpSession session, Model model, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/member/login";
		}
		if ("KAKAO".equals(session.getAttribute("loginType"))) {
			ra.addFlashAttribute("profileError", "카카오 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
			return "redirect:/member/profile";
		}
		model.addAttribute("member", memberService.getMember(loginUser));
		model.addAttribute("contentPage", "/WEB-INF/views/member/password.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "password/update", method = RequestMethod.POST)
	public String updatePassword(
			@RequestParam String currentPassword,
			@RequestParam String newPassword,
			@RequestParam String confirmPassword,
			HttpSession session, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/member/login";
		}

		Boolean verified = (Boolean) session.getAttribute("pendingEmailVerified");
		String verifiedEmail = (String) session.getAttribute("pendingEmail");
		MemberVO member = memberService.getMember(loginUser);

		if (verified == null || !verified
				|| verifiedEmail == null || !verifiedEmail.equals(member.getEmail())) {
			ra.addFlashAttribute("passwordError", "이메일 인증을 먼저 완료해주세요.");
			return "redirect:/member/password";
		}
		if (!newPassword.equals(confirmPassword)) {
			ra.addFlashAttribute("passwordError", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
			return "redirect:/member/password";
		}
		if (!memberService.updatePassword(loginUser, currentPassword, newPassword)) {
			ra.addFlashAttribute("passwordError", "현재 비밀번호가 올바르지 않습니다.");
			return "redirect:/member/password";
		}

		clearPendingSession(session);
		session.invalidate();
		ra.addFlashAttribute("authMessage", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
		return "redirect:/member/login";
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

	@RequestMapping(value = "delete", method = RequestMethod.GET)
	public String deleteForm(HttpSession session, Model model, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			ra.addFlashAttribute("authError", "로그인 후 이용해주세요.");
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

		// 카카오 사용자는 비밀번호 검증 없이 탈퇴
		boolean isKakao = "KAKAO".equals(session.getAttribute("loginType"));
		if (!isKakao) {
			if (password == null || password.trim().isEmpty()) {
				ra.addFlashAttribute("deleteError", "비밀번호를 입력해주세요.");
				return "redirect:/member/delete";
			}
			MemberVO member = memberService.getMember(loginUser);
			if (member == null || !memberService.checkPassword(password, member.getPassword())) {
				ra.addFlashAttribute("deleteError", "비밀번호가 올바르지 않습니다.");
				return "redirect:/member/delete";
			}
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
	public boolean checkId(String username, HttpSession session) {
		if (username == null || username.trim().isEmpty()) {
			session.removeAttribute("checkedUsername");
			return false;
		}
		try {
			boolean available = memberService.getMember(username.trim()) == null;
			if (available) {
				session.setAttribute("checkedUsername", username.trim());
			} else {
				session.removeAttribute("checkedUsername");
			}
			return available;
		} catch (Exception e) {
			e.printStackTrace();
			session.removeAttribute("checkedUsername");
			return false;
		}
	}
	
	// ==================== 아이디 찾기 ====================

		@RequestMapping(value = "findId", method = RequestMethod.GET)
		public String findId(Model model) {
			model.addAttribute("contentPage", "/WEB-INF/views/member/findId.jsp");
			return "layout/layout";
		}

		@RequestMapping(value = "findId/sendOtp", method = RequestMethod.POST)
		@ResponseBody
		public ResponseEntity<?> findIdSendOtp(@RequestParam("email") String email, HttpSession session) {
			if (email == null || email.trim().isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("result", "fail", "message", "이메일을 입력해주세요."));
			}

			MemberVO member = memberService.findUsernameByEmail(email.trim());
			if (member == null) {
				return ResponseEntity.badRequest().body(
						Map.of("result", "fail", "message", "해당 이메일로 가입된 계정을 찾을 수 없습니다.")
				);
			}

			String otp = generateOtp();
			long expiry = System.currentTimeMillis() + OTP_VALIDITY_MS;

			session.setAttribute("findIdOtp", otp);
			session.setAttribute("findIdOtpExpiry", expiry);
			session.setAttribute("findIdEmail", email.trim());
			session.setAttribute("findIdVerified", false);

			try {
				emailService.sendOtpEmail(email.trim(), member.getNickname(), otp, "아이디 찾기");
				return ResponseEntity.ok(
						Map.of("result", "success", "message", "인증번호가 발송되었습니다.", "remainingSec", 180)
				);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(
						Map.of("result", "fail", "message", e.getMessage())
				);
			}
		}

		@RequestMapping(value = "findId/verifyOtp", method = RequestMethod.POST)
		@ResponseBody
		public ResponseEntity<?> findIdVerifyOtp(@RequestParam("otp") String inputOtp, HttpSession session) {
			String savedOtp = (String) session.getAttribute("findIdOtp");
			Long expiry = (Long) session.getAttribute("findIdOtpExpiry");
			String email = (String) session.getAttribute("findIdEmail");

			if (savedOtp == null || expiry == null || email == null) {
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

			MemberVO member = memberService.findUsernameByEmail(email);
			if (member == null) {
				return ResponseEntity.badRequest().body(
						Map.of("result", "fail", "message", "계정 정보를 확인할 수 없습니다.")
				);
			}

			session.removeAttribute("findIdOtp");
			session.removeAttribute("findIdOtpExpiry");
			session.removeAttribute("findIdEmail");
			session.removeAttribute("findIdVerified");

			return ResponseEntity.ok(
					Map.of("result", "success", "username", member.getUsername())
			);
		}

		// ==================== 비밀번호 찾기 ====================

		@RequestMapping(value = "findPassword", method = RequestMethod.GET)
		public String findPassword(Model model) {
			model.addAttribute("contentPage", "/WEB-INF/views/member/findPassword.jsp");
			return "layout/layout";
		}

		@RequestMapping(value = "findPassword/sendOtp", method = RequestMethod.POST)
		@ResponseBody
		public ResponseEntity<?> findPasswordSendOtp(
				@RequestParam("username") String username,
				@RequestParam("email") String email,
				HttpSession session) {
			if (username == null || username.trim().isEmpty()
					|| email == null || email.trim().isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("result", "fail", "message", "아이디와 이메일을 모두 입력해주세요."));
			}

			MemberVO member = memberService.findMemberByUsernameAndEmail(username.trim(), email.trim());
			if (member == null) {
				return ResponseEntity.badRequest().body(
						Map.of("result", "fail", "message", "아이디와 이메일이 일치하는 계정을 찾을 수 없습니다.")
				);
			}

			String otp = generateOtp();
			long expiry = System.currentTimeMillis() + OTP_VALIDITY_MS;

			session.setAttribute("findPwOtp", otp);
			session.setAttribute("findPwOtpExpiry", expiry);
			session.setAttribute("findPwUsername", member.getUsername());
			session.setAttribute("findPwEmail", email.trim());

			try {
				emailService.sendOtpEmail(email.trim(), member.getNickname(), otp, "비밀번호 찾기");
				return ResponseEntity.ok(
						Map.of("result", "success", "message", "인증번호가 발송되었습니다.", "remainingSec", 180)
				);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(
						Map.of("result", "fail", "message", e.getMessage())
				);
			}
		}

		@RequestMapping(value = "findPassword/verifyOtp", method = RequestMethod.POST)
		@ResponseBody
		public ResponseEntity<?> findPasswordVerifyOtp(@RequestParam("otp") String inputOtp, HttpSession session) {
			String savedOtp = (String) session.getAttribute("findPwOtp");
			Long expiry = (Long) session.getAttribute("findPwOtpExpiry");
			String username = (String) session.getAttribute("findPwUsername");
			String email = (String) session.getAttribute("findPwEmail");

			if (savedOtp == null || expiry == null || username == null || email == null) {
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

			MemberVO member = memberService.findMemberByUsernameAndEmail(username, email);
			if (member == null) {
				return ResponseEntity.badRequest().body(
						Map.of("result", "fail", "message", "계정 정보를 확인할 수 없습니다.")
				);
			}

			try {
				String tempPassword = memberService.resetPasswordToTemp(username);
				emailService.sendTempPasswordEmail(email, member.getNickname(), tempPassword);

				session.removeAttribute("findPwOtp");
				session.removeAttribute("findPwOtpExpiry");
				session.removeAttribute("findPwUsername");
				session.removeAttribute("findPwEmail");

				return ResponseEntity.ok(
						Map.of("result", "success", "message", "임시 비밀번호가 이메일로 발송되었습니다.")
				);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(
						Map.of("result", "fail", "message", e.getMessage())
				);
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
		session.removeAttribute("checkedUsername");
	}
}
