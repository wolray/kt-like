package com.github.wolray.kt.lazy;

import com.github.wolray.kt.seq.SeqScope;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public interface LazyScope extends SeqScope {
    default <T> LazyVar<T> lazyOf(Supplier<T> supplier) {
        return new LazyVar<>(supplier);
    }

    default <T> LazyVar<T> lazyOf(Supplier<T> supplier, Consumer<T> consumer) {
        return new LazyVar<>(supplier).then(consumer);
    }

    default LazyJob job(Runnable runnable) {
        return new LazyJob(runnable);
    }

    default <T> LazySeq<T> lazySeq(Supplier<Iterable<T>> supplier) {
        return new LazySeq<>(() -> seq(supplier.get()));
    }
}