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
    private String role;
    
    // 상윤 - 배송정보 자동입력용 필드 추가
    private String defaultReceiver;
    private String defaultPhone;
    private String defaultAddress;

    // 상윤 - 마일리지 / 회원등급용 필드 추가
    private int mileage;
    private int totalMileage;
    private String grade;
}