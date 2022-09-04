package ktlike.extensions.java.lang.Object;

import com.github.wolray.kt.seq.IntPair;
import com.github.wolray.kt.seq.LongPair;
import com.github.wolray.kt.seq.Pair;
import com.github.wolray.kt.seq.Triple;
import manifold.ext.rt.api.Extension;

@Extension
public class Tuples {
    @Extension
    public static <A, B> Pair<A, B> pairOf(A a, B b) {
        return new Pair<>(a, b);
    }

    @Extension
    public static <B> IntPair<B> intPairOf(int a, B b) {
        return new IntPair<>(a, b);
    }

    @Extension
    public static <B> LongPair<B> longPairOf(long a, B b) {
        return new LongPair<>(a, b);
    }

    @Extension
    public static <A, B, C> Triple<A, B, C> tripleOf(A a, B b, C c) {
        return new Triple<>(a, b, c);
    }
}
