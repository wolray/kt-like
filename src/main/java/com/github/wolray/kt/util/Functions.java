package com.github.wolray.kt.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @author wolray
 */
public class Functions {
    public static <T> UnaryOperator<T> asUnaryOp(Consumer<T> consumer) {
        return t -> {
            consumer.accept(t);
            return t;
        };
    }

    public static <T> Supplier<T> orDefault(Supplier<T> supplier, T defaultValue) {
        return () -> {
            T t = supplier.get();
            return t != null ? t : defaultValue;
        };
    }

    public static <T> Supplier<T> orBy(Supplier<T> supplier, Supplier<T> defaultGetter) {
        return () -> {
            T t = supplier.get();
            return t != null ? t : defaultGetter.get();
        };
    }
}
