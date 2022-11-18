package com.github.wolray.kt.lazy;

import com.github.wolray.kt.seq.SeqSet;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class LazySet<T> extends LazyVar<SeqSet<T>> implements SeqSet<T> {
    public LazySet(Supplier<SeqSet<T>> supplier) {
        super(supplier);
    }

    @Override
    public Set<T> backer() {
        return get();
    }
}
