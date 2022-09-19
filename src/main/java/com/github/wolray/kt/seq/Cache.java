package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.List;

/**
 * @author wolray
 */
public interface Cache<T> {
    boolean exists();

    Iterable<T> read();

    void write(List<T> ts);

    interface Cacheable<T, S> {
        Iterator<T> iterator();

        S _convert(Iterable<T> iterable);

        default S cacheBy(Cache<T> cache) {
            return cacheBy(10, cache);
        }

        default S cacheBy(int batchSize, Cache<T> cache) {
            if (cache.exists()) {
                return _convert(cache.read());
            } else {
                List<T> list = new BatchList<>(batchSize);
                Iterator<T> iterator = iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
                if (!list.isEmpty()) {
                    cache.write(list);
                }
                return _convert(list);
            }
        }
    }
}