package com.github.wolray.kt.seq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author wolray
 */
public class Yield<T> {
    final LinkedList<ArrayList<T>> list = new LinkedList<>();
    private final int batchSize;
    private ArrayList<T> cur;
    private int mod;

    public Yield(int batchSize) {
        this.batchSize = batchSize;
        mod = batchSize;
    }

    public void yield(T t) {
        if (mod == batchSize) {
            mod = 0;
            cur = new ArrayList<>(batchSize);
            list.add(cur);
        }
        cur.add(t);
        mod++;
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
