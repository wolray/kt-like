package com.github.wolray.kt.seq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class Yield<T> {
    final List<Iterable<T>> list;
    private final int batchSize;
    private BatchList<T> cur;

    Yield(int batchSize) {
        this.batchSize = batchSize;
        list = new ArrayList<>();
    }

    private void ensureAppender() {
        if (cur == null) {
            cur = new BatchList<>(batchSize);
            list.add(cur);
        }
    }

    public void yield(T t) {
        ensureAppender();
        cur.add(t);
    }

    @SafeVarargs
    public final void yield(T... t) {
        ensureAppender();
        cur.addAll(Arrays.asList(t));
    }

    public void yieldAll(Iterator<T> iterator) {
        ensureAppender();
        while (iterator.hasNext()) {
            cur.add(iterator.next());
        }
    }

    public void yieldAll(Iterable<T> iterable) {
        list.add(iterable);
        cur = null;
    }

    public void yieldAll(Supplier<T> supplier) {
        yieldAll(() -> EndlessItr.of(supplier));
    }
}
