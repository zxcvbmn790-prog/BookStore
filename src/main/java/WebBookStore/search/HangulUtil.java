package WebBookStore.search;

public class HangulUtil {

    private static final char[] CHOSUNG = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    public static String toChosung(String text) {
        if (text == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= 0xAC00 && ch <= 0xD7A3) {
                int index = (ch - 0xAC00) / 588;
                sb.append(CHOSUNG[index]);
            } else if (!Character.isWhitespace(ch)) {
                sb.append(Character.toLowerCase(ch));
            }
        }
        return sb.toString();
    }

    public static boolean hasChosung(String text) {
        if (text == null) return false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= 'ㄱ' && ch <= 'ㅎ') return true;
        }
        return false;
    }
}
