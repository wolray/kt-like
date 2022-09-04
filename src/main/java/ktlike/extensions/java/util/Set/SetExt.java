package ktlike.extensions.java.util.Set;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Extension
public class SetExt {
    @Extension
    @SafeVarargs
    public static <T> Set<T> of(T... ts) {
        Set<T> res = new HashSet<>(ts.length);
        res.addAll(Arrays.asList(ts));
        return res;
    }

    @Self
    public static <T> Set<T> put(@This Set<T> self, T t) {
        self.add(t);
        return self;
    }

    @Self
    public static <T> Set<T> putAll(@This Set<T> self, Iterable<T> ts) {
        ts.forEach(self::add);
        return self;
    }
}