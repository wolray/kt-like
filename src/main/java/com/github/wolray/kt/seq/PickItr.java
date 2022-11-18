package com.github.wolray.kt.seq;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public abstract class PickItr<T> implements Iterator<T> {
    private T next;
    private State state = State.Unset;

    public static <T> PickItr<T> gen(Supplier<T> supplier) {
        return new PickItr<T>() {
            @Override
            public T pick() {
                return supplier.get();
            }
        };
    }

    static <T> Iterator<T> genUntilNull(Supplier<T> supplier) {
        return new PickItr<T>() {
            @Override
            public T pick() {
                T res = supplier.get();
                return res != null ? res : stop();
            }
        };
    }

    public static <S, T> PickItr<T> gen(S seed, Function<S, T> function) {
        return gen(() -> function.apply(seed));
    }

    public static <A, B, T> PickItr<T> gen(A seed1, B seed2, BiFunction<A, B, T> function) {
        return gen(() -> function.apply(seed1, seed2));
    }

    public static <T> PickItr<T> dropWhile(Iterator<T> iterator, Predicate<T> predicate) {
        return new PickItr<T>() {
            boolean done;

            @Override
            public T pick() {
                while (iterator.hasNext()) {
                    T t = iterator.next();
                    if (done) {
                        return t;
                    }
                    if (!predicate.test(t)) {
                        done = true;
                        return t;
                    }
                }
                return stop();
            }
        };
    }

    public static <T, E> PickItr<T> distinctBy(Iterator<T> iterator, Function<T, E> function) {
        return new PickItr<T>() {
            Set<E> set = new HashSet<>();

            @Override
            public T pick() {
                while (iterator.hasNext()) {
                    T next = iterator.next();
                    if (set.add(function.apply(next))) {
                        return next;
                    }
                }
                return stop();
            }
        };
    }

    public static <T> PickItr<T> flat(Iterator<? extends Iterable<T>> iterator) {
        return new PickItr<T>() {
            Iterator<T> cur = Collections.emptyIterator();

            @Override
            public T pick() {
                while (!cur.hasNext()) {
                    if (!iterator.hasNext()) {
                        stop();
                    }
                    cur = iterator.next().iterator();
                }
                return cur.next();
            }
        };
    }

    public static <T> PickItr<SeqList<T>> window(Iterator<T> iterator, int size) {
        return gen(() -> {
            if (!iterator.hasNext()) {
                stop();
            }
            List<T> list = new ArrayList<>(size);
            int n = size;
            while (iterator.hasNext() && n > 0) {
                list.add(iterator.next());
                n--;
            }
            return SeqList.of(list);
        });
    }

    public static <T> T stop() {
        throw StopException.INSTANCE;
    }

    public abstract T pick();

    @Override
    public boolean hasNext() {
        if (state == State.Unset) {
            try {
                next = pick();
                state = State.Cached;
            } catch (StopException e) {
                state = State.Done;
            }
        }
        return state == State.Cached;
    }

    @Override
    public T next() {
        if (hasNext()) {
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
        Done
    }

    static class StopException extends RuntimeException {
        static final StopException INSTANCE = new StopException() {
            @Override
            public synchronized Throwable fillInStackTrace() {
                return this;
            }
        };
    }
}
