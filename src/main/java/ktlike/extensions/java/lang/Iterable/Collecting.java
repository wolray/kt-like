package ktlike.extensions.java.lang.Iterable;

import com.github.wolray.kt.seq.BatchList;
import com.github.wolray.kt.seq.Grouping;
import com.github.wolray.kt.seq.SinglyList;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@Extension
public class Collecting {
    private static <T> int sizeOrDefault(Iterable<T> iterable) {
        return iterable instanceof Collection<?> ? ((Collection<T>)iterable).size() : 10;
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
}
