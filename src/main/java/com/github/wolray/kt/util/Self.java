package com.github.wolray.kt.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author wolray
 */
public interface Self<T> {
    T _self();

    default T also(Consumer<T> consumer) {
        T t = _self();
        consumer.accept(t);
        return t;
    }

    default T alsoIf(boolean condition, Consumer<T> consumer) {
        T t = _self();
        if (condition) {
            consumer.accept(t);
        }
        return t;
    }

    default <E> E let(Function<T, E> function) {
        return function.apply(_self());
    }

    default T replaceIf(boolean condition, UnaryOperator<T> operator) {
        return condition ? operator.apply(_self()) : _self();
    }
}
