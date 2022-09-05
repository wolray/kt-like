package com.github.wolray.kt.seq;

import com.github.wolray.kt.util.Any;
import com.github.wolray.kt.util.Functions;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author wolray
 */
public abstract class Seq<T> extends IterableExt<T> {
    static <T> Seq<T> convert(Iterable<T> iterable) {
        return new Seq<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }
        };
    }

    public static <T> Seq<T> of(Iterable<T> iterable) {
        if (iterable instanceof Seq<?>) {
            return (Seq<T>)iterable;
        }
        Seq<T> seq = convert(iterable);
        seq.setSize(iterable);
        return seq;
    }

    @SafeVarargs
    public static <T> Seq<T> of(T... ts) {
        return of(Arrays.asList(ts));
    }

    public static <K, V> Seq<Map.Entry<K, V>> of(Map<K, V> map) {
        return of(map.entrySet());
    }

    @SafeVarargs
    public static <T> Seq<T> join(Iterable<T>... iterable) {
        return join(Arrays.asList(iterable));
    }

    public static <T> Seq<T> join(Iterable<? extends Iterable<T>> iterables) {
        return convert(() -> PickItr.flat(iterables.iterator()));
    }

    public static <T> Seq<T> empty() {
        return convert(Collections::emptyIterator);
    }

    public static Seq<Integer> range(int until) {
        return range(0, until, 1);
    }

    public static Seq<Integer> range(int start, int until) {
        return range(start, until, 1);
    }

    public static Seq<Integer> range(int start, int until, int step) {
        return convert(() -> CountItr.range(start, until, step));
    }

    public static <T> Seq<T> repeat(T t, int n) {
        return convert(() -> CountItr.repeat(t, n));
    }

    public static <S, T> Seq<T> recur(Supplier<S> seedSupplier, Function<S, T> function) {
        return convert(() -> PickItr.gen(seedSupplier.get(), function));
    }

    public static <T> Seq<T> genUntilNull(Supplier<T> supplier) {
        return gen(Functions.orBy(supplier, PickItr::stop));
    }

    public static <T> Seq<T> gen(Supplier<T> supplier) {
        return convert(() -> PickItr.gen(supplier));
    }

    public static Seq<Integer> gen(int seed, IntUnaryOperator operator) {
        return convert(() -> PickItr.gen(new int[]{seed}, a -> {
            int t = a[0];
            a[0] = operator.applyAsInt(a[0]);
            return t;
        }));
    }

    public static <T> Seq<T> gen(T seed, UnaryOperator<T> operator) {
        return convert(() -> PickItr.gen(new Mutable<>(seed), m -> {
            T t = m.it;
            m.it = operator.apply(m.it);
            return t;
        }));
    }

    public static Seq<Integer> gen(int seed1, int seed2, IntBinaryOperator operator) {
        return join(Arrays.asList(seed1, seed2),
            () -> PickItr.gen(new int[]{seed1, seed2}, a ->
                a[1] = operator.applyAsInt(a[0], a[0] = a[1])));
    }

    public static <T> Seq<T> gen(T seed1, T seed2, BinaryOperator<T> operator) {
        return join(Arrays.asList(seed1, seed2),
            () -> PickItr.gen(new MutablePair<>(seed1, seed2), p ->
                p.second = operator.apply(p.first, p.first = p.second)));
    }

    public static <T> Seq<T> by(Consumer<Yield<T>> yieldConsumer) {
        return by(10, yieldConsumer);
    }

    public static <T> Seq<T> by(int batchSize, Consumer<Yield<T>> yieldConsumer) {
        return join(Any.also(new Yield<>(batchSize), yieldConsumer).list);
    }

    public <E> Seq<E> recur(Function<Iterator<T>, E> function) {
        return recur(this::iterator, function);
    }

    @Override
    public String toString() {
        return join("=>", String::valueOf);
    }

    public <S, E> Seq<E> map(Supplier<S> stateSupplier, BiFunction<T, S, E> function) {
        return convert(() -> MapItr.of(iterator(), stateSupplier.get(), function));
    }

    public <E> Seq<E> map(Function<T, E> function) {
        return convert(() -> MapItr.of(iterator(), function));
    }

    public <E> Seq<E> mapNotNull(Function<T, E> function) {
        return recur(itr -> {
            while (itr.hasNext()) {
                E e = function.apply(itr.next());
                if (e != null) {
                    return e;
                }
            }
            return PickItr.stop();
        });
    }

    public Seq<T> filter(Predicate<T> predicate) {
        return recur(itr -> {
            while (itr.hasNext()) {
                T t = itr.next();
                if (predicate.test(t)) {
                    return t;
                }
            }
            return PickItr.stop();
        });
    }

    public Seq<T> filterNotNull() {
        return filter(Objects::nonNull);
    }

    public Seq<T> distinct() {
        return distinctBy(it -> it);
    }

    public <E> Seq<T> distinctBy(Function<T, E> function) {
        return convert(() -> PickItr.distinctBy(iterator(), function));
    }

    public Seq<T> takeWhile(Predicate<T> predicate) {
        return recur(itr -> {
            if (itr.hasNext()) {
                T t = itr.next();
                if (predicate.test(t)) {
                    return t;
                }
            }
            return PickItr.stop();
        });
    }

    public Seq<T> untilNull() {
        return takeWhile(Objects::nonNull);
    }

    private boolean outRange(int n) {
        return size != null && n >= size;
    }

    public Seq<T> take(int n) {
        return n <= 0 ? empty()
            : outRange(n) ? this
            : convert(() -> CountItr.take(iterator(), n));
    }

    public Seq<T> drop(int n) {
        return n <= 0 ? this
            : outRange(n) ? empty()
            : convert(() -> CountItr.drop(iterator(), n));
    }

    public Seq<T> dropWhile(Predicate<T> predicate) {
        return convert(() -> PickItr.dropWhile(iterator(), predicate));
    }

    public Seq<List<T>> chunked(int size) {
        return convert(() -> PickItr.window(iterator(), size));
    }

    public <E> Seq<E> runningFold(E init, BiFunction<T, E, E> function) {
        return convert(() -> MapItr.of(iterator(), new Mutable<>(init),
            (t, m) -> m.it = function.apply(t, m.it)));
    }

    public Seq<T> onEach(Consumer<T> consumer) {
        return map(Functions.asUnaryOp(consumer));
    }

    public Seq<T> cache() {
        return cache(IterableExt::toBatchList);
    }

    public Seq<T> cache(int batchSize) {
        return cache(it -> it.toBatchList(batchSize));
    }

    public Seq<T> cache(Function<Seq<T>, List<T>> byList) {
        return size != null ? this : of(byList.apply(this));
    }

    public <E> E let(Function<Seq<T>, E> function) {
        return function.apply(this);
    }

    public <R> Seq<R> flatMap(Function<T, Iterable<R>> function) {
        return join(map(function));
    }

    public Seq<T> append(Iterable<T> iterable) {
        return join(Arrays.asList(this, iterable));
    }

    @SafeVarargs
    public final Seq<T> append(T... t) {
        return append(Arrays.asList(t));
    }

    public Seq<IntPair<T>> withIndex() {
        return convert(() -> MapItr.of(iterator(), new int[1], (t, a) -> new IntPair<>(a[0]++, t)));
    }

    public <B> Seq<Pair<T, B>> zip(Iterable<B> bs) {
        return SeqScope.INSTANCE.zip(this, bs);
    }

    public <B, R> Seq<R> zip(Iterable<B> bs, BiFunction<T, B, R> function) {
        return SeqScope.INSTANCE.zip(this, bs, function);
    }

    public <B, C> Seq<Triple<T, B, C>> zip(Iterable<B> bs, Iterable<C> cs) {
        return SeqScope.INSTANCE.zip(this, bs, cs);
    }

    public <E> Seq<Pair<T, E>> cartesian(Iterable<E> es) {
        return cartesian(es, Pair::new);
    }

    public <E, R> Seq<R> cartesian(Iterable<E> es, BiFunction<T, E, R> function) {
        Seq<E> seq = convert(es);
        return flatMap(t -> seq.map(e -> function.apply(t, e)));
    }

    public Stream<T> stream(boolean parallel) {
        return StreamSupport.stream(spliterator(), parallel);
    }

    @SafeVarargs
    public final void assertTo(T... ts) {
        Iterator<T> iterator = iterator();
        for (T t : ts) {
            assert iterator.hasNext() && Objects.equals(iterator.next(), t) : "mismatched";
        }
        assert !iterator.hasNext() : "exceeded";
    }
}
