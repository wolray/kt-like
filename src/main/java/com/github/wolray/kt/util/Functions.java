package com.github.wolray.kt.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @author wolray
 */
public class Functions {
    public interface IoFun<T, E> {
        E apply(T t) throws IOException;
    }

    public static <T, E> Function<T, E> byIo(IoFun<T, E> function) {
        return it -> {
            try {
                return function.apply(it);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

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
