package com.github.wolray.kt.seq;

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
        return convert(() -> new FlatItr<>(iterables.iterator()));
    }

    @SuppressWarnings("unchecked")
    public static <T> Seq<T> empty() {
        return (Seq<T>)EmptySeq.INSTANCE;
    }

    public static <S, T> Seq<T> recur(Supplier<S> stateSupplier, Function<S, T> function) {
        return convert(() -> EndlessItr.of(stateSupplier.get(), function));
    }

    public static <T> Seq<T> gen(Supplier<T> supplier) {
        return convert(() -> EndlessItr.of(supplier));
    }

    public static <T> Seq<T> gen(T seed, UnaryOperator<T> operator) {
        return join(Collections.singletonList(seed),
            recur(() -> new MutablePair<>(seed, null),
                p -> p.first = operator.apply(p.first)));
    }

    public static <T> Seq<T> gen(T seed1, T seed2, BinaryOperator<T> operator) {
        return join(Arrays.asList(seed1, seed2),
            recur(() -> new MutablePair<>(seed1, seed2), p -> {
                T t = operator.apply(p.first, p.second);
                p.first = p.second;
                p.second = t;
                return t;
            }));
    }

    public static <T> Seq<T> gen(Consumer<Yield<T>> yieldConsumer) {
        return gen(10, yieldConsumer);
    }

    public static <T> Seq<T> gen(int batchSize, Consumer<Yield<T>> yieldConsumer) {
        Yield<T> yield = new Yield<>(batchSize);
        yieldConsumer.accept(yield);
        return join(yield.list);
    }

    @Override
    public String toString() {
        return join("=>", String::valueOf);
    }

    public <S, E> Seq<E> map(Supplier<S> stateSupplier, BiFunction<S, T, E> function) {
        return convert(() -> {
            S s = stateSupplier.get();
            return map(it -> function.apply(s, it)).iterator();
        });
    }

    public <E> Seq<E> map(Function<T, E> function) {
        Seq<E> res = convert(() -> new Iterator<E>() {
            Iterator<T> iterator = iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return function.apply(iterator.next());
            }
        });
        res.setSize(this);
        return res;
    }

    public <E> Seq<E> mapNotNull(Function<T, E> function) {
        return map(function).filterNotNull();
    }

    public Seq<T> filter(Predicate<T> predicate) {
        return convert(() -> PickItr.filter(iterator(), predicate));
    }

    public Seq<T> filterNotNull() {
        return filter(Objects::nonNull);
    }

    public Seq<T> distinct() {
        Set<T> set = new HashSet<>();
        return filter(set::add);
    }

    public <E> Seq<T> distinctBy(Function<T, E> function) {
        Set<E> set = new HashSet<>();
        return filter(it -> set.add(function.apply(it)));
    }

    public Seq<T> takeWhile(Predicate<T> predicate) {
        return convert(() -> PickItr.takeWhile(iterator(), predicate));
    }

    public Seq<T> untilNull() {
        return takeWhile(Objects::nonNull);
    }

    public Seq<T> take(int n) {
        if (n <= 0) {
            return empty();
        }
        return new SubSeq<>(this, 0, n);
    }

    public Seq<T> drop(int n) {
        if (n <= 0) {
            return this;
        }
        return new SubSeq<>(this, n, null);
    }

    public Seq<T> dropWhile(Predicate<T> predicate) {
        return convert(() -> PickItr.dropWhile(iterator(), predicate));
    }

    public Seq<List<T>> chunked(int size) {
        return convert(() -> new WindowItr<>(iterator(), size));
    }

    public <E> Seq<E> runningFold(E init, BiFunction<E, T, E> function) {
        return map(() -> new MutablePair<>(init, null),
            (p, it) -> p.first = function.apply(p.first, it));
    }

    public Seq<T> onEach(Consumer<T> consumer) {
        return map(it -> {
            consumer.accept(it);
            return it;
        });
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

    public Seq<T> append(Iterable<T> seq) {
        return join(Arrays.asList(this, seq));
    }

    @SafeVarargs
    public final Seq<T> append(T... t) {
        return append(Arrays.asList(t));
    }

    public Seq<IndexedValue<T>> withIndex() {
        return map(() -> new int[1], (a, it) -> new IndexedValue<>(a[0]++, it));
    }

    public <E> Seq<Pair<T, E>> zip(Iterable<E> es) {
        return SeqScope.INSTANCE.zip(this, es);
    }

    public <B, R> Seq<R> zip(Iterable<B> es, BiFunction<T, B, R> function) {
        return SeqScope.INSTANCE.zip(this, es, function);
    }

    public <B, C> Seq<Triple<T, B, C>> zip(Iterable<B> bs, Iterable<C> cs) {
        return SeqScope.INSTANCE.zip(this, bs, cs);
    }

    public <E> Seq<Pair<T, E>> cartesian(Iterable<E> es) {
        return cartesian(es, Pair::new);
    }

    public <E, R> Seq<R> cartesian(Iterable<E> es, BiFunction<T, E, R> function) {
        Seq<E> seq = of(es);
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

    static class EmptySeq {
        static final Seq<Object> INSTANCE = convert(Collections::emptyIterator);
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
