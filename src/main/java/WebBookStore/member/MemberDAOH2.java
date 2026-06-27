package WebBookStore.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDAOH2 implements MemberDAO {

	@Autowired
	Connection conn;

	private static final String KAKAO_ID_PREFIX = "kakao_";

	@Override
	public MemberVO login(String username, String password) {
		String sql = "SELECT num, id, pw, email, hp, nickname FROM member WHERE id = ? AND pw = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

	// ⚠️ [수정됨] 인터페이스 변경에 맞춰 파라미터를 2개로 줄이고 쿼리문을 수정했습니다.
	@Override
	public int updatePassword(String username, String newPassword) {
		String sql = "UPDATE member SET pw = ? WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
				list.add(mapRow(rs)); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// feature_kakaoLogin 브랜치: 카카오 관련 메서드 추가
	@Override
	public MemberVO findByKakaoId(String kakaoId) {
		return findByUsername(KAKAO_ID_PREFIX + kakaoId);
	}

	@Override
	public int registerKakaoMember(KakaoUserInfo kakaoUserInfo) {
		String sql = "MERGE INTO member (id, pw, hp, email, nickname) KEY(id) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
	
	// dev 브랜치: 다수 유저 검색 (또는 오타로 생성된) 메서드 추가
	@Override
	public MemberVO findByUsernames(String username) {
		String sql = "SELECT num, id, pw, email, hp, nickname FROM member WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

	// dev 브랜치 채택: 안전한 Setter 방식의 mapRow 유지 (중복 생성된 메서드는 삭제함)
	private MemberVO mapRow(ResultSet rs) throws SQLException {
		MemberVO member = new MemberVO();
		
		member.setId(rs.getInt("num"));            
		member.setUsername(rs.getString("id"));     
		member.setPassword(rs.getString("pw"));     
		member.setEmail(rs.getString("email"));
		member.setPhone(rs.getString("hp"));        
		member.setNickname(rs.getString("nickname"));
		
		return member;
	}
}