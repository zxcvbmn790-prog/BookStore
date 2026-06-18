package WebBookStore.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesVO {
    private String orderDay;   // 날짜
    private int salesAmount;   // 해당 날짜 매출
    private int orderCount;    // 해당 날짜 주문건수
}