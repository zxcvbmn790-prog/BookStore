package WebBookStore.search;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WebBookStore.book.UserBookDAO;

@Service
public class BookSearchService {

    private static final String AUTO_CACHE_PREFIX = "bookforest:auto:";
    private static final String POPULAR_KEY_PREFIX = "bookforest:popular:";
    private static final int AUTO_CACHE_SECONDS = 300;

    private final List<String> defaultAnnualKeywords = Arrays.asList(
            "AI", "제미나이", "클로드", "바이브 코딩", "엑셀", //인기 검색어 하고 싶었는데... // 일단 고정으로 해놓음
            "데이터", "파이썬", "자바", "스프링", "보안"
    );

    @Autowired
    private UserBookDAO bookDAO;

    @Autowired
    private RawRedisClient redisClient;

    private Trie trie = new Trie();

    private int titleCount = 0;

    @PostConstruct
    public void init() {
        reloadTrie();
    }

    public synchronized void reloadTrie() {
        Trie newTrie = new Trie();
        List<String> titles = bookDAO.findAllBookNames();

        for (String title : titles) {
            if (title == null || title.trim().isEmpty()) continue;
            String cleanTitle = title.trim();
            newTrie.insert(cleanTitle, cleanTitle);
            newTrie.insert(HangulUtil.toChosung(cleanTitle), cleanTitle);
        }
        trie = newTrie;
        titleCount = titles.size();
    }

    public List<String> autocomplete(String keyword) {
        keyword = clean(keyword);
        if (keyword.isEmpty()) return new ArrayList<String>();
        if (titleCount == 0) reloadTrie();

        String cacheKey = AUTO_CACHE_PREFIX + keyword;
        String cached = redisClient.get(cacheKey);
        if (cached != null) return decode(cached);

        List<String> result = trie.searchPrefix(keyword, 10);
        redisClient.setex(cacheKey, AUTO_CACHE_SECONDS, encode(result));
        return result;
    }

    public void recordKeyword(String keyword) {
        keyword = clean(keyword);
        if (keyword.isEmpty()) return;
        redisClient.zincrby(popularKey(), 1.0, keyword);
    }

    public List<String> annualKeywords() {
        Set<String> merged = new LinkedHashSet<String>();

        for (String keyword : redisClient.zrevrange(popularKey(), 0, 9)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                merged.add(keyword.trim());
            }
        }

        merged.addAll(defaultAnnualKeywords);
        return new ArrayList<String>(merged);
    }

    private String popularKey() {
        return POPULAR_KEY_PREFIX + Year.now().getValue();
    }

    private String clean(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }

    private String encode(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            if (item == null) continue;
            if (sb.length() > 0) sb.append('\n');
            sb.append(item.replace("\r", " ").replace("\n", " "));
        }
        return sb.toString();
    }

    private List<String> decode(String value) {
        List<String> list = new ArrayList<String>();
        if (value == null || value.trim().isEmpty()) return list;
        String[] arr = value.split("\\n");
        for (String item : arr) {
            if (item != null && !item.trim().isEmpty()) list.add(item.trim());
        }
        return list;
    }
}
