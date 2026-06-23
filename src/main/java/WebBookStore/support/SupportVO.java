package WebBookStore.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportVO {
    private int supportId;
    private int memberId;
    private String title;
    private String content;
    private Date createdDate;
    private String status; // 예: "답변대기", "완료"
}