package com.github.wolray.kt.util;

import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * @author worlay
 */
public class Loggers {
    public static void infoTime(Logger log, String message, Runnable runnable) {
        infoTime(log, message, () -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T infoTime(Logger log, String message, Supplier<T> supplier) {
        log.info(message);
        long tic = Timing.now();
        T res = supplier.get();
        log.info("{} done in {}ms", message, Timing.now() - tic);
        return res;
    }
}
