package WebBookStore.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import WebBookStore.cart.CartVO;

@Repository
public class OrderDAO {

	@Autowired
	private DataSource ds;

	public int placeOrder(String userid, List<CartVO> cartList,
			String receiver, String phone, String address) {

		String insertSql = "INSERT INTO orders "
				+ "(userid, isbn, bookname, price, amount, total_price, receiver, phone, address, order_date, status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

		String deleteCartSql = "DELETE FROM cart WHERE userid = ?";

		int orderCount = 0;

		try (Connection conn = ds.getConnection()) {
			conn.setAutoCommit(false);

			try (
				PreparedStatement insertPs = conn.prepareStatement(insertSql);
				PreparedStatement deletePs = conn.prepareStatement(deleteCartSql)
			) {
				for (CartVO cart : cartList) {
					int price = parsePrice(cart.getPrice());
					int totalPrice = price * cart.getAmount();

					insertPs.setString(1, userid);
					insertPs.setInt(2, cart.getIsbn());
					insertPs.setString(3, cart.getBookname());
					insertPs.setInt(4, price);
					insertPs.setInt(5, cart.getAmount());
					insertPs.setInt(6, totalPrice);
					insertPs.setString(7, receiver);
					insertPs.setString(8, phone);
					insertPs.setString(9, address);
					insertPs.setString(10, "결제완료");

					insertPs.addBatch();
					orderCount++;
				}

				insertPs.executeBatch();

				deletePs.setString(1, userid);
				deletePs.executeUpdate();

				conn.commit();
				return orderCount;

			} catch (Exception e) {
				conn.rollback();
				e.printStackTrace();
			} finally {
				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public List<OrderVO> getOrderList(String userid) {
		List<OrderVO> list = new ArrayList<>();

		String sql = "SELECT o.*, b.image "
				+ "FROM orders o "
				+ "LEFT JOIN book b ON o.isbn = b.isbn "
				+ "WHERE o.userid = ? "
				+ "ORDER BY o.order_id DESC";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, userid);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					OrderVO order = new OrderVO();
					order.setOrderId(rs.getInt("order_id"));
					order.setUserid(rs.getString("userid"));
					order.setIsbn(rs.getInt("isbn"));
					order.setBookname(rs.getString("bookname"));
					order.setPrice(rs.getInt("price"));
					order.setAmount(rs.getInt("amount"));
					order.setTotalPrice(rs.getInt("total_price"));
					order.setReceiver(rs.getString("receiver"));
					order.setPhone(rs.getString("phone"));
					order.setAddress(rs.getString("address"));
					order.setOrderDate(rs.getTimestamp("order_date"));
					order.setStatus(rs.getString("status"));
					order.setImage(rs.getString("image"));   // 추가

					list.add(order);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	private int parsePrice(String price) {
		if (price == null) {
			return 0;
		}

		String onlyNumber = price.replaceAll("[^0-9]", "");

		if (onlyNumber.isEmpty()) {
			return 0;
		}

		return Integer.parseInt(onlyNumber);
	}
}