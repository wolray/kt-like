package com.github.wolray.kt.lazy;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public interface LazyScope {
    default <T> LazyVar<T> lazyOf(Supplier<T> supplier) {
        return new LazyVar<>(supplier);
    }

    default <T> LazyVar<T> lazyOf(Supplier<T> supplier, Consumer<T> consumer) {
        return new LazyVar<>(supplier).then(consumer);
    }

    default LazyJob job(Runnable runnable) {
        return new LazyJob(runnable);
    }
}