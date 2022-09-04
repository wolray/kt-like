package ktlike.extensions.java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@Extension
public class ConsumerExt {
    public static <T> T use(@This Consumer<T> self, T t) {
        self.accept(t);
        return t;
    }

    public static <T> UnaryOperator<T> asUnaryOp(@This Consumer<T> self) {
        return t -> {
            self.accept(t);
            return t;
        };
    }
}