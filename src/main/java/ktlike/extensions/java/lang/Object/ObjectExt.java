package ktlike.extensions.java.lang.Object;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.function.Consumer;
import java.util.function.Function;

@Extension
public class ObjectExt {
    public static void println(@This Object self) {
        System.out.println(self);
    }

    @Extension
    public static <T> T also(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    @Extension
    public static <T, E> E let(T t, Function<T, E> function) {
        return function.apply(t);
    }
}