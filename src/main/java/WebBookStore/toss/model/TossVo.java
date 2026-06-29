package WebBookStore.toss.model;


import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossVo {

	private String orderId;          // 토스 orderId
    private int purchaseId;          // purchase.id (연결 핵심)
    private String guestPurchaseId;          // purchase.id (연결 핵심)
    private int totalPrice;          // 결제 금액
    private String member_id;		//유저명

    private String orderStatus;      // READY, PAID, CANCEL
    private String paymentKey;       // 토스 paymentKey

    private Timestamp orderDate;    // 생성 시간
    private Timestamp paidAt;       // 결제 완료 시간

    
}