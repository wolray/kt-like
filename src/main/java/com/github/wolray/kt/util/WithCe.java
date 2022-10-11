package com.github.wolray.kt.util;

import java.util.Iterator;

/**
 * @author wolray
 */
public interface WithCe {
    interface Function<T, V> {
        V apply(T t) throws Exception;
    }

    interface BiFunction<T, V, R> {
        R apply(T t, V v) throws Exception;
    }

    interface Consumer<T> {
        void accept(T t) throws Exception;
    }

    interface BiConsumer<T, V> {
        void accept(T t, V v) throws Exception;
    }

    interface Supplier<T> {
        T get() throws Exception;
    }

    interface Iterable<T> {
        Iterator<T> iterator() throws Exception;
    }

    static <T, V> java.util.function.Function<T, V> mapper(Function<T, V> function) {
        return it -> {
            try {
                return function.apply(it);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <T, V, R> java.util.function.BiFunction<T, V, R> mapper(BiFunction<T, V, R> function) {
        return (t, v) -> {
            try {
                return function.apply(t, v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <T> java.util.function.Consumer<T> acceptor(Consumer<T> consumer) {
        return it -> {
            try {
                consumer.accept(it);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <T, V> java.util.function.BiConsumer<T, V> acceptor(BiConsumer<T, V> consumer) {
        return (t, v) -> {
            try {
                consumer.accept(t, v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <T> java.util.function.Supplier<T> getter(Supplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <T extends AutoCloseable> void safeAccept(T t, Consumer<T> consumer) throws Exception {
        safeApply(t, it -> {
            consumer.accept(it);
            return null;
        });
    }

    static <T extends AutoCloseable, E> E safeApply(T t, Function<T, E> function) throws Exception {
        Throwable throwable = null;
        try {
            return function.apply(t);
        } catch (Exception e) {
            throwable = e;
            throw e;
        } finally {
            if (throwable != null) {
                try {
                    t.close();
                } catch (Throwable e) {
                    throwable.addSuppressed(e);
                }
            } else {
                t.close();
            }
        }
    }
}
