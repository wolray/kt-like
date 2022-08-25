package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author wolray
 */
public abstract class MapItr<T, E> implements Iterator<E> {
    private final Iterator<T> iterator;

    public MapItr(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public static <T, E> MapItr<T, E> of(Iterator<T> iterator, Function<T, E> function) {
        return new MapItr<T, E>(iterator) {
            @Override
            public E map(T t) {
                return function.apply(t);
            }
        };
    }

    public static <T, S, E> MapItr<T, E> of(Iterator<T> iterator, S state, BiFunction<T, S, E> function) {
        return new MapItr<T, E>(iterator) {
            @Override
            public E map(T t) {
                return function.apply(t, state);
            }
        };
    }

    protected abstract E map(T t);

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        return map(iterator.next());
    }
}
