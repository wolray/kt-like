package ktlike.extensions.java.util.Map;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Extension
public class MapExt {
    @Self
    public static <K, V> Map<K, V> also(@This Map<K, V> self, Consumer<Map<K, V>> consumer) {
        consumer.accept(self);
        return self;
    }

    public static <K, V, E> E let(@This Map<K, V> self, Function<Map<K, V>, E> function) {
        return function.apply(self);
    }

    private static <K, V> Map<K, V> makeMap(Map<?, ?> map) {
        if (map instanceof LinkedHashMap<?, ?>) {
            return new LinkedHashMap<>(map.size());
        }
        if (map instanceof HashMap<?, ?>) {
            return new HashMap<>(map.size());
        }
        if (map instanceof TreeMap<?, ?>) {
            return new TreeMap<>();
        }
        if (map instanceof ConcurrentHashMap<?, ?>) {
            return new ConcurrentHashMap<>(map.size());
        }
        return new HashMap<>(map.size());
    }

    public static boolean isNullOrEmpty(@This Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    @Extension
    public static <K, V> Map<K, V> by(Consumer<Map<K, V>> mapConsumer) {
        return mapConsumer.use(new HashMap<>());
    }

    public static <K, V, T> Map<T, V> newKeys(@This Map<K, V> self, BiFunction<K, V, T> function) {
        Map<T, V> map = makeMap(self);
        for (Map.Entry<K, V> entry : self.entrySet()) {
            map.put(function.apply(entry.getKey(), entry.getValue()), entry.getValue());
        }
        return map;
    }

    public static <K, V, T> Map<K, T> newValues(@This Map<K, V> self, BiFunction<K, V, T> function) {
        Map<K, T> map = makeMap(self);
        for (Map.Entry<K, V> entry : self.entrySet()) {
            map.put(entry.getKey(), function.apply(entry.getKey(), entry.getValue()));
        }
        return map;
    }

    @Self
    public static <K, V> Map<K, V> add(@This Map<K, V> self, K key, V value) {
        self.put(key, value);
        return self;
    }
}