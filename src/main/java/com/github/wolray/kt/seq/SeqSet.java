package com.github.wolray.kt.seq;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wolray
 */
public interface SeqSet<T> extends Set<T>, Seq.Backed<T> {
    Set<T> proxy();

    static <T> SeqSet<T> of(Set<T> set) {
        return set instanceof SeqSet ? (SeqSet<T>)set : new SeqSet<T>() {
            @Override
            public Set<T> proxy() {
                return set;
            }

            @Override
            public String toString() {
                return set.toString();
            }
        };
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
    default boolean retainAll(Collection<?> c) {
        return proxy().retainAll(c);
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        return proxy().removeAll(c);
    }

    @Override
    default void clear() {
        proxy().clear();
    }
}
