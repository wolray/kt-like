package com.github.wolray.kt.util;

import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * @author worlay
 */
public class Strings {
    public static String joinBy(String sep, Consumer<StringJoiner> consumer) {
        return Any.also(new StringJoiner(sep), consumer).toString();
    }

    public static String removePrefix(String s, String prefix) {
        if (s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }

    public static String removeSuffix(String s, String suffix) {
        if (s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    public static String removeSurrounding(String s, String prefix, String suffix) {
        if (s.length() >= prefix.length() + suffix.length() && s.startsWith(prefix) && s.endsWith(suffix)) {
            return s.substring(prefix.length(), s.length() - suffix.length());
        }
        return s;
    }
}
