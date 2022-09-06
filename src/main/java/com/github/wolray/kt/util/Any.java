package com.github.wolray.kt.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author worlay
 */
public class Any {
    public static <T> T also(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    public static <T, E> E let(T t, Function<T, E> function) {
        return function.apply(t);
    }

    public static <T> T orElse(T t, T defaultValue) {
        return t != null ? t : defaultValue;
    }

    public static <T> T orElse(T t, Supplier<T> supplier) {
        return t != null ? t : supplier.get();
    }

    public static <T> T maybeAlso(T t, Consumer<T> consumer) {
        if (t != null) {
            consumer.accept(t);
        }
        return t;
    }

    public static <T, E> E maybeLet(T t, Function<T, E> function) {
        return t != null ? function.apply(t) : null;
    }

    public static <T, E> E maybeLet(T t, Function<T, E> function, E defaultValue) {
        return orElse(maybeLet(t, function), defaultValue);
    }

    @SafeVarargs
    public static <T, E> E firstBy(T t, Function<T, E>... functions) {
        for (Function<T, E> f : functions) {
            E res = f.apply(t);
            if (res != null) {
                return res;
            }
        }
        return null;
    }
}
