package com.github.wolray.kt.seq;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author wolray
 */
public class YieldSeq<T> implements Seq<T> {
    private transient Node<T> dummy = new Node<>();
    private transient Node<T> last = dummy;

    YieldSeq() {}

    private YieldSeq<T> add(Node<T> node) {
        last.next = node;
        last = node;
        return this;
    }

    public YieldSeq<T> yield(T t) {
        Node<T> it = new Node<>();
        it.t = t;
        return add(it);
    }

    public YieldSeq<T> yieldAll(Iterable<T> iterable) {
        Objects.requireNonNull(iterable);
        Multi<T> it = new Multi<>();
        it.iterable = iterable;
        return add(it);
    }

    public YieldSeq<T> yieldAll(Iterator<T> iterator) {
        Objects.requireNonNull(iterator);
        while (iterator.hasNext()) {
            this.yield(iterator.next());
        }
        return this;
    }

    @Override
    public Iterator<T> iterator() {
        return new PickItr<T>() {
            Node<T> curr = dummy;
            Iterator<T> iterator = Collections.emptyIterator();

            @Override
            public T pick() {
                if (iterator.hasNext()) {
                    return iterator.next();
                }
                while (curr.next != null) {
                    curr = curr.next;
                    if (curr instanceof Multi) {
                        iterator = ((Multi<T>)curr).iterable.iterator();
                        if (iterator.hasNext()) {
                            return iterator.next();
                        }
                    } else {
                        return curr.t;
                    }
                }
                return stop();
            }
        };
    }

    static class Node<T> {
        T t;
        Node<T> next;
    }

    static class Multi<T> extends Node<T> {
        Iterable<T> iterable;
    }
}
