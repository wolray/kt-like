package com.github.wolray.kt.seq;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author wolray
 */
public interface IterableBoost<T> extends Iterable<T> {
    default int sizeOrDefault() {
        return 10;
    }

    default T get(int index) {
        if (Seq.Backed.outSize(this, index)) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        for (T t : this) {
            if (index-- == 0) {
                return t;
            }
        }
        return null;
    }

    default <K> Grouping<T, K> groupBy(Function<T, K> kFunction) {
        return new Grouping<>(this, kFunction);
    }

    default <E> E fold(E init, BiFunction<E, T, E> function) {
        E acc = init;
        for (T t : this) {
            acc = function.apply(acc, t);
        }
        return acc;
    }

    default <E> E foldBy(E des, BiConsumer<E, T> consumer) {
        for (T t : this) {
            consumer.accept(des, t);
        }
        return des;
    }

    default List<T> filterTo(Predicate<T> predicate) {
        return filterTo(new ArrayList<>(), predicate);
    }

    default List<T> filterTo(List<T> des, Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                des.add(t);
            }
        }
        return des;
    }

    default <K, V> Map<K, V> toMap(Function<T, K> kFunction, Function<T, V> vFunction) {
        return toMap(new HashMap<>(sizeOrDefault()), kFunction, vFunction);
    }

    default <K> Map<K, T> toMapBy(Function<T, K> kFunction) {
        return toMapBy(new HashMap<>(sizeOrDefault()), kFunction);
    }

    default <V> Map<T, V> toMapWith(Function<T, V> vFunction) {
        return toMapWith(new HashMap<>(sizeOrDefault()), vFunction);
    }

    default <K, V> Map<K, V> toMap(Map<K, V> des,
        Function<T, K> kFunction,
        Function<T, V> vFunction) {
        return foldBy(des, (res, t) ->
            res.put(kFunction.apply(t), vFunction.apply(t)));
    }

    default <K> Map<K, T> toMapBy(Map<K, T> des, Function<T, K> kFunction) {
        return foldBy(des, (res, t) -> res.put(kFunction.apply(t), t));
    }

    default <V> Map<T, V> toMapWith(Map<T, V> des, Function<T, V> vFunction) {
        return foldBy(des, (res, t) -> res.put(t, vFunction.apply(t)));
    }

    default <E> List<E> mapTo(Function<T, E> function) {
        return toCollection(new ArrayList<>(sizeOrDefault()), function);
    }

    default <E> List<E> mapTo(List<E> des, Function<T, E> function) {
        return toCollection(des, function);
    }

    default Set<T> toSet() {
        return toCollection(new HashSet<>(sizeOrDefault()));
    }

    default <E> Set<E> toSet(Function<T, E> function) {
        return toCollection(new HashSet<>(sizeOrDefault()), function);
    }

    default List<T> toList() {
        return toCollection(new ArrayList<>(sizeOrDefault()));
    }

    default List<T> toSinglyList() {
        return toCollection(new SinglyList<>());
    }

    default List<T> toBatchList() {
        return toCollection(new BatchList<>());
    }

    default List<T> toBatchList(int batchSize) {
        return toCollection(new BatchList<>(batchSize));
    }

    default <C extends Collection<T>> C toCollection(C des) {
        return foldBy(des, Collection::add);
    }

    default <E, C extends Collection<E>> C toCollection(C des, Function<T, E> function) {
        return foldBy(des, (res, t) -> res.add(function.apply(t)));
    }

    default String join(String sep, Function<T, String> function) {
        StringJoiner joiner = new StringJoiner(sep);
        for (T t : this) {
            joiner.add(function.apply(t));
        }
        return joiner.toString();
    }

    default <E> void zipWith(Iterable<E> es, BiConsumer<T, E> consumer) {
        Iterator<T> ti = iterator();
        Iterator<E> ei = es.iterator();
        while (ti.hasNext() && ei.hasNext()) {
            consumer.accept(ti.next(), ei.next());
        }
    }

    default void forEachIndexed(BiConsumer<Integer, T> consumer) {
        int index = 0;
        for (T t : this) {
            consumer.accept(index++, t);
        }
    }

    default Pair<List<T>, List<T>> partition(Predicate<T> predicate) {
        List<T> trueList = new BatchList<>();
        List<T> falseList = new BatchList<>();
        for (T t : this) {
            if (predicate.test(t)) {
                trueList.add(t);
            } else {
                falseList.add(t);
            }
        }
        return new Pair<>(trueList, falseList);
    }

    default Stream<T> stream(boolean parallel) {
        return StreamSupport.stream(spliterator(), parallel);
    }

    @SuppressWarnings("unchecked")
    default <E> E[] toArray(IntFunction<E[]> generator) {
        List<T> list = toBatchList();
        E[] res = generator.apply(list.size());
        int i = 0;
        for (T t : list) {
            res[i++] = (E)t;
        }
        return res;
    }

    default boolean all(Predicate<T> predicate) {
        for (T t : this) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    default boolean any(Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    default boolean none(Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    default int count() {
        int c = 0;
        for (T ignored : this) {
            c++;
        }
        return c;
    }

    default int count(Predicate<T> predicate) {
        int c = 0;
        for (T t : this) {
            if (predicate.test(t)) {
                c++;
            }
        }
        return c;
    }

    default double sum(ToDoubleFunction<T> function) {
        double res = 0;
        for (T t : this) {
            res += function.applyAsDouble(t);
        }
        return res;
    }

    default int sumInt(ToIntFunction<T> function) {
        int res = 0;
        for (T t : this) {
            res += function.applyAsInt(t);
        }
        return res;
    }

    default long sumLong(ToLongFunction<T> function) {
        long res = 0;
        for (T t : this) {
            res += function.applyAsLong(t);
        }
        return res;
    }

    default double average(ToDoubleFunction<T> function) {
        double res = 0;
        int count = 0;
        for (T t : this) {
            res += function.applyAsDouble(t);
            count++;
        }
        return count > 0 ? res / count : 0;
    }

    default DoubleSummaryStatistics summarize(ToDoubleFunction<T> function) {
        return foldBy(new DoubleSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsDouble(t)));
    }

    default IntSummaryStatistics summarizeInt(ToIntFunction<T> function) {
        return foldBy(new IntSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsInt(t)));
    }

    default LongSummaryStatistics summarizeLong(ToLongFunction<T> function) {
        return foldBy(new LongSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsLong(t)));
    }

    default T first() {
        Iterator<T> iterator = iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    default T firstNotNull() {
        return first(Objects::nonNull);
    }

    default T first(Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    default void printAll() {
        forEach(System.out::println);
    }

    default <V extends Comparable<V>> Pair<T, V> maxOf(Function<T, V> function) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        T max = iterator.next();
        V maxValue = function.apply(max);
        while (iterator.hasNext()) {
            T t = iterator.next();
            V v = function.apply(t);
            if (v.compareTo(maxValue) > 0) {
                max = t;
                maxValue = v;
            }
        }
        return new Pair<>(max, maxValue);
    }

    default T maxWith(Comparator<T> comparator) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        T max = iterator.next();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (comparator.compare(t, max) > 0) {
                max = t;
            }
        }
        return max;
    }

    default <V extends Comparable<V>> Pair<T, V> minOf(Function<T, V> function) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        T min = iterator.next();
        V minValue = function.apply(min);
        while (iterator.hasNext()) {
            T t = iterator.next();
            V v = function.apply(t);
            if (v.compareTo(minValue) < 0) {
                min = t;
                minValue = v;
            }
        }
        return new Pair<>(min, minValue);
    }

    default T minWith(Comparator<T> comparator) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        T max = iterator.next();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (comparator.compare(t, max) < 0) {
                max = t;
            }
        }
        return max;
    }
}
