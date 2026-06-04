package WebBookStore.qna;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class QnaQuestionVO {
	private int questionId;
	private String userid;
	private String subject;
	private String content;
	private String status;
	private Date createdAt;
	private Date updatedAt;

	private List<QnaAnswerVO> answerList;
}