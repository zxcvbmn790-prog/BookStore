package WebBookStore.book;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;

@Component
public class BookCsvInitializer {

	@Autowired
	private DataSource ds;

	@PostConstruct
	public void init() {
		try {
			createBookTableIfNotExists();

			if (countBooks() > 0) {
				System.out.println("[BookCsvInitializer] book 테이블에 이미 데이터가 있어서 CSV 적재를 건너뜁니다.");
				return;
			}

			InputStream is = getClass().getClassLoader().getResourceAsStream("data/books.csv");

			if (is == null) {
				System.out.println("[BookCsvInitializer] data/books.csv 파일을 찾을 수 없습니다.");
				return;
			}

			try (CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

				String[] arr;
				boolean firstLine = true;

				while ((arr = reader.readNext()) != null) {
					if (firstLine) {
						firstLine = false;
						continue;
					}
					if (arr.length < 7) { // 💡 컬럼이 7개(category 포함)인지 확인
						continue;
					}

					int isbn = Integer.parseInt(arr[0].trim());
					String bookname = arr[1].trim();
					String author = arr[2].trim();
					String publisher = arr[3].trim();
					String image = arr[4].trim();
					String price = arr[5].trim();
					String category = arr[6].trim(); // 💡 카테고리 파싱

					insertBook(isbn, bookname, author, publisher, image, price, category);
				}
			}

			System.out.println("[BookCsvInitializer] CSV 초기 데이터 적재 완료");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createBookTableIfNotExists() throws Exception {
		String sql = "CREATE TABLE IF NOT EXISTS book (" + "isbn INT PRIMARY KEY, " + "bookname VARCHAR(500), "
				+ "author VARCHAR(300), " + "publisher VARCHAR(300), " + "image VARCHAR(1000), " + "price VARCHAR(50), "
				+ "category VARCHAR(100)" // 💡 카테고리 추가
				+ ")";

		try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.execute();
		}
	}

	private int countBooks() throws Exception {
		String sql = "SELECT COUNT(*) FROM book";

		try (Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			rs.next();
			return rs.getInt(1);
		}
	}

	private void insertBook(int isbn, String bookname, String author, String publisher, String image, String price,
			String category) throws Exception {
		String sql = "INSERT INTO book (isbn, bookname, author, publisher, image, price, category) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, isbn);
			ps.setString(2, bookname);
			ps.setString(3, author);
			ps.setString(4, publisher);
			ps.setString(5, image);
			ps.setString(6, price);
			ps.setString(7, category); // 💡 세팅
			ps.executeUpdate();
		}
	}
}