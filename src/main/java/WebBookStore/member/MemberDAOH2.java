package WebBookStore.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
				list.add(mapRow(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private MemberVO mapRow(ResultSet rs) throws Exception {
		return new MemberVO(rs.getInt("num"), rs.getString("id"), rs.getString("pw"), rs.getString("email"),
				rs.getString("hp"), rs.getString("nickname"));
	}
}
