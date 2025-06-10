package com.yidigun.base.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static final Map<String, Pattern> delimiterPatternCache = new ConcurrentHashMap<>();

    public static Pattern compileDelimiterPattern(String delimiter) {
        return delimiterPatternCache.computeIfAbsent(delimiter.trim(),
                d -> Pattern.compile("\\s*" + d.trim() + "\\s*"));
    }

}
