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
	        String receiver, String phone, String address,
	        int usedMileage, int earnedMileage, int finalPayment,
	        String newGrade, boolean isMemberOrder) {

		String insertSql = "INSERT INTO orders "
		        + "(userid, isbn, bookname, price, amount, total_price, "
		        + "receiver, phone, address, order_date, status, traking_status, "
		        + "used_mileage, earned_mileage, final_payment) "
		        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, '접수', ?, ?, ?)";

		String deleteCartSql = "DELETE FROM cart WHERE userid = ?";

		String updateMemberSql = "UPDATE member "
		        + "SET default_receiver = ?, default_phone = ?, default_address = ?, "
		        + "mileage = mileage - ? + ?, "
		        + "total_mileage = total_mileage + ?, "
		        + "grade = ? "
		        + "WHERE id = ?";

		int orderCount = 0;

		try (Connection conn = ds.getConnection()) {
			conn.setAutoCommit(false);

			try (
				    PreparedStatement insertPs = conn.prepareStatement(insertSql);
				    PreparedStatement deletePs = conn.prepareStatement(deleteCartSql);
				    PreparedStatement updateMemberPs = conn.prepareStatement(updateMemberSql)
				) {
				for (CartVO cart : cartList) {
					int price = parsePrice(cart.getPrice());
					int totalPrice = price * cart.getAmount();

					insertPs.setString(1, userid);
					insertPs.setLong(2, cart.getIsbn());
					insertPs.setString(3, cart.getBookname());
					insertPs.setInt(4, price);
					insertPs.setInt(5, cart.getAmount());
					insertPs.setInt(6, totalPrice);
					insertPs.setString(7, receiver);
					insertPs.setString(8, phone);
					insertPs.setString(9, address);
					insertPs.setString(10, "결제완료");
					insertPs.setInt(11, usedMileage);
					insertPs.setInt(12, earnedMileage);
					insertPs.setInt(13, finalPayment);

					insertPs.addBatch();
					orderCount++;
				}

				insertPs.executeBatch();

				deletePs.setString(1, userid);
				deletePs.executeUpdate();

				if (isMemberOrder) {
				    updateMemberPs.setString(1, receiver);
				    updateMemberPs.setString(2, phone);
				    updateMemberPs.setString(3, address);
				    updateMemberPs.setInt(4, usedMileage);
				    updateMemberPs.setInt(5, earnedMileage);
				    updateMemberPs.setInt(6, earnedMileage);
				    updateMemberPs.setString(7, newGrade);
				    updateMemberPs.setString(8, userid);
				    updateMemberPs.executeUpdate();
				}

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
					order.setIsbn(rs.getLong("isbn"));
					order.setBookname(rs.getString("bookname"));
					order.setPrice(rs.getInt("price"));
					order.setAmount(rs.getInt("amount"));
					order.setTotalPrice(rs.getInt("total_price"));
					order.setReceiver(rs.getString("receiver"));
					order.setPhone(rs.getString("phone"));
					order.setAddress(rs.getString("address"));
					order.setOrderDate(rs.getTimestamp("order_date"));
					order.setStatus(rs.getString("status"));
					order.setTrakingstatus(rs.getString("traking_status"));
					order.setImage(rs.getString("image"));

					order.setUsedMileage(rs.getInt("used_mileage"));
					order.setEarnedMileage(rs.getInt("earned_mileage"));
					order.setFinalPayment(rs.getInt("final_payment"));

					list.add(order);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<OrderVO> getOrderAllList() {
		List<OrderVO> list = new ArrayList<>();

		String sql = "SELECT o.*, b.image "
				+ "FROM orders o "
				+ "LEFT JOIN book b ON o.isbn = b.isbn "
				+ "ORDER BY o.order_id DESC";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					OrderVO order = new OrderVO();
					order.setOrderId(rs.getInt("order_id"));
					order.setUserid(rs.getString("userid"));
					order.setIsbn(rs.getLong("isbn"));
					order.setBookname(rs.getString("bookname"));
					order.setPrice(rs.getInt("price"));
					order.setAmount(rs.getInt("amount"));
					order.setTotalPrice(rs.getInt("total_price"));
					order.setReceiver(rs.getString("receiver"));
					order.setPhone(rs.getString("phone"));
					order.setAddress(rs.getString("address"));
					order.setOrderDate(rs.getTimestamp("order_date"));
					order.setStatus(rs.getString("status"));
					order.setTrakingstatus(rs.getString("traking_status"));
					order.setImage(rs.getString("image"));

					list.add(order);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public void updateTrackingStatus(int orderId, String trakingstatus) {
		String sql = "UPDATE orders SET traking_status = ? WHERE order_id = ?";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, trakingstatus);
			ps.setInt(2, orderId);
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	/**
	 * 특정 주문번호(orderId) 1건에 대한 상세 정보를 조회하는 메서드
	 * (배송 현황 조회 팝업창에서 사용)
	 */
	public OrderVO getOrderDetail(int orderId) {
	    OrderVO order = null;

	    // 1. 실행할 SQL 단건 조회 쿼리 (도서 이미지까지 가져오기 위해 LEFT JOIN 포함)
	    String sql = "SELECT o.*, b.image "
	            + "FROM orders o "
	            + "LEFT JOIN book b ON o.isbn = b.isbn "
	            + "WHERE o.order_id = ?";

	    // 2. DataSource를 이용해 Connection 및 PreparedStatement 생성
	    try (Connection conn = ds.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        // 3. 조건절(?)에 주문번호 파라미터 바인딩
	        ps.setInt(1, orderId);

	        // 4. 쿼리 실행 및 결과셋(ResultSet) 처리
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) { // 단건 조회의 경우 while 대신 if를 사용합니다.
	                order = new OrderVO();
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
	                order.setTrakingstatus(rs.getString("traking_status"));
	                order.setImage(rs.getString("image")); // 도서 이미지 정보 매핑
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // 5. 조회된 주문 객체 반환 (데이터가 없으면 null 반환)
	    return order;
	}
}