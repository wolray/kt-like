package ktlike.extensions.java.lang.Iterable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

@Extension
public class ToPrimitive {
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
}
