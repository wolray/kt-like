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
        Seq<Integer> seq = Seq.of(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
        Predicate<Integer> predicate = i -> (i & 1) == 0;
        System.out.println(seq);
        System.out.println(seq.filter(predicate));
        System.out.println(seq.dropWhile(predicate));
        System.out.println(seq.takeWhile(predicate));
//        System.out.println(seq.take(5));
//        System.out.println(seq.take(5).drop(2));
//        System.out.println(Seq.gen(() -> 1).take(4));
//        System.out.println(Seq.gen(() -> 1).take(5));
    }

    @Test
    public void testBatchList() {
        Seq<Integer> seq = Seq.of(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
        List<Integer> list = seq.toBatchList(5);
        System.out.println(list);
        System.out.println(seq.runningFold(0, Integer::sum));
        Seq<Integer> fib = Seq.gen(1, 1, Integer::sum).take(10);
        System.out.println(fib);
        System.out.println(fib);
        Seq<Integer> quad = Seq.gen(1, i -> i * 2).take(10);
        System.out.println(quad);
        System.out.println(quad);
    }

    @Test
    public void testChunked() {
        List<Integer> list = Arrays.asList(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
        System.out.println(Seq.of(list).chunked(2));
        System.out.println(Seq.of(list).chunked(3));
        System.out.println(Seq.of(list).chunked(4));
        System.out.println(Seq.of(list).chunked(5));
    }
}
