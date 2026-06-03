package WebBookStore.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

	@Autowired
	MemberDAO dao;

	public MemberVO getLoginUser(String username, String password) {
		return dao.login(username, password);
	}

	public boolean registerMember(MemberVO mv) {
		return dao.register(mv) > 0;
	}

	public MemberVO getMember(String username) {
		return dao.findByUsername(username);
	}


	public List<MemberVO> getAllMembers() {
		return dao.findAllMembers();
	}

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
}