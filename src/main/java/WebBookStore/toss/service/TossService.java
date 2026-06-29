package WebBookStore.toss.service;


import WebBookStore.toss.model.TossVo;

public interface TossService {

    // 주문 생성 (READY 상태)
	public String createOrder(int purchaseId, String member_id, int amount);

	public String createOrder(String guestPurchaseId, String member_id, int amount);
	
    // 결제 승인 처리
    void confirmPayment(String orderId, String paymentKey, int amount);

    // 주문 단건 조회
    TossVo getOrder(String orderId);

    // 결제 성공 처리
    void approveOrder(String orderId, String paymentKey);
}