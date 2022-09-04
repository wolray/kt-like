package ktlike.extensions.java.lang.Object;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class Predicating {
    public static boolean isNull(@This Object self) {
        return self == null;
    }

    public static boolean notNull(@This Object self) {
        return self != null;
    }

    @Extension
    public static boolean onTrue(boolean bool, Runnable runnable) {
        if (bool) {
            runnable.run();
        }
        return bool;
    }

    @Extension
    public static boolean onFalse(boolean bool, Runnable runnable) {
        if (!bool) {
            runnable.run();
        }
        return bool;
    }
}
