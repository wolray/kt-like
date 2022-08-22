package com.github.wolray.kt.seq;

/**
 * @author wolray
 */
public class StateBox<T, S> {
    public T item;
    public S state;

    public static <T, S> StateBox<T, S> ofItem(T item) {
        StateBox<T, S> res = new StateBox<>();
        res.item = item;
        return res;
    }

    public static <T, S> StateBox<T, S> ofState(S state) {
        StateBox<T, S> res = new StateBox<>();
        res.state = state;
        return res;
    }
}
