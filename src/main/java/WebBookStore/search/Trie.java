package WebBookStore.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Trie {

    private final Node root = new Node();

    public void insert(String key, String title) {
        if (key == null || title == null) return;

        String normalized = key.trim().toLowerCase();
        if (normalized.isEmpty()) return;

        Node current = root;
        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            Node next = current.children.get(ch);
            if (next == null) {
                next = new Node();
                current.children.put(ch, next);
            }
            current = next;
            current.titles.add(title);
        }
    }

    public List<String> searchPrefix(String prefix, int limit) {
        List<String> result = new ArrayList<String>();
        if (prefix == null || prefix.trim().isEmpty() || limit < 1) return result;

        String normalized = prefix.trim().toLowerCase();
        Node current = root;
        for (int i = 0; i < normalized.length(); i++) {
            current = current.children.get(normalized.charAt(i));
            if (current == null) return result;
        }

        int count = 0;
        for (String title : current.titles) {
            result.add(title);
            count++;
            if (count >= limit) break;
        }
        return result;
    }

    private static class Node {
        private final Map<Character, Node> children = new LinkedHashMap<Character, Node>();
        private final Set<String> titles = new LinkedHashSet<String>();
    }
}