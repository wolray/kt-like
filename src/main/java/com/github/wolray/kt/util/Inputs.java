package com.github.wolray.kt.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author wolray
 */
public class Inputs {
    public static void copy(InputStream input, OutputStream output, int bufferKB) throws IOException {
        byte[] buffer = new byte[bufferKB << 10];
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
    }

    public static byte[] toBytes(InputStream input) throws Exception {
        return toBytes(input, 4);
    }

    public static byte[] toBytes(InputStream input, int bufferKB) throws Exception {
        return WithCe.safeApply(new ByteArrayOutputStream(), it -> {
            copy(input, it, bufferKB);
            return it.toByteArray();
        });
    }
}
