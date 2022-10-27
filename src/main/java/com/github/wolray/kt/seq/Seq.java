package com.github.wolray.kt.seq;

import com.github.wolray.kt.util.*;

import java.util.*;
import java.util.function.*;

/**
 * @author wolray
 */
public interface Seq<T> extends IterableBoost<T>, Self<Seq<T>>, Cache.Cacheable<T, Seq<T>> {
    static <T> Seq<T> of(Iterable<T> iterable) {
        if (iterable instanceof Seq<?>) {
            return (Seq<T>)iterable;
        }
        if (iterable instanceof Collection<?>) {
            return new Backed<>((Collection<T>)iterable);
        }
        return iterable::iterator;
    }

    static <T> SafeSeq<T> ofSafe(WithCe.Iterable<T> iterable) {
        return new SafeSeq<T>() {
            @Override
            public Iterator<T> iterator() {
                try {
                    return iterable.iterator();
                } catch (Exception e) {
                    if (errorType != null && errorType.isAssignableFrom(e.getClass())) {
                        return Collections.emptyIterator();
                    }
                    throw new RuntimeException(e);
                }
            }
        };
    }

    static <T> Seq<T> of(Supplier<Enumeration<T>> enumerationSupplier) {
        return () -> new Iterator<T>() {
            Enumeration<T> enumeration = enumerationSupplier.get();

            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }
        };
    }

    @SafeVarargs
    static <T> Seq<T> of(T... ts) {
        return of(Arrays.asList(ts));
    }

    @SafeVarargs
    static <T> Seq<T> join(Iterable<T>... iterable) {
        return join(Arrays.asList(iterable));
    }

    static <T> Seq<T> join(Iterable<? extends Iterable<T>> iterables) {
        return () -> PickItr.flat(iterables.iterator());
    }

    static <T> Seq<T> empty() {
        return Collections::emptyIterator;
    }

    static Seq<Integer> range(int until) {
        return range(0, until, 1);
    }

    static Seq<Integer> range(int start, int until) {
        return range(start, until, 1);
    }

    static Seq<Integer> range(int start, int until, int step) {
        return () -> CountItr.range(start, until, step);
    }

    static <T> Seq<T> repeat(int n, T t) {
        return () -> CountItr.repeat(n, t);
    }

    static <S, T> Seq<T> recur(Supplier<S> seedSupplier, Function<S, T> function) {
        return () -> PickItr.gen(seedSupplier.get(), function);
    }

    static <T> Seq<T> genUntilNull(Supplier<T> supplier) {
        return () -> PickItr.genUntilNull(supplier);
    }

    static <T> YieldSeq<T> gen() {
        return new YieldSeq<>();
    }

    static <T> Seq<T> gen(Supplier<T> supplier) {
        return () -> PickItr.gen(supplier);
    }

    static Seq<Integer> gen(int seed, IntUnaryOperator operator) {
        return () -> PickItr.gen(new int[]{seed}, a -> {
            int t = a[0];
            a[0] = operator.applyAsInt(a[0]);
            return t;
        });
    }

    static <T> Seq<T> gen(T seed, UnaryOperator<T> operator) {
        return () -> PickItr.gen(new Mutable<>(seed), m -> {
            T t = m.it;
            m.it = operator.apply(m.it);
            return t;
        });
    }

    static Seq<Integer> gen(int seed1, int seed2, IntBinaryOperator operator) {
        return join(Arrays.asList(seed1, seed2),
            () -> PickItr.gen(new int[]{seed1, seed2}, a ->
                a[1] = operator.applyAsInt(a[0], a[0] = a[1])));
    }

    static <T> Seq<T> gen(T seed1, T seed2, BinaryOperator<T> operator) {
        return join(Arrays.asList(seed1, seed2),
            () -> PickItr.gen(new MutablePair<>(seed1, seed2), p ->
                p.second = operator.apply(p.first, p.first = p.second)));
    }

    @Override
    default Seq<T> get() {
        return this;
    }

    @Override
    default Seq<T> apply(Iterable<T> iterable) {
        return of(iterable);
    }

    default <E> Seq<E> recur(Function<Iterator<T>, E> function) {
        return recur(this::iterator, function);
    }

    default <S, E> Seq<E> map(Supplier<S> stateSupplier, BiFunction<T, S, E> function) {
        return () -> MapItr.of(iterator(), stateSupplier.get(), function);
    }

    default <E> Seq<E> map(Function<T, E> function) {
        return map(0, function);
    }

    default <E> Seq<E> map(int skip, Function<T, E> function) {
        return () -> {
            Iterator<T> iterator = iterator();
            if (skip > 0) {
                for (int i = 0; i < skip && iterator.hasNext(); i++) {
                    iterator.next();
                }
            }
            if (function instanceof ContextMapper) {
                ((ContextMapper<T, E>)function).preprocess(iterator);
            }
            return MapItr.of(iterator, function);
        };
    }

    default <E> Seq<E> mapCe(WithCe.Function<T, E> function) {
        return () -> MapItr.of(iterator(), WithCe.mapper(function));
    }

    default <E> Seq<E> mapNotNull(Function<T, E> function) {
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

    default Seq<T> filter(Predicate<T> predicate) {
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

    default Seq<T> filterNotNull() {
        return filter(Objects::nonNull);
    }

    default Seq<T> distinct() {
        return distinctBy(it -> it);
    }

    default <E> Seq<T> distinctBy(Function<T, E> function) {
        return () -> PickItr.distinctBy(iterator(), function);
    }

    default Seq<T> takeWhile(Predicate<T> predicate) {
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

    default Seq<T> untilNull() {
        return takeWhile(Objects::nonNull);
    }

    default Seq<T> take(int n) {
        return n <= 0 ? empty() : Backed.outSize(this, n) ? this : () -> CountItr.take(iterator(), n);
    }

    default Seq<T> drop(int n) {
        return n <= 0 ? this : Backed.outSize(this, n) ? empty() : () -> CountItr.drop(iterator(), n);
    }

    default Seq<T> dropWhile(Predicate<T> predicate) {
        return () -> PickItr.dropWhile(iterator(), predicate);
    }

    default Seq<List<T>> chunked(int size) {
        return () -> PickItr.window(iterator(), size);
    }

    default <E> Seq<E> runningFold(E init, BiFunction<T, E, E> function) {
        return () -> MapItr.of(iterator(), new Mutable<>(init), (t, m) -> m.it = function.apply(t, m.it));
    }

    default Seq<T> onEach(Consumer<T> consumer) {
        return map(Functions.asUnaryOp(consumer));
    }

    default Seq<T> cache() {
        return cache(10);
    }

    default Seq<T> cache(int batchSize) {
        return this instanceof Backed ? this : new Backed<>(toBatchList(batchSize));
    }

    default <R> Seq<R> flatMap(Function<T, Iterable<R>> function) {
        return join(map(function));
    }

    default Seq<T> append(Iterable<T> iterable) {
        return join(Arrays.asList(this, iterable));
    }

    @SuppressWarnings("unchecked")
    default Seq<T> append(T... t) {
        return append(Arrays.asList(t));
    }

    default Seq<IntPair<T>> withIndex() {
        return () -> MapItr.of(iterator(), new int[1], (t, a) -> new IntPair<>(a[0]++, t));
    }

    default <B> Seq<Pair<T, B>> zip(Iterable<B> bs) {
        return Iterables.zip(this, bs);
    }

    default <B, R> Seq<R> zip(Iterable<B> bs, BiFunction<T, B, R> function) {
        return Iterables.zip(this, bs, function);
    }

    default <B, C> Seq<Triple<T, B, C>> zip(Iterable<B> bs, Iterable<C> cs) {
        return Iterables.zip(this, bs, cs);
    }

    default <E> Seq<Pair<T, E>> cartesian(Iterable<E> es) {
        return cartesian(es, Pair::new);
    }

    default <E, R> Seq<R> cartesian(Iterable<E> es, BiFunction<T, E, R> function) {
        Seq<E> seq = es::iterator;
        return flatMap(t -> seq.map(e -> function.apply(t, e)));
    }

    @SuppressWarnings("unchecked")
    default void assertTo(T... ts) {
        Iterator<T> iterator = iterator();
        for (T t : ts) {
            assert iterator.hasNext() && Objects.equals(iterator.next(), t) : "mismatched";
        }
        assert !iterator.hasNext() : "exceeded";
    }

    abstract class SafeSeq<T> implements Seq<T> {
        Class<? extends Exception> errorType;

        public Seq<T> ignore(Class<? extends Exception> type) {
            errorType = type;
            return this;
        }
    }

    class Backed<T> implements Seq<T> {
        private final Collection<T> collection;

        public Backed(Collection<T> collection) {
            this.collection = collection;
        }

        static boolean outSize(IterableBoost<?> boost, int n) {
            return boost instanceof Backed && n >= boost.sizeOrDefault();
        }

        @Override
        public int sizeOrDefault() {
            return collection.size();
        }

        @Override
        public Iterator<T> iterator() {
            return collection.iterator();
        }
    }

    interface ContextMapper<T, E> extends Function<T, E> {
        void preprocess(Iterator<T> iterator);
    }
}
