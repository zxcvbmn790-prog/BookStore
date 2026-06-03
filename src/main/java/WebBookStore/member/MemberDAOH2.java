//package WebBookStore.member;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class MemberDAOH2 implements MemberDAO {
//
//	@Autowired
//	Connection conn;
//
//	@Override
//	public MemberVO login(String username, String password) {
//		// Spring Security 도입 후 이 메소드는 주로 사용되지 않지만, 기존 코드 호환을 위해 유지합니다.
//		String sql = "SELECT num, id, pw, email, hp, nickname, role FROM member WHERE id = ? AND pw = ?";
//		try (PreparedStatement ps = conn.prepareStatement(sql)) {
//			ps.setString(1, username);
//			ps.setString(2, password);
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					return mapRow(rs);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	@Override
//	public int register(MemberVO mv) {
//		// 회원가입 시 권한(role)도 함께 저장하도록 쿼리 확장 (DB에 role 컬럼이 존재해야 합니다)
//		String sql = "INSERT INTO member (id, pw, hp, email, nickname, role) VALUES (?, ?, ?, ?, ?, ?)";
//		try (PreparedStatement ps = conn.prepareStatement(sql)) {
//			ps.setString(1, mv.getUsername());
//			ps.setString(2, mv.getPassword());
//			ps.setString(3, mv.getPhone());
//			ps.setString(4, mv.getEmail());
//			ps.setString(5, mv.getNickname());
//			ps.setString(6, mv.getRole());
//			return ps.executeUpdate();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//
//	@Override
//	public MemberVO findByUsername(String username) {
//		String sql = "SELECT num, id, pw, email, hp, nickname, role FROM member WHERE id = ?";
//		try (PreparedStatement ps = conn.prepareStatement(sql)) {
//			ps.setString(1, username);
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					return mapRow(rs);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	@Override
//	public int updateProfile(MemberVO member) {
//		String sql = "UPDATE member SET nickname = ?, email = ?, hp = ? WHERE id = ?";
//		try (PreparedStatement ps = conn.prepareStatement(sql)) {
//			ps.setString(1, member.getNickname());
//			ps.setString(2, member.getEmail());
//			ps.setString(3, member.getPhone());
//			ps.setString(4, member.getUsername());
//			return ps.executeUpdate();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//
//	@Override
//	public int updatePassword(String username, String newPassword) {
//		// ★ 중요: 암호화된 비밀번호는 SQL에서 직접 비교할 수 없으므로 id 조건만으로 업데이트합니다.
//		String sql = "UPDATE member SET pw = ? WHERE id = ?";
//		try (PreparedStatement ps = conn.prepareStatement(sql)) {
//			ps.setString(1, newPassword);
//			ps.setString(2, username);
//			return ps.executeUpdate();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//
//	@Override
//	public int deleteMember(String username) {
//		String[] sqls = {
//			"DELETE FROM book_like WHERE userid = ?",
//			"DELETE FROM book_rating WHERE userid = ?",
//			"DELETE FROM cart WHERE userid = ?",
//			"DELETE FROM orders WHERE userid = ?",
//			"DELETE FROM chat_message WHERE room_id = ? OR sender = ?",
//			"DELETE FROM member WHERE id = ?"
//		};
//		int result = 0;
//		for (String sql : sqls) {
//			try (PreparedStatement ps = conn.prepareStatement(sql)) {
//				ps.setString(1, username);
//				if (sql.contains("sender = ?")) {
//					ps.setString(2, username);
//				}
//				result = ps.executeUpdate();
//			} catch (Exception e) {
//				// 테이블이 없는 등의 예외는 유연하게 넘김
//			}
//		}
//		return result;
//	}
//
//	@Override
//	public List<MemberVO> findAllMembers() {
//		List<MemberVO> list = new ArrayList<>();
//		String sql = "SELECT num, id, pw, email, hp, nickname, role FROM member ORDER BY num DESC";
//		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
//			while (rs.next()) {
//				list.add(mapRow(rs));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
//
//	private MemberVO mapRow(ResultSet rs) throws Exception {
//		// 만약 DB 테이블에 아직 role 컬럼을 추가하지 않았다면 예외가 발생할 수 있습니다.
//		// 예외를 방지하기 위해 사용중인 H2 DB의 member 테이블에 [ALTER TABLE member ADD COLUMN role VARCHAR(20);] 명령을 실행해 주는 것이 좋습니다.
//		String role = "ROLE_USER"; 
//		try {
//			role = rs.getString("role");
//			if(role == null || role.isEmpty()) role = "ROLE_USER";
//		} catch (Exception e) {
//			// DB에 컬럼이 없는 과도기 단계를 위한 예외 처리 코드
//			role = "ROLE_USER";
//		}
//
//		return new MemberVO(
//			rs.getInt("num"), 
//			rs.getString("id"), 
//			rs.getString("pw"), 
//			rs.getString("email"),
//			rs.getString("hp"), 
//			rs.getString("nickname"),
//			role
//		);
//	}
//}


