package WebBookStore.book;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import WebBookStore.search.BookSearchService;

@Controller
@RequestMapping("/book")
public class UserBookController {

	@Autowired
	private UserBookService bookService;

	@Autowired
	private BookSearchService bookSearchService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(@RequestParam(value = "category", defaultValue = "전체") String category,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "viewAll", defaultValue = "false") boolean viewAll, Model model) {

		if ("전체".equals(category) && page == 1 && !viewAll) {
			model.addAttribute("itBooks", bookService.getTopBooksByCategory("인공지능"));
			model.addAttribute("novelBooks", bookService.getTopBooksByCategory("초보자를 위한 컴퓨터 책"));
			model.addAttribute("economyBooks", bookService.getTopBooksByCategory("경영전략/혁신"));
			model.addAttribute("examBooks", bookService.getTopBooksByCategory("인공지능/빅데이터"));
			model.addAttribute("liberalBooks", bookService.getTopBooksByCategory("컴퓨터공학/전산학 개론"));
			model.addAttribute("isMain", true);
		} else {
			int limit = 4;
			int offset = (page - 1) * limit;

			List<BookVO> list = bookService.getBookListByPage(category, offset, limit, viewAll);
			int totalCount = bookService.getTotalCount(category);
			int totalPages = (int) Math.ceil((double) totalCount / limit);

			int blockLimit = 5;
			int startPage = (((int) Math.ceil((double) page / blockLimit)) - 1) * blockLimit + 1;
			int endPage = startPage + blockLimit - 1;
			if (endPage > totalPages) {
				endPage = totalPages;
			}

			model.addAttribute("list", list);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("startPage", startPage);
			model.addAttribute("endPage", endPage);
			model.addAttribute("hasNext", (page * limit) < totalCount);
			model.addAttribute("isMain", false);
		}
		model.addAttribute("category", category);
		model.addAttribute("currentPage", page);
		model.addAttribute("viewAll", viewAll);
		model.addAttribute("contentPage", "/WEB-INF/views/book/views.jsp");

		return "layout/layout";
	}

	@RequestMapping(value = "/list/ajax", method = RequestMethod.GET)
	@org.springframework.web.bind.annotation.ResponseBody
	public java.util.Map<String, Object> listAjax(
			@RequestParam(value = "category", defaultValue = "전체") String category,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "viewAll", defaultValue = "false") boolean viewAll) {

		int limit = 10; // 인피니트 스크롤시에는 한 번에 10개씩 로딩
		int offset = (page - 1) * limit;

		List<BookVO> list = bookService.getBookListByPage(category, offset, limit, viewAll);
		int totalCount = bookService.getTotalCount(category);
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		java.util.Map<String, Object> result = new java.util.HashMap<>();
		result.put("list", list);
		result.put("totalPages", totalPages);
		result.put("currentPage", page);
		result.put("hasNext", page < totalPages);

		return result;
	}


	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(@RequestParam(value = "keyword", defaultValue = "") String keyword, Model model) {
		String word = keyword == null ? "" : keyword.trim();

		if (!word.isEmpty()) {
			bookSearchService.recordKeyword(word);
		}

		List<BookVO> list = bookService.searchByBookName(word);

		model.addAttribute("list", list);
		model.addAttribute("category", "검색결과");
		model.addAttribute("currentPage", 1);
		model.addAttribute("totalPages", 1);
		model.addAttribute("startPage", 1);
		model.addAttribute("endPage", 1);
		model.addAttribute("hasNext", false);
		model.addAttribute("viewAll", true);
		model.addAttribute("isMain", false);
		model.addAttribute("isSearch", true);
		model.addAttribute("searchKeyword", word);
		model.addAttribute("contentPage", "/WEB-INF/views/book/views.jsp");

		return "layout/layout";
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(@RequestParam("isbn") long isbn, HttpSession session, Model model) {
		String loginUser = (String) session.getAttribute("loginUser");
		model.addAttribute("book", bookService.getBook(isbn));
		model.addAttribute("feedback", bookService.getBookFeedback(isbn, loginUser));
		model.addAttribute("contentPage", "/WEB-INF/views/book/view.jsp");
		return "layout/layout";
	}

	@RequestMapping(value = "/like", method = RequestMethod.POST)
	public String like(@RequestParam("isbn") long isbn, HttpSession session, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
		}

		bookService.toggleLike(isbn, loginUser);
		ra.addFlashAttribute("feedbackMessage", "좋아요 반영이 완료되었습니다.");
		return "redirect:/book/view?isbn=" + isbn;
	}

	@RequestMapping(value = "/rate", method = RequestMethod.POST)
	public String rate(@RequestParam("isbn") long isbn, @RequestParam("rating") int rating, HttpSession session,
			RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
		}

		if (rating < 1 || rating > 5) {
			ra.addFlashAttribute("feedbackMessage", "별점은 1점부터 5점까지 선택할 수 있습니다.");
			return "redirect:/book/view?isbn=" + isbn;
		}

		bookService.saveRating(isbn, loginUser, rating);
		ra.addFlashAttribute("feedbackMessage", "별점이 저장되었습니다.");
		return "redirect:/book/view?isbn=" + isbn;
	}


	@RequestMapping(value = "/rate/cancel", method = RequestMethod.POST)
	public String cancelRate(@RequestParam("isbn") long isbn, HttpSession session, RedirectAttributes ra) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null || "admin".equals(loginUser)) {
			return "redirect:/member/login";
		}
		bookService.deleteRating(isbn, loginUser);
		ra.addFlashAttribute("feedbackMessage", "별점이 취소되었습니다.");
		return "redirect:/book/view?isbn=" + isbn;
	}
}
