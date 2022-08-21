package com.github.wolray.kt.seq;

import java.util.Iterator;

/**
 * @author wolray
 */
public class Yield<T> {
    final BatchList<T> list;

    Yield(int batchSize) {
        list = new BatchList<>(batchSize);
    }

    public void yield(T t) {
        list.add(t);
    }

    public void yieldAll(Iterable<T> iterable) {
        iterable.forEach(this::yield);
    }

    public void yieldAll(Iterator<T> iterator) {
        while (iterator.hasNext()) {
            this.yield(iterator.next());
        }
    }
}
