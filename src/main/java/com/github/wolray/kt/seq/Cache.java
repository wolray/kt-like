package com.github.wolray.kt.seq;

import java.util.List;
import java.util.function.Function;

/**
 * @author wolray
 */
public interface Cache<T> {
    boolean exists();

    Iterable<T> read();

    void write(List<T> ts);

    interface Cacheable<T, S> extends Iterable<T>, Function<Iterable<T>, S> {
        default S cacheBy(Cache<T> cache) {
            return cacheBy(BatchList.DEFAULT_BATCH_SIZE, cache);
        }

        default S cacheBy(int batchSize, Cache<T> cache) {
            if (cache.exists()) {
                return apply(cache.read());
            } else {
                BatchList<T> list = new BatchList<>(batchSize);
                for (T t : this) {
                    list.add(t);
                }
                if (list.isNotEmpty()) {
                    cache.write(list);
                }
                return apply(list);
            }
        }
    }
}