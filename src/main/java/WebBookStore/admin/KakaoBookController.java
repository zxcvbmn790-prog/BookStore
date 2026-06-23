package WebBookStore.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 카카오 도서 검색 REST API를 호출하여 ISBN으로 도서 정보를 조회하는 컨트롤러
 * 
 * 사용 전 REST_API_KEY를 카카오 개발자 콘솔에서 발급받은 키로 교체하세요.
 * https://developers.kakao.com → 내 애플리케이션 → 앱 키 → REST API 키
 */
@Controller
@RequestMapping("/admin")
public class KakaoBookController {

	// ★★★ 여기에 본인의 카카오 REST API 키를 입력하세요 ★★★
	private static final String REST_API_KEY = "4e4ed3dcdc7947f6e283c21698761c6e";

	private static final String KAKAO_BOOK_API_URL = "https://dapi.kakao.com/v3/search/book";

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * ISBN으로 카카오 도서 검색 API를 호출하고 결과를 JSON으로 반환
	 * 
	 * @param isbn 검색할 ISBN 번호
	 * @return 도서 정보 (title, authors, publisher, thumbnail, price) 또는 에러 메시지
	 */
	@RequestMapping("/kakaoBookSearch")
	@ResponseBody
	public Map<String, Object> kakaoBookSearch(@RequestParam("isbn") String isbn) {

		Map<String, Object> result = new HashMap<>();

		try {
			// 카카오 도서 검색 API 호출 URL 구성
			String encodedIsbn = URLEncoder.encode(isbn.trim(), StandardCharsets.UTF_8.toString());
			String apiUrl = KAKAO_BOOK_API_URL + "?target=isbn&query=" + encodedIsbn;

			URL url = new URL(apiUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "KakaoAK " + REST_API_KEY);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			int responseCode = conn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				// 응답 읽기
				BufferedReader br = new BufferedReader(
						new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();

				// JSON 파싱
				JsonNode root = objectMapper.readTree(sb.toString());
				JsonNode documents = root.get("documents");

				if (documents != null && documents.size() > 0) {
					JsonNode book = documents.get(0);

					result.put("success", true);
					result.put("title", book.has("title") ? book.get("title").asText() : "");

					// authors 배열 → 콤마로 연결
					if (book.has("authors") && book.get("authors").size() > 0) {
						StringBuilder authors = new StringBuilder();
						for (int i = 0; i < book.get("authors").size(); i++) {
							if (i > 0) authors.append(", ");
							authors.append(book.get("authors").get(i).asText());
						}
						result.put("authors", authors.toString());
					} else {
						result.put("authors", "");
					}

					result.put("publisher", book.has("publisher") ? book.get("publisher").asText() : "");
					result.put("thumbnail", book.has("thumbnail") ? book.get("thumbnail").asText() : "");
					result.put("price", book.has("price") ? book.get("price").asInt() : 0);

				} else {
					result.put("success", false);
					result.put("message", "해당 ISBN에 대한 검색 결과가 없습니다.");
				}
			} else {
				// API 오류 응답 읽기
				BufferedReader br = new BufferedReader(
						new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();

				result.put("success", false);
				result.put("message", "카카오 API 호출 오류 (HTTP " + responseCode + ")");
			}

			conn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
		}

		return result;
	}
}
