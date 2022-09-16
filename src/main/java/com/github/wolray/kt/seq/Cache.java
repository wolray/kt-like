package com.github.wolray.kt.seq;

import java.util.List;

/**
 * @author wolray
 */
public interface Cache<T> {
    default int batchSize() {
        return 10;
    }

    boolean exists();

    Iterable<T> read();

    void write(List<T> ts);

    interface Cacheable<T, S> {
        S fromCache(Iterable<T> iterable);

        List<T> collectForCache(int batchSize);

        S afterCache(List<T> list);

        default S cacheBy(Cache<T> cache) {
            if (cache.exists()) {
                return fromCache(cache.read());
            } else {
                List<T> list = collectForCache(cache.batchSize());
                if (!list.isEmpty()) {
                    cache.write(list);
                }
                return afterCache(list);
            }
        }
    }
}
