package com.github.wolray.kt.lazy;

import com.github.wolray.kt.seq.SeqList;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class LazyList<T> extends LazyVar<SeqList<T>> implements SeqList<T> {
    public LazyList(Supplier<SeqList<T>> supplier) {
        super(supplier);
    }

    @Override
    public List<T> backer() {
        return get();
    }
}
