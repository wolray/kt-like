package ktlike.extensions.java.lang.Object;

import manifold.ext.rt.api.Extension;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Extension
public class Maybe {
    @Extension
    public static <T> T orElse(T t, T orElse) {
        return t != null ? t : orElse;
    }

    @Extension
    public static <T> T orElse(T t, Supplier<T> supplier) {
        return t != null ? t : supplier.get();
    }

    @Extension
    public static <T> T maybeAlso(T t, Consumer<T> consumer) {
        if (t != null) {
            consumer.accept(t);
        }
        return t;
    }

    @Extension
    public static <T, E> E maybeLet(T t, Function<T, E> function) {
        return t != null ? function.apply(t) : null;
    }

    @Extension
    public static <T, E> E maybeLet(T t, Function<T, E> function, E orElse) {
        return orElse(maybeLet(t, function), orElse);
    }

    @Extension
    @SafeVarargs
    public static <T, E> E firstBy(T t, Function<T, E>... functions) {
        for (Function<T, E> f : functions) {
            E res = f.apply(t);
            if (res != null) {
                return res;
            }
        }
        return null;
    }
}
