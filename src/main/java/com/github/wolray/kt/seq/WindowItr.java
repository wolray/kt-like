package com.github.wolray.kt.seq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author wolray
 */
public class WindowItr<T> implements Iterator<List<T>> {
    private final Iterator<T> iterator;
    private final int size;
    private List<T> cur;

    public WindowItr(Iterator<T> iterator, int size) {
        this.iterator = iterator;
        this.size = size;
    }

    private boolean computeNext() {
        if (cur != null) {
            return true;
        }
        if (!iterator.hasNext()) {
            return false;
        }
        cur = new ArrayList<>(size);
        int n = size;
        while (iterator.hasNext() && n > 0) {
            cur.add(iterator.next());
            n--;
        }
        return true;
    }

    @Override
    public boolean hasNext() {
        return computeNext();
    }

    @Override
    public List<T> next() {
        if (!computeNext()) {
            throw new NoSuchElementException();
        }
        List<T> res = cur;
        cur = null;
        return res;
    }
}
