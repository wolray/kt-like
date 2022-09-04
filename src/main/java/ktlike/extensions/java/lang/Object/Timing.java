package ktlike.extensions.java.lang.Object;

import com.github.wolray.kt.seq.LongPair;
import manifold.ext.rt.api.Extension;

import java.util.function.Supplier;

@Extension
public class Timing {
    @Extension
    public static long now() {
        return System.currentTimeMillis();
    }

    @Extension
    public static <T> LongPair<T> measureTime(Supplier<T> supplier) {
        long tic = now();
        T t = supplier.get();
        return new LongPair<>(now() - tic, t);
    }

    @Extension
    public static long measureTime(Runnable runnable) {
        long tic = now();
        runnable.run();
        return now() - tic;
    }
}
