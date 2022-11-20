package com.github.wolray.kt.seq;

import java.util.*;

/**
 * @author wolray
 */
public interface SeqMap<K, V> extends Map<K, V>, Seq.Backed<Map.Entry<K, V>> {
    Map<K, V> map();
    String toString();

    static <K, V> SeqMap<K, V> of(Map<K, V> map) {
        return map instanceof SeqMap ? (SeqMap<K, V>)map : new SeqMap<K, V>() {
            @Override
            public Map<K, V> map() {
                return map;
            }

            @Override
            public String toString() {
                return map.toString();
            }
        };
    }

    static <K, V> SeqMap<K, V> hash() {
        return of(new HashMap<>());
    }

    static <K, V> SeqMap<K, V> tree(Comparator<K> comparator) {
        return of(new TreeMap<>(comparator));
    }

    @Override
    default Set<Entry<K, V>> backer() {
        return map().entrySet();
    }

    @Override
    default Iterator<Entry<K, V>> iterator() {
        return entrySet().iterator();
    }

    @Override
    default int size() {
        return map().size();
    }

    @Override
    default boolean isEmpty() {
        return map().isEmpty();
    }

    @Override
    default boolean containsKey(Object key) {
        return map().containsKey(key);
    }

    @Override
    default boolean containsValue(Object value) {
        return map().containsValue(value);
    }

    @Override
    default V get(Object key) {
        return map().get(key);
    }

    @Override
    default V put(K key, V value) {
        return map().put(key, value);
    }

    @Override
    default V remove(Object key) {
        return map().remove(key);
    }

    @Override
    default void putAll(Map<? extends K, ? extends V> m) {
        map().putAll(m);
    }

    @Override
    default void clear() {
        map().clear();
    }

    @Override
    default Set<K> keySet() {
        return map().keySet();
    }

    @Override
    default Collection<V> values() {
        return map().values();
    }

    @Override
    default Set<Entry<K, V>> entrySet() {
        return map().entrySet();
    }
}
