package WebBookStore.book;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBookService {

	@Autowired
	private UserBookDAO dao;

	public List<BookVO> getBookList(String searchType, String keyword) {
		return dao.findAll(searchType, keyword);
	}

	public BookVO getBook(long isbn) {
		return dao.findByIsbn(isbn);
	}

	public List<BookVO> getBookListByPage(String category, int offset, int limit, boolean viewAll) {
		return dao.findByCategoryAndPage(category, offset, limit, viewAll);
	}

	public int getTotalCount(String category) {
		return dao.countByCategory(category);
	}

	public List<BookVO> getTopBooksByCategory(String category) {
		return dao.findTopNByCategory(category, 4);
	}

	public List<BookVO> getDiscountedBooks() {
		return dao.findDiscountedBooks();
	}

	public BookFeedbackVO getBookFeedback(long isbn, String loginUser) {
		return dao.getBookFeedback(isbn, loginUser);
	}

	public boolean toggleLike(long isbn, String userid) {
		return dao.toggleLike(isbn, userid);
	}

	public boolean saveRating(long isbn, String userid, int rating) {
		return dao.saveRating(isbn, userid, rating);
	}


	public boolean deleteRating(long isbn, String userid) {
		return dao.deleteRating(isbn, userid);
	}
	public List<BookVO> searchByBookName(String keyword) {
		return dao.searchByBookName(keyword);
	}
}
