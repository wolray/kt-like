package com.github.wolray.kt.seq;

import com.github.wolray.kt.util.WithCe;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author wolray
 */
public interface SeqReader<S, T> {
    Iterator<T> iterator(S source) throws Exception;

    default SafeSeq<T> read(S source) {
        return new SafeSeq<T>() {
            @Override
            public Iterator<T> iterator() {
                try {
                    return SeqReader.this.iterator(source);
                } catch (Exception e) {
                    if (errorType != null && errorType.isAssignableFrom(e.getClass())) {
                        return Collections.emptyIterator();
                    }
                    throw new RuntimeException(e);
                }
            }
        };
    }

    abstract class SafeSeq<T> implements Seq<T> {
        Class<? extends Exception> errorType;

        public Seq<T> ignore(Class<? extends Exception> type) {
            errorType = type;
            return this;
        }
    }

    interface Is<T> extends SeqReader<Text, T> {
        default SafeSeq<T> read(URL url) {
            return read(url::openStream);
        }

        default SafeSeq<T> read(String file) {
            return read(() -> Files.newInputStream(Paths.get(file)));
        }

        default SafeSeq<T> read(File file) {
            return read(() -> Files.newInputStream(file.toPath()));
        }

        default SafeSeq<T> read(Class<?> cls, String resource) {
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

    interface Str extends SeqReader.Is<String> {
        Str INSTANCE = new Str() {};

        @Override
        default Iterator<String> iterator(Text source) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(source.get()));
            return new PickItr<String>() {
                @Override
                public String pick() {
                    try {
                        String s = reader.readLine();
                        if (s == null) {
                            reader.close();
                            return stop();
                        }
                        return s;
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