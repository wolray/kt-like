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
    public static <E> List<E> of(E... es) {
        return Arrays.asList(es);
    }

    @Self
    public static <E> List<E> put(@This List<E> self, E t) {
        self.add(t);
        return self;
    }

    public static <E> List<E> putAll(@This List<E> self, Iterable<E> ts) {
        ts.forEach(self::add);
        return self;
    }
}