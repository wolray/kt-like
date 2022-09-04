package ktlike.extensions.java.util.List;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

import java.util.Arrays;
import java.util.List;

@Extension
public class ListExt {
    @Extension
    @SafeVarargs
    public static <T> List<T> of(T... ts) {
        return Arrays.asList(ts);
    }

    @Self
    public static <T> List<T> put(@This List<T> self, T t) {
        self.add(t);
        return self;
    }

    @Self
    public static <T> List<T> putAll(@This List<T> self, Iterable<T> ts) {
        ts.forEach(self::add);
        return self;
    }
}