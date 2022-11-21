package com.github.wolray.kt.seq;

import java.util.*;

/**
 * @author wolray
 */
public interface SeqList<T> extends List<T>, Seq.Backed<T> {
    @Override
    List<T> backer();
    @Override
    String toString();

    static <T> SeqList<T> of(List<T> list) {
        return list instanceof SeqList ? (SeqList<T>)list : new SeqList<T>() {
            @Override
            public List<T> backer() {
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
        return backer().size();
    }

    @Override
    default boolean isEmpty() {
        return backer().isEmpty();
    }

    @Override
    default boolean contains(Object o) {
        return backer().contains(o);
    }

    @Override
    default Iterator<T> iterator() {
        return backer().iterator();
    }

    @Override
    default Object[] toArray() {
        return backer().toArray();
    }

    @Override
    default <E> E[] toArray(E[] a) {
        return backer().toArray(a);
    }

    @Override
    default boolean add(T t) {
        return backer().add(t);
    }

    @Override
    default boolean remove(Object o) {
        return backer().remove(o);
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        return backer().containsAll(c);
    }

    @Override
    default boolean addAll(Collection<? extends T> c) {
        return backer().addAll(c);
    }

    @Override
    default boolean addAll(int index, Collection<? extends T> c) {
        return backer().addAll(c);
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        return backer().removeAll(c);
    }

    @Override
    default boolean retainAll(Collection<?> c) {
        return backer().retainAll(c);
    }

    @Override
    default void clear() {
        backer().clear();
    }

    @Override
    default T get(int index) {
        return backer().get(index);
    }

    @Override
    default T set(int index, T element) {
        return backer().set(index, element);
    }

    @Override
    default void add(int index, T element) {
        backer().add(index, element);
    }

    @Override
    default T remove(int index) {
        return backer().remove(index);
    }

    @Override
    default int indexOf(Object o) {
        return backer().indexOf(o);
    }

    @Override
    default int lastIndexOf(Object o) {
        return backer().lastIndexOf(o);
    }

    @Override
    default ListIterator<T> listIterator() {
        return backer().listIterator();
    }

    @Override
    default ListIterator<T> listIterator(int index) {
        return backer().listIterator(index);
    }

    @Override
    default List<T> subList(int fromIndex, int toIndex) {
        return backer().subList(fromIndex, toIndex);
    }
}
