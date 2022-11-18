package com.github.wolray.kt.seq;

import java.util.*;

/**
 * @author wolray
 */
public interface SeqList<T> extends List<T>, Seq.Backed<T> {
    List<T> proxy();
    String toString();

    static <T> SeqList<T> of(List<T> list) {
        return list instanceof SeqList ? (SeqList<T>)list : new SeqList<T>() {
            @Override
            public List<T> proxy() {
                return list;
            }

            @Override
            public String toString() {
                return list.toString();
            }
        };
    }

    @SafeVarargs
    static <T> SeqList<T> of(T... ts) {
        return of(Arrays.asList(ts));
    }

    static <T> SeqList<T> array() {
        return of(new ArrayList<>());
    }

    static <T> SeqList<T> linked() {
        return of(new LinkedList<>());
    }

    @Override
    default int size() {
        return proxy().size();
    }

    @Override
    default boolean isEmpty() {
        return proxy().isEmpty();
    }

    @Override
    default boolean contains(Object o) {
        return proxy().contains(o);
    }

    @Override
    default Iterator<T> iterator() {
        return proxy().iterator();
    }

    @Override
    default Object[] toArray() {
        return proxy().toArray();
    }

    @Override
    default <E> E[] toArray(E[] a) {
        return proxy().toArray(a);
    }

    @Override
    default boolean add(T t) {
        return proxy().add(t);
    }

    @Override
    default boolean remove(Object o) {
        return proxy().remove(o);
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        return proxy().containsAll(c);
    }

    @Override
    default boolean addAll(Collection<? extends T> c) {
        return proxy().addAll(c);
    }

    @Override
    default boolean addAll(int index, Collection<? extends T> c) {
        return proxy().addAll(c);
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        return proxy().removeAll(c);
    }

    @Override
    default boolean retainAll(Collection<?> c) {
        return proxy().retainAll(c);
    }

    @Override
    default void clear() {
        proxy().clear();
    }

    @Override
    default T get(int index) {
        return proxy().get(index);
    }

    @Override
    default T set(int index, T element) {
        return proxy().set(index, element);
    }

    @Override
    default void add(int index, T element) {
        proxy().add(index, element);
    }

    @Override
    default T remove(int index) {
        return proxy().remove(index);
    }

    @Override
    default int indexOf(Object o) {
        return proxy().indexOf(o);
    }

    @Override
    default int lastIndexOf(Object o) {
        return proxy().lastIndexOf(o);
    }

    @Override
    default ListIterator<T> listIterator() {
        return proxy().listIterator();
    }

    @Override
    default ListIterator<T> listIterator(int index) {
        return proxy().listIterator(index);
    }

    @Override
    default List<T> subList(int fromIndex, int toIndex) {
        return proxy().subList(fromIndex, toIndex);
    }
}
