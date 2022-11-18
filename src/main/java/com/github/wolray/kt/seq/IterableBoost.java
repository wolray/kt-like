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

    default <K, V> SeqMap<K, V> toMap(Function<T, K> kFunction, Function<T, V> vFunction) {
        return toMap(new HashMap<>(sizeOrDefault()), kFunction, vFunction);
    }

    default <K> SeqMap<K, T> toMapBy(Function<T, K> kFunction) {
        return toMapBy(new HashMap<>(sizeOrDefault()), kFunction);
    }

    default <V> SeqMap<T, V> toMapWith(Function<T, V> vFunction) {
        return toMapWith(new HashMap<>(sizeOrDefault()), vFunction);
    }

    default <K, V> SeqMap<K, V> toMap(Map<K, V> des,
        Function<T, K> kFunction,
        Function<T, V> vFunction) {
        return new SeqMap<>(foldBy(des, (res, t) ->
            res.put(kFunction.apply(t), vFunction.apply(t))));
    }

    default <K> SeqMap<K, T> toMapBy(Map<K, T> des, Function<T, K> kFunction) {
        return new SeqMap<>(foldBy(des, (res, t) -> res.put(kFunction.apply(t), t)));
    }

    default <V> SeqMap<T, V> toMapWith(Map<T, V> des, Function<T, V> vFunction) {
        return new SeqMap<>(foldBy(des, (res, t) -> res.put(t, vFunction.apply(t))));
    }

    default SeqSet<T> toSet() {
        return new SeqSet<>(toCollection(new HashSet<>(sizeOrDefault())));
    }

    default <E> SeqSet<E> toSet(Function<T, E> function) {
        return new SeqSet<>(toCollection(new HashSet<>(sizeOrDefault()), function));
    }

    default SeqList<T> toList() {
        if (this instanceof Seq.Backed) {
            Collection<T> collection = ((Seq.Backed<T>)this)._collection();
            return new SeqList<>(new ArrayList<>(collection));
        }
        return new SeqList<>(toCollection(new ArrayList<>(sizeOrDefault())));
    }

    default SinglyList<T> toSinglyList() {
        return toCollection(new SinglyList<>());
    }

    default BatchList<T> toBatchList() {
        return toCollection(new BatchList<>());
    }

    default BatchList<T> toBatchList(int batchSize) {
        return toCollection(new BatchList<>(batchSize));
    }

    default <C extends Collection<T>> C toCollection(C des) {
        return foldBy(des, Collection::add);
    }

    default <E, C extends Collection<E>> C toCollection(C des, Function<T, E> function) {
        return foldBy(des, (res, t) -> res.add(function.apply(t)));
    }

    default String join(String sep) {
        return join(sep, String::valueOf);
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

    default Pair<SeqList<T>, SeqList<T>> partition(Predicate<T> predicate) {
        SeqList<T> trueList = new SeqList<>(new BatchList<>());
        SeqList<T> falseList = new SeqList<>(new BatchList<>());
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

    default boolean any(Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    default boolean anyNot(Predicate<T> predicate) {
        return any(predicate.negate());
    }

    default boolean none(Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    default boolean all(Predicate<T> predicate) {
        return none(predicate.negate());
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

    default int countNot(Predicate<T> predicate) {
        return count(predicate.negate());
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
        double sum = 0;
        int count = 0;
        for (T t : this) {
            sum += function.applyAsDouble(t);
            count++;
        }
        return count > 0 ? sum / count : 0;
    }

    default double average(ToDoubleFunction<T> function, ToDoubleFunction<T> weightFunction) {
        double sum = 0, weight = 0;
        for (T t : this) {
            double v = function.applyAsDouble(t);
            double w = weightFunction.applyAsDouble(t);
            sum += v * w;
            weight += w;
        }
        return weight > 0 ? sum / weight : 0;
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

    default T firstNot(Predicate<T> predicate) {
        return first(predicate.negate());
    }

    default void printAll() {
        forEach(System.out::println);
    }

    default <V extends Comparable<V>> Pair<T, V> maxWith(Function<T, V> function) {
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

    default <V extends Comparable<V>> T maxBy(Function<T, V> function) {
        return max(Comparator.comparing(function));
    }

    default T max(Comparator<T> comparator) {
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

    default <V extends Comparable<V>> Pair<T, V> minWith(Function<T, V> function) {
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

    default <V extends Comparable<V>> T minBy(Function<T, V> function) {
        return min(Comparator.comparing(function));
    }

    default T min(Comparator<T> comparator) {
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
