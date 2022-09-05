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
}
