package com.github.wolray.kt.seq;

import java.util.*;
import java.util.function.*;

/**
 * @author wolray
 */
public abstract class Seq<T> extends IterableExt<T> {
    public static <T> Seq<T> of(Iterable<T> iterable) {
        if (iterable instanceof Seq<?>) {
            return (Seq<T>)iterable;
        }
        Seq<T> seq = convert(iterable);
        seq.setSize(iterable);
        return seq;
    }

    static <T> Seq<T> convert(Iterable<T> iterable) {
        return new Seq<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }
        };
    }

    @SafeVarargs
    public static <T> Seq<T> of(T... ts) {
        return of(Arrays.asList(ts));
    }

    public static <K, V> Seq<Map.Entry<K, V>> of(Map<K, V> map) {
        return of(map.entrySet());
    }

    @SuppressWarnings("unchecked")
    public static <T> Seq<T> empty() {
        return (Seq<T>)EmptySeq.INSTANCE;
    }

    public static <T> Seq<T> of(Consumer<Yield<T>> yieldConsumer) {
        return of(10, yieldConsumer);
    }

    public static <T> Seq<T> of(int batchSize, Consumer<Yield<T>> yieldConsumer) {
        Yield<T> yield = new Yield<>(batchSize);
        yieldConsumer.accept(yield);
        return of(yield.list);
    }

    public static <T> Seq<T> gen(boolean breakAtNull, Supplier<T> supplier) {
        return breakAtNull
            ? convert(() -> PickItr.takeWhile(EndlessItr.of(supplier), Objects::nonNull))
            : convert(() -> EndlessItr.of(supplier));
    }

    public static <T> Seq<T> gen(T seed, UnaryOperator<T> operator) {
        return convert(() -> EndlessItr.of(seed, operator));
    }

    public static <T> Seq<T> gen(T seed1, T seed2, BinaryOperator<T> operator) {
        return convert(() -> EndlessItr.of(seed1, seed2, operator));
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
        return convert(() -> new FlatItr<>(map(function).iterator()));
    }

    public Seq<T> append(Iterable<T> seq) {
        return of(this, seq).flatMap(s -> s);
    }

    @SafeVarargs
    public final Seq<T> append(T... t) {
        return append(of(t));
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
