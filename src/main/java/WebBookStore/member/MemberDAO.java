package WebBookStore.member;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface MemberDAO {
	MemberVO login(String id, String pw);
	int register(MemberVO mv);
	MemberVO findByUsername(String username);
	int updateProfile(MemberVO member);
	int updatePassword(String username, String currentPassword, String newPassword);
	int deleteMember(String username);
	List<MemberVO> findAllMembers();
}
