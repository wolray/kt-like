package com.github.wolray.kt.util;

import com.github.wolray.kt.lazy.LazyJob;
import com.github.wolray.kt.lazy.LazySeq;
import com.github.wolray.kt.lazy.LazyVar;
import com.github.wolray.kt.seq.Seq;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class Lazies {
    public static <T> Seq<T> seq(Iterable<T> iterable) {
        return Seq.of(iterable);
    }

    @SafeVarargs
    public static <T> Seq<T> seq(T... ts) {
        return Seq.of(Arrays.asList(ts));
    }

    public static <K, V> Seq<Map.Entry<K, V>> seq(Map<K, V> map) {
        return Seq.of(map.entrySet());
    }

    public static <T> LazyVar<T> lazyOf(Supplier<T> supplier) {
        return new LazyVar<>(supplier);
    }

    public static <T> LazyVar<T> lazyOf(Supplier<T> supplier, Consumer<T> consumer) {
        return new LazyVar<>(supplier).then(consumer);
    }

    public static LazyJob lazyJob(Runnable runnable) {
        return new LazyJob(runnable);
    }

    public static <T> LazySeq<T> lazySeq(Supplier<Iterable<T>> supplier) {
        return new LazySeq<>(() -> Seq.of(supplier.get()));
    }
}
