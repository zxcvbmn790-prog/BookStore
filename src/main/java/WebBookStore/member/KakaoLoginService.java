package WebBookStore.member;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoLoginService {

	private static final String KAKAO_REST_API_KEY = "3350a3be4ca50a8f574c59bcbf9ec96d";
	private static final String KAKAO_REDIRECT_URI = "https://garment-bonded-citation.ngrok-free.dev/member/kakaoLogin";
	private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
	private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

	private final RestTemplate restTemplate = new RestTemplate();

	public String getAuthorizeUrl() {
		return "https://kauth.kakao.com/oauth/authorize"
				+ "?response_type=code"
				+ "&client_id=" + KAKAO_REST_API_KEY
				+ "&redirect_uri=" + KAKAO_REDIRECT_URI;
	}

	public String getAccessToken(String code) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", KAKAO_REST_API_KEY);
		params.add("redirect_uri", KAKAO_REDIRECT_URI);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, Map.class);
		Map<String, Object> body = response.getBody();

		if (body == null || body.get("access_token") == null) {
			throw new IllegalStateException("카카오 access token 발급에 실패했습니다.");
		}

		return String.valueOf(body.get("access_token"));
	}

	@SuppressWarnings("unchecked")
	public KakaoUserInfo getUserInfo(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<Map> response = restTemplate.exchange(
				KAKAO_USER_INFO_URL,
				HttpMethod.GET,
				request,
				Map.class
		);

		Map<String, Object> body = response.getBody();

		if (body == null || body.get("id") == null) {
			throw new IllegalStateException("카카오 사용자 정보 조회에 실패했습니다.");
		}

		String kakaoId = String.valueOf(body.get("id"));
		String email = null;
		String nickname = "카카오사용자";

		Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
		if (kakaoAccount != null) {
			Object emailValue = kakaoAccount.get("email");
			if (emailValue != null) {
				email = String.valueOf(emailValue);
			}

			Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
			if (profile != null && profile.get("nickname") != null) {
				nickname = String.valueOf(profile.get("nickname"));
			}
		}

		return new KakaoUserInfo(kakaoId, email, nickname);
	}
}
