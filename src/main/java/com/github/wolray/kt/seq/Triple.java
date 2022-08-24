package com.github.wolray.kt.seq;

/**
 * @author wolray
 */
public class Triple<T, A, B> {
    public final T first;
    public final A second;
    public final B third;

    public Triple(T first, A second, B third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", first, second, third);
    }
}
