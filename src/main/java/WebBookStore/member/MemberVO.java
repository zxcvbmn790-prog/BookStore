package WebBookStore.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberVO {
    private int id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
}