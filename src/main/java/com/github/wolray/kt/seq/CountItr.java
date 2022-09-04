package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author wolray
 */
public abstract class CountItr<T> implements Iterator<T> {
    int cur;

    public CountItr(int cur) {
        this.cur = cur;
    }

    public static <T> CountItr<T> take(Iterator<T> iterator, int n) {
        return new CountItr<T>(n) {
            @Override
            public boolean hasNext() {
                return cur > 0 && iterator.hasNext();
            }

            @Override
            public T next() {
                if (hasNext()) {
                    cur--;
                    return iterator.next();
                }
                throw new NoSuchElementException();
            }
        };
    }

    public static <T> CountItr<T> drop(Iterator<T> iterator, int n) {
        return new CountItr<T>(n) {
            @Override
            public boolean hasNext() {
                while (cur > 0 && iterator.hasNext()) {
                    cur--;
                    iterator.next();
                }
                return iterator.hasNext();
            }

            @Override
            public T next() {
                if (hasNext()) {
                    return iterator.next();
                }
                throw new NoSuchElementException();
            }
        };
    }

    public static CountItr<Integer> range(int start, int until, int step) {
        if (step == 0) {
            throw new IllegalArgumentException("zero step");
        }
        boolean increase = step > 0;
        return new CountItr<Integer>(start) {
            @Override
            public boolean hasNext() {
                return increase ? cur < until : cur > until;
            }

            @Override
            public Integer next() {
                int res = cur;
                cur += step;
                return res;
            }
        };
    }

    public static <T> CountItr<T> repeat(T t, int n) {
        return new CountItr<T>(n) {
            @Override
            public boolean hasNext() {
                return cur > 0;
            }

            @Override
            public T next() {
                if (cur > 0) {
                    cur--;
                    return t;
                }
                throw new NoSuchElementException();
            }
        };
    }
}
