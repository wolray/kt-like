package com.github.wolray.kt.util;

import com.github.wolray.kt.seq.Seq;

import java.util.Iterator;

/**
 * @author wolray
 */
public class WithCe {
    public interface Function<T, E> {
        E apply(T t) throws Exception;

        default java.util.function.Function<T, E> asNormal() {
            return it -> {
                try {
                    return apply(it);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public interface Consumer<T> {
        void accept(T t) throws Exception;

        default java.util.function.Consumer<T> asNormal() {
            return it -> {
                try {
                    accept(it);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public interface Supplier<T> {
        T get() throws Exception;

        default java.util.function.Supplier<T> asNormal() {
            return () -> {
                try {
                    return get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public interface Iterable<T> {
        Iterator<T> iterator() throws Exception;

        default Seq<T> asSeq() {
            return () -> {
                try {
                    return iterator();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public static <T extends AutoCloseable> void safeAccept(T t, Consumer<T> consumer) throws Exception {
        safeApply(t, it -> {
            consumer.accept(it);
            return null;
        });
    }

    public static <T extends AutoCloseable, E> E safeApply(T t, Function<T, E> function) throws Exception {
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
