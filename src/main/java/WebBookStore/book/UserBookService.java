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

	public BookVO getBook(int isbn) {
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

	public BookFeedbackVO getBookFeedback(int isbn, String loginUser) {
		return dao.getBookFeedback(isbn, loginUser);
	}

	public boolean toggleLike(int isbn, String userid) {
		return dao.toggleLike(isbn, userid);
	}

	public boolean saveRating(int isbn, String userid, int rating) {
		return dao.saveRating(isbn, userid, rating);
	}


	public boolean deleteRating(int isbn, String userid) {
		return dao.deleteRating(isbn, userid);
	}
}
