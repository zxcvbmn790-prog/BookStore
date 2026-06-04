package WebBookStore.qna;

import java.util.Date;

import lombok.Data;

@Data
public class QnaAnswerVO {
	private int answerId;
	private int questionId;
	private String writer;
	private String content;
	private boolean admin;
	private Date createdAt;
}