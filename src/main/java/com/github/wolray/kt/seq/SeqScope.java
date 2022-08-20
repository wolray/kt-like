package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author wolray
 */
public interface SeqScope {
    SeqScope INSTANCE = new SeqScope() {};

    default <T> Seq<T> seq(T[] ts) {
        return Seq.of(ts);
    }

    default <T> Seq<T> seq(Iterable<T> ts) {
        return Seq.of(ts);
    }

    default <K, V> Seq<Map.Entry<K, V>> seq(Map<K, V> ts) {
        return Seq.of(ts);
    }

    default <A, B> Seq<Pair<A, B>> zip(Iterable<A> as, Iterable<B> bs) {
        return zip(as, bs, Pair::new);
    }

    default <A, B, R> Seq<R> zip(Iterable<A> as, Iterable<B> bs, BiFunction<A, B, R> function) {
        return Seq.of(() -> new Iterator<R>() {
            Iterator<A> ai = as.iterator();
            Iterator<B> bi = bs.iterator();

            @Override
            public boolean hasNext() {
                return ai.hasNext() && bi.hasNext();
            }

            @Override
            public R next() {
                return function.apply(ai.next(), bi.next());
            }
        });
    }

    default <A, B, C> Seq<Triple<A, B, C>> zip(Iterable<A> as, Iterable<B> bs, Iterable<C> cs) {
        return Seq.of(() -> new Iterator<Triple<A, B, C>>() {
            Iterator<A> ai = as.iterator();
            Iterator<B> bi = bs.iterator();
            Iterator<C> ci = cs.iterator();

            @Override
            public boolean hasNext() {
                return ai.hasNext() && bi.hasNext() && ci.hasNext();
            }

            @Override
            public Triple<A, B, C> next() {
                return new Triple<>(ai.next(), bi.next(), ci.next());
            }
        });
    }

    default <A, B> Pair<Seq<A>, Seq<B>> unzip(Seq<Pair<A, B>> seq) {
        return new Pair<>(seq.map(p -> p.first), seq.map(p -> p.second));
    }

    default <A, B, C> Triple<Seq<A>, Seq<B>, Seq<C>> unzip3(Seq<Triple<A, B, C>> seq) {
        return new Triple<>(seq.map(p -> p.first), seq.map(p -> p.second), seq.map(p -> p.third));
    }
}
