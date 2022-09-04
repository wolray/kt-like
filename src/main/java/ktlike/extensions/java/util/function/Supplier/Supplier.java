package ktlike.extensions.java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.function.Supplier;

@Extension
public class SupplierExt {
    public static <T> Supplier<T> or(@This Supplier<T> self, T defaultValue) {
        return () -> {
            T t = self.get();
            return t != null ? t : defaultValue;
        };
    }

    public static <T> Supplier<T> or(@This Supplier<T> self, Supplier<T> defaultGetter) {
        return () -> {
            T t = self.get();
            return t != null ? t : defaultGetter.get();
        };
    }
}