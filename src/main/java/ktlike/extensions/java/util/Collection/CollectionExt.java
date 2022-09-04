package ktlike.extensions.java.util.Collection;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import java.util.Collection;

@Extension
public class CollectionExt {
    public static boolean isNullOrEmpty(@This Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}