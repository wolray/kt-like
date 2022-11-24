package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * @author wolray
 */
public class SinglyList<T> implements AdderList<T>, Queue<T> {
    private transient Node<T> head;
    private transient Node<T> last;
    private transient int size = 0;

    @Override
    public String toString() {
        return toStr();
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> node = head;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public T next() {
                Node<T> curr = node;
                node = node.next;
                return curr.t;
            }
        };
    }

    @Override
    public boolean add(T t) {
        Node<T> it = new Node<>();
        it.t = t;
        if (head != null) {
            last.next = it;
        } else {
            head = it;
        }
        last = it;
        size++;
        return true;
    }

    @Override
    public void clear() {
        for (Node<T> node = head; node != null; ) {
            Node<T> next = node.next;
            node.next = null;
            node = next;
        }
        head = last = null;
        size = 0;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException(String.format("%d, %d", index, size));
        }
        Node<T> node = head;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node.t;
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
        if (head == null) {
            return null;
        }
        Node<T> first = head;
        head = first.next;
        first.next = null;
        size--;
        return first.t;
    }

    @Override
    public T element() {
        if (head != null) {
            return head.t;
        }
        throw new NoSuchElementException();
    }

    @Override
    public T peek() {
        if (head != null) {
            return head.t;
        }
        return null;
    }

    private static class Node<T> {
        T t;
        Node<T> next;
    }
}
