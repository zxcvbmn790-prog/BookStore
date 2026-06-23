package WebBookStore.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

	@Autowired
	private CartDAO cartDao;

	public void insertCart(CartVO cartVO) {
		cartDao.insertCart(cartVO);
	}

	public List<CartVO> getCartList(String userid) {
		return cartDao.getCartList(userid);
	}

	public void insertOrUpdateCart(CartVO cartVO) {
		int count = cartDao.checkCart(cartVO.getUserid(), cartVO.getIsbn());

		if (count == 0) {
			// 장바구니에 없으면 새로 삽입
			cartDao.insertCart(cartVO);
		} else {
			// 이미 있으면 수량만 업데이트
			cartDao.updateCart(cartVO);
		}
	}

	public void updateAmount(CartVO cartVO) {
	    cartDao.updateAmount(cartVO);
	}
	
	public int deleteBook(String userid, int isbn) {
	    return cartDao.delete(userid, isbn);
	}
}