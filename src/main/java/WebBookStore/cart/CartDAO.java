package WebBookStore.cart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CartDAO {

	@Autowired
	private Connection conn;

	public void insertCart(CartVO cartVO) {
		String sql = "INSERT INTO cart (userid, isbn, amount) VALUES (?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cartVO.getUserid());
			ps.setInt(2, cartVO.getIsbn());
			ps.setInt(3, cartVO.getAmount());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<CartVO> getCartList(String userid) {
		List<CartVO> list = new ArrayList<>();

		String sql = "SELECT c.cart_id, c.userid, c.isbn, c.amount, " + "b.bookname, b.price, b.image "
				+ "FROM cart c JOIN book b ON c.isbn = b.isbn " + "WHERE c.userid = ? ORDER BY c.cart_id DESC";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, userid);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					CartVO cart = new CartVO();
					cart.setCart_id(rs.getInt("cart_id"));
					cart.setUserid(rs.getString("userid"));
					cart.setIsbn(rs.getInt("isbn"));
					cart.setAmount(rs.getInt("amount"));

					cart.setBookname(rs.getString("bookname"));
					cart.setPrice(rs.getString("price"));
					cart.setImage(rs.getString("image"));

					int priceInt = Integer.parseInt(rs.getString("price"));
					cart.setTotalPrice(priceInt * rs.getInt("amount"));

					list.add(cart);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int checkCart(String userid, int isbn) {
		String sql = "SELECT COUNT(*) FROM cart WHERE userid = ? AND isbn = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, userid);
			ps.setInt(2, isbn);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1); // 0이면 없음, 1 이상이면 이미 존재함
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void updateCart(CartVO cartVO) {
		String sql = "UPDATE cart SET amount = amount + ? WHERE userid = ? AND isbn = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cartVO.getAmount());
			ps.setString(2, cartVO.getUserid());
			ps.setInt(3, cartVO.getIsbn());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateAmount(CartVO cartVO) {
	    String sql = "UPDATE cart SET amount = ? WHERE userid = ? AND isbn = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, cartVO.getAmount());
	        ps.setString(2, cartVO.getUserid());
	        ps.setInt(3, cartVO.getIsbn());
	        ps.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	// isbn만 쓰면 어떤 책인지만 알 수 있고 userid까지 같이 써야 누구 장바구니의 어떤 책인지를 정확히 지정할 수 있어서 userid + isbn 로수정
	public int delete(String userid, int isbn) {
	    String sql = "DELETE FROM cart WHERE userid = ? AND isbn = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, userid);
	        ps.setInt(2, isbn);
	        return ps.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0;
	}
}