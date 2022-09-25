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
}
