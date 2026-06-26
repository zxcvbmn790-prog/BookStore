package WebBookStore.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WebBookStore.cart.CartVO;

import WebBookStore.member.MemberService;
import WebBookStore.member.MemberVO;

@Service
public class OrderService {

	@Autowired
	private OrderDAO orderDAO;

	@Autowired
	private MemberService memberService;

	public int placeOrder(String userid, List<CartVO> cartList,
	        String receiver, String phone, String address, int useMileage) {

	    // 1. 장바구니가 비었으면 주문 불가
	    if (cartList == null || cartList.isEmpty()) {
	        return 0;
	    }

	    // 2. 장바구니 총 금액 계산
	    int sumMoney = 0;
	    for (CartVO cart : cartList) {
	        sumMoney += cart.getTotalPrice();
	    }

	    // 3. 회원 정보 조회
	    // 회원 주문이면 member가 존재하고,
	    // 비회원 주문이면 member가 null일 수 있다.
	    MemberVO member = memberService.getMember(userid);
	    boolean isMemberOrder = member != null;

	    // 4. 기본값 세팅
	    int usedMileage = 0;
	    int earnedMileage = 0;
	    int finalPayment = sumMoney;
	    String newGrade = null;

	    // 5. 회원 주문일 때만 마일리지 계산
	    if (isMemberOrder) {
	        usedMileage = useMileage;

	        // 음수 마일리지 방지
	        if (usedMileage < 0) {
	            usedMileage = 0;
	        }

	        // 보유 마일리지보다 많이 사용하지 못하게 제한
	        if (usedMileage > member.getMileage()) {
	            usedMileage = member.getMileage();
	        }

	        // 주문 금액보다 많이 사용하지 못하게 제한
	        if (usedMileage > sumMoney) {
	            usedMileage = sumMoney;
	        }

	        // 실제 결제 금액
	        finalPayment = sumMoney - usedMileage;

	        // 등급별 적립률 조회
	        int mileageRate = memberService.getMileageRate(member.getGrade());

	        // 실제 결제 금액 기준으로 마일리지 적립
	        earnedMileage = finalPayment * mileageRate / 100;

	        // 누적 마일리지 기준으로 새 등급 계산
	        int newTotalMileage = member.getTotalMileage() + earnedMileage;
	        newGrade = memberService.calculateGrade(newTotalMileage);
	    }

	    // 6. 계산 결과를 DAO로 넘겨서 DB 처리
	    return orderDAO.placeOrder(userid, cartList, receiver, phone, address,
	            usedMileage, earnedMileage, finalPayment, newGrade, isMemberOrder);
	}

	public List<OrderVO> getOrderList(String userid) {
		return orderDAO.getOrderList(userid);
	}

	public List<OrderVO> getAllOrderList() {
		return orderDAO.getOrderAllList();
	}

	public void updateTrackingStatus(int orderId, String trakingstatus) {
		orderDAO.updateTrackingStatus(orderId, trakingstatus);
	}
	
	
	public OrderVO getOrderDetail(int orderId) {
	    return orderDAO.getOrderDetail(orderId);
	}
}