package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class RecurringItr<T> implements Iterator<T> {
    private final Supplier<T> supplier;
    private T next;
    private State state = State.Unset;

    RecurringItr(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <A, B, T> RecurringItr<T> of(A seed1, B seed2, BiFunction<A, B, T> function) {
        return new RecurringItr<>(() -> function.apply(seed1, seed2));
    }

    public static <S, T> RecurringItr<T> of(S seed, Function<S, T> function) {
        return new RecurringItr<>(() -> function.apply(seed));
    }

    private boolean ensureNext() {
        if (state == State.Unset) {
            try {
                next = supplier.get();
                state = State.Cached;
            } catch (StopException e) {
                state = State.Done;
            }
        }
        return state == State.Cached;
    }

    @Override
    public boolean hasNext() {
        return ensureNext();
    }

    @Override
    public T next() {
        if (ensureNext()) {
            T res = next;
            next = null;
            state = State.Unset;
            return res;
        }
        throw new NoSuchElementException();
    }

    enum State {
        Unset,
        Cached,
        Done,
    }

    static class StopException extends RuntimeException {}
}
