package com.github.wolray.kt.util;

/**
 * @author wolray
 */
public class Encoding {
    public static int bitSize(long v) {
        for (int i = 63; i > 0; i--) {
            if ((v & (1L << (i - 1))) > 0) {
                return i;
            }
        }
        return 0;
    }

    public static int bitSize(int v) {
        for (int i = 31; i > 0; i--) {
            if ((v & (1 << (i - 1))) > 0) {
                return i;
            }
        }
        return 0;
    }
}
