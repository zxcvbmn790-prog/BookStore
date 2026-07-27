package WebBookStore.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AdminVO {
	private int isbn;         // 도서 번호
    private String bookname;  // 도서 제목
    private String author;    // 저자
    private String publisher; // 출판사
    private String image;     // 도서 이미지 경로
    private String price;	  // 가격
}