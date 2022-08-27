package ktlike.extensions.java.lang.String;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.StringJoiner;
import java.util.function.Consumer;

@Extension
public class StringExt {
    public static boolean isNullOrEmpty(@This String self) {
        return self == null || self.isEmpty();
    }

    public static String join(@This String self, Consumer<StringJoiner> consumer) {
        StringJoiner joiner = new StringJoiner(self);
        consumer.accept(joiner);
        return joiner.toString();
    }
}