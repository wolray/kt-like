package com.github.wolray.kt.seq;

import java.util.Iterator;

/**
 * @author wolray
 */
public class RepeatItr {
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
