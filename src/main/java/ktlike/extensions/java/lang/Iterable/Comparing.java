package ktlike.extensions.java.lang.Iterable;

import com.github.wolray.kt.seq.Pair;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;

@Extension
public class Comparing {
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
}
