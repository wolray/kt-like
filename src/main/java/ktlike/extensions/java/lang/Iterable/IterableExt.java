package ktlike.extensions.java.lang.Iterable;

import com.github.wolray.kt.seq.*;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Extension
public class IterableExt {
    private static <T> int sizeOrDefault(Iterable<T> iterable) {
        return iterable instanceof Collection<?> ? ((Collection<T>)iterable).size() : 10;
    }

    public static <T> T get(@This Iterable<T> self, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        for (T t : self) {
            if (index-- == 0) {
                return t;
            }
        }
        return null;
    }

    public static <T, K> Grouping<T, K> groupBy(@This Iterable<T> self, Function<T, K> kFunction) {
        return new Grouping<>(self, kFunction);
    }

    public static <T, E> E fold(@This Iterable<T> self, E init, BiFunction<E, T, E> function) {
        E acc = init;
        for (T t : self) {
            acc = function.apply(acc, t);
        }
        return acc;
    }

    public static <T, E> E foldBy(@This Iterable<T> self, E des, BiConsumer<E, T> consumer) {
        for (T t : self) {
            consumer.accept(des, t);
        }
        return des;
    }

    public static <T> List<T> filterTo(@This Iterable<T> self, Predicate<T> predicate) {
        return filterTo(self, new ArrayList<>(), predicate);
    }

    public static <T> List<T> filterTo(@This Iterable<T> self, List<T> des, Predicate<T> predicate) {
        for (T t : self) {
            if (predicate.test(t)) {
                des.add(t);
            }
        }
        return des;
    }

    public static <T, K, V> Map<K, V> toMap(@This Iterable<T> self, Function<T, K> kFunction, Function<T, V> vFunction) {
        return toMap(self, new HashMap<>(sizeOrDefault(self)), kFunction, vFunction);
    }

    public static <T, K> Map<K, T> toMapBy(@This Iterable<T> self, Function<T, K> kFunction) {
        return toMapBy(self, new HashMap<>(sizeOrDefault(self)), kFunction);
    }

    public static <T, V> Map<T, V> toMapWith(@This Iterable<T> self, Function<T, V> vFunction) {
        return toMapWith(self, new HashMap<>(sizeOrDefault(self)), vFunction);
    }

    public static <T, K, V> Map<K, V> toMap(@This Iterable<T> self, Map<K, V> des,
        Function<T, K> kFunction,
        Function<T, V> vFunction) {
        return foldBy(self, des, (res, t) ->
            res.put(kFunction.apply(t), vFunction.apply(t)));
    }

    public static <T, K> Map<K, T> toMapBy(@This Iterable<T> self, Map<K, T> des, Function<T, K> kFunction) {
        return foldBy(self, des, (res, t) -> res.put(kFunction.apply(t), t));
    }

    public static <T, V> Map<T, V> toMapWith(@This Iterable<T> self, Map<T, V> des, Function<T, V> vFunction) {
        return foldBy(self, des, (res, t) -> res.put(t, vFunction.apply(t)));
    }

    public static <T, E> List<E> mapTo(@This Iterable<T> self, Function<T, E> function) {
        return toCollection(self, new ArrayList<>(sizeOrDefault(self)), function);
    }

    public static <T, E> List<E> mapTo(@This Iterable<T> self, List<E> des, Function<T, E> function) {
        return toCollection(self, des, function);
    }

    public static <T> Set<T> toSet(@This Iterable<T> self) {
        return toCollection(self, new HashSet<>(sizeOrDefault(self)));
    }

    public static <T, E> Set<E> toSet(@This Iterable<T> self, Function<T, E> function) {
        return toCollection(self, new HashSet<>(sizeOrDefault(self)), function);
    }

    public static <T> List<T> toList(@This Iterable<T> self) {
        return toCollection(self, new ArrayList<>(sizeOrDefault(self)));
    }

    public static <T> List<T> toSinglyList(@This Iterable<T> self) {
        return toCollection(self, new SinglyList<>());
    }

    public static <T> List<T> toBatchList(@This Iterable<T> self) {
        return toCollection(self, new BatchList<>());
    }

    public static <T> List<T> toBatchList(@This Iterable<T> self, int batchSize) {
        return toCollection(self, new BatchList<>(batchSize));
    }

    public static <T, C extends Collection<T>> C toCollection(@This Iterable<T> self, C des) {
        return foldBy(self, des, Collection::add);
    }

    public static <T, E, C extends Collection<E>> C toCollection(@This Iterable<T> self, C des, Function<T, E> function) {
        return foldBy(self, des, (res, t) -> res.add(function.apply(t)));
    }

