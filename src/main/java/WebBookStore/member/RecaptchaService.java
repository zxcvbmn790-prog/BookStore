package WebBookStore.member;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    // 테스트용 SECRET_KEY
    // 실제 운영에서는 코드에 직접 넣지 말고 설정 파일로 빼는 것이 좋음
    private static final String SECRET_KEY = "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe";

    public boolean verify(String token, HttpServletRequest request) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
            params.add("secret", SECRET_KEY);
            params.add("response", token);

            if (request != null) {
                params.add("remoteip", request.getRemoteAddr());
            }

            Map result = restTemplate.postForObject(
                    VERIFY_URL,
                    params,
                    Map.class
            );

            if (result == null) {
                return false;
            }

            Object success = result.get("success");

            return Boolean.TRUE.equals(success);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}