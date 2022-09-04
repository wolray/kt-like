package ktlike.extensions.java.lang.Iterable;

import com.github.wolray.kt.seq.BatchList;
import com.github.wolray.kt.seq.Pair;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.*;
import java.util.function.*;

@Extension
public class Reducing {
    public static <T> String display(@This Iterable<T> self) {
        return self.join("=>", String::valueOf);
    }

    public static <T> String join(@This Iterable<T> self, String sep, Function<T, String> function) {
        StringJoiner joiner = new StringJoiner(sep);
        for (T t : self) {
            joiner.add(function.apply(t));
        }
        return joiner.toString();
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

    public static <T> DoubleSummaryStatistics summarize(@This Iterable<T> self, ToDoubleFunction<T> function) {
        return self.foldBy(new DoubleSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsDouble(t)));
    }

    public static <T> IntSummaryStatistics summarizeInt(@This Iterable<T> self, ToIntFunction<T> function) {
        return self.foldBy(new IntSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsInt(t)));
    }

    public static <T> LongSummaryStatistics summarizeLong(@This Iterable<T> self, ToLongFunction<T> function) {
        return self.foldBy(new LongSummaryStatistics(), (stat, t) -> stat.accept(function.applyAsLong(t)));
    }

    public static <T> T first(@This Iterable<T> self) {
        Iterator<T> iterator = self.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    public static <T> T firstNotNull(@This Iterable<T> self) {
        T first = first(self, Objects::nonNull);
        if (first == null) {
            throw new NoSuchElementException();
        }
        return first;
    }

    public static <T> T first(@This Iterable<T> self, Predicate<T> predicate) {
        for (T t : self) {
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }
}
