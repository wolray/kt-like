package com.github.wolray.kt.lazy;

/**
 * @author wolray
 */
public class LazyJob extends LazyVar<Runnable> {
    public LazyJob(Runnable runnable) {
        super(() -> {
            runnable.run();
            return runnable;
        });
    }
}
