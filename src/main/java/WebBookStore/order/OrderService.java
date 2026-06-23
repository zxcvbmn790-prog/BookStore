package WebBookStore.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WebBookStore.cart.CartVO;

@Service
public class OrderService {

	@Autowired
	private OrderDAO orderDAO;

	public int placeOrder(String userid, List<CartVO> cartList,
			String receiver, String phone, String address) {

		if (cartList == null || cartList.isEmpty()) {
			return 0;
		}

		return orderDAO.placeOrder(userid, cartList, receiver, phone, address);
	}

	public List<OrderVO> getOrderList(String userid) {
		return orderDAO.getOrderList(userid);
	}
}