package com.example.bucketlist.utils;

import java.util.HashMap;
import java.util.Map;

public class EscapeUtils {

    private static final Map<Character, String> escapeMap = new HashMap<>();
    private static final Map<String, Character> unescapeMap = new HashMap<>();

    static {
        escapeMap.put('<', "&lt;");
        escapeMap.put('>', "&gt;");
        escapeMap.put('&', "&amp;");
        escapeMap.put('"', "&quot;");
        escapeMap.put('\'', "&#x27;");
        escapeMap.put('/', "&#x2F;");

        unescapeMap.put("&lt;", '<');
        unescapeMap.put("&gt;", '>');
        unescapeMap.put("&amp;", '&');
        unescapeMap.put("&quot;", '"');
        unescapeMap.put("&#x27;", '\'');
        unescapeMap.put("&#x2F;", '/');
    }

    public static String escapeHtml(String html) {

        if (html == null || html.equals(""))
            return "";

        StringBuffer escapedString = new StringBuffer();
        for (char c : html.toCharArray()) {

            String escape = escapeMap.get(c);
            if (escape == null) {
                escapedString.append(c);
            } else {
                escapedString.append(escape);
            }

        }

        return escapedString.toString();
    }

    public static String unescapeHtml(String html) {

        if (html == null || html.equals(""))
            return "";

        // 주어진 문자열을 반복하며, 매핑된 값을 찾고, 있으면 변환된 문자 추가
        for (String key : unescapeMap.keySet()) {
                html = html.replace(key, unescapeMap.get(key).toString());
        }

        return html;
    }
}
