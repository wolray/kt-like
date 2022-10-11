package com.github.wolray.kt.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @author wolray
 */
public interface Self<T> extends Supplier<T> {
    default T also(Consumer<T> consumer) {
        T t = get();
        consumer.accept(t);
        return t;
    }

    default T alsoIf(boolean condition, Consumer<T> consumer) {
        T t = get();
        if (condition) {
            consumer.accept(t);
        }
        return t;
    }

    default <E> E let(Function<T, E> function) {
        return function.apply(get());
    }

    default T replaceIf(boolean condition, UnaryOperator<T> operator) {
        return condition ? operator.apply(get()) : get();
    }
}
