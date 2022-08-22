package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author wolray
 */
public class SubSeq<T> extends Seq<T> {
    private final Seq<T> seq;
    private final int start;
    private final Integer end;

    SubSeq(Seq<T> seq, int start, Integer end) {
        this.seq = seq;
        this.start = start;
        this.end = end;
        if (end != null) {
            size = end - start;
        }
    }

    private boolean outRange(int n) {
        return size != null && n >= size;
    }

    @Override
    public Seq<T> drop(int n) {
        if (n <= 0) {
            return this;
        }
        return outRange(n) ? Seq.empty() : new SubSeq<>(this, start + n, end);
    }

    @Override
    public Seq<T> take(int n) {
        if (n <= 0) {
            return Seq.empty();
        }
        return outRange(n) ? this : new SubSeq<>(this, start, start + n);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> iterator = seq.iterator();
        return new Iterator<T>() {
            int pos = 0;

            private void drop() {
                while (pos < start && iterator.hasNext()) {
                    iterator.next();
                    pos++;
                }
            }

            @Override
            public boolean hasNext() {
                drop();
                return pos < end && iterator.hasNext();
            }

            @Override
            public T next() {
                drop();
                if (pos >= end) {
                    throw new NoSuchElementException();
                }
                pos++;
                return iterator.next();
            }
        };
    }
}
