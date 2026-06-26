package WebBookStore.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import WebBookStore.member.MemberVO;

@Repository
public class AdminDAO {

	@Autowired
	Connection conn;

	public AdminDAO() {
		System.out.println("dao:" + conn);
	}

	// 전체 목록
	public List<AdminVO> findAll() {
		String sql = "SELECT * FROM book";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			List<AdminVO> list = new ArrayList<AdminVO>();
			while (rs.next()) {
				AdminVO vo = new AdminVO();
					vo.setIsbn(rs.getLong("isbn"));
					vo.setBookname(rs.getString("bookname"));
					vo.setAuthor(rs.getString("author"));
					vo.setPublisher(rs.getString("publisher"));
					vo.setImage(rs.getString("image"));
					vo.setPrice(rs.getString("price"));
					rs.getString("category");
					try { vo.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e) {}
					list.add(vo);
			}
			rs.close();
			ps.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 등록
	public int insert(AdminVO book) {
		String sql = "INSERT INTO book "
		        + "(isbn, bookname, author, publisher, image, price, category, discount_rate) "
		        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, book.getIsbn());
			ps.setString(2, book.getBookname());
			ps.setString(3, book.getAuthor());
			ps.setString(4, book.getPublisher());
			ps.setString(5, book.getImage());
			ps.setString(6, book.getPrice());
			ps.setString(7, book.getCategory());
			ps.setInt(8, book.getDiscountRate());
			int result = ps.executeUpdate();
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 수정
	public int update(AdminVO book) {
		String sql = "UPDATE book SET bookname=?, author=?, publisher=?, image=?, price=?, category=?, discount_rate=? WHERE isbn=?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, book.getBookname());
			ps.setString(2, book.getAuthor());
			ps.setString(3, book.getPublisher());
			ps.setString(4, book.getImage());
			ps.setString(5, book.getPrice());
			ps.setString(6, book.getCategory());
			ps.setInt(7, book.getDiscountRate());
			ps.setLong(8, book.getIsbn());
			int result = ps.executeUpdate();
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int delete(long isbn) {
		String sqlCart = "DELETE FROM cart WHERE isbn = ?";
		String sqlBook = "DELETE FROM book WHERE isbn = ?";

		try {
			PreparedStatement psCart = conn.prepareStatement(sqlCart);
			psCart.setLong(1, isbn);
			psCart.executeUpdate();
			psCart.close();

			PreparedStatement psBook = conn.prepareStatement(sqlBook);
			psBook.setLong(1, isbn);
			int result = psBook.executeUpdate();
			psBook.close();

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 단건 조회
	public AdminVO findById(long isbn) {
		String sql = "SELECT * FROM book WHERE isbn = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, isbn);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				AdminVO book = new AdminVO();
				book.setIsbn(rs.getLong("isbn"));
				book.setBookname(rs.getString("bookname"));
				book.setAuthor(rs.getString("author"));
				book.setPublisher(rs.getString("publisher"));
				book.setImage(rs.getString("image"));
				book.setPrice(rs.getString("price"));
				book.setCategory(rs.getString("category"));
				try { book.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e) {}
				rs.close();
				ps.close();
				return book;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 판매 요약
	public SalesSummaryVO getSalesSummary() {
	    String sql = "SELECT "
	            + "COALESCE(SUM(total_price), 0) AS total_sales, "
	            + "COUNT(*) AS total_orders, "
	            + "COALESCE(SUM(amount), 0) AS total_quantity, "
	            + "COALESCE(SUM(CASE "
	            + "WHEN FORMATDATETIME(order_date, 'yyyy-MM-dd') = FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd') "
	            + "THEN total_price ELSE 0 END), 0) AS today_sales "
	            + "FROM orders";

	    try {
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            SalesSummaryVO summary = new SalesSummaryVO();
	            summary.setTotalSales(rs.getInt("total_sales"));
	            summary.setTotalOrders(rs.getInt("total_orders"));
	            summary.setTotalQuantity(rs.getInt("total_quantity"));
	            summary.setTodaySales(rs.getInt("today_sales"));

	            rs.close();
	            ps.close();
	            return summary;
	        }

	        rs.close();
	        ps.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return new SalesSummaryVO(0, 0, 0, 0);
	}
	
	// 기간별 매출
	public List<DailySalesVO> getDailySalesList(String period) {
		String labelExpr = "FORMATDATETIME(order_date, 'yyyy-MM-dd')";
		int limit = 7;

		if ("week".equals(period)) {
			labelExpr = "CONCAT(YEAR(order_date), '-W', RIGHT(CONCAT('00', ISO_WEEK(order_date)), 2))";
			limit = 8;
		} else if ("month".equals(period)) {
			labelExpr = "FORMATDATETIME(order_date, 'yyyy-MM')";
			limit = 12;
		} else if ("year".equals(period)) {
			labelExpr = "FORMATDATETIME(order_date, 'yyyy')";
			limit = 5;
		}

		String sql = "SELECT "
				+ labelExpr + " AS order_day, "
				+ "COALESCE(SUM(total_price), 0) AS sales_amount, "
				+ "COUNT(*) AS order_count, "
				+ "MIN(order_date) AS sort_value "
				+ "FROM orders "
				+ "GROUP BY " + labelExpr + " "
				+ "ORDER BY sort_value DESC "
				+ "LIMIT ?";

		List<DailySalesVO> list = new ArrayList<>();

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, limit);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				DailySalesVO vo = new DailySalesVO();
				vo.setOrderDay(rs.getString("order_day"));
				vo.setSalesAmount(rs.getInt("sales_amount"));
				vo.setOrderCount(rs.getInt("order_count"));
				list.add(vo);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
// 많이 팔린 책 TOP 5
	public List<BookSalesVO> getTopBookSalesList() {
	    String sql = "SELECT "
	            + "isbn, bookname, "
	            + "COALESCE(SUM(amount), 0) AS total_quantity, "
	            + "COALESCE(SUM(total_price), 0) AS total_sales "
	            + "FROM orders "
	            + "GROUP BY isbn, bookname "
	            + "ORDER BY total_quantity DESC, total_sales DESC "
	            + "LIMIT 5";

	    List<BookSalesVO> list = new ArrayList<>();

	    try {
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            BookSalesVO vo = new BookSalesVO();
	            vo.setIsbn(rs.getLong("isbn"));
	            vo.setBookname(rs.getString("bookname"));
	            vo.setTotalQuantity(rs.getInt("total_quantity"));
	            vo.setTotalSales(rs.getInt("total_sales"));
	            list.add(vo);
	        }

	        rs.close();
	        ps.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}


	public List<MemberVO> getMemberList() {
	    List<MemberVO> list = new ArrayList<>();

	    String sql = "SELECT num, id, pw, email, hp, nickname, role, "
	            + "default_receiver, default_phone, default_address, "
	            + "mileage, total_mileage, grade "
	            + "FROM member ORDER BY num DESC";

	    try (PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            MemberVO member = new MemberVO();

	            member.setId(rs.getInt("num"));
	            member.setUsername(rs.getString("id"));
	            member.setPassword(rs.getString("pw"));
	            member.setEmail(rs.getString("email"));
	            member.setPhone(rs.getString("hp"));
	            member.setNickname(rs.getString("nickname"));
	            member.setRole(rs.getString("role"));

	            member.setDefaultReceiver(rs.getString("default_receiver"));
	            member.setDefaultPhone(rs.getString("default_phone"));
	            member.setDefaultAddress(rs.getString("default_address"));

	            member.setMileage(rs.getInt("mileage"));
	            member.setTotalMileage(rs.getInt("total_mileage"));
	            member.setGrade(rs.getString("grade"));

	            list.add(member);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

	public List<MemberVO> searchMembers(String keyword) {
		List<MemberVO> list = new ArrayList<>();
		boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
		String sql = "SELECT num, id, pw, email, hp, nickname, role, "
				+ "default_receiver, default_phone, default_address, "
				+ "mileage, total_mileage, grade FROM member";
		if (hasKeyword) {
			sql += " WHERE id LIKE ? OR nickname LIKE ? OR email LIKE ?";
		}
		sql += " ORDER BY num DESC";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			if (hasKeyword) {
				String kw = "%" + keyword.trim() + "%";
				ps.setString(1, kw);
				ps.setString(2, kw);
				ps.setString(3, kw);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					MemberVO member = new MemberVO();
					member.setId(rs.getInt("num"));
					member.setUsername(rs.getString("id"));
					member.setPassword(rs.getString("pw"));
					member.setEmail(rs.getString("email"));
					member.setPhone(rs.getString("hp"));
					member.setNickname(rs.getString("nickname"));
					member.setRole(rs.getString("role"));
					member.setDefaultReceiver(rs.getString("default_receiver"));
					member.setDefaultPhone(rs.getString("default_phone"));
					member.setDefaultAddress(rs.getString("default_address"));
					member.setMileage(rs.getInt("mileage"));
					member.setTotalMileage(rs.getInt("total_mileage"));
					member.setGrade(rs.getString("grade"));
					list.add(member);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<AdminVO> searchBooks(String keyword) {
		List<AdminVO> list = new ArrayList<>();
		String sql = "SELECT * FROM book";
		boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
		if (hasKeyword) {
			sql += " WHERE bookname LIKE ? OR author LIKE ? OR CAST(isbn AS VARCHAR) LIKE ?";
		}
		sql += " ORDER BY isbn";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			if (hasKeyword) {
				String kw = "%" + keyword.trim() + "%";
				ps.setString(1, kw);
				ps.setString(2, kw);
				ps.setString(3, kw);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					AdminVO vo = new AdminVO();
					vo.setIsbn(rs.getLong("isbn"));
					vo.setBookname(rs.getString("bookname"));
					vo.setAuthor(rs.getString("author"));
					vo.setPublisher(rs.getString("publisher"));
					vo.setImage(rs.getString("image"));
					vo.setPrice(rs.getString("price"));
					try { vo.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e) {}
					try { vo.setAd(rs.getBoolean("is_ad")); } catch (Exception e) {}
					list.add(vo);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int updateAdStatus(long isbn, boolean isAd) {
		String sql = "UPDATE book SET is_ad = ? WHERE isbn = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setBoolean(1, isAd);
			ps.setLong(2, isbn);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<AdminVO> getAdBooks() {
		List<AdminVO> list = new ArrayList<>();
		String sql = "SELECT isbn, bookname, author, publisher, image, price, discount_rate FROM book WHERE is_ad = TRUE ORDER BY isbn";
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				AdminVO vo = new AdminVO();
				vo.setIsbn(rs.getLong("isbn"));
				vo.setBookname(rs.getString("bookname"));
				vo.setAuthor(rs.getString("author"));
				vo.setPublisher(rs.getString("publisher"));
				vo.setImage(rs.getString("image"));
				vo.setPrice(rs.getString("price"));
				try { vo.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e) {}
				vo.setAd(true);
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int updateDiscountRate(long isbn, int discountRate) {
		String sql = "UPDATE book SET discount_rate = ? WHERE isbn = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, discountRate);
			ps.setLong(2, isbn);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int deleteMember(String username) {
		String[] sqls = {
			"DELETE FROM book_like WHERE userid = ?",
			"DELETE FROM book_rating WHERE userid = ?",
			"DELETE FROM cart WHERE userid = ?",
			"DELETE FROM orders WHERE userid = ?",
			"DELETE FROM chat_message WHERE room_id = ? OR sender = ?",
			"DELETE FROM member WHERE id = ?"
		};
		int result = 0;
		for (String sql : sqls) {
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, username);
				if (sql.contains("sender = ?")) {
					ps.setString(2, username);
				}
				result = ps.executeUpdate();
			} catch (Exception e) {
				// 테이블이 아직 없을 수 있으므로 다음 단계로 계속 진행
			}
		}
		return result;
	}
}
