package com.github.wolray.kt.lazy;

import com.github.wolray.kt.seq.SeqMap;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class LazyMap<K, V> extends LazyVar<Map<K, V>> implements SeqMap<K, V> {
    public LazyMap(Supplier<Map<K, V>> supplier) {
        super(supplier);
    }

    @Override
    public Map<K, V> map() {
        return get();
    }
}
