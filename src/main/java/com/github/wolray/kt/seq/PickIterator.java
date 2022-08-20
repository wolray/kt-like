package com.github.wolray.kt.seq;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * @author wolray
 */
public class PickIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;
    private final Runnable computeNext;
    private final State afterPick;
    private final Predicate<T> predicate;
    private State state = State.Unset;
    private T next;

    public PickIterator(Iterator<T> iterator, boolean checkOnce, State afterPick, Predicate<T> predicate) {
        this.iterator = iterator;
        computeNext = checkOnce ? this::computeNextOnce : this::computeNextUntil;
        this.afterPick = afterPick;
        this.predicate = predicate;
    }

    private void computeNextOnce() {
        if (iterator.hasNext()) {
            if (nextItem()) {
                return;
            }
        }
        state = State.Done;
    }

    private void computeNextUntil() {
        while (iterator.hasNext()) {
            if (nextItem()) {
                return;
            }
        }
        state = State.Done;
    }

    boolean nextItem() {
        T t = iterator.next();
        if (predicate.test(t)) {
            next = t;
            state = State.Cached;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        if (state == State.Unset) {
            computeNext.run();
        }
        if (state == State.Cached) {
            return true;
        }
        return afterPick == State.Done && iterator.hasNext();
    }

    @Override
    public T next() {
        if (state == State.Unset) {
            computeNext.run();
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
