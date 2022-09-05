package ktlike.extensions.java.lang.Iterable;

import com.github.wolray.kt.seq.Seq;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class IterableExt {
    public static <T> Seq<T> seq(@This Iterable<T> self) {
        return Seq.of(self);
    }
}
