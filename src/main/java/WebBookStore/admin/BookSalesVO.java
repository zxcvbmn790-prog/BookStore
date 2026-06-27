package WebBookStore.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSalesVO {
    private int isbn;
    private String bookname;
    private int totalQuantity; // 총 판매수량
    private int totalSales;    // 총 매출액
}