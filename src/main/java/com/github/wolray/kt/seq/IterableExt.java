package com.github.wolray.kt.seq;

import java.util.*;
import java.util.function.*;

/**
 * @author wolray
 */
public abstract class IterableExt<T> implements Iterable<T> {
    Integer size;

    void setSize(Iterable<?> iterable) {
        if (iterable instanceof Collection<?>) {
            size = ((Collection<?>)iterable).size();
        }
    }

    int sizeOrDefault() {
        return size != null ? size : 10;
    }

    public T get(int index) {
        if (index < 0 || size != null && index >= size) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        for (T t : this) {
            if (index-- == 0) {
                return t;
            }
        }
        return null;
    }

    public <K> Grouping<T, K> groupBy(Function<T, K> kFunction) {
        return new Grouping<>(this, kFunction);
    }

    public <E> E fold(E init, BiFunction<E, T, E> function) {
        E acc = init;
        for (T t : this) {
            acc = function.apply(acc, t);
        }
        return acc;
    }

    public <E> E foldBy(E des, BiConsumer<E, T> consumer) {
        for (T t : this) {
            consumer.accept(des, t);
        }
        return des;
    }

    public List<T> filterTo(Predicate<T> predicate) {
        return filterTo(new ArrayList<>(), predicate);
    }

    public List<T> filterTo(List<T> des, Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                des.add(t);
            }
        }
        return des;
    }

    public <K, V> Map<K, V> toMap(Function<T, K> kFunction, Function<T, V> vFunction) {
        return toMap(new HashMap<>(sizeOrDefault()), kFunction, vFunction);
    }

    public <K> Map<K, T> toMapBy(Function<T, K> kFunction) {
        return toMapBy(new HashMap<>(sizeOrDefault()), kFunction);
    }

    public <V> Map<T, V> toMapWith(Function<T, V> vFunction) {
        return toMapWith(new HashMap<>(sizeOrDefault()), vFunction);
    }

    public <K, V> Map<K, V> toMap(Map<K, V> des,
        Function<T, K> kFunction,
        Function<T, V> vFunction) {
        return foldBy(des, (res, t) ->
            res.put(kFunction.apply(t), vFunction.apply(t)));
    }

    public <K> Map<K, T> toMapBy(Map<K, T> des, Function<T, K> kFunction) {
        return foldBy(des, (res, t) -> res.put(kFunction.apply(t), t));
    }

    public <V> Map<T, V> toMapWith(Map<T, V> des, Function<T, V> vFunction) {
        return foldBy(des, (res, t) -> res.put(t, vFunction.apply(t)));
    }

    public <E> List<E> mapTo(Function<T, E> function) {
        return toCollection(new ArrayList<>(sizeOrDefault()), function);
    }

    public <E> List<E> mapTo(List<E> des, Function<T, E> function) {
        return toCollection(des, function);
    }

    public Set<T> toSet() {
        return toCollection(new HashSet<>(sizeOrDefault()));
    }

    public <E> Set<E> toSet(Function<T, E> function) {
        return toCollection(new HashSet<>(sizeOrDefault()), function);
    }

    public List<T> toList() {
        return toCollection(new ArrayList<>(sizeOrDefault()));
    }

    public List<T> toSinglyList() {
        return toCollection(new SinglyList<>());
    }

    public List<T> toBatchList() {
        return toCollection(new BatchList<>());
    }

    public List<T> toBatchList(int batchSize) {
        return toCollection(new BatchList<>(batchSize));
    }

    public <C extends Collection<T>> C toCollection(C des) {
        return foldBy(des, Collection::add);
    }

    public <E, C extends Collection<E>> C toCollection(C des, Function<T, E> function) {
        return foldBy(des, (res, t) -> res.add(function.apply(t)));
    }

    public String join(String sep, Function<T, String> function) {
        StringJoiner joiner = new StringJoiner(sep);
        for (T t : this) {
            joiner.add(function.apply(t));
        }
        return joiner.toString();
    }

    public <E> void zipWith(Iterable<E> es, BiConsumer<T, E> consumer) {
        Iterator<T> ti = iterator();
        Iterator<E> ei = es.iterator();
        while (ti.hasNext() && ei.hasNext()) {
            consumer.accept(ti.next(), ei.next());
        }
    }

    public void forEachIndexed(BiConsumer<Integer, T> consumer) {
        int index = 0;
        for (T t : this) {
            consumer.accept(index++, t);
        }
    }

    public Pair<List<T>, List<T>> partition(Predicate<T> predicate) {
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

    public boolean all(Predicate<T> predicate) {
        for (T t : this) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    public boolean any(Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    public boolean none(Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    public int count() {
        if (size != null) {
            return size;
        }
        int c = 0;
        for (T ignored : this) {
            c++;
        }
        size = c;
        return c;
    }

    public double sum(ToDoubleFunction<T> function) {
        double res = 0;
        for (T t : this) {
            res += function.applyAsDouble(t);
        }
        return res;
    }

    public int sumInt(ToIntFunction<T> function) {
        int res = 0;
        for (T t : this) {
            res += function.applyAsInt(t);
        }
        return res;
    }

    public long sumLong(ToLongFunction<T> function) {
        long res = 0;
        for (T t : this) {
            res += function.applyAsLong(t);
        }
        return res;
    }

    public T first() {
        Iterator<T> iterator = iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    public T first(Predicate<T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    public <V extends Comparable<V>> Pair<T, V> maxOf(Function<T, V> function) {
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

    public T maxWith(Comparator<T> comparator) {
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

    public <V extends Comparable<V>> Pair<T, V> minOf(Function<T, V> function) {
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

    public T minWith(Comparator<T> comparator) {
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
