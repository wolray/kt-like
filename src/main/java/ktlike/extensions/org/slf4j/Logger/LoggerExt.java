package ktlike.extensions.org.slf4j.Logger;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Extension
public class LoggerExt {
    public static void infoTime(@This Logger log, String message, Runnable runnable) {
        infoTime(log, message, () -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T infoTime(@This Logger log, String message, Supplier<T> supplier) {
        log.info(message);
        long tic = now();
        T res = supplier.get();
        log.info("{} done in {}ms", message, now() - tic);
        return res;
    }
}