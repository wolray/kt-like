package com.github.wolray.kt.seq;

import java.util.List;

/**
 * @author wolray
 */
public interface Cache<T> {
    boolean exists();

    Iterable<T> read();

    void write(List<T> ts);

    interface Cacheable<T, S> extends Iterable<T> {
        S convert(Iterable<T> iterable);

        default S cacheBy(int batchSize, Cache<T> cache) {
            if (cache.exists()) {
                return convert(cache.read());
            } else {
                List<T> list = new BatchList<>(batchSize);
                for (T t : this) {
                    list.add(t);
                }
                if (!list.isEmpty()) {
                    cache.write(list);
                }
                return convert(list);
            }
        }
    }
}