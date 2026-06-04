package WebBookStore.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// XML 설정의 user-service-ref="userService" 와 이름을 일치시킵니다.
@Service("userService")
public class MemberService implements UserDetailsService {

	@Autowired
	private MemberDAO dao;
	
	@Autowired
	private BCryptPasswordEncoder pwe;

	// Spring Security 필수 구현 메소드
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		System.out.println("로그인 시도 : " + username);

		// 기존에 섞여있던 userdao를 dao로 수정
		MemberVO member = dao.findByUsername(username);

		if (member == null) {
			throw new UsernameNotFoundException("사용자 없음: " + username);
		}

		// Security 내부의 User 빌더 객체를 이용해 리턴
		return org.springframework.security.core.userdetails.User
				.builder()
				.username(member.getUsername())
				.password(member.getPassword()) // 디비에 저장된 암호화된 비밀번호여야 합니다.
				.authorities(new SimpleGrantedAuthority(member.getRole() != null ? member.getRole() : "ROLE_USER"))
				.build();
	}

	// 회원가입 비즈니스 로직
	public boolean registerMember(MemberVO member) {

		// 아이디 중복 확인
		if (dao.findByUsername(member.getUsername()) != null) {
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		}

		// 비밀번호 BCrypt 암호화 처리
		member.setPassword(pwe.encode(member.getPassword()));

		// 기본 권한 설정 (ROLE_USER)
		if (member.getRole() == null || member.getRole().isEmpty()) {
			member.setRole("ROLE_USER");
		}
		
		return dao.register(member) > 0;
	}

	public MemberVO getMember(String username) {
		return dao.findByUsername(username);
	}

	// dev 브랜치 반영: 프로필 업데이트
	public boolean updateProfile(MemberVO member) {
		return dao.updateProfile(member) > 0;
	}

	// dev 브랜치 반영: 비밀번호 업데이트 (이전에 수정한 DAO와 완벽 호환됨)
	public boolean updatePassword(String username, String currentPassword, String newPassword) {
	    // 1. 디비에서 현재 유저 정보 가져오기
	    MemberVO member = dao.findByUsername(username);
	    if (member == null) return false;

	    // 2. 입력한 현재 비밀번호가 암호화된 디비 비밀번호와 일치하는지 비교 (Service단 검증)
	    if (!pwe.matches(currentPassword, member.getPassword())) {
	        return false; // 비밀번호 불일치 시 실패 리턴
	    }

	    // 3. 일치한다면 새 비밀번호를 암호화하여 DB에 업데이트 요청
	    // (DAO에는 username과 새 비밀번호 2개만 전달)
	    String encryptedNewPassword = pwe.encode(newPassword);
	    return dao.updatePassword(username, encryptedNewPassword) > 0;
	}

	// dev 브랜치 반영: 회원 탈퇴
	public boolean deleteMember(String username) {
		return dao.deleteMember(username) > 0;
	}

	public List<MemberVO> getAllMembers() {
		return dao.findAllMembers();
	}

	// feature_kakaoLogin 브랜치 반영: 카카오 회원가입/조회
	public MemberVO getOrRegisterKakaoMember(KakaoUserInfo kakaoUserInfo) {
		MemberVO member = dao.findByKakaoId(kakaoUserInfo.getKakaoId());

		if (member != null) {
			return member;
		}

		dao.registerKakaoMember(kakaoUserInfo);

		member = dao.findByKakaoId(kakaoUserInfo.getKakaoId());

		if (member == null) {
			throw new IllegalStateException("카카오 회원 조회 또는 저장에 실패했습니다.");
		}

		return member;
	}

	// dev 브랜치 반영: 아이디 중복 확인용 유저 검색 (오타 의심 메서드)
	public MemberVO getMemberByUsernames(String username) {
		return dao.findByUsernames(username);
	}
}