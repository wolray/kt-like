package ktlike.extensions.java.lang.String;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;

@Extension
public class StringExt {
    public static String also(@This String self, Consumer<String> consumer) {
        consumer.accept(self);
        return self;
    }

    public static <E> E let(@This String self, Function<String, E> function) {
        return function.apply(self);
    }

    public static String orEmpty(@This String self) {
        return self == null ? "" : self;
    }

    public static boolean isNullOrEmpty(@This String self) {
        return self == null || self.isEmpty();
    }

    public static String format(@This String self, Object... objects) {
        return String.format(self, objects);
    }

    public static String join(@This String self, Consumer<StringJoiner> consumer) {
        return consumer.use(new StringJoiner(self)).toString();
    }
}