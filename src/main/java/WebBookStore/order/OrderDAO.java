package WebBookStore.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		int totalAmount = 0;

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
					totalAmount += cart.getAmount();
				}

				insertPs.executeBatch();
				
				// 누적 스탬프 계산 및 랜덤 마일리지 지급
				int currentStamp = 0;

				String selectStampSql = "SELECT stamp_count FROM stamp WHERE userid = ?";
				try (PreparedStatement stampSelectPs = conn.prepareStatement(selectStampSql)) {
				    stampSelectPs.setString(1, userid);

				    try (ResultSet rs = stampSelectPs.executeQuery()) {
				        if (rs.next()) {
				            currentStamp = rs.getInt("stamp_count");
				        }
				    }
				}

				int newStampTotal = currentStamp + totalAmount;
				int rewardCount = newStampTotal / 10;
				int remainStamp = newStampTotal % 10;

				// 10권마다 랜덤 마일리지 지급
				if (rewardCount > 0) {
				    int totalRewardMileage = 0;
				    Random random = new Random();

				    for (int i = 0; i < rewardCount; i++) {
				        int chance = random.nextInt(100) + 1;

				        if (chance <= 40) {
				            totalRewardMileage += 100;
				        } else if (chance <= 70) {
				            totalRewardMileage += 300;
				        } else if (chance <= 90) {
				            totalRewardMileage += 500;
				        } else if (chance <= 98) {
				            totalRewardMileage += 1000;
				        } else {
				            totalRewardMileage += 3000;
				        }
				    }

				    String mileageSql =
				    	    "UPDATE member SET mileage = mileage + ?, total_mileage = total_mileage + ? WHERE id = ?";
				    try (PreparedStatement mileagePs = conn.prepareStatement(mileageSql)) {
				    	mileagePs.setInt(1, totalRewardMileage);
				    	mileagePs.setInt(2, totalRewardMileage);
				    	mileagePs.setString(3, userid);
				        mileagePs.executeUpdate();
				    }
				}

				// 남은 스탬프 저장
				String updateStampSql =
				        "MERGE INTO stamp (userid, stamp_count, updated_at) KEY(userid) VALUES (?, ?, CURRENT_TIMESTAMP)";

				try (PreparedStatement stampUpdatePs = conn.prepareStatement(updateStampSql)) {
				    stampUpdatePs.setString(1, userid);
				    stampUpdatePs.setInt(2, remainStamp);
				    stampUpdatePs.executeUpdate();
				}

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
	
	public int getStampCount(String userid) {
	    String sql = "SELECT stamp_count FROM stamp WHERE userid = ?";

	    try (Connection conn = ds.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, userid);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("stamp_count");
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return 0;
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