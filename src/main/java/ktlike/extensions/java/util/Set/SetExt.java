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
    public static <E> Set<E> of(E... es) {
        Set<E> res = new HashSet<>(es.length);
        res.addAll(Arrays.asList(es));
        return res;
    }

    @Self
    public static <E> Set<E> put(@This Set<E> self, E e) {
        self.add(e);
        return self;
    }

    @Self
    public static <E> Set<E> putAll(@This Set<E> self, Iterable<E> es) {
        es.forEach(self::add);
        return self;
    }
}