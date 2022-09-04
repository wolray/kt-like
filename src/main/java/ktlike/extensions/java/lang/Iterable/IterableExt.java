package ktlike.extensions.java.lang.Iterable;

import com.github.wolray.kt.seq.*;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Extension
public class IterableExt {
    @Extension
    @SafeVarargs
    public static <T> Iterable<T> of(T... t) {
        return Arrays.asList(t);
    }

    @Extension
    @SafeVarargs
    public static <T> Iterable<T> joinAll(Iterable<T>... iterable) {
        return joinIterables(Arrays.asList(iterable));
    }

    @Extension
    public static <S, T> Iterable<T> recurBy(Supplier<S> seedSupplier, Function<S, T> function) {
        return () -> PickItr.gen(seedSupplier.get(), function);
    }

    @Self
    public static <T> Iterable<T> also(@This Iterable<T> self, Consumer<Iterable<T>> consumer) {
        consumer.accept(self);
        return self;
    }

    public static <T, E> E let(@This Iterable<T> self, Function<Iterable<T>, E> function) {
        return function.apply(self);
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
        return () -> MapItr.of(self.iterator(), new Mutable<>(init), (t, m) -> m.it = function.apply(t, m.it));
    }

    public static <T> Iterable<T> onEach(@This Iterable<T> self, Consumer<T> consumer) {
        return map(self, consumer.asUnaryOp());
    }

    public static <T, R> Iterable<R> flatMap(@This Iterable<T> self, Function<T, Iterable<R>> function) {
        return joinIterables(map(self, function));
    }

    public static <T> Iterable<T> append(@This Iterable<T> self, Iterable<T> iterable) {
        return joinAll(self, iterable);
    }

    @SafeVarargs
    public static <T> Iterable<T> append(@This Iterable<T> self, T... t) {
        return append(self, Arrays.asList(t));
    }

    public static <T> Iterable<IntPair<T>> withIndex(@This Iterable<T> self) {
        return () -> MapItr.of(self.iterator(), new int[1], (t, a) -> new IntPair<>(a[0]++, t));
    }

    public static <T, B> Iterable<Pair<T, B>> zip(@This Iterable<T> self, Iterable<B> bs) {
        return zip(self, bs, Pair::new);
    }

    public static <T, B, R> Iterable<R> zip(@This Iterable<T> self, Iterable<B> bs, BiFunction<T, B, R> function) {
        return () -> new PickItr<R>() {
            Iterator<T> ti = self.iterator();
            Iterator<B> bi = bs.iterator();

            @Override
            public R pick() {
                if (ti.hasNext() && bi.hasNext()) {
                    return function.apply(ti.next(), bi.next());
                }
                return stop();
            }
        };
    }

    public static <T, B, C> Iterable<Triple<T, B, C>> zip(@This Iterable<T> self, Iterable<B> bs, Iterable<C> cs) {
        return () -> new PickItr<Triple<T, B, C>>() {
            Iterator<T> ti = self.iterator();
            Iterator<B> bi = bs.iterator();
            Iterator<C> ci = cs.iterator();

            @Override
            public Triple<T, B, C> pick() {
                if (ti.hasNext() && bi.hasNext() && ci.hasNext()) {
                    return new Triple<>(ti.next(), bi.next(), ci.next());
                }
                return stop();
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

    public static <T> Stream<T> stream(@This Iterable<T> self) {
        return StreamSupport.stream(self.spliterator(), false);
    }

    public static <T> Stream<T> parallelStream(@This Iterable<T> self) {
        return StreamSupport.stream(self.spliterator(), true);
    }

    @SafeVarargs
    public static <T> void assertTo(@This Iterable<T> self, T... ts) {
        Iterator<T> iterator = self.iterator();
        for (T t : ts) {
            assert iterator.hasNext() && Objects.equals(iterator.next(), t) : "mismatched";
        }
        assert !iterator.hasNext() : "exceeded";
    }
}