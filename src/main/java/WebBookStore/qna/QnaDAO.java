package WebBookStore.qna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class QnaDAO {

	@Autowired
	private DataSource ds;

	public List<QnaQuestionVO> findAll() {
		List<QnaQuestionVO> list = new ArrayList<QnaQuestionVO>();

		String sql = "SELECT question_id, userid, subject, content, status, created_at, updated_at "
				+ "FROM qna_question "
				+ "ORDER BY question_id DESC";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(toQuestionVO(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public QnaQuestionVO findById(int questionId) {
		String sql = "SELECT question_id, userid, subject, content, status, created_at, updated_at "
				+ "FROM qna_question "
				+ "WHERE question_id = ?";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, questionId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return toQuestionVO(rs);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<QnaAnswerVO> findAnswersByQuestionId(int questionId) {
		List<QnaAnswerVO> list = new ArrayList<QnaAnswerVO>();

		String sql = "SELECT answer_id, question_id, writer, content, is_admin, created_at "
				+ "FROM qna_answer "
				+ "WHERE question_id = ? "
				+ "ORDER BY answer_id ASC";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, questionId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(toAnswerVO(rs));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int insertQuestion(QnaQuestionVO question) {
		String sql = "INSERT INTO qna_question (userid, subject, content) "
				+ "VALUES (?, ?, ?)";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, question.getUserid());
			ps.setString(2, question.getSubject());
			ps.setString(3, question.getContent());

			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public int insertAnswer(QnaAnswerVO answer) {
		String sql = "INSERT INTO qna_answer (question_id, writer, content, is_admin) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, answer.getQuestionId());
			ps.setString(2, answer.getWriter());
			ps.setString(3, answer.getContent());
			ps.setBoolean(4, answer.isAdmin());

			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public int updateStatusAnswered(int questionId) {
		String sql = "UPDATE qna_question "
				+ "SET status = '답변완료', updated_at = CURRENT_TIMESTAMP "
				+ "WHERE question_id = ?";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, questionId);
			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public int deleteQuestion(int questionId, String userid) {
		String sql = "DELETE FROM qna_question "
				+ "WHERE question_id = ? AND userid = ?";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, questionId);
			ps.setString(2, userid);

			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public int deleteQuestionByAdmin(int questionId) {
		String sql = "DELETE FROM qna_question "
				+ "WHERE question_id = ?";

		try (Connection conn = ds.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, questionId);
			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	private QnaQuestionVO toQuestionVO(ResultSet rs) throws Exception {
		QnaQuestionVO vo = new QnaQuestionVO();

		vo.setQuestionId(rs.getInt("question_id"));
		vo.setUserid(rs.getString("userid"));
		vo.setSubject(rs.getString("subject"));
		vo.setContent(rs.getString("content"));
		vo.setStatus(rs.getString("status"));
		vo.setCreatedAt(rs.getTimestamp("created_at"));
		vo.setUpdatedAt(rs.getTimestamp("updated_at"));

		return vo;
	}

	private QnaAnswerVO toAnswerVO(ResultSet rs) throws Exception {
		QnaAnswerVO vo = new QnaAnswerVO();

		vo.setAnswerId(rs.getInt("answer_id"));
		vo.setQuestionId(rs.getInt("question_id"));
		vo.setWriter(rs.getString("writer"));
		vo.setContent(rs.getString("content"));
		vo.setAdmin(rs.getBoolean("is_admin"));
		vo.setCreatedAt(rs.getTimestamp("created_at"));

		return vo;
	}
}