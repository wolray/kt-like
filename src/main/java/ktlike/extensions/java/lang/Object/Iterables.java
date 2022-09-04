package ktlike.extensions.java.lang.Object;

import com.github.wolray.kt.seq.CountItr;
import com.github.wolray.kt.seq.Mutable;
import com.github.wolray.kt.seq.MutablePair;
import com.github.wolray.kt.seq.PickItr;
import manifold.ext.rt.api.Extension;

import java.util.Arrays;
import java.util.function.*;

@Extension
public class Iterables {
    @Extension
    public static <T> Iterable<T> genUntilNull(Supplier<T> supplier) {
        return genIterable(supplier.or(PickItr::stop));
    }

    @Extension
    public static <T> Iterable<T> genIterable(Supplier<T> supplier) {
        return () -> PickItr.gen(supplier);
    }

    @Extension
    public static Iterable<Integer> genIterable(int seed, IntUnaryOperator operator) {
        return () -> PickItr.gen(new int[]{seed}, a -> {
            int t = a[0];
            a[0] = operator.applyAsInt(a[0]);
            return t;
        });
    }

    @Extension
    public static <T> Iterable<T> genIterable(T seed, UnaryOperator<T> operator) {
        return () -> PickItr.gen(new Mutable<>(seed), m -> {
            T t = m.it;
            m.it = operator.apply(m.it);
            return t;
        });
    }

    @Extension
    public static Iterable<Integer> genIterable(int seed1, int seed2, IntBinaryOperator operator) {
        return Iterable.joinAll(Arrays.asList(seed1, seed2),
            () -> PickItr.gen(new int[]{seed1, seed2}, a ->
                a[1] = operator.applyAsInt(a[0], a[0] = a[1])));
    }

    @Extension
    public static <T> Iterable<T> genIterable(T seed1, T seed2, BinaryOperator<T> operator) {
        return Iterable.joinAll(Arrays.asList(seed1, seed2),
            () -> PickItr.gen(new MutablePair<>(seed1, seed2), p ->
                p.second = operator.apply(p.first, p.first = p.second)));
    }

    @Extension
    public static <T> Iterable<T> joinIterables(Iterable<? extends Iterable<T>> iterables) {
        return () -> PickItr.flat(iterables.iterator());
    }

    @Extension
    public static <T> Iterable<T> repeatAsIterable(T t, int n) {
        return () -> CountItr.repeat(t, n);
    }

    @Extension
    public static Iterable<Integer> rangeOf(int n) {
        return rangeBy(0, n, 1);
    }

    @Extension
    public static Iterable<Integer> rangeUntil(int start, int until) {
        return rangeBy(start, until, 1);
    }

    @Extension
    public static Iterable<Integer> rangeBy(int start, int until, int step) {
        return () -> CountItr.range(start, until, step);
    }
}
