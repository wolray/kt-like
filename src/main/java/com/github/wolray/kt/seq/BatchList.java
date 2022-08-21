package com.github.wolray.kt.seq;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author wolray
 */
public class BatchList<T> extends AbstractList<T> {
    private transient final SinglyList<ArrayList<T>> list = new SinglyList<>();
    private transient final int batchSize;
    private transient int mod;
    private transient int size;
    private transient ArrayList<T> cur;

    public BatchList() {
        this(10);
    }

    public BatchList(int batchSize) {
        this.batchSize = mod = batchSize;
    }

    @Override
    public boolean add(T t) {
        if (mod == batchSize) {
            mod = 0;
            cur = new ArrayList<>(batchSize);
            list.add(cur);
        }
        cur.add(t);
        mod++;
        size++;
        return true;
    }

    @Override
    public T get(int index) {
        return list.get(index / batchSize).get(index % batchSize);
    }

    @Override
    public Iterator<T> iterator() {
        return new FlatIterator<>(list.iterator(), it -> it);
    }

    @Override
    public void clear() {
        list.forEach(ArrayList::clear);
        list.clear();
    }

    @Override
    public int size() {
        return size;
    }
}
