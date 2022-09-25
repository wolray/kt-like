package com.github.wolray.kt.seq;

import com.github.wolray.kt.util.WithCe;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @author wolray
 */
public interface SeqGenerator<S, T> {
    Iterator<T> iterator(S source);

    default Seq<T> seq(S source) {
        return () -> iterator(source);
    }

    interface Is<T> extends SeqGenerator<Text, T> {
        default Seq<T> seq(URL url) {
            Text source = url::openStream;
            return seq(source);
        }

        default Seq<T> seq(String file) {
            return seq(() -> Files.newInputStream(Paths.get(file)));
        }

        default Seq<T> seq(File file) {
            return seq(() -> Files.newInputStream(file.toPath()));
        }

        default Seq<T> seq(Class<?> cls, String resource) {
            return seq(() -> cls.getResourceAsStream(resource));
        }
    }

    interface Text extends WithCe.Supplier<InputStream> {}

    interface Simple extends Is<String> {
        @Override
        default Iterator<String> iterator(Text source) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(source.asNormal().get()));
            WithCe.Supplier<String> supplier = reader::readLine;
            return PickItr.genUntilNull(supplier.asNormal());
        }
    }
}