package com.github.wolray.kt.util;

import com.github.wolray.kt.seq.LongPair;

import java.util.function.Supplier;

/**
 * @author worlay
 */
public class Timing {
    public static long now() {
        return System.currentTimeMillis();
    }

    public static <T> LongPair<T> measure(Supplier<T> supplier) {
        long tic = now();
        T t = supplier.get();
        return new LongPair<>(t, now() - tic);
    }

    public static long measure(Runnable runnable) {
        long tic = now();
        runnable.run();
        return now() - tic;
    }
}
