package com.github.wolray.kt.seq;

import org.junit.Test;

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
        System.out.println(seq.take(5));
        System.out.println(seq.take(5).drop(2));
        System.out.println(Seq.gen(() -> 1).take(4));
        System.out.println(Seq.gen(() -> 1).take(5));
    }
}
