package WebBookStore.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private BookSearchService bookSearchService;

    @RequestMapping(value = "/autocomplete", method = RequestMethod.GET)
    @ResponseBody
    public List<String> autocomplete(@RequestParam(value = "keyword", defaultValue = "") String keyword) {
        return bookSearchService.autocomplete(keyword);
    }

    @RequestMapping(value = "/popular", method = RequestMethod.GET)
    @ResponseBody
    public List<String> popular() {
        return bookSearchService.annualKeywords();
    }

    @RequestMapping(value = "/record", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> record(@RequestParam(value = "keyword", defaultValue = "") String keyword) {
        bookSearchService.recordKeyword(keyword);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        return result;
    }
}