package WebBookStore.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // ◀ 정확한 SQL 패키지 임포트 보장
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDAOH2 implements MemberDAO {

	@Autowired
	Connection conn;

	@Override
	public MemberVO login(String username, String password) {
		String sql = "SELECT num, id, pw, email, hp, nickname FROM member WHERE id = ? AND pw = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs); // ◀ 파라미터 1개짜리 호출 유지
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int register(MemberVO mv) {
		String sql = "INSERT INTO member (id, pw, hp, email, nickname) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
		String sql = "SELECT num, id, pw, email, hp, nickname FROM member WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs); // ◀ 파라미터 1개짜리 호출 유지
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int updateProfile(MemberVO member) {
		String sql = "UPDATE member SET nickname = ?, email = ?, hp = ? WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, member.getNickname());
			ps.setString(2, member.getEmail());
			ps.setString(3, member.getPhone());
			ps.setString(4, member.getUsername());
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updatePassword(String username, String currentPassword, String newPassword) {
		String sql = "UPDATE member SET pw = ? WHERE id = ? AND pw = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, newPassword);
			ps.setString(2, username);
			ps.setString(3, currentPassword);
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

	@Override
	public List<MemberVO> findAllMembers() {
		List<MemberVO> list = new ArrayList<>();
		String sql = "SELECT num, id, pw, email, hp, nickname FROM member ORDER BY num DESC";
		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapRow(rs)); // ◀ 파라미터 1개짜리 호출 유지
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 내부 매핑 헬퍼 메서드 (기존 쿼리문 컬럼명과 MemberVO의 Setter 완벽 매칭)
	 */
	private MemberVO mapRow(ResultSet rs) throws SQLException {
		MemberVO member = new MemberVO();
		
		// DB 조회 컬럼(num, id, pw, email, hp, nickname) 명칭에 맞춰 Setter 주입
		member.setId(rs.getInt("num"));            // 주로 primary key 번호는 num 또는 id에 매핑됩니다.
		member.setUsername(rs.getString("id"));     // DB의 id -> username
		member.setPassword(rs.getString("pw"));     // DB의 pw -> password
		member.setEmail(rs.getString("email"));
		member.setPhone(rs.getString("hp"));        // DB의 hp -> phone
		member.setNickname(rs.getString("nickname"));
		
		return member;
	}
	
	@Override
	public MemberVO findByUsernames(String username) {
		String sql = "SELECT num, id, pw, email, hp, nickname FROM member WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				// ◀ 중요: rs.next()가 참일 때만 객체를 만들고, 데이터가 없으면 'null'을 반환하게 못 박습니다.
				if (rs.next()) {
					return mapRow(rs); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // ◀ 데이터가 없으면 완벽하게 null이 반환됩니다.
	}

	/**
	 * 내부 매핑 헬퍼 메서드
	 * rs.next() 검증이 완료된 데이터만 들어오므로 안전하게 매핑을 진행합니다.
	 */
	private MemberVO mapRow(ResultSet rs) throws SQLException {
		MemberVO member = new MemberVO();
		
		// DB 조회 컬럼 매핑
		member.setId(rs.getInt("num"));            
		member.setUsername(rs.getString("id"));     
		member.setPassword(rs.getString("pw"));     
		member.setEmail(rs.getString("email"));
		member.setPhone(rs.getString("hp"));        
		member.setNickname(rs.getString("nickname"));
		
		return member;
	}
	
}