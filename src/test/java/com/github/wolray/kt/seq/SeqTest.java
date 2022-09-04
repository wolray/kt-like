package com.github.wolray.kt.seq;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author wolray
 */
public class SeqTest {
    @Test
    public void testResult() {
        List<Integer> seq = List.of(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
        System.out.println(seq);
        Predicate<Integer> predicate = i -> (i & 1) == 0;
        seq.filter(predicate).assertTo(0, 2, 4, 6, 10, 12);
        seq.dropWhile(predicate).assertTo(1, 6, 3, 5, 7, 10, 11, 12);
        seq.takeWhile(predicate).assertTo(0, 2, 4);
        seq.take(5).assertTo(0, 2, 4, 1, 6);
        seq.take(5).drop(2).assertTo(4, 1, 6);
        genIterable(() -> 1).take(4).assertTo(1, 1, 1, 1);
        genIterable(() -> 1).take(5).assertTo(1, 1, 1, 1, 1);
        repeatAsIterable(1, 5).assertTo(1, 1, 1, 1, 1);
        rangeBy(0, 10, 2).assertTo(0, 2, 4, 6, 8);
    }

    @Test
    public void testBatchList() {
        Iterable<Integer> seq = Iterable.of(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
        List<Integer> list = seq.toBatchList(5);
        System.out.println(list);
        seq.runningFold(0, Integer::sum).assertTo(0, 2, 6, 7, 13, 16, 21, 28, 38, 49, 61);
        Iterable<Integer> fib = genIterable(1, 1, Integer::sum).take(10);
        fib.assertTo(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
        fib.assertTo(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
        Iterable<Integer> quad = genIterable(1, i -> i * 2).take(10);
        quad.assertTo(1, 2, 4, 8, 16, 32, 64, 128, 256, 512);
        quad.assertTo(1, 2, 4, 8, 16, 32, 64, 128, 256, 512);
    }

    @Test
    public void testWindow() {
        System.out.println(rangeOf(5).display());
        System.out.println(rangeUntil(0, 10).display());
        System.out.println(rangeBy(0, 10, 2).display());
        System.out.println(rangeBy(10, 0, -2).display());

        List<Integer> list = Arrays.asList(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
        list.map(i -> i + 1)
            .filter(i -> i < 10)
            .toList();
        list.all(i -> i > 0);
        System.out.println(list.chunked(2).display());
        System.out.println(list.chunked(3).display());
        System.out.println(list.chunked(4).display());
        System.out.println(list.chunked(5).display());
        Iterable<Integer> take = list.take(3);
        System.out.println(take.cartesian(take).display());
    }
}
