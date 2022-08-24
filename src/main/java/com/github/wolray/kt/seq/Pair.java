package com.github.wolray.kt.seq;

/**
 * @author wolray
 */
public class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", first, second);
    }
}
