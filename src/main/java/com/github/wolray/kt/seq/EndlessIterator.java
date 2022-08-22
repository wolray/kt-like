package com.github.wolray.kt.seq;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @author wolray
 */
public abstract class EndlessIterator<T> implements Iterator<T> {
    public static <T> Iterator<T> of(T seed, UnaryOperator<T> operator) {
        MutablePair<T, T> pair = new MutablePair<>(seed, null);
        return of(Arrays.asList(
            () -> seed,
            () -> pair.first = operator.apply(pair.first)));
    }

    public static <T> Iterator<T> of(T seed1, T seed2, BinaryOperator<T> operator) {
        MutablePair<T, T> pair = new MutablePair<>(seed1, seed2);
        return of(Arrays.asList(
            () -> seed1,
            () -> seed2,
            () -> {
                T t = operator.apply(pair.first, pair.second);
                pair.first = pair.second;
                pair.second = t;
                return t;
            }));
    }

    public static <T> Iterator<T> of(Supplier<T> supplier) {
        return new EndlessIterator<T>() {
            @Override
            public T next() {
                return supplier.get();
            }
        };
    }

    public static <T> Iterator<T> of(Iterable<Supplier<T>> suppliers) {
        Iterator<Supplier<T>> iterator = suppliers.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("empty suppliers");
        }
        return new EndlessIterator<T>() {
            boolean done;
            Supplier<T> supplier = iterator.next();

            @Override
            public T next() {
                T t = supplier.get();
                if (done) {
                    return t;
                }
                if (iterator.hasNext()) {
                    supplier = iterator.next();
                } else {
                    done = true;
                }
                return t;
            }
        };
    }

    @Override
    public boolean hasNext() {
        return true;
    }
}
