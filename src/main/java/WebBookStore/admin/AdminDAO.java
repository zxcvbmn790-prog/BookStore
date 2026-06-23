package WebBookStore.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
				list.add(new AdminVO(rs.getInt("isbn"), rs.getString("bookname"), rs.getString("author"),
						rs.getString("publisher"), rs.getString("image"), rs.getString("price")));
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
		String sql = "INSERT INTO book VALUES (?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, book.getIsbn());
			ps.setString(2, book.getBookname());
			ps.setString(3, book.getAuthor());
			ps.setString(4, book.getPublisher());
			ps.setString(5, book.getImage());
			ps.setString(6, book.getPrice());
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
		String sql = "UPDATE book SET bookname=?, author=?, publisher=?, image=?, price=? WHERE isbn=?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, book.getBookname());
			ps.setString(2, book.getAuthor());
			ps.setString(3, book.getPublisher());
			ps.setString(4, book.getImage());
			ps.setString(5, book.getPrice());
			ps.setInt(6, book.getIsbn());
			int result = ps.executeUpdate();
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int delete(int isbn) {
		String sqlCart = "DELETE FROM cart WHERE isbn = ?";
		String sqlBook = "DELETE FROM book WHERE isbn = ?";

		try {
			PreparedStatement psCart = conn.prepareStatement(sqlCart);
			psCart.setInt(1, isbn);
			psCart.executeUpdate();
			psCart.close();

			PreparedStatement psBook = conn.prepareStatement(sqlBook);
			psBook.setInt(1, isbn);
			int result = psBook.executeUpdate();
			psBook.close();

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 단건 조회
	public AdminVO findById(int isbn) {
		String sql = "SELECT * FROM book WHERE isbn = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, isbn);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				AdminVO book = new AdminVO(rs.getInt("isbn"), rs.getString("bookname"), rs.getString("author"),
						rs.getString("publisher"), rs.getString("image"), rs.getString("price"));
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
	            vo.setIsbn(rs.getInt("isbn"));
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


	public List<WebBookStore.member.MemberVO> getMemberList() {
		List<WebBookStore.member.MemberVO> list = new ArrayList<>();
		String sql = "SELECT num, id, pw, email, hp, nickname, role FROM member ORDER BY num DESC";
		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(new WebBookStore.member.MemberVO(rs.getInt("num"), rs.getString("id"), rs.getString("pw"), rs.getString("email"), rs.getString("hp"), rs.getString("nickname"), rs.getString("ROLE")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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
