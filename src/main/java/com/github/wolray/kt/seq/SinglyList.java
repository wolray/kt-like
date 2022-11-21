package com.github.wolray.kt.seq;

import java.util.*;

/**
 * @author wolray
 */
public class SinglyList<T> extends AbstractList<T> implements SeqList<T>, Queue<T> {
    private transient Node<T> dummy = new Node<>();
    private transient Node<T> last = dummy;
    private transient int size = 0;

    @Override
    public List<T> backer() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return dummy.next == null;
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
        if (index >= size) {
            throw new IndexOutOfBoundsException(String.format("%d >= %d", index, size));
        }
        Node<T> node = dummy;
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

    @Override
    public boolean offer(T t) {
        return add(t);
    }

    @Override
    public T remove() {
        T t = poll();
        if (t != null) {
            return t;
        }
        throw new NoSuchElementException();
    }

    @Override
    public T poll() {
        Node<T> first = dummy.next;
        if (first == null) {
            return null;
        }
        dummy.next = first.next;
        first.next = null;
        size--;
        return first.t;
    }

    @Override
    public T element() {
        T t = peek();
        if (t != null) {
            return t;
        }
        throw new NoSuchElementException();
    }

    @Override
    public T peek() {
        Node<T> first = dummy.next;
        if (first != null) {
            return first.t;
        }
        return null;
    }

    private static class Node<T> {
        T t;
        Node<T> next;
    }
}
