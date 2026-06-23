package WebBookStore.common;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

	@Autowired
	private DataSource ds;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@PostConstruct
	public void init() {
		System.out.println("[DatabaseInitializer] 전체 테이블 생성 시작");

		createMemberTable();
		insertDefaultMembers();

		createBookTable();
		createCartTable();
		createOrdersTable();
		createBookLikeTable();
		createBookRatingTable();
		createChatMessageTable();
		createQnaQuestionTable();
		createQnaAnswerTable();

		System.out.println("[DatabaseInitializer] 전체 테이블 생성 완료");
	}

	private void createMemberTable() {
		String sql = "CREATE TABLE IF NOT EXISTS member ("
				+ "num INT AUTO_INCREMENT PRIMARY KEY, "
				+ "id VARCHAR(100) NOT NULL UNIQUE, "
				+ "pw VARCHAR(100) NOT NULL, "
				+ "email VARCHAR(200), "
				+ "hp VARCHAR(30), "
				+ "nickname VARCHAR(100), "
				+ "role VARCHAR(50) DEFAULT 'ROLE_USER'"
				+ ")";

		execute(sql, "member");

		execute("ALTER TABLE member ADD COLUMN IF NOT EXISTS role VARCHAR(50) DEFAULT 'ROLE_USER'", "member role column");
		execute("UPDATE member SET role = 'ROLE_USER' WHERE role IS NULL", "member role default");
		
		// 상윤 담당 기능: 기본 배송정보 자동입력용 컬럼
		execute("ALTER TABLE member ADD COLUMN IF NOT EXISTS default_receiver VARCHAR(100)", "member default_receiver column");
		execute("ALTER TABLE member ADD COLUMN IF NOT EXISTS default_phone VARCHAR(30)", "member default_phone column");
		execute("ALTER TABLE member ADD COLUMN IF NOT EXISTS default_address VARCHAR(1000)", "member default_address column");

		// 상윤 담당 기능: 마일리지 / 회원등급 컬럼
		execute("ALTER TABLE member ADD COLUMN IF NOT EXISTS mileage INT DEFAULT 0", "member mileage column");
		execute("ALTER TABLE member ADD COLUMN IF NOT EXISTS total_mileage INT DEFAULT 0", "member total_mileage column");
		execute("ALTER TABLE member ADD COLUMN IF NOT EXISTS grade VARCHAR(30) DEFAULT 'BRONZE'", "member grade column");

		execute("UPDATE member SET mileage = 0 WHERE mileage IS NULL", "member mileage default");
		execute("UPDATE member SET total_mileage = 0 WHERE total_mileage IS NULL", "member total_mileage default");
		execute("UPDATE member SET grade = 'BRONZE' WHERE grade IS NULL", "member grade default");
	}

	private void insertDefaultMembers() {
		System.out.println("[DatabaseInitializer] 기본 회원 데이터 생성 시작");

		insertDefaultMember(
				"admin",
				"1234",
				"admin@test.com",
				"010-1111-1111",
				"관리자",
				"ROLE_ADMIN"
		);

		insertDefaultMember(
				"user",
				"1234",
				"user@test.com",
				"010-2222-2222",
				"일반사용자",
				"ROLE_USER"
		);

		System.out.println("[DatabaseInitializer] 기본 회원 데이터 생성 완료");
	}

	private void insertDefaultMember(String id, String rawPassword, String email, String hp, String nickname, String role) {
		String sql = "MERGE INTO member (id, pw, email, hp, nickname, role) "
				+ "KEY(id) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, id);
			ps.setString(2, passwordEncoder.encode(rawPassword));
			ps.setString(3, email);
			ps.setString(4, hp);
			ps.setString(5, nickname);
			ps.setString(6, role);

			ps.executeUpdate();

			System.out.println("[DatabaseInitializer] 기본 회원 확인 완료: " + id + " / " + role);

		} catch (Exception e) {
			System.out.println("[DatabaseInitializer] 기본 회원 생성 중 오류 발생: " + id);
			e.printStackTrace();
		}
	}

	private void createBookTable() {
		String sql = "CREATE TABLE IF NOT EXISTS book ("
				+ "isbn INT PRIMARY KEY, "
				+ "bookname VARCHAR(500), "
				+ "author VARCHAR(300), "
				+ "publisher VARCHAR(300), "
				+ "image VARCHAR(1000), "
				+ "price VARCHAR(50), "
				+ "category VARCHAR(100)"
				+ ")";

		execute(sql, "book");
	}

	private void createCartTable() {
		String sql = "CREATE TABLE IF NOT EXISTS cart ("
				+ "cart_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "isbn INT NOT NULL, "
				+ "amount INT DEFAULT 1"
				+ ")";

		execute(sql, "cart");
	}

	private void createOrdersTable() {
		String sql = "CREATE TABLE IF NOT EXISTS orders ("
				+ "order_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "isbn INT NOT NULL, "
				+ "bookname VARCHAR(500), "
				+ "price INT, "
				+ "amount INT, "
				+ "total_price INT, "
				+ "receiver VARCHAR(100), "
				+ "phone VARCHAR(30), "
				+ "address VARCHAR(1000), "
				+ "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "status VARCHAR(50)"
				+ ")";

		execute(sql, "orders");
		
		// 기존 OrderDAO 주문 저장 SQL에서 사용하는 배송상태 컬럼
		execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS traking_status VARCHAR(50) DEFAULT '접수'", "orders traking_status column");
		
		// 상윤 담당 기능: 주문별 마일리지 사용/적립/실결제금액 기록
		execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS used_mileage INT DEFAULT 0", "orders used_mileage column");
		execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS earned_mileage INT DEFAULT 0", "orders earned_mileage column");
		execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS final_payment INT DEFAULT 0", "orders final_payment column");
	}

	private void createBookLikeTable() {
		String sql = "CREATE TABLE IF NOT EXISTS book_like ("
				+ "like_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "isbn INT NOT NULL, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT uk_book_like UNIQUE (isbn, userid)"
				+ ")";

		execute(sql, "book_like");
	}

	private void createBookRatingTable() {
		String sql = "CREATE TABLE IF NOT EXISTS book_rating ("
				+ "rating_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "isbn INT NOT NULL, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "rating INT NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT uk_book_rating UNIQUE (isbn, userid)"
				+ ")";

		execute(sql, "book_rating");
	}

	private void createChatMessageTable() {
		String sql = "CREATE TABLE IF NOT EXISTS chat_message ("
				+ "message_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "room_id VARCHAR(100) NOT NULL, "
				+ "sender VARCHAR(100) NOT NULL, "
				+ "content VARCHAR(2000) NOT NULL, "
				+ "sent_at VARCHAR(30) NOT NULL, "
				+ "is_admin BOOLEAN DEFAULT FALSE"
				+ ")";

		execute(sql, "chat_message");
	}

	private void createQnaQuestionTable() {
		String sql = "CREATE TABLE IF NOT EXISTS qna_question ("
				+ "question_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "subject VARCHAR(200) NOT NULL, "
				+ "content VARCHAR(4000) NOT NULL, "
				+ "status VARCHAR(20) DEFAULT '답변대기', "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_at TIMESTAMP"
				+ ")";

		execute(sql, "qna_question");
	}

	private void createQnaAnswerTable() {
		String sql = "CREATE TABLE IF NOT EXISTS qna_answer ("
				+ "answer_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "question_id INT NOT NULL, "
				+ "writer VARCHAR(100) NOT NULL, "
				+ "content VARCHAR(4000) NOT NULL, "
				+ "is_admin BOOLEAN DEFAULT FALSE, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT fk_qna_answer_question "
				+ "FOREIGN KEY (question_id) "
				+ "REFERENCES qna_question(question_id) "
				+ "ON DELETE CASCADE"
				+ ")";

		execute(sql, "qna_answer");
	}

	private void execute(String sql, String tableName) {
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.execute();
			System.out.println("[DatabaseInitializer] " + tableName + " 테이블 확인 완료");

		} catch (Exception e) {
			System.out.println("[DatabaseInitializer] " + tableName + " 테이블 생성 중 오류 발생");
			e.printStackTrace();
		}
	}
}
