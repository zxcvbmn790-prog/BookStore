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

	public boolean updateProfile(MemberVO member) {
		return dao.updateProfile(member) > 0;
	}

	public boolean updatePassword(String username, String currentPassword, String newPassword) {
		return dao.updatePassword(username, currentPassword, newPassword) > 0;
	}

	public boolean deleteMember(String username) {
		return dao.deleteMember(username) > 0;
	}

	public List<MemberVO> getAllMembers() {
		return dao.findAllMembers();
	}
}
