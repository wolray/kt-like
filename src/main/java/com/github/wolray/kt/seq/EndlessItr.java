package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public abstract class EndlessItr<T> implements Iterator<T> {
    public static <T> Iterator<T> of(Supplier<T> supplier) {
        return new EndlessItr<T>() {
            @Override
            public T next() {
                return supplier.get();
            }
        };
    }

    public static <S, T> Iterator<T> of(S state, Function<S, T> function) {
        return new EndlessItr<T>() {
            @Override
            public T next() {
                return function.apply(state);
            }
        };
    }

    @Override
    public boolean hasNext() {
        return true;
    }
}
