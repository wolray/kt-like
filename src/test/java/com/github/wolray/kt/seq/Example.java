package com.github.wolray.kt.seq;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wolray
 */
public class Example {
    public void streamExample() {
        Stream<Integer> stream = Stream.of(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);

        stream
            .map(i -> i + 1)
            .filter(i -> i % 2 == 0)
            .peek(System.out::println)
            .distinct()
            .flatMap(i -> Stream.of(i, i + 1, i + 2))
            .forEach(i -> {});

        List<Integer> linkedList = stream.collect(Collectors.toCollection(LinkedList::new));
        Set<Integer> set = stream.collect(Collectors.toSet());
        Map<Integer, Integer> map = stream.collect(Collectors.toMap(i -> i, i -> i));
        Map<Integer, Integer> linkedMap = stream.collect(Collectors.toMap(i -> i, i -> i, (v1, v2) -> v1, LinkedHashMap::new));
        Map<Integer, List<Integer>> listMap = stream.collect(Collectors.groupingBy(i -> i % 5));
        Map<Integer, List<String>> stringListMap = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.mapping(String::valueOf, Collectors.toList())));
        Map<Integer, List<String>> sortedStringListMap = stream.collect(Collectors.groupingBy(i -> i % 5,
            Collectors.mapping(String::valueOf, Collectors.collectingAndThen(Collectors.toList(), ls -> {
                Collections.sort(ls);
                return ls;
            }))));
        Map<Integer, Set<Integer>> setMap = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.toSet()));
        Map<Integer, Map<Integer, Integer>> mapMap = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.toMap(i -> i, i -> i)));
        Map<Integer, Integer> sumMap = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.summingInt(i -> i)));
        stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.mapping(i -> Arrays.asList(Integer.toString(i).split("")),
            Collectors.reducing(new ArrayList<String>(), (l1, l2) -> {
                l1.addAll(l2);
                return l1;
            }))));
    }

    @Test
    public void tmp() {
        Function<Integer, List<String>> function = i -> Arrays.asList(Integer.toString(i).split(""));
        Stream<Integer> stream = Stream.of(11, 21, 31, 41);
        Iterable<Integer> seq = Iterable.of(11, 21, 31, 41);
        Map<Integer, List<String>> map;
        map = stream.collect(Collectors.groupingBy(i -> i % 5,
            Collectors.mapping(function,
                Collectors.reducing(new ArrayList<>(), (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                }))));
        System.out.println(map);
        map = seq.groupBy(i -> i % 5).foldBy(ArrayList::new, (ls, i) -> ls.addAll(function.apply(i)));
        System.out.println(map);
    }

    public void seqExample() {
        Iterable<Integer> seq = Iterable.of(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);

        List<Integer> list = seq
            .map(i -> i + 1)
            .filter(i -> i % 2 == 0)
            .toList();

        seq
            .map(i -> i + 1)
            .filter(i -> i % 2 == 0)
            .filterNotNull()
            .onEach(System.out::println)
            .distinct()
            .distinctBy(i -> i % 10)
            .flatMap(i -> Iterable.of(i, i + 1, i + 2))
            .forEach(i -> {});

        List<Integer> linkedList = seq.toCollection(new LinkedList<>());
        Set<Integer> set = seq.toSet();
        Map<Integer, Integer> map = seq.toMap(i -> i, i -> i);
        Map<Integer, Integer> mapBy = seq.toMapBy(i -> i);
        Map<Integer, Integer> mapWith = seq.toMapWith(i -> i);
        Map<Integer, Integer> linkedMap = seq.toMap(new LinkedHashMap<>(), i -> i, i -> i);

        Map<Integer, List<Integer>> listMap = seq.groupBy(i -> i % 5).toList();
        Map<Integer, List<String>> stringListMap = seq.groupBy(i -> i % 5).toList(String::valueOf);
        Map<Integer, List<String>> sortedStringListMap = seq.groupBy(i -> i % 5).foldBy(ArrayList::new, (ls, i) -> ls.add(String.valueOf(i)));

        Map<Integer, Set<Integer>> setMap = seq.groupBy(i -> i % 5).toSet();
        Map<Integer, Map<Integer, Integer>> mapMap = seq.groupBy(i -> i % 5).toMap(i -> i, i -> i);
        seq.groupBy(i -> i % 5).count();
    }
}
