package com.github.wolray.kt.util;

import com.github.wolray.kt.lazy.LazyJob;
import com.github.wolray.kt.lazy.LazyVar;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class Lazies {
    public static <T> LazyVar<T> lazyOf(Supplier<T> supplier) {
        return new LazyVar<>(supplier);
    }

    public static <T> LazyVar<T> lazyOf(Supplier<T> supplier, Consumer<T> consumer) {
        return new LazyVar<>(supplier).afterInit(consumer);
    }

    public static LazyJob lazyJob(Runnable runnable) {
        return new LazyJob(runnable);
    }
}
