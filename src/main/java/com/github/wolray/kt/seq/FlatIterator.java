package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author wolray
 */
public class FlatIterator<R, T extends Iterable<R>> implements Iterator<R> {
    private final Iterator<T> iterator;
    private Iterator<R> cur;

    public FlatIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return ensureItemIterator();
    }

    @Override
    public R next() {
        if (!ensureItemIterator()) {
            throw new NoSuchElementException();
        }
        return cur.next();
    }

    private boolean ensureItemIterator() {
        if (cur != null && !cur.hasNext()) {
            cur = null;
        }
        while (cur == null) {
            if (!iterator.hasNext()) {
                return false;
            } else {
                T t = iterator.next();
                Iterator<R> itr = t.iterator();
                if (itr.hasNext()) {
                    cur = itr;
                    return true;
                }
            }
        }
        return true;
    }
}
