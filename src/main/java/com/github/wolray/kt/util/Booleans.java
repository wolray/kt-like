package com.github.wolray.kt.util;

/**
 * @author wolray
 */
public class Booleans {
    public static boolean onTrue(boolean bool, Runnable runnable) {
        if (bool) {
            runnable.run();
        }
        return bool;
    }

    public static boolean onFalse(boolean bool, Runnable runnable) {
        if (!bool) {
            runnable.run();
        }
        return bool;
    }
}
