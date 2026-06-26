package WebBookStore.book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import WebBookStore.search.HangulUtil;

@Repository
public class UserBookDAO {

	@Autowired
	private Connection conn;

	public List<BookVO> findAll(String searchType, String keyword) {
		List<BookVO> list = new ArrayList<BookVO>();
		StringBuilder sql = new StringBuilder("SELECT * FROM book");

		boolean isSearch = keyword != null && !keyword.trim().isEmpty();

		if (isSearch) {
			if ("title".equals(searchType)) {
				sql.append(" WHERE bookname LIKE ?");
			} else if ("author".equals(searchType)) {
				sql.append(" WHERE author LIKE ?");
			}
		}

		sql.append(" ORDER BY isbn");

		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			if (isSearch) {
				ps.setString(1, "%" + keyword + "%");
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					BookVO book = new BookVO(rs.getLong("isbn"), rs.getString("bookname"), rs.getString("author"),
							rs.getString("publisher"), rs.getString("image"), rs.getString("price"),
							rs.getString("category"));
					try { book.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e2) {}
					list.add(book);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<BookVO> findByCategoryAndPage(String category, int offset, int limit, boolean viewAll) {
		List<BookVO> list = new ArrayList<BookVO>();
		ensureFeedbackTables();
		StringBuilder sql = new StringBuilder(
				"SELECT b.*, "
				+ "COALESCE((SELECT COUNT(*) FROM book_like bl WHERE bl.isbn = b.isbn), 0) AS like_count, "
				+ "COALESCE((SELECT AVG(br.rating) FROM book_rating br WHERE br.isbn = b.isbn), 0) AS avg_rating, "
				+ "COALESCE((SELECT COUNT(*) FROM book_rating br WHERE br.isbn = b.isbn), 0) AS rating_count "
				+ "FROM book b");

		boolean hasCategory = category != null && !category.isEmpty() && !"전체".equals(category);
		if (hasCategory) {
			sql.append(" WHERE b.category = ?");
		}
		sql.append(" ORDER BY b.isbn");

		if (!viewAll) {
			sql.append(" LIMIT ? OFFSET ?");
		}

		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			int paramIdx = 1;
			if (hasCategory) {
				ps.setString(paramIdx++, category);
			}
			if (!viewAll) {
				ps.setInt(paramIdx++, limit);
				ps.setInt(paramIdx++, offset);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					BookVO book = new BookVO(rs.getLong("isbn"), rs.getString("bookname"), rs.getString("author"),
							rs.getString("publisher"), rs.getString("image"), rs.getString("price"),
							rs.getString("category"));
					book.setLikeCount(rs.getInt("like_count"));
					book.setAverageRating(rs.getDouble("avg_rating"));
					book.setRatingCount(rs.getInt("rating_count"));
					try { book.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e2) {}
					list.add(book);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countByCategory(String category) {
		String sql = "SELECT COUNT(*) FROM book";
		boolean hasCategory = category != null && !"전체".equals(category);
		if (hasCategory) {
			sql += " WHERE category = ?";
		}

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			if (hasCategory) {
				ps.setString(1, category);
			}
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public BookVO findByIsbn(long isbn) {
		String sql = "SELECT * FROM book WHERE isbn = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, isbn);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					BookVO book = new BookVO(rs.getLong("isbn"), rs.getString("bookname"), rs.getString("author"),
							rs.getString("publisher"), rs.getString("image"), rs.getString("price"),
							rs.getString("category"));
					try { book.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e2) {}
					return book;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<BookVO> findTopNByCategory(String category, int limit) {
		List<BookVO> list = new ArrayList<>();
		ensureFeedbackTables();
		String sql = "SELECT b.*, "
				+ "COALESCE((SELECT COUNT(*) FROM book_like bl WHERE bl.isbn = b.isbn), 0) AS like_count, "
				+ "COALESCE((SELECT AVG(br.rating) FROM book_rating br WHERE br.isbn = b.isbn), 0) AS avg_rating, "
				+ "COALESCE((SELECT COUNT(*) FROM book_rating br WHERE br.isbn = b.isbn), 0) AS rating_count "
				+ "FROM book b WHERE b.category LIKE ? ORDER BY b.isbn LIMIT ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, "%" + category + "%");
			ps.setInt(2, limit);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					BookVO book = new BookVO(rs.getLong("isbn"), rs.getString("bookname"), rs.getString("author"),
							rs.getString("publisher"), rs.getString("image"), rs.getString("price"),
							rs.getString("category"));
					book.setLikeCount(rs.getInt("like_count"));
					book.setAverageRating(rs.getDouble("avg_rating"));
					book.setRatingCount(rs.getInt("rating_count"));
					try { book.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e2) {}
					list.add(book);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (list.isEmpty()) {
			System.out.println("[DEBUG] " + category + " 카테고리에 데이터가 없습니다.");
		}

		return list;
	}

	public List<BookVO> findDiscountedBooks() {
		List<BookVO> list = new ArrayList<>();
		ensureFeedbackTables();
		String sql = "SELECT b.*, "
				+ "COALESCE((SELECT COUNT(*) FROM book_like bl WHERE bl.isbn = b.isbn), 0) AS like_count, "
				+ "COALESCE((SELECT AVG(br.rating) FROM book_rating br WHERE br.isbn = b.isbn), 0) AS avg_rating, "
				+ "COALESCE((SELECT COUNT(*) FROM book_rating br WHERE br.isbn = b.isbn), 0) AS rating_count "
				+ "FROM book b WHERE b.discount_rate > 0 ORDER BY b.discount_rate DESC";

		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				BookVO book = new BookVO(rs.getLong("isbn"), rs.getString("bookname"), rs.getString("author"),
						rs.getString("publisher"), rs.getString("image"), rs.getString("price"),
						rs.getString("category"));
				book.setLikeCount(rs.getInt("like_count"));
				book.setAverageRating(rs.getDouble("avg_rating"));
				book.setRatingCount(rs.getInt("rating_count"));
				book.setDiscountRate(rs.getInt("discount_rate"));
				list.add(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private void ensureFeedbackTables() {
		String createLikeTable = "CREATE TABLE IF NOT EXISTS book_like ("
				+ "like_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "isbn BIGINT NOT NULL, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT uk_book_like UNIQUE (isbn, userid))";

		String createRatingTable = "CREATE TABLE IF NOT EXISTS book_rating ("
				+ "rating_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "isbn BIGINT NOT NULL, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "rating INT NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT uk_book_rating UNIQUE (isbn, userid))";

		try (Statement st = conn.createStatement()) {
			st.execute(createLikeTable);
			st.execute(createRatingTable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BookFeedbackVO getBookFeedback(long isbn, String loginUser) {
		String createLikeTable = "CREATE TABLE IF NOT EXISTS book_like ("
				+ "like_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "isbn BIGINT NOT NULL, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT uk_book_like UNIQUE (isbn, userid))";

		String createRatingTable = "CREATE TABLE IF NOT EXISTS book_rating ("
				+ "rating_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "isbn BIGINT NOT NULL, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "rating INT NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT uk_book_rating UNIQUE (isbn, userid))";

		String summarySql = "SELECT "
				+ "(SELECT COUNT(*) FROM book_like WHERE isbn = ?) AS like_count, "
				+ "(SELECT COALESCE(AVG(rating), 0) FROM book_rating WHERE isbn = ?) AS avg_rating, "
				+ "(SELECT COUNT(*) FROM book_rating WHERE isbn = ?) AS rating_count";

		String likedSql = "SELECT COUNT(*) FROM book_like WHERE isbn = ? AND userid = ?";
		String myRatingSql = "SELECT rating FROM book_rating WHERE isbn = ? AND userid = ?";

		ensureFeedbackTables();
		try {
			BookFeedbackVO feedback = new BookFeedbackVO(0, 0.0, 0, null, false);

			try (PreparedStatement ps = conn.prepareStatement(summarySql)) {
				ps.setLong(1, isbn);
				ps.setLong(2, isbn);
				ps.setLong(3, isbn);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						feedback.setLikeCount(rs.getInt("like_count"));
						feedback.setAverageRating(rs.getDouble("avg_rating"));
						feedback.setRatingCount(rs.getInt("rating_count"));
					}
				}
			}

			if (loginUser != null && !loginUser.trim().isEmpty()) {
				try (PreparedStatement ps = conn.prepareStatement(likedSql)) {
					ps.setLong(1, isbn);
					ps.setString(2, loginUser);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							feedback.setLiked(rs.getInt(1) > 0);
						}
					}
				}

				try (PreparedStatement ps = conn.prepareStatement(myRatingSql)) {
					ps.setLong(1, isbn);
					ps.setString(2, loginUser);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							feedback.setMyRating(rs.getInt("rating"));
						}
					}
				}
			}

			return feedback;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new BookFeedbackVO(0, 0.0, 0, null, false);
	}

	public boolean toggleLike(long isbn, String userid) {
		String createLikeTable = "CREATE TABLE IF NOT EXISTS book_like ("
				+ "like_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "isbn BIGINT NOT NULL, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT uk_book_like UNIQUE (isbn, userid))";

		String checkSql = "SELECT COUNT(*) FROM book_like WHERE isbn = ? AND userid = ?";
		String deleteSql = "DELETE FROM book_like WHERE isbn = ? AND userid = ?";
		String insertSql = "INSERT INTO book_like (isbn, userid) VALUES (?, ?)";

		ensureFeedbackTables();
		try {
			boolean alreadyLiked = false;
			try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
				ps.setLong(1, isbn);
				ps.setString(2, userid);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						alreadyLiked = rs.getInt(1) > 0;
					}
				}
			}

			try (PreparedStatement ps = conn.prepareStatement(alreadyLiked ? deleteSql : insertSql)) {
				ps.setLong(1, isbn);
				ps.setString(2, userid);
				return ps.executeUpdate() > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean saveRating(long isbn, String userid, int rating) {
		String mergeSql = "MERGE INTO book_rating (isbn, userid, rating, updated_at) KEY (isbn, userid) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

		ensureFeedbackTables();
		try (PreparedStatement ps = conn.prepareStatement(mergeSql)) {
			ps.setLong(1, isbn);
			ps.setString(2, userid);
			ps.setInt(3, rating);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public boolean deleteRating(long isbn, String userid) {
		String sql = "DELETE FROM book_rating WHERE isbn = ? AND userid = ?";
		ensureFeedbackTables();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, isbn);
			ps.setString(2, userid);
			ps.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<String> findAllBookNames() {
		List<String> titles = new ArrayList<String>();
		String sql = "SELECT bookname FROM book WHERE bookname IS NOT NULL ORDER BY bookname";

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				titles.add(rs.getString("bookname"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return titles;
	}

	public List<BookVO> searchByBookName(String keyword) {
		List<BookVO> list = new ArrayList<BookVO>();
		ensureFeedbackTables();

		String word = keyword == null ? "" : keyword.trim().toLowerCase();
		if (word.isEmpty()) return list;

		String sql = "SELECT b.*, "
				+ "COALESCE((SELECT COUNT(*) FROM book_like bl WHERE bl.isbn = b.isbn), 0) AS like_count, "
				+ "COALESCE((SELECT AVG(br.rating) FROM book_rating br WHERE br.isbn = b.isbn), 0) AS avg_rating, "
				+ "COALESCE((SELECT COUNT(*) FROM book_rating br WHERE br.isbn = b.isbn), 0) AS rating_count "
				+ "FROM book b ORDER BY b.bookname";

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				BookVO book = new BookVO(rs.getLong("isbn"), rs.getString("bookname"), rs.getString("author"),
						rs.getString("publisher"), rs.getString("image"), rs.getString("price"),
						rs.getString("category"));
				book.setLikeCount(rs.getInt("like_count"));
				book.setAverageRating(rs.getDouble("avg_rating"));
				book.setRatingCount(rs.getInt("rating_count"));

				if (isSearchMatched(book, word)) {
					list.add(book);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	private boolean isSearchMatched(BookVO book, String word) {
		String title = book.getBookname() == null ? "" : book.getBookname().toLowerCase();
		String author = book.getAuthor() == null ? "" : book.getAuthor().toLowerCase();
		String publisher = book.getPublisher() == null ? "" : book.getPublisher().toLowerCase();
		String category = book.getCategory() == null ? "" : book.getCategory().toLowerCase();

		if (title.contains(word) || author.contains(word) || publisher.contains(word) || category.contains(word)) {
			return true;
		}

		if (HangulUtil.hasChosung(word)) {
			String chosungTitle = HangulUtil.toChosung(title);
			return chosungTitle.contains(word);
		}

		return false;
	}

	public List<BookVO> findAdBooks() {
		List<BookVO> list = new ArrayList<>();
		String sql = "SELECT * FROM book WHERE is_ad = TRUE ORDER BY isbn";
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				BookVO book = new BookVO(rs.getLong("isbn"), rs.getString("bookname"), rs.getString("author"),
						rs.getString("publisher"), rs.getString("image"), rs.getString("price"),
						rs.getString("category"));
				try { book.setDiscountRate(rs.getInt("discount_rate")); } catch (Exception e2) {}
				list.add(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
