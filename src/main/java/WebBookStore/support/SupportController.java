package WebBookStore.support;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support")
public class SupportController {

    @RequestMapping("/faq")
    public String faq(Model model) {
        List<FaqItem> faqList = Arrays.asList(
                new FaqItem("회원가입은 어떻게 하나요?", "상단의 회원가입 버튼에서 아이디, 비밀번호, 연락처, 이메일을 입력하면 바로 가입할 수 있습니다."),
                new FaqItem("장바구니에 담은 상품은 어디서 보나요?", "로그인 후 상단 메뉴의 장바구니에서 담아둔 도서를 확인하고 주문할 수 있습니다."),
                new FaqItem("주문 내역은 어디서 확인하나요?", "로그인 후 주문내역 메뉴에서 결제 완료된 주문 목록을 확인할 수 있습니다."),
                new FaqItem("평점과 좋아요는 누구나 남길 수 있나요?", "로그인한 회원만 도서 상세 페이지에서 좋아요와 별점 평가를 남길 수 있습니다."),
                new FaqItem("실시간 상담은 어떻게 이용하나요?", "우측 하단 상담 기능 또는 관리자 실시간상담 메뉴를 통해 문의할 수 있습니다."));

        model.addAttribute("faqList", faqList);
        model.addAttribute("contentPage", "/WEB-INF/views/support/faq.jsp");
        return "layout/layout";
    }

    public static class FaqItem {
        private final String question;
        private final String answer;

        public FaqItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
