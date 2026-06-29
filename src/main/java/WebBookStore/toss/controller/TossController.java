package WebBookStore.toss.controller;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import WebBookStore.cart.CartService;
import WebBookStore.cart.CartVO;
import WebBookStore.toss.service.TossService;



@Controller
@RequestMapping("/order")
public class TossController {
	
	private final TossService tossService;

	public TossController(TossService tossService) {
	    this.tossService = tossService;
	}
	
	@Autowired
	private CartService purchaseService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // =========================
    // 1. 결제 승인 API (토스)
    // =========================
    //@PostMapping("/confirm")
    @PostMapping("/pay")
    @ResponseBody
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {
    	System.out.println("호출!!!!!!!!!!");
    	
        JSONParser parser = new JSONParser();

        JSONObject requestData = (JSONObject) parser.parse(jsonBody);

        String paymentKey = (String) requestData.get("paymentKey");
        String orderId = (String) requestData.get("orderId");
        int amount = Integer.parseInt(String.valueOf(requestData.get("amount")));

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        String widgetSecretKey = "test_sk_LlDJaYngroawzQAvQJ9l3ezGdRpX";

        Base64.Encoder encoder = Base64.getEncoder();
        String authorizations =
                "Basic " + new String(encoder.encode((widgetSecretKey + ":")
                .getBytes(StandardCharsets.UTF_8)));

        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();

        InputStream responseStream =
                code == 200 ? connection.getInputStream() : connection.getErrorStream();

        JSONObject jsonObject = (JSONObject) parser.parse(
                new InputStreamReader(responseStream, StandardCharsets.UTF_8));

        responseStream.close();

        // 🔥 핵심 추가 (DB 반영)
        if (code == 200) {
            tossService.confirmPayment(orderId, paymentKey, amount);
            tossService.approveOrder(orderId, paymentKey);
        }

        return ResponseEntity.status(code).body(jsonObject);
    }

    // =========================
    // 2. 결제 성공 페이지
    // =========================
    @GetMapping("/success")
    public String success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam int amount,
            Model model) {

        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        
        // 💥 [궤도 수정] 알맹이 jsp 경로를 pageName에 담고 공통 레이아웃으로 쏜다!
        model.addAttribute("pageName", "toss/success"); // ⚠️ 실제 폴더 경로에 맞게 조정할 것 (예: pages/toss/success)
        return "redirect:/order/complete";
    }

    // =========================
    // 3. 메인 결제 페이지
    // =========================
    @GetMapping("/checkout")
    public String checkout(@RequestParam String orderId,
                           @RequestParam int amount,
                           Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        
        // 💥 [궤도 수정]
        model.addAttribute("pageName", "toss/checkout2"); 
        return "redirect:/order/checkout?error=true";
    }

    // =========================
    // 4. 결제 실패 페이지
    // =========================
    @GetMapping("/fail")
    public String fail(HttpServletRequest request, Model model) {

        String code = request.getParameter("code");
        String message = request.getParameter("message");

        if (code == null) code = "UNKNOWN_ERROR";
        if (message == null) message = "결제 실패";

        model.addAttribute("code", code);
        model.addAttribute("message", message);
        
        // 💥 [궤도 수정]
        model.addAttribute("pageName", "toss/fail"); 
        return "redirect:/order/checkout?error=true";
    }
    
 // 기존 TossController 내부에 추가 혹은 병합할 메서드

    @PostMapping("/prepare")
    @ResponseBody
    public ResponseEntity<JSONObject> prepareOrder(@RequestBody JSONObject requestData, HttpSession session, HttpServletRequest request) {
        JSONObject responseJson = new JSONObject();
        
        // 1. 사용자 ID 식별
        String loginUser = (String) session.getAttribute("loginUser");
        String userid = loginUser;
        if (userid == null) {
            // 비회원 체크를 위해 기존에 사용하시던 메소드가 있는 경우 활용
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("guestId".equals(cookie.getName())) {
                        userid = cookie.getValue();
                    }
                }
            }
        }
        
        if (userid == null) {
            responseJson.put("success", false);
            responseJson.put("message", "사용자 세션이 만료되었습니다.");
            return ResponseEntity.ok(responseJson);
        }

        // 2. 장바구니 목록 조회 및 총액 계산
        // 기존 주입받으시는 cartService 등을 사용
        List<CartVO> cartList = purchaseService.getCartList(userid); // 주입된 CartService 변수명 매핑
        if (cartList == null || cartList.isEmpty()) {
            responseJson.put("success", false);
            responseJson.put("message", "장바구니가 비어 있습니다.");
            return ResponseEntity.ok(responseJson);
        }

        int sumMoney = 0;
        for (CartVO cart : cartList) {
            sumMoney += cart.getTotalPrice();
        }

        // 3. 요청 데이터 파싱
        String receiver = (String) requestData.get("receiver");
        String phone = (String) requestData.get("phone");
        String address = (String) requestData.get("address");
        int useMileage = Integer.parseInt(String.valueOf(requestData.get("useMileage")));

        // 4. 고유한 orderId 생성 (토스 규격에 맞는 UUID 혹은 타임스탬프 결합 문자열)
        String uniqueOrderId = "BOOK-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        int finalAmount = sumMoney - useMileage;

        // 대표 주문 상품명 생성
        String orderName = cartList.get(0).getBookname();
        if (cartList.size() > 1) {
            orderName += " 외 " + (cartList.size() - 1) + "권";
        }

        try {
            // 5. 🔥 [핵심 비즈니스 로직] 
            // 결제 승인이 떨어지기 전 '가주문' 상태로 배송지 정보 및 사용 마일리지를 DB에 임시 저장(준비상태)해두는 서비스 메서드가 필요합니다.
            // 예시: tossService.readyOrder(userid, uniqueOrderId, finalAmount, receiver, phone, address, useMileage);
            
            responseJson.put("success", true);
            responseJson.put("orderId", uniqueOrderId);
            responseJson.put("amount", finalAmount);
            responseJson.put("orderName", orderName);
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "주문 데이터 생성 중 오류 발생: " + e.getMessage());
        }

        return ResponseEntity.ok(responseJson);
    }
}