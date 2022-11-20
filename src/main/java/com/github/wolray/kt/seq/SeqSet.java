package com.github.wolray.kt.seq;

import java.util.*;

/**
 * @author wolray
 */
public interface SeqSet<T> extends Set<T>, Seq.Backed<T> {
    Set<T> backer();
    String toString();

    static <T> SeqSet<T> of(Set<T> set) {
        return set instanceof SeqSet ? (SeqSet<T>)set : new SeqSet<T>() {
            @Override
            public Set<T> backer() {
                return set;
            }

            @Override
            public String toString() {
                return set.toString();
            }
        };
    }

    static <T> SeqSet<T> hash() {
        return of(new HashSet<>());
    }

    static <T> SeqSet<T> tree(Comparator<T> comparator) {
        return of(new TreeSet<>(comparator));
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
    default boolean retainAll(Collection<?> c) {
        return backer().retainAll(c);
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        return backer().removeAll(c);
    }

    @Override
    default void clear() {
        backer().clear();
    }
}
