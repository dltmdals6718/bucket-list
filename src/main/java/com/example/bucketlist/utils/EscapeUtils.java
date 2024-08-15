package com.example.bucketlist.utils;

import java.util.HashMap;
import java.util.Map;

public class EscapeUtils {

    private static final Map<Character, String> escapeMap = new HashMap<>();

    static {
        escapeMap.put('<', "&lt;");
        escapeMap.put('>', "&gt;");
        escapeMap.put('&', "&amp;");
        escapeMap.put('"', "&quot;");
        escapeMap.put('\'', "&#x27;");
        escapeMap.put('/', "&#x2F;");
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
}
