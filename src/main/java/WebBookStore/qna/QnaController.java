package WebBookStore.qna;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/qna")
public class QnaController {

	@Autowired
	private QnaService qnaService;

	@RequestMapping("/list")
	public String list(Model model) {
		model.addAttribute("qnaList", qnaService.getQuestionList());
		model.addAttribute("contentPage", "/WEB-INF/views/qna/list.jsp");
		return "layout/layout";
	}

	@RequestMapping("/view")
	public String view(@RequestParam("questionId") int questionId,
					   Model model,
					   RedirectAttributes ra) {

		QnaQuestionVO question = qnaService.getQuestion(questionId);

		if (question == null) {
			ra.addFlashAttribute("qnaError", "존재하지 않는 문의입니다.");
			return "redirect:/qna/list";
		}

		model.addAttribute("question", question);
		model.addAttribute("contentPage", "/WEB-INF/views/qna/view.jsp");

		return "layout/layout";
	}

	@RequestMapping(value = "/write", method = RequestMethod.GET)
	public String writeForm(HttpSession session, Model model, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");

		if (loginUser == null || "admin".equals(loginUser)) {
			ra.addFlashAttribute("authError", "일반 회원으로 로그인 후 문의를 작성할 수 있습니다.");
			return "redirect:/member/login";
		}

		model.addAttribute("contentPage", "/WEB-INF/views/qna/write.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public String write(QnaQuestionVO question,
						HttpSession session,
						RedirectAttributes ra) {

		String loginUser = (String) session.getAttribute("loginUser");

		if (loginUser == null || "admin".equals(loginUser)) {
			ra.addFlashAttribute("authError", "일반 회원으로 로그인 후 문의를 작성할 수 있습니다.");
			return "redirect:/member/login";
		}

		if (question.getSubject() == null || question.getSubject().trim().isEmpty()
				|| question.getContent() == null || question.getContent().trim().isEmpty()) {
			ra.addFlashAttribute("qnaError", "제목과 내용을 모두 입력해주세요.");
			return "redirect:/qna/write";
		}

		question.setUserid(loginUser);

		if (qnaService.writeQuestion(question)) {
			ra.addFlashAttribute("qnaMessage", "문의가 등록되었습니다.");
			return "redirect:/qna/list";
		}

		ra.addFlashAttribute("qnaError", "문의 등록에 실패했습니다.");
		return "redirect:/qna/write";
	}

	@RequestMapping(value = "/answer", method = RequestMethod.POST)
	public String answer(QnaAnswerVO answer,
						 HttpSession session,
						 RedirectAttributes ra) {

		String loginUser = (String) session.getAttribute("loginUser");

		if (!"admin".equals(loginUser)) {
			ra.addFlashAttribute("qnaError", "관리자만 답변을 등록할 수 있습니다.");
			return "redirect:/qna/list";
		}

		if (answer.getContent() == null || answer.getContent().trim().isEmpty()) {
			ra.addFlashAttribute("qnaError", "답변 내용을 입력해주세요.");
			return "redirect:/qna/view?questionId=" + answer.getQuestionId();
		}

		answer.setWriter(loginUser);
		answer.setAdmin(true);

		if (qnaService.writeAnswer(answer)) {
			ra.addFlashAttribute("qnaMessage", "답변이 등록되었습니다.");
		} else {
			ra.addFlashAttribute("qnaError", "답변 등록에 실패했습니다.");
		}

		return "redirect:/qna/view?questionId=" + answer.getQuestionId();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(@RequestParam("questionId") int questionId,
						 HttpSession session,
						 RedirectAttributes ra) {

		String loginUser = (String) session.getAttribute("loginUser");

		if (loginUser == null) {
			ra.addFlashAttribute("authError", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}

		boolean admin = "admin".equals(loginUser);

		if (qnaService.deleteQuestion(questionId, loginUser, admin)) {
			ra.addFlashAttribute("qnaMessage", "문의가 삭제되었습니다.");
		} else {
			ra.addFlashAttribute("qnaError", "삭제 권한이 없거나 삭제에 실패했습니다.");
		}

		return "redirect:/qna/list";
	}
}