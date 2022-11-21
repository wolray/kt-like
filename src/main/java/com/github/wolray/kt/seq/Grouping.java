package com.github.wolray.kt.seq;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

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

    public <E> SeqMap<K, List<E>> toList(Function<T, E> function) {
        return toCollection(ArrayList::new, function);
    }

    public <E> SeqMap<K, Set<E>> toSet(Function<T, E> function) {
        return toCollection(HashSet::new, function);
    }

    public <E, C extends Collection<E>> SeqMap<K, C> toCollection(Supplier<C> supplier, Function<T, E> function) {
        return foldBy(supplier, (res, t) -> res.add(function.apply(t)));
    }

    public SeqMap<K, List<T>> toList() {
        return toCollection(ArrayList::new);
    }

    public SeqMap<K, Set<T>> toSet() {
        return toCollection(HashSet::new);
    }

    public <C extends Collection<T>> SeqMap<K, C> toCollection(Supplier<C> supplier) {
        return foldBy(supplier, Collection::add);
    }

    public <E, V> SeqMap<K, Map<E, V>> toMap(Function<T, E> kFunction, Function<T, V> vFunction) {
        return toMap(HashMap::new, kFunction, vFunction, null);
    }

    public <E, V> SeqMap<K, Map<E, V>> toMap(Function<T, E> kFunction, Function<T, V> vFunction, BinaryOperator<V> merging) {
        return toMap(HashMap::new, kFunction, vFunction, merging);
    }

    public <E, V> SeqMap<K, Map<E, V>> toMap(Supplier<Map<E, V>> supplier, Function<T, E> kFunction, Function<T, V> vFunction) {
        return toMap(supplier, kFunction, vFunction, null);
    }

    public <E, V> SeqMap<K, Map<E, V>> toMap(Supplier<Map<E, V>> supplier,
        Function<T, E> kFunction, Function<T, V> vFunction,
        BinaryOperator<V> merging) {
        if (merging != null) {
            return foldBy(supplier, (res, t) -> res.merge(kFunction.apply(t), vFunction.apply(t), merging));
        } else {
            return foldBy(supplier, (res, t) -> res.computeIfAbsent(kFunction.apply(t), k -> vFunction.apply(t)));
        }
    }

    public <C> SeqMap<K, C> foldBy(Supplier<C> supplier, BiConsumer<C, T> consumer) {
        Map<K, C> map = makeMap();
        for (T t : iterable) {
            K k = function.apply(t);
            C e = map.computeIfAbsent(k, it -> supplier.get());
            consumer.accept(e, t);
        }
        return SeqMap.of(map);
    }

    public <V> SeqMap<K, V> fold(Function<T, V> mapper, BinaryOperator<V> operator) {
        Map<K, V> map = makeMap();
        for (T t : iterable) {
            K k = function.apply(t);
            map.merge(k, mapper.apply(t), operator);
        }
        return SeqMap.of(map);
    }

    public SeqMap<K, Integer> count() {
        return fold(t -> 1, Integer::sum);
    }

    public SeqMap<K, Double> sum(Function<T, Double> function) {
        return fold(function, Double::sum);
    }

    public SeqMap<K, Integer> sumInt(Function<T, Integer> function) {
        return fold(function, Integer::sum);
    }

    public SeqMap<K, Long> sumLong(Function<T, Long> function) {
        return fold(function, Long::sum);
    }

    public SeqMap<K, DoubleSummaryStatistics> summarize(ToDoubleFunction<T> function) {
        return foldBy(DoubleSummaryStatistics::new, (stat, t) -> stat.accept(function.applyAsDouble(t)));
    }

    public SeqMap<K, IntSummaryStatistics> summarizeInt(ToIntFunction<T> function) {
        return foldBy(IntSummaryStatistics::new, (stat, t) -> stat.accept(function.applyAsInt(t)));
    }

    public SeqMap<K, LongSummaryStatistics> summarizeLong(ToLongFunction<T> function) {
        return foldBy(LongSummaryStatistics::new, (stat, t) -> stat.accept(function.applyAsLong(t)));
    }

    public <E extends Comparable<E>> SeqMap<K, E> max(Function<T, E> function) {
        return fold(function, (t1, t2) -> t1.compareTo(t2) > 0 ? t1 : t2);
    }

    public <E> SeqMap<K, E> max(Function<T, E> function, Comparator<E> comparator) {
        return fold(function, (t1, t2) -> comparator.compare(t1, t2) > 0 ? t1 : t2);
    }

    public <E extends Comparable<E>> SeqMap<K, E> min(Function<T, E> function) {
        return fold(function, (t1, t2) -> t1.compareTo(t2) < 0 ? t1 : t2);
    }

    public <E> SeqMap<K, E> min(Function<T, E> function, Comparator<E> comparator) {
        return fold(function, (t1, t2) -> comparator.compare(t1, t2) < 0 ? t1 : t2);
    }
}