    public static <T> String join(@This Iterable<T> self, String sep, Function<T, String> function) {
        StringJoiner joiner = new StringJoiner(sep);
        for (T t : self) {
            joiner.add(function.apply(t));
        }
        return joiner.toString();
    }

    public static <T, E> void zipWith(@This Iterable<T> self, Iterable<E> es, BiConsumer<T, E> consumer) {
        Iterator<T> ti = self.iterator();
        Iterator<E> ei = es.iterator();
        while (ti.hasNext() && ei.hasNext()) {
            consumer.accept(ti.next(), ei.next());
        }
    }

    public static <T> void forEachIndexed(@This Iterable<T> self, BiConsumer<Integer, T> consumer) {
        int index = 0;
        for (T t : self) {
            consumer.accept(index++, t);
        }
    }

    public static <T> Pair<List<T>, List<T>> partition(@This Iterable<T> self, Predicate<T> predicate) {
        List<T> trueList = new BatchList<>();
        List<T> falseList = new BatchList<>();
        for (T t : self) {
            if (predicate.test(t)) {
                trueList.add(t);
            } else {
                falseList.add(t);
            }
        }
        return new Pair<>(trueList, falseList);
    }

    public static <T> boolean all(@This Iterable<T> self, Predicate<T> predicate) {
        for (T t : self) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean any(@This Iterable<T> self, Predicate<T> predicate) {
        for (T t : self) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean none(@This Iterable<T> self, Predicate<T> predicate) {
        for (T t : self) {
            if (predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> int count(@This Iterable<T> self) {
        int c = 0;
        for (T ignored : self) {
            c++;
        }
        return c;
    }

    public static <T> int count(@This Iterable<T> self, Predicate<T> predicate) {
        int c = 0;
        for (T t : self) {
            if (predicate.test(t)) {
                c++;
            }
        }
        return c;
    }

    public static <T> double sum(@This Iterable<T> self, ToDoubleFunction<T> function) {
        double res = 0;
        for (T t : self) {
            res += function.applyAsDouble(t);
        }
        return res;
    }

    public static <T> int sumInt(@This Iterable<T> self, ToIntFunction<T> function) {
        int res = 0;
        for (T t : self) {
            res += function.applyAsInt(t);
        }
        return res;
    }

    public static <T> long sumLong(@This Iterable<T> self, ToLongFunction<T> function) {
        long res = 0;
        for (T t : self) {
            res += function.applyAsLong(t);
        }
        return res;
    }

    public static <T> double average(@This Iterable<T> self, ToDoubleFunction<T> function) {
        double res = 0;
        int count = 0;
        for (T t : self) {
            res += function.applyAsDouble(t);
            count++;
        }
        return count > 0 ? res / count : 0;
    }

    public static <T> DoubleSummaryStatistics summarize(@This Iterable<T> self, ToDoubleFunction<T> function) {
        return foldBy(self, new DoubleSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsDouble(t)));
    }

    public static <T> IntSummaryStatistics summarizeInt(@This Iterable<T> self, ToIntFunction<T> function) {
        return foldBy(self, new IntSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsInt(t)));
    }

    public static <T> LongSummaryStatistics summarizeLong(@This Iterable<T> self, ToLongFunction<T> function) {
        return foldBy(self, new LongSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsLong(t)));
    }

    public static <T> T first(@This Iterable<T> self) {
        Iterator<T> iterator = self.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    public static <T> T firstNotNull(@This Iterable<T> self) {
        return first(self, Objects::nonNull);
    }

    public static <T> T first(@This Iterable<T> self, Predicate<T> predicate) {
        for (T t : self) {
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    public static <T, V extends Comparable<V>> Pair<T, V> maxOf(@This Iterable<T> self, Function<T, V> function) {
        Iterator<T> iterator = self.iterator();
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

    public static <T> T maxWith(@This Iterable<T> self, Comparator<T> comparator) {
        Iterator<T> iterator = self.iterator();
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

    public static <T, V extends Comparable<V>> Pair<T, V> minOf(@This Iterable<T> self, Function<T, V> function) {
        Iterator<T> iterator = self.iterator();
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

    public static <T> T minWith(@This Iterable<T> self, Comparator<T> comparator) {
        Iterator<T> iterator = self.iterator();
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

    @Extension
    @SafeVarargs
    public static <T> Iterable<T> of(T... t) {
        return Arrays.asList(t);
    }

    @Extension
    public static <T> Iterable<T> joinAll(Iterable<? extends Iterable<T>> iterables) {
        return () -> PickItr.flat(iterables.iterator());
    }

    @Extension
    @SafeVarargs
    public static <T> Iterable<T> joinAll(Iterable<T>... iterable) {
        return joinAll(Arrays.asList(iterable));
    }

    @Extension
    public static Iterable<Integer> range(int until) {
        return range(0, until, 1);
    }

    @Extension
    public static Iterable<Integer> range(int start, int until) {
        return range(start, until, 1);
    }

    @Extension
    public static Iterable<Integer> range(int start, int until, int step) {
        if (start > until) {
            throw new IllegalArgumentException();
        }
        return () -> CountItr.range(start, until, step);
    }

    @Extension
    public static <T> Iterable<T> repeat(T t, int n) {
        return () -> CountItr.repeat(t, n);
    }

    @Extension
    public static <T> Iterable<T> gen(Supplier<T> supplier) {
        return () -> PickItr.gen(supplier);
    }

    @Extension
    public static Iterable<Integer> gen(int seed, IntUnaryOperator operator) {
        return () -> PickItr.gen(new int[]{seed}, a -> {
            int t = a[0];
            a[0] = operator.applyAsInt(a[0]);
            return t;
        });
    }

    @Extension
    public static <T> Iterable<T> gen(T seed, UnaryOperator<T> operator) {
        return () -> PickItr.gen(new Mutable<>(seed), m -> {
            T t = m.it;
            m.it = operator.apply(m.it);
            return t;
        });
    }

    @Extension
    public static Iterable<Integer> gen(int seed1, int seed2, IntBinaryOperator operator) {
        return joinAll(Arrays.asList(seed1, seed2),
            () -> PickItr.gen(new int[]{seed1, seed2}, a ->
                a[1] = operator.applyAsInt(a[0], a[0] = a[1])));
    }

    @Extension
    public static <T> Iterable<T> gen(T seed1, T seed2, BinaryOperator<T> operator) {
        return joinAll(Arrays.asList(seed1, seed2),
            () -> PickItr.gen(new MutablePair<>(seed1, seed2), p ->
                p.second = operator.apply(p.first, p.first = p.second)));
    }

    @Extension
    public static <T> Iterable<T> by(Consumer<Yield<T>> yieldConsumer) {
        return by(10, yieldConsumer);
    }

    @Extension
    public static <T> Iterable<T> by(int batchSize, Consumer<Yield<T>> yieldConsumer) {
        Yield<T> yield = new Yield<>(batchSize);
        yieldConsumer.accept(yield);
        return joinAll(yield.list);
    }

    @Extension
    public static <S, T> Iterable<T> recurBy(Supplier<S> seedSupplier, Function<S, T> function) {
        return () -> PickItr.gen(seedSupplier.get(), function);
    }

    public static <T, E> Iterable<E> recur(@This Iterable<T> self, Function<Iterator<T>, E> function) {
        return recurBy(self::iterator, function);
    }

    public static <T, S, E> Iterable<E> map(@This Iterable<T> self, Supplier<S> stateSupplier, BiFunction<T, S, E> function) {
        return () -> MapItr.of(self.iterator(), stateSupplier.get(), function);
    }

    public static <T, E> Iterable<E> map(@This Iterable<T> self, Function<T, E> function) {
        return () -> MapItr.of(self.iterator(), function);
    }

    public static <T, E> Iterable<E> mapNotNull(@This Iterable<T> self, Function<T, E> function) {
        return recur(self, itr -> {
            while (itr.hasNext()) {
                E e = function.apply(itr.next());
                if (e != null) {
                    return e;
                }
            }
            return PickItr.stop();
        });
    }

    public static <T> Iterable<T> filter(@This Iterable<T> self, Predicate<T> predicate) {
        return recur(self, itr -> {
            while (itr.hasNext()) {
                T t = itr.next();
                if (predicate.test(t)) {
                    return t;
                }
            }
            return PickItr.stop();
        });
    }

    public static <T> Iterable<T> filterNotNull(@This Iterable<T> self) {
        return filter(self, Objects::nonNull);
    }

    public static <T> Iterable<T> distinct(@This Iterable<T> self) {
        return distinctBy(self, it -> it);
    }

    public static <T, E> Iterable<T> distinctBy(@This Iterable<T> self, Function<T, E> function) {
        return () -> PickItr.distinctBy(self.iterator(), function);
    }

    public static <T> Iterable<T> takeWhile(@This Iterable<T> self, Predicate<T> predicate) {
        return recur(self, itr -> {
            if (itr.hasNext()) {
                T t = itr.next();
                if (predicate.test(t)) {
                    return t;
                }
            }
            return PickItr.stop();
        });
    }

    public static <T> Iterable<T> untilNull(@This Iterable<T> self) {
        return takeWhile(self, Objects::nonNull);
    }

    public static <T> Iterable<T> take(@This Iterable<T> self, int n) {
        return n <= 0 ? Collections.emptyList()
            : () -> CountItr.take(self.iterator(), n);
    }

    public static <T> Iterable<T> drop(@This Iterable<T> self, int n) {
        return n <= 0 ? self
            : () -> CountItr.drop(self.iterator(), n);
    }

    public static <T> Iterable<T> dropWhile(@This Iterable<T> self, Predicate<T> predicate) {
        return () -> PickItr.dropWhile(self.iterator(), predicate);
    }

    public static <T> Iterable<List<T>> chunked(@This Iterable<T> self, int size) {
        return () -> PickItr.window(self.iterator(), size);
    }

    public static <T, E> Iterable<E> runningFold(@This Iterable<T> self, E init, BiFunction<T, E, E> function) {
        return () -> MapItr.of(self.iterator(), new Mutable<>(init),
            (t, m) -> m.it = function.apply(t, m.it));
    }

    public static <T> Iterable<T> onEach(@This Iterable<T> self, Consumer<T> consumer) {
        return map(self, it -> {
            consumer.accept(it);
            return it;
        });
    }

    public static <T, E> E let(@This Iterable<T> self, Function<Iterable<T>, E> function) {
        return function.apply(self);
    }

    public static <T, R> Iterable<R> flatMap(@This Iterable<T> self, Function<T, Iterable<R>> function) {
        return joinAll(map(self, function));
    }

    public static <T> Iterable<T> append(@This Iterable<T> self, Iterable<T> iterable) {
        return joinAll(Arrays.asList(self, iterable));
    }

    @SafeVarargs
    public static <T> Iterable<T> append(@This Iterable<T> self, T... t) {
        return append(self, Arrays.asList(t));
    }

    public static <T> Iterable<IndexedValue<T>> withIndex(@This Iterable<T> self) {
        return () -> MapItr.of(self.iterator(), new int[1],
            (t, a) -> new IndexedValue<>(a[0]++, t));
    }

    public static <T, B> Iterable<Pair<T, B>> zip(@This Iterable<T> self, Iterable<B> bs) {
        return zip(self, bs, Pair::new);
    }

    public static <T, B, R> Iterable<R> zip(@This Iterable<T> self, Iterable<B> bs, BiFunction<T, B, R> function) {
        return () -> new Iterator<R>() {
            Iterator<T> ti = self.iterator();
            Iterator<B> bi = bs.iterator();

            @Override
            public boolean hasNext() {
                return ti.hasNext() && bi.hasNext();
            }

            @Override
            public R next() {
                return function.apply(ti.next(), bi.next());
            }
        };
    }

    public static <T, B, C> Iterable<Triple<T, B, C>> zip(@This Iterable<T> self, Iterable<B> bs, Iterable<C> cs) {
        return () -> new Iterator<Triple<T, B, C>>() {
            Iterator<T> ai = self.iterator();
            Iterator<B> bi = bs.iterator();
            Iterator<C> ci = cs.iterator();

            @Override
            public boolean hasNext() {
                return ai.hasNext() && bi.hasNext() && ci.hasNext();
            }

            @Override
            public Triple<T, B, C> next() {
                return new Triple<>(ai.next(), bi.next(), ci.next());
            }
        };
    }

    @Extension
    public static <T, B> Pair<Iterable<T>, Iterable<B>> unzip(Iterable<Pair<T, B>> pairs) {
        return new Pair<>(map(pairs, p -> p.first), map(pairs, p -> p.second));
    }

    @Extension
    public static <T, B, C> Triple<Iterable<T>, Iterable<B>, Iterable<C>> unzip3(Iterable<Triple<T, B, C>> triples) {
        return new Triple<>(map(triples, p -> p.first), map(triples, p -> p.second), map(triples, p -> p.third));
    }

    public static <T, E> Iterable<Pair<T, E>> cartesian(@This Iterable<T> self, Iterable<E> es) {
        return cartesian(self, es, Pair::new);
    }

    public static <T, E, R> Iterable<R> cartesian(@This Iterable<T> self, Iterable<E> es, BiFunction<T, E, R> function) {
        return flatMap(self, t -> map(es, e -> function.apply(t, e)));
    }

    public static <T> Stream<T> stream(@This Iterable<T> self, boolean parallel) {
        return StreamSupport.stream(self.spliterator(), parallel);
    }

    public static <T> String str(@This Iterable<T> self) {
        return join(self, "=>", String::valueOf);
    }

    @SafeVarargs
    public static <T> void assertTo(@This Iterable<T> self, T... ts) {
        Iterator<T> iterator = self.iterator();
        for (T t : ts) {
            assert iterator.hasNext() && Objects.equals(iterator.next(), t) : "mismatched";
        }
        assert !iterator.hasNext() : "exceeded";
    }

    public static class IndexedValue<T> {
        public final int index;
        public final T value;

        public IndexedValue(int index, T value) {
            this.index = index;
            this.value = value;
        }
    }
}