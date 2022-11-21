package com.github.wolray.kt.lazy;

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
    private Consumer<T> after;

    public LazyVar(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyVar<T> of(Supplier<T> supplier) {
        return new LazyVar<>(supplier);
    }

    public static <T> LazyVar<T> of(Supplier<T> supplier, Consumer<T> consumer) {
        return new LazyVar<>(supplier).then(consumer);
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
            if (after != null) {
                after.accept(value);
            }
        }
        return value;
    }

    public synchronized void set(T value) {
        this.value = value;
    }

    public synchronized void reset() {
        value = null;
    }

    public synchronized LazyVar<T> then(Consumer<T> consumer) {
        after = after != null ? after.andThen(consumer) : consumer;
        return this;
    }

    public <E> LazyVar<E> applyBy(Function<T, E> function) {
        return new LazyVar<>(() -> function.apply(get()));
    }

    public <E> LazyVar<E> applyBy(Function<T, E> function, BiConsumer<E, T> consumer) {
        return new LazyVar<>(() -> {
            T t = get();
            E e = function.apply(t);
            consumer.accept(e, t);
            return e;
        });
    }
}
