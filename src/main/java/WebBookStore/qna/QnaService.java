package WebBookStore.qna;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QnaService {

	@Autowired
	private QnaDAO qnaDAO;

	public List<QnaQuestionVO> getQuestionList() {
		return qnaDAO.findAll();
	}

	public QnaQuestionVO getQuestion(int questionId) {
		QnaQuestionVO question = qnaDAO.findById(questionId);

		if (question != null) {
			question.setAnswerList(qnaDAO.findAnswersByQuestionId(questionId));
		}

		return question;
	}

	public boolean writeQuestion(QnaQuestionVO question) {
		return qnaDAO.insertQuestion(question) > 0;
	}

	public boolean writeAnswer(QnaAnswerVO answer) {
		boolean result = qnaDAO.insertAnswer(answer) > 0;

		if (result) {
			qnaDAO.updateStatusAnswered(answer.getQuestionId());
		}

		return result;
	}

	public boolean deleteQuestion(int questionId, String userid, boolean admin) {
		if (admin) {
			return qnaDAO.deleteQuestionByAdmin(questionId) > 0;
		}

		return qnaDAO.deleteQuestion(questionId, userid) > 0;
	}
}