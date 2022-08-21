package com.github.wolray.kt.seq;

import java.util.*;
import java.util.function.*;

/**
 * @author wolray
 */
public abstract class Seq<T> extends IterableExt<T> {
    public static <T> Seq<T> of(Iterable<T> iterable) {
        if (iterable instanceof Seq<?>) {
            return ((Seq<T>)iterable);
        }
        Seq<T> seq = new Seq<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }
        };
        seq.setSize(iterable);
        return seq;
    }

    @SafeVarargs
    public static <T> Seq<T> of(T... ts) {
        return of(Arrays.asList(ts));
    }

    public static <K, V> Seq<Map.Entry<K, V>> of(Map<K, V> map) {
        return of(map.entrySet());
    }

    @SuppressWarnings("unchecked")
    public static <T> Seq<T> empty() {
        return (Seq<T>)EmptySeq.INSTANCE;
    }


    public static <T> Seq<T> of(Consumer<Yield<T>> yieldConsumer) {
        return of(10, yieldConsumer);
    }

    public static <T> Seq<T> of(int batchSize, Consumer<Yield<T>> yieldConsumer) {
        Yield<T> yield = new Yield<>(batchSize);
        yieldConsumer.accept(yield);
        return Seq.of(yield.list);
    }

    public static <T> Seq<T> gen(Supplier<T> supplier) {
        return of(() -> new Iterator<T>() {
            private T next;
            private int state = -1;

            private void computeNext() {
                next = supplier.get();
                if (next != null) {
                    state = 1;
                    return;
                }
                state = 0;
            }

            @Override
            public boolean hasNext() {
                if (state == -1) {
                    computeNext();
                }
                return state == 1;
            }

            @Override
            public T next() {
                if (state == -1) {
                    computeNext();
                }
                if (state == 1) {
                    T res = next;
                    next = null;
                    state = -1;
                    return res;
                }
                throw new NoSuchElementException();
            }
        });
    }

    @Override
    public String toString() {
        return join("=>", String::valueOf);
    }

    public <E> Seq<E> map(Function<T, E> function) {
        return of(() -> new Iterator<E>() {
            Iterator<T> iterator = iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return function.apply(iterator.next());
            }
        });
    }

    private Seq<T> stateful(boolean checkOnce, PickIterator.State afterPick, Predicate<T> predicate) {
        return of(() -> new PickIterator<>(iterator(), checkOnce, afterPick, predicate));
    }

    private Seq<T> takeIf(boolean checkOnce, Predicate<T> predicate) {
        return stateful(checkOnce, PickIterator.State.Unset, predicate);
    }

    public Seq<T> filter(Predicate<T> predicate) {
        return takeIf(false, predicate);
    }

    public Seq<T> filterNotNull() {
        return filter(Objects::nonNull);
    }

    public Seq<T> distinct() {
        Set<T> set = new HashSet<>();
        return filter(set::add);
    }

    public <E> Seq<T> distinctBy(Function<T, E> function) {
        Set<E> set = new HashSet<>();
        return filter(it -> set.add(function.apply(it)));
    }

    public Seq<T> takeWhile(Predicate<T> predicate) {
        return takeIf(true, predicate);
    }

    public Seq<T> take(int n) {
        if (n <= 0) {
            return empty();
        }
        return new SubSeq<>(this, 0, n);
    }

    public Seq<T> drop(int n) {
        if (n <= 0) {
            return this;
        }
        return new SubSeq<>(this, n, null);
    }

    public Seq<T> dropWhile(Predicate<T> predicate) {
        return stateful(false, PickIterator.State.Done, predicate.negate());
    }

    public Seq<T> onEach(Consumer<T> consumer) {
        return map(it -> {
            consumer.accept(it);
            return it;
        });
    }

    public <R> Seq<R> flatMap(Function<T, Iterable<R>> function) {
        return of(() -> new FlatIterator<>(iterator(), function));
    }

    public Seq<T> append(Iterable<T> seq) {
        return of(this, seq).flatMap(s -> s);
    }

    @SafeVarargs
    public final Seq<T> append(T... t) {
        return append(of(t));
    }

    public Seq<IndexedValue<T>> withIndex() {
        return of(() -> new Iterator<IndexedValue<T>>() {
            Iterator<T> iterator = iterator();
            int index = 0;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public IndexedValue<T> next() {
                return new IndexedValue<>(index++, iterator.next());
            }
        });
    }

    public <E> Seq<Pair<T, E>> zip(Iterable<E> es) {
        return SeqScope.INSTANCE.zip(this, es);
    }

    public <B, R> Seq<R> zip(Iterable<B> es, BiFunction<T, B, R> function) {
        return SeqScope.INSTANCE.zip(this, es, function);
    }

    public <B, C> Seq<Triple<T, B, C>> zip(Iterable<B> bs, Iterable<C> cs) {
        return SeqScope.INSTANCE.zip(this, bs, cs);
    }

    static class EmptySeq {
        static final Seq<Object> INSTANCE = of(Collections::emptyIterator);
    }

    public static class IndexedValue<T> {
        public final int index;
        public final T value;

        public IndexedValue(int index, T value) {
            this.index = index;
            this.value = value;
        }
    }
}
