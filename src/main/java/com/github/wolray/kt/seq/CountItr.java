package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author wolray
 */
public class CountItr {
    public static <T> Iterator<T> take(Iterator<T> iterator, int n) {
        return new Iterator<T>() {
            int left = n;

            @Override
            public boolean hasNext() {
                return left > 0 && iterator.hasNext();
            }

            @Override
            public T next() {
                if (hasNext()) {
                    left--;
                    return iterator.next();
                }
                throw new NoSuchElementException();
            }
        };
    }

    public static <T> Iterator<T> drop(Iterator<T> iterator, int n) {
        return new Iterator<T>() {
            int left = n;

            @Override
            public boolean hasNext() {
                while (iterator.hasNext() && left-- > 0) {
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

    public static Iterator<Integer> range(int start, int until, int step) {
        return new Iterator<Integer>() {
            int cur = start;

            @Override
            public boolean hasNext() {
                return cur < until;
            }

            @Override
            public Integer next() {
                int res = cur;
                cur += step;
                return res;
            }
        };
    }

    public static <T> Iterator<T> repeat(T t, int n) {
        return new Iterator<T>() {
            int c = n;

            @Override
            public boolean hasNext() {
                return c > 0;
            }

            @Override
            public T next() {
                c--;
                return t;
            }
        };
    }
}
