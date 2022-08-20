package com.github.wolray.kt.lazy;

import com.github.wolray.kt.seq.Seq;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class LazySeq<T> extends LazyVar<Seq<T>> {
    public LazySeq(Supplier<Seq<T>> supplier) {
        super(supplier);
    }

    public <E> LazySeq<E> mapSeq(Function<Seq<T>, Iterable<E>> function) {
        return new LazySeq<>(() -> Seq.of(function.apply(get())));
    }
}
