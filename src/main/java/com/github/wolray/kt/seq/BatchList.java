package com.github.wolray.kt.seq;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author wolray
 */
public class BatchList<T> extends AbstractList<T> implements Seq.Backed<T> {
    private transient final SinglyList<ArrayList<T>> list = new SinglyList<>();
    private transient final int batchSize;
    private transient int size;
    private transient ArrayList<T> cur;

    public BatchList() {
        this(10);
    }

    public BatchList(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public Collection<T> _collection() {
        return this;
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
    public T get(int index) {
        return list.get(index / batchSize).get(index % batchSize);
    }

    @Override
    public Iterator<T> iterator() {
        return PickItr.flat(list.iterator());
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
