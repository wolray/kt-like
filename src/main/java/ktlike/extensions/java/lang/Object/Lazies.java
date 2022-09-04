package ktlike.extensions.java.lang.Object;

import com.github.wolray.kt.lazy.LazyJob;
import com.github.wolray.kt.lazy.LazyVar;
import manifold.ext.rt.api.Extension;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Extension
public class Lazies {
    @Extension
    public static <T> LazyVar<T> lazyOf(Supplier<T> supplier) {
        return new LazyVar<>(supplier);
    }

    @Extension
    public static <T> LazyVar<T> lazyOf(Supplier<T> supplier, Consumer<T> consumer) {
        return new LazyVar<>(supplier).then(consumer);
    }

    @Extension
    public static LazyJob lazyJob(Runnable runnable) {
        return new LazyJob(runnable);
    }
}
