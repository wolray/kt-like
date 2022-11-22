package com.github.wolray.kt.seq;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wolray
 */
public class BatchList<T> extends AbstractList<T> implements SeqList<T> {
    public static final int DEFAULT_BATCH_SIZE = 10;
    private transient final SinglyList<ArrayList<T>> list = new SinglyList<>();
    private transient final int batchSize;
    private transient int size;
    private transient ArrayList<T> cur;

    public BatchList() {
        this(DEFAULT_BATCH_SIZE);
    }

    public BatchList(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String toString() {
        return toStr();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return PickItr.flat(list.iterator());
    }

    @Override
    public boolean add(T t) {
        if (cur == null) {
            cur = new ArrayList<>(batchSize);
            list.add(cur);
        }
        cur.add(t);
        size++;
        if (cur.size() == batchSize) {
            cur = null;
        }
        return true;
    }

    @Override
    public void clear() {
        list.forEach(ArrayList::clear);
        list.clear();
        size = 0;
    }

    @Override
    public T get(int index) {
        return list.get(index / batchSize).get(index % batchSize);
    }
}
