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
		insertDummyOrders();
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
				+ "hp VARCHAR(50), " // 30 -> 50로 맞춤
				+ "email VARCHAR(100), " // 200 -> 100로 맞춤
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

		insertDefaultMember(
				"tracking",
				"1234",
				"tracking@test.com",
				"010-3333-3333",
				"배송관리자",
				"ROLE_TRAKING"
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
				+ "isbn BIGINT PRIMARY KEY, "
				+ "bookname VARCHAR(500), "
				+ "author VARCHAR(300), "
				+ "publisher VARCHAR(300), "
				+ "image VARCHAR(1000), "
				+ "price VARCHAR(50), "
				+ "category VARCHAR(100)"
				+ ")";

		execute(sql, "book");
		execute("ALTER TABLE book ADD COLUMN IF NOT EXISTS discount_rate INT DEFAULT 0", "book discount_rate column");
		execute("ALTER TABLE book ADD COLUMN IF NOT EXISTS is_ad BOOLEAN DEFAULT FALSE", "book is_ad column");
	}

	private void createCartTable() {
		String sql = "CREATE TABLE IF NOT EXISTS cart ("
				+ "cart_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "isbn BIGINT NOT NULL, "
				+ "amount INT DEFAULT 1"
				+ ")";

		execute(sql, "cart");
	}

	private void createOrdersTable() {
		String sql = "CREATE TABLE IF NOT EXISTS orders ("
				+ "order_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "isbn BIGINT NOT NULL, "
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
				+ "isbn BIGINT NOT NULL, "
				+ "userid VARCHAR(100) NOT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "CONSTRAINT uk_book_like UNIQUE (isbn, userid)"
				+ ")";

		execute(sql, "book_like");
	}

	private void createBookRatingTable() {
		String sql = "CREATE TABLE IF NOT EXISTS book_rating ("
				+ "rating_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "isbn BIGINT NOT NULL, "
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

	private void insertDummyOrders() {
		try (Connection conn = ds.getConnection();
			 PreparedStatement countPs = conn.prepareStatement("SELECT COUNT(*) FROM orders");
			 java.sql.ResultSet rs = countPs.executeQuery()) {
			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("[DatabaseInitializer] 주문 더미 데이터 이미 존재 - 건너뜀");
				return;
			}
		} catch (Exception e) {
			System.out.println("[DatabaseInitializer] 주문 수 확인 오류: " + e.getMessage());
			return;
		}

		System.out.println("[DatabaseInitializer] 판매통계 더미 데이터 생성 시작");

		long[]   isbns     = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
		String[] booknames = {
			"박태웅의 AI 강의 2026", "이게 되네? 제미나이 완전 미친 활용법 81제",
			"하루 30분, 나는 제미나이로 돈을 번다", "옵시디언 프로페셔널 노트",
			"혼자 공부하는 바이브 코딩 with 클로드 코드", "오픈클로 with GPT",
			"클로드 코드를 활용한 바이브 코딩 완벽 입문", "요즘 바이브 코딩 안티그래비티 완벽 가이드",
			"AI 최전선", "한 권으로 끝내는 AI 활용"
		};
		int[]    prices    = {20700, 21600, 17100, 22500, 27000, 19800, 23400, 25200, 16020, 18000};
		String[] users     = {"dummy_user1", "dummy_user2", "dummy_user3", "dummy_user4", "dummy_user5"};
		String[] receivers = {"김민준", "이서연", "박지호", "최수아", "정예준"};
		String[] phones    = {"010-1234-5678", "010-2345-6789", "010-3456-7890", "010-4567-8901", "010-5678-9012"};
		String[] addresses = {
			"서울시 강남구 테헤란로 123", "서울시 마포구 홍대입구로 45",
			"부산시 해운대구 해운대로 78", "경기도 성남시 분당구 판교로 56",
			"서울시 서초구 강남대로 200"
		};

		java.util.Random rand = new java.util.Random(2024L);
		int total = 0;

		// 최근 7일: 하루 3~5건
		for (int day = 0; day >= -6; day--) {
			int cnt = 3 + rand.nextInt(3);
			for (int i = 0; i < cnt; i++)
				total += insertOneOrder(isbns, booknames, prices, users, receivers, phones, addresses, day, rand);
		}

		// 2~8주 전: 주당 8~12건
		for (int week = 1; week <= 7; week++) {
			int cnt = 8 + rand.nextInt(5);
			for (int i = 0; i < cnt; i++) {
				int off = -(7 + week * 7 - rand.nextInt(6));
				total += insertOneOrder(isbns, booknames, prices, users, receivers, phones, addresses, off, rand);
			}
		}

		// 3~12개월 전: 월당 20~35건
		for (int month = 2; month <= 11; month++) {
			int cnt = 20 + rand.nextInt(16);
			for (int i = 0; i < cnt; i++) {
				int off = -(57 + month * 30 - rand.nextInt(29));
				total += insertOneOrder(isbns, booknames, prices, users, receivers, phones, addresses, off, rand);
			}
		}

		// 1~4년 전: 연당 60~90건
		for (int year = 1; year <= 4; year++) {
			int cnt = 60 + rand.nextInt(31);
			for (int i = 0; i < cnt; i++) {
				int off = -(365 * year + rand.nextInt(364));
				total += insertOneOrder(isbns, booknames, prices, users, receivers, phones, addresses, off, rand);
			}
		}

		System.out.println("[DatabaseInitializer] 판매통계 더미 데이터 생성 완료: " + total + "건");
	}

	private int insertOneOrder(long[] isbns, String[] booknames, int[] prices,
			String[] users, String[] receivers, String[] phones, String[] addresses,
			int dayOffset, java.util.Random rand) {
		String sql = "INSERT INTO orders "
				+ "(userid, isbn, bookname, price, amount, total_price, receiver, phone, address, "
				+ " order_date, status, traking_status, earned_mileage, final_payment) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, DATEADD(DAY, ?, CURRENT_TIMESTAMP), "
				+ "'배송완료', '배송완료', ?, ?)";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			int bi = rand.nextInt(isbns.length);
			int ui = rand.nextInt(users.length);
			int amount = 1 + rand.nextInt(3);
			int price  = prices[bi];
			int total  = price * amount;
			int earned = (int)(total * 0.05);

			ps.setString(1,  users[ui]);
			ps.setLong(2,    isbns[bi]);
			ps.setString(3,  booknames[bi]);
			ps.setInt(4,     price);
			ps.setInt(5,     amount);
			ps.setInt(6,     total);
			ps.setString(7,  receivers[ui]);
			ps.setString(8,  phones[ui]);
			ps.setString(9,  addresses[ui]);
			ps.setInt(10,    dayOffset);
			ps.setInt(11,    earned);
			ps.setInt(12,    total);
			ps.executeUpdate();
			return 1;
		} catch (Exception e) {
			return 0;
		}
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