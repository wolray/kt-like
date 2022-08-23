package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author wolray
 */
public class PickItr<T> implements Iterator<T> {
    private final Iterator<T> iterator;
    private final Supplier<State> computeNext;
    private final State afterPick;
    private final Predicate<T> predicate;
    private State state = State.Unset;
    private T next;

    public PickItr(Iterator<T> iterator, boolean checkOnce, State afterPick, Predicate<T> predicate) {
        this.iterator = iterator;
        computeNext = checkOnce ? this::computeNextOnce : this::computeNextUntil;
        this.afterPick = afterPick;
        this.predicate = predicate;
    }

    static <T> Iterator<T> takeIf(Iterator<T> source, boolean checkOnce, Predicate<T> predicate) {
        return new PickItr<>(source, checkOnce, State.Unset, predicate);
    }

    public static <T> Iterator<T> filter(Iterator<T> source, Predicate<T> predicate) {
        return takeIf(source, false, predicate);
    }

    public static <T> Iterator<T> takeWhile(Iterator<T> source, Predicate<T> predicate) {
        return takeIf(source, true, predicate);
    }

    public static <T> Iterator<T> dropWhile(Iterator<T> source, Predicate<T> predicate) {
        return new PickItr<>(source, false, State.Done, predicate.negate());
    }

    private State computeNextOnce() {
        if (iterator.hasNext()) {
            if (nextItem()) {
                return State.Cached;
            }
        }
        return State.Done;
    }

    private State computeNextUntil() {
        while (iterator.hasNext()) {
            if (nextItem()) {
                return State.Cached;
            }
        }
        return State.Done;
    }

    private boolean nextItem() {
        T t = iterator.next();
        if (predicate.test(t)) {
            next = t;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        if (state == State.Unset) {
            state = computeNext.get();
        }
        if (state == State.Cached) {
            return true;
        }
        return afterPick == State.Done && iterator.hasNext();
    }

    @Override
    public T next() {
        if (state == State.Unset) {
            state = computeNext.get();
        }
        if (state == State.Cached) {
            T res = next;
            next = null;
            state = afterPick;
            return res;
        }
        if (afterPick == State.Done) {
            return iterator.next();
        }
        throw new NoSuchElementException();
    }

    enum State {
        Unset,
        Done,
        Cached
    }
}