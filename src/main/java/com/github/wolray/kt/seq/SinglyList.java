package com.github.wolray.kt.seq;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author wolray
 */
public class SinglyList<T> extends AbstractList<T> implements Seq.Backed<T> {
    private transient Node<T> dummy = new Node<>();
    private transient Node<T> last = dummy;
    private transient int size = 0;

    @Override
    public Collection<T> _collection() {
        return this;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(T t) {
        Node<T> it = new Node<>();
        it.t = t;
        last.next = it;
        last = it;
        size++;
        return true;
    }

    @Override
    public T get(int index) {
        Node<T> node = dummy;
        if (index >= size) {
            throw new IndexOutOfBoundsException(String.format("%d >= %d", index, size));
        }
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node.t;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> node = dummy;

            @Override
            public boolean hasNext() {
                return node.next != null;
            }

            @Override
            public T next() {
                node = node.next;
                return node.t;
            }
        };
    }

    @Override
    public void clear() {
        for (Node<T> node = dummy; node != null; ) {
            Node<T> next = node.next;
            node.next = null;
            node = next;
        }
        dummy = last = null;
        size = 0;
    }

    private static class Node<T> {
        T t;
        Node<T> next;
    }
}
