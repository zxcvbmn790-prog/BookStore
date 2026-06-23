package WebBookStore.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
public class MemberService implements UserDetailsService {

	@Autowired
	private MemberDAO dao;

	@Autowired
	private BCryptPasswordEncoder pwe;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		MemberVO member = dao.findByUsername(username);

		if (member == null) {
			throw new UsernameNotFoundException("사용자 없음: " + username);
		}

		return org.springframework.security.core.userdetails.User
				.builder()
				.username(member.getUsername())
				.password(member.getPassword())
				// ⚠️ DB의 실제 Role을 시큐리티에 넘겨줌
				.authorities(new SimpleGrantedAuthority(member.getRole() != null ? member.getRole() : "ROLE_USER"))
				.build();
	}

	public boolean registerMember(MemberVO member) {
		if (dao.findByUsername(member.getUsername()) != null) {
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		}
		member.setPassword(pwe.encode(member.getPassword()));
		if (member.getRole() == null || member.getRole().isEmpty()) {
			member.setRole("ROLE_USER");
		}
		return dao.register(member) > 0;
	}

	public MemberVO prepareRegistration(MemberVO member) {
		if (dao.findByUsername(member.getUsername()) != null) {
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		}
		if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
			throw new RuntimeException("이메일은 필수 입력 항목입니다.");
		}
		member.setPassword(pwe.encode(member.getPassword()));
		if (member.getRole() == null || member.getRole().isEmpty()) {
			member.setRole("ROLE_USER");
		}
		return member;
	}

	public boolean finalizeRegistration(MemberVO member) {
		if (dao.findByUsername(member.getUsername()) != null) {
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		}
		return dao.register(member) > 0;
	}

	public MemberVO getMember(String username) {
		return dao.findByUsername(username);
	}

	public boolean updateProfile(MemberVO member) {
		return dao.updateProfile(member) > 0;
	}

	public boolean updatePassword(String username, String currentPassword, String newPassword) {
		MemberVO member = dao.findByUsername(username);
		if (member == null) return false;

		if (!pwe.matches(currentPassword, member.getPassword())) {
			return false;
		}

		String encryptedNewPassword = pwe.encode(newPassword);
		return dao.updatePassword(username, encryptedNewPassword) > 0;
	}

	public boolean deleteMember(String username) {
		return dao.deleteMember(username) > 0;
	}

	public List<MemberVO> getAllMembers() {
		return dao.findAllMembers();
	}

	public MemberVO getOrRegisterKakaoMember(KakaoUserInfo kakaoUserInfo) {
		MemberVO member = dao.findByKakaoId(kakaoUserInfo.getKakaoId());
		if (member != null) { return member; }

		dao.registerKakaoMember(kakaoUserInfo);
		member = dao.findByKakaoId(kakaoUserInfo.getKakaoId());

		if (member == null) {
			throw new IllegalStateException("카카오 회원 조회 또는 저장에 실패했습니다.");
		}
		return member;
	}

	public MemberVO getMemberByUsernames(String username) {
		return dao.findByUsernames(username);
	}

	// 회원 등급에 따른 마일리지 적립률 계산
	public int getMileageRate(String grade) {
	    if ("VIP".equals(grade)) {
	        return 5;
	    } else if ("GOLD".equals(grade)) {
	        return 3;
	    } else if ("SILVER".equals(grade)) {
	        return 2;
	    }
	    return 1;
	}

	// 누적 적립 마일리지 기준으로 회원 등급 계산
	public String calculateGrade(int totalMileage) {
	    if (totalMileage >= 10000) {
	        return "VIP";
	    } else if (totalMileage >= 5000) {
	        return "GOLD";
	    } else if (totalMileage >= 1000) {
	        return "SILVER";
	    }
	    return "BRONZE";
	}
}
