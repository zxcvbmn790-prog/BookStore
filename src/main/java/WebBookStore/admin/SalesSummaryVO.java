package WebBookStore.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummaryVO {
    private int totalSales;     // 전체 매출액
    private int totalOrders;    // 전체 주문건수
    private int totalQuantity;  // 전체 판매권수
    private int todaySales;     // 오늘 매출액
}