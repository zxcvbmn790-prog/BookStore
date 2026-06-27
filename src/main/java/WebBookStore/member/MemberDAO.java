package WebBookStore.member;

import java.util.List;

public interface MemberDAO {
    MemberVO login(String id, String pw);
    int register(MemberVO mv);
    MemberVO findByUsername(String username);
    int updateProfile(MemberVO member);
    
    int updatePassword(String username, String newPassword); 
    
    int deleteMember(String username);
    List<MemberVO> findAllMembers();
    
    MemberVO findByKakaoId(String kakaoId);
    int registerKakaoMember(KakaoUserInfo kakaoUserInfo);

    MemberVO findByUsernames(String username);
	Object upRole(String username, String role);
	
	MemberVO findByEmail(String email);
}