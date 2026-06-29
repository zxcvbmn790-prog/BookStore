package WebBookStore.toss.service;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WebBookStore.toss.model.TossVo;
import WebBookStore.toss.repository.TossDAO;

@Service
public class TossServiceImpl implements TossService {
	@Autowired
    private TossDAO tossDAO;

	@Override
	public String createOrder(int purchaseId, String member_id, int amount) {

	    TossVo purchase = tossDAO.findPurchaseById(purchaseId);

	    String orderId = UUID.randomUUID().toString();

	    TossVo order = new TossVo();
	    order.setOrderId(orderId);
	    order.setPurchaseId(purchaseId);
	    order.setMember_id(member_id); // 🔥 핵심 추가
	    order.setOrderStatus("READY");
	    order.setTotalPrice(amount);

	    tossDAO.insertOrder(order);

	    return orderId;
	}

	@Override
	public String createOrder(String guestPurchaseId, String member_id, int amount) {
		TossVo purchase = tossDAO.findGuestPurchaseById(guestPurchaseId);
		
		String orderId = UUID.randomUUID().toString();
		
		TossVo order = new TossVo();
		order.setOrderId(orderId);
		order.setGuestPurchaseId(guestPurchaseId);
	    order.setMember_id(member_id); // 🔥 핵심 추가
	    order.setOrderStatus("READY");
	    order.setTotalPrice(amount);

	    tossDAO.insertOrder2(order);

	    return orderId;
		
	}
	
	@Override
	public void confirmPayment(String orderId, String paymentKey, int amount) {

	    // 1. DB 조회
	    TossVo order = tossDAO.findOrderById(orderId);

	    // 2. 금액 검증 (핵심)
	    if (order.getTotalPrice() != amount) {
	        throw new RuntimeException("결제 금액 불일치");
	    }

	    // 3. 결제 완료 처리
	    tossDAO.updatePaymentSuccess(orderId, paymentKey);
	}

	@Override
	public TossVo getOrder(String orderId) {
	    return tossDAO.findOrderById(orderId);
	}

	@Override
	public void approveOrder(String orderId, String paymentKey) {
	    tossDAO.updatePaymentSuccess(orderId, paymentKey);
	}

	
}