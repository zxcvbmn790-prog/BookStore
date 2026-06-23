package WebBookStore.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDAOH2 implements MemberDAO, InitializingBean {

	private static final String MEMBER_COLUMNS = "num, id, pw, email, hp, nickname, role, "
	        + "default_receiver, default_phone, default_address, "
	        + "mileage, total_mileage, grade";
	
	@Autowired
	private DataSource ds;

	private static final String KAKAO_ID_PREFIX = "kakao_";

	@Override
	public void afterPropertiesSet() throws Exception {
		// ⚠️ role 컬럼을 포함한 테이블 생성 로직
		String sql = "CREATE TABLE IF NOT EXISTS member ("
				+ "num INT AUTO_INCREMENT PRIMARY KEY, "
				+ "id VARCHAR(100) NOT NULL UNIQUE, "
				+ "pw VARCHAR(100) NOT NULL, "
				+ "hp VARCHAR(50), "
				+ "email VARCHAR(100), "
				+ "nickname VARCHAR(100), "
				+ "role VARCHAR(50) DEFAULT 'ROLE_USER'" 
				+ ")";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public MemberVO login(String username, String password) {
		// String sql = "SELECT num, id, pw, email, hp, nickname, role FROM member WHERE id = ? AND pw = ?";
		String sql = "SELECT " + MEMBER_COLUMNS + " FROM member WHERE id = ? AND pw = ?";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int register(MemberVO mv) {
		String sql = "INSERT INTO member (id, pw, hp, email, nickname, role) VALUES (?, ?, ?, ?, ?, 'ROLE_USER')";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, mv.getUsername());
			ps.setString(2, mv.getPassword());
			ps.setString(3, mv.getPhone());
			ps.setString(4, mv.getEmail());
			ps.setString(5, mv.getNickname());
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public MemberVO findByUsername(String username) {
		// String sql = "SELECT num, id, pw, email, hp, nickname, role FROM member WHERE id = ?";
		String sql = "SELECT " + MEMBER_COLUMNS + " FROM member WHERE id = ?";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int updateProfile(MemberVO member) {
		// String sql = "UPDATE member SET nickname = ?, email = ?, hp = ? WHERE id = ?";
		String sql = "UPDATE member "
		        + "SET nickname = ?, email = ?, hp = ?, "
		        + "default_receiver = ?, default_phone = ?, default_address = ? "
		        + "WHERE id = ?";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, member.getNickname());
			ps.setString(2, member.getEmail());
			ps.setString(3, member.getPhone());
			ps.setString(4, member.getDefaultReceiver());
			ps.setString(5, member.getDefaultPhone());
			ps.setString(6, member.getDefaultAddress());
			ps.setString(7, member.getUsername());
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updatePassword(String username, String newPassword) {
		String sql = "UPDATE member SET pw = ? WHERE id = ?";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, newPassword);
			ps.setString(2, username);
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
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
		try (Connection conn = ds.getConnection()) {
			for (String sql : sqls) {
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setString(1, username);
					if (sql.contains("sender = ?")) {
						ps.setString(2, username);
					}
					result = ps.executeUpdate();
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<MemberVO> findAllMembers() {
		List<MemberVO> list = new ArrayList<>();
		// String sql = "SELECT num, id, pw, email, hp, nickname, role FROM member ORDER BY num DESC";
		String sql = "SELECT " + MEMBER_COLUMNS + " FROM member ORDER BY num DESC";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql); 
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapRow(rs)); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public MemberVO findByKakaoId(String kakaoId) {
		return findByUsername(KAKAO_ID_PREFIX + kakaoId);
	}

	@Override
	public int registerKakaoMember(KakaoUserInfo kakaoUserInfo) {
		String sql = "MERGE INTO member (id, pw, hp, email, nickname, role) KEY(id) VALUES (?, ?, ?, ?, ?, 'ROLE_USER')";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, KAKAO_ID_PREFIX + kakaoUserInfo.getKakaoId());
			ps.setString(2, "KAKAO_LOGIN_USER");
			ps.setString(3, "");
			ps.setString(4, kakaoUserInfo.getEmail());
			ps.setString(5, kakaoUserInfo.getNickname());
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public MemberVO findByUsernames(String username) {
		// String sql = "SELECT num, id, pw, email, hp, nickname, role FROM member WHERE id = ?";
		String sql = "SELECT " + MEMBER_COLUMNS + " FROM member WHERE id = ?";
		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}

	private MemberVO mapRow(ResultSet rs) throws SQLException {
		MemberVO member = new MemberVO();
		
		member.setId(rs.getInt("num"));            
		member.setUsername(rs.getString("id"));     
		member.setPassword(rs.getString("pw"));     
		member.setEmail(rs.getString("email"));
		member.setPhone(rs.getString("hp"));        
		member.setNickname(rs.getString("nickname"));
		member.setRole(rs.getString("role")); // ⚠️ DB에서 꺼낸 권한 저장
		
		member.setDefaultReceiver(rs.getString("default_receiver"));
		member.setDefaultPhone(rs.getString("default_phone"));
		member.setDefaultAddress(rs.getString("default_address"));

		member.setMileage(rs.getInt("mileage"));
		member.setTotalMileage(rs.getInt("total_mileage"));
		member.setGrade(rs.getString("grade"));
		
		return member;
	}
}