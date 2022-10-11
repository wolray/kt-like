package com.github.wolray.kt.seq;

import com.github.wolray.kt.util.WithCe;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @author wolray
 */
public interface SeqReader<S, T> {
    Iterator<T> iterator(S source) throws Exception;

    default Seq.SafeSeq<T> read(S source) {
        return Seq.ofSafe(() -> iterator(source));
    }

    interface Is<T> extends SeqReader<Text, T> {
        default Seq.SafeSeq<T> read(URL url) {
            return read(url::openStream);
        }

        default Seq.SafeSeq<T> read(String file) {
            return read(() -> Files.newInputStream(Paths.get(file)));
        }

        default Seq.SafeSeq<T> read(File file) {
            return read(() -> Files.newInputStream(file.toPath()));
        }

        default Seq.SafeSeq<T> read(Class<?> cls, String resource) {
            return read(() -> {
                InputStream res = cls.getResourceAsStream(resource);
                if (res == null) {
                    throw new FileNotFoundException(resource);
                }
                return res;
            });
        }
    }

    interface Text extends WithCe.Supplier<InputStream> {}

    class Str implements SeqReader.Is<String> {
        static final Str INSTANCE = new Str() {};

        @Override
        public Iterator<String> iterator(Text source) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(source.get()));
            return new PickItr<String>() {
                @Override
                public String pick() {
                    try {
                        String s = reader.readLine();
                        if (s != null) {
                            return s;
                        }
                        reader.close();
                        return stop();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            };
        }
    }

    static Is<String> str() {
        return Str.INSTANCE;
    }
}