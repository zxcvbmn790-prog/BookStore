package WebBookStore.toss.repository;


import WebBookStore.toss.model.TossVo;

public interface TossDAO {

    // 주문 생성 (READY 상태 insert)
    void insertOrder(TossVo order);

    // 주문 단건 조회
    TossVo findOrderById(String orderId);

    // 결제 완료 처리 (PAID 업데이트)
    void updatePaymentSuccess(String orderId, String paymentKey);

    // 주문 상태 변경
    void updateOrderStatus(String orderId, String status);
    
    TossVo findPurchaseById(int purchaseId);
    
    TossVo findGuestPurchaseById(String guestPurchaseId);

	void insertOrder2(TossVo order);
}