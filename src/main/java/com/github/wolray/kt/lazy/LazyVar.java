package com.github.wolray.kt.lazy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class LazyVar<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value;

    public LazyVar(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyVar<T> of(Supplier<T> supplier) {
        return new LazyVar<>(supplier);
    }

    public static <T> LazyVar<T> of(Supplier<T> supplier, Consumer<T> consumer) {
        return new LazyVar<>(() -> {
            T t = supplier.get();
            consumer.accept(t);
            return t;
        });
    }

    public boolean isLoaded() {
        return value != null;
    }

    public void ifLoaded(Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    @Override
    public synchronized T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

    public synchronized void set(T value) {
        this.value = value;
    }

    public synchronized void reset() {
        value = null;
    }

    public <E> LazyList<E> transformList(Function<T, List<E>> function) {
        return new LazyList<>(() -> function.apply(get()));
    }

    public <E> LazySet<E> transformSet(Function<T, Set<E>> function) {
        return new LazySet<>(() -> function.apply(get()));
    }

    public <K, V> LazyMap<K, V> transformMap(Function<T, Map<K, V>> function) {
        return new LazyMap<>(() -> function.apply(get()));
    }

    public <E> LazyVar<E> transform(Function<T, E> function) {
        return new LazyVar<>(() -> function.apply(get()));
    }

    public <E> LazyVar<E> transform(Function<T, E> function, BiConsumer<E, T> consumer) {
        return new LazyVar<>(() -> {
            T t = get();
            E e = function.apply(t);
            consumer.accept(e, t);
            return e;
        });
    }
}
