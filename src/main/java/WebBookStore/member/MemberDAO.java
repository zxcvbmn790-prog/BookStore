package WebBookStore.member;

import java.util.List;

public interface MemberDAO {
    MemberVO login(String id, String pw);
    int register(MemberVO mv);
    MemberVO findByUsername(String username);
    int updateProfile(MemberVO member);
    // 복잡한 암호화 비교를 위해 인터페이스 파라미터 변경 (현재 비밀번호는 Service단에서 검증)
    int updatePassword(String username, String newPassword); 
    int deleteMember(String username);
    List<MemberVO> findAllMembers();
	MemberVO findByUsernames(String username);
}
