package com.github.wolray.kt.seq;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
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
        seq.filter(predicate).assertTo(0, 2, 4, 6, 10, 12);
        seq.dropWhile(predicate).assertTo(1, 6, 3, 5, 7, 10, 11, 12);
        seq.takeWhile(predicate).assertTo(0, 2, 4);
        seq.take(5).assertTo(0, 2, 4, 1, 6);
        seq.take(5).drop(2).assertTo(4, 1, 6);
        Seq.gen(() -> 1).take(4).assertTo(1, 1, 1, 1);
        Seq.gen(() -> 1).take(5).assertTo(1, 1, 1, 1, 1);
        Seq.repeat(5, 1).assertTo(1, 1, 1, 1, 1);
        Seq.range(0, 10, 2).assertTo(0, 2, 4, 6, 8);
    }

    @Test
    public void testBatchList() {
        Seq<Integer> seq = Seq.of(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
        seq.runningFold(0, Integer::sum).assertTo(0, 2, 6, 7, 13, 16, 21, 28, 38, 49, 61);
        Seq<Integer> fib = Seq.gen(1, 1, Integer::sum).take(10);
        fib.assertTo(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
        fib.assertTo(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
        Seq<Integer> quad = Seq.gen(1, i -> i * 2).take(10);
        quad.assertTo(1, 2, 4, 8, 16, 32, 64, 128, 256, 512);
        quad.assertTo(1, 2, 4, 8, 16, 32, 64, 128, 256, 512);
    }

    @Test
    public void testChunked() {
        List<Integer> list = Arrays.asList(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
        System.out.println(Seq.of(list).chunked(2).toList());
        System.out.println(Seq.of(list).chunked(3).toList());
        System.out.println(Seq.of(list).chunked(4).toList());
        System.out.println(Seq.of(list).chunked(5).toList());
    }

    @Test
    public void testYield() {
        Seq.by(it -> it
            .yield(2)
            .yield(3)
            .yield(4)
            .yieldAll(Arrays.asList(5, 6, 7))
            .yield(8)
            .yieldAll(Collections.emptyList())
            .yield(9)
            .yieldAll(Collections.singletonList(10))
            .yield(11)).assertTo(2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
    }

    @Test
    public void testSeqList() {
        SeqList<Integer> list = SeqList.array();
        list.add(1);
        list.add(2);
        list.add(3);
        list.set(0, 6);
        list.add(2, 10);
        list.assertTo(6, 2, 10, 3);
    }
}
