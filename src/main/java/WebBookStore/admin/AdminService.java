package WebBookStore.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
	@Autowired
	private AdminDAO dao;

    public AdminService() {

    }

    // 전체 목록
    public List<AdminVO> getBookList() {
        return dao.findAll();
    }

    // 등록
    public int insertBook(AdminVO book) {
        return dao.insert(book);
    }

    // 수정
    public boolean updateBook(AdminVO admin) {
    	return dao.update(admin)>0 ? true : false;
    }

    // 삭제
    public int deleteBook(long isbn) {
        return dao.delete(isbn);
    }

    // 세부 조회
    public AdminVO getBookById(long isbn) {
        return dao.findById(isbn);
    }

    public SalesSummaryVO getSalesSummary() {
        return dao.getSalesSummary();
    }

    public List<DailySalesVO> getDailySalesList(String period) {
        return dao.getDailySalesList(period);
    }

    public List<BookSalesVO> getTopBookSalesList() {
        return dao.getTopBookSalesList();
    }


    public java.util.List<WebBookStore.member.MemberVO> getMemberList() {
        return dao.getMemberList();
    }

    public List<AdminVO> searchBooks(String keyword) {
        return dao.searchBooks(keyword);
    }

    public boolean updateDiscountRate(long isbn, int discountRate) {
        return dao.updateDiscountRate(isbn, discountRate) > 0;
    }

    public boolean deleteMember(String username) {
        return dao.deleteMember(username) > 0;
    }
}
