package com.github.wolray.kt.seq;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class Grouping<T, K> {
    private final Iterable<T> iterable;
    private final Function<T, K> function;
    private Class<?> mapClass;

    public Grouping(Iterable<T> iterable, Function<T, K> function) {
        this.iterable = iterable;
        this.function = function;
    }

    @SuppressWarnings("rawtypes")
    public Grouping<T, K> destination(Class<? extends Map> mapClass) {
        this.mapClass = mapClass;
        return this;
    }

    private <E> Map<K, E> makeMap() {
        if (mapClass == null || HashMap.class.equals(mapClass)) {
            return new HashMap<>();
        }
        if (LinkedHashMap.class.equals(mapClass)) {
            return new LinkedHashMap<>();
        }
        if (TreeMap.class.equals(mapClass)) {
            return new TreeMap<>();
        }
        if (ConcurrentHashMap.class.equals(mapClass)) {
            return new ConcurrentHashMap<>();
        }
        return new HashMap<>();
    }

    public <E> Map<K, List<E>> toList(Function<T, E> function) {
        return toCollection(ArrayList::new, function);
    }

    public <E> Map<K, Set<E>> toSet(Function<T, E> function) {
        return toCollection(HashSet::new, function);
    }

    public <E, C extends Collection<E>> Map<K, C> toCollection(Supplier<C> supplier, Function<T, E> function) {
        return foldBy(supplier, (res, t) -> res.add(function.apply(t)));
    }

    public Map<K, List<T>> toList() {
        return toCollection(ArrayList::new);
    }

    public Map<K, Set<T>> toSet() {
        return toCollection(HashSet::new);
    }

    public <C extends Collection<T>> Map<K, C> toCollection(Supplier<C> supplier) {
        return foldBy(supplier, Collection::add);
    }

    public <E, V> Map<K, Map<E, V>> toMap(Function<T, E> kFunction, Function<T, V> vFunction) {
        return toMap(HashMap::new, kFunction, vFunction, null);
    }

    public <E, V> Map<K, Map<E, V>> toMap(Function<T, E> kFunction, Function<T, V> vFunction, BinaryOperator<V> merging) {
        return toMap(HashMap::new, kFunction, vFunction, merging);
    }

    public <E, V> Map<K, Map<E, V>> toMap(Supplier<Map<E, V>> supplier, Function<T, E> kFunction, Function<T, V> vFunction) {
        return toMap(supplier, kFunction, vFunction, null);
    }

    public <E, V> Map<K, Map<E, V>> toMap(Supplier<Map<E, V>> supplier,
        Function<T, E> kFunction, Function<T, V> vFunction,
        BinaryOperator<V> merging) {
        if (merging != null) {
            return foldBy(supplier, (res, t) -> res.merge(kFunction.apply(t), vFunction.apply(t), merging));
        } else {
            return foldBy(supplier, (res, t) -> res.computeIfAbsent(kFunction.apply(t), k -> vFunction.apply(t)));
        }
    }

    public <C> Map<K, C> foldBy(Supplier<C> supplier, BiConsumer<C, T> consumer) {
        Map<K, C> map = makeMap();
        for (T t : iterable) {
            K k = function.apply(t);
            C e = map.computeIfAbsent(k, it -> supplier.get());
            consumer.accept(e, t);
        }
        return map;
    }

    public <V> Map<K, V> fold(Function<T, V> mapper, BinaryOperator<V> operator) {
        Map<K, V> map = makeMap();
        for (T t : iterable) {
            K k = function.apply(t);
            map.merge(k, mapper.apply(t), operator);
        }
        return map;
    }

    public Map<K, Integer> count() {
        return fold(t -> 1, Integer::sum);
    }

    public Map<K, Double> sum(Function<T, Double> mapper) {
        return fold(mapper, Double::sum);
    }

    public Map<K, Integer> sumInt(Function<T, Integer> mapper) {
        return fold(mapper, Integer::sum);
    }

    public Map<K, Long> sumLong(Function<T, Long> mapper) {
        return fold(mapper, Long::sum);
    }
}
