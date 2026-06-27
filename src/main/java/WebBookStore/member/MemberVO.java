package WebBookStore.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {
    private int id;           // DB의 num (일련번호) 매핑
    private String username;   // DB의 id (로그인 아이디) 매핑
    private String password;   // DB의 pw (암호화된 비밀번호) 매핑
    private String email;
    private String phone;
    private String nickname;
    private String role;       // Spring Security 권한 저장용 필드 추가 (ROLE_USER, ROLE_ADMIN 등)
}