package ktlike.extensions.java.lang.Object;

import manifold.ext.rt.api.Extension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Extension
public class Empties {
    @Extension
    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    @Extension
    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    @Extension
    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }
}
