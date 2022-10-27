package com.github.wolray.kt.seq;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author wolray
 */
public class YieldSeq<T> implements Seq<T> {
    private transient Node<T> dummy = new Node<>();
    private transient Node<T> last = dummy;

    YieldSeq() {}

    public void yield(T t) {
        Node<T> it = new Node<>();
        it.t = t;
        last.next = it;
        last = it;
    }

    public void yieldAll(Iterable<T> iterable) {
        Node<T> it = new Node<>();
        it.iterable = iterable;
        last.next = it;
        last = it;
    }

    public void yieldAll(Iterator<T> iterator) {
        while (iterator.hasNext()) {
            this.yield(iterator.next());
        }
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
                    if (curr.iterable != null) {
                        iterator = curr.iterable.iterator();
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

    private static class Node<T> {
        T t;
        Iterable<T> iterable;
        Node<T> next;
    }
}
