package ktlike.extensions.java.lang.Boolean;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class BooleanExt {
    public static boolean onTrue(@This Boolean self, Runnable runnable) {
        if (self) {
            runnable.run();
        }
        return self;
    }

    public static boolean onFalse(@This Boolean self, Runnable runnable) {
        if (!self) {
            runnable.run();
        }
        return self;
    }
}