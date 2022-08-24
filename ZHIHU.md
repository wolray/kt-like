# 前言
为了缓解小拇指疼痛，啊不，为了Java能用的更爽，我前后折腾了几天，把Kotlin的一大杀手特性：`Sequence`，移植到了Java。同时我还做了很多优化，相比官方源码实现上会更为优雅紧凑，并顺带提供了许多非常有用的新特性。

用过Kotlin的朋友应该都了解，Sequence用起来有多么的丝滑流畅，其API设计不管是简洁易用性还是功能丰富程度都可以说完爆Java8的`Stream`。然而，哪怕Kotlin和Java可以无缝互相调用，要想在Java环境下使用`Sequence`却没那么容易。这是由于`Sequence`的所有功能都是通过扩展函数的机制实现的，其本身只有一个跟`Iterable`一模一样的接口。强行通过`SequenceKt`的静态方法调用则会破坏链式调用的结构，失去实用价值。个人猜测这可能也是Kotlin为了吸引Java用户所刻意为之的设计。

如果没用过Kotlin或者用过Kotlin但是对`Sequence`不熟悉也没关系，把它理解为一个API全面优升级化之后的高级版`Stream`就行。这个新的容器，我称之为`Seq`。你或许曾在Github上搜到过一个名为`Sek`库，它也是提供了一个Java环境下调用`Sequence`的方式，只不过实质上只是对`Sequence`的封装，需要依赖很重的Kotlin的runtime。

废话不多说，我下面会先与`Stream`对比，展现它好在哪里。然后再与`Sequence`对比，介绍它的新特性。

# API介绍
## Seq对比Stream
首先我们有一个`List<Integer>`
```java
List<Integer> list = Arrays.asList(0, 2, 4, 1, 6, 3, 5, 7, 10, 11, 12);
```
然后分别构建出`Stream<Integer>`和`Seq<Integer>`
```java
//stream
Stream<Integer> stream = list.stream();
//seq
Seq<Integer> seq = Seq.of(list);
```
### 终端操作对比
所谓终端操作，就是把惰性的`Stream`或者`Seq`收集为一些常见的容器类型，或者计数、求和等不可逆操作。我们首先对比这两种API在终端操作上的区别。

假设我们要做三件事情，先每个元素+1，然后过滤其中的偶数，最后转为`List`，两种实现方式分别是
```java
//stream
List<Integer> list = stream
    .map(i -> i + 1)
    .filter(i -> i % 2 == 0)
    .collect(Collectors.toList());
//seq
List<Integer> list = seq
    .map(i -> i + 1)
    .filter(i -> i % 2 == 0)
    .toList();
```
转为`LinkedList`
```java
List<Integer> linked;
//stream
linked = stream.collect(Collectors.toCollection(LinkedList::new));
//seq
linked = seq.toCollection(new LinkedList<>());
```
转为`Set`
```java
Set<Integer> set;
//stream
set = stream.collect(Collectors.toSet());
//seq
set = seq.toSet();
```
转为`Map`
```java
Map<Integer, Integer> map;
//stream
map = stream.collect(Collectors.toMap(i -> i, i -> i));
//seq
map = seq.toMap(i -> i, i -> i);
```
或者更简单的，当只需要生成额外的key或者value时
```java
map = seq.toMapBy(i -> i);
map = seq.toMapWith(i -> i);
```
如果你的目标`Map`不是默认的`HashMap`，而是要指定类型为`LinkedHashMap`
```java
Map<Integer, Integer> linkedMap;
//stream
linkedMap = stream.collect(Collectors.toMap(i -> i, i -> i, (v1, v2) -> v1, LinkedHashMap::new));
//seq
linkedMap = seq.toMap(new LinkedHashMap<>(), i -> i, i -> i);
```
如果你要按除以5的余数做GroupBy
```java
Map<Integer, List<Integer>> listMap;
//stream
listMap = stream.collect(Collectors.groupingBy(i -> i % 5));
//seq
listMap = seq.groupBy(i -> i % 5).toList();
```
如果你要做GroupBy并且希望每个分组都是一个`Set`
```java
Map<Integer, Set<Integer>> setMap;
//stream
setMap = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.toSet()));
//seq
setMap = seq.groupBy(i -> i % 5).toSet();
```
如果你要做GroupBy并且希望每个分组的元素都先转为`String`
```java
Map<Integer, List<String>> stringListMap;
//stream
stringListMap = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.mapping(String::valueOf, Collectors.toList())));
//seq
stringListMap = seq.groupBy(i -> i % 5).toList(String::valueOf);
```
如果你要做GroupBy并且希望每个分组都转为一个`Map`
```java
Map<Integer, Map<Integer, Integer>> mapMap;
//stream
mapMap = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.toMap(i -> i, i -> i)));
//seq
mapMap = seq.groupBy(i -> i % 5).toMap(i -> i, i -> i);
```
如果你要做GroupBy然后统计每个分组的元素个数
```java
//stream 默认只能统计为Long
Map<Integer, Long> countMapAsLong = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.counting()));
//seq 默认统计为Integer
Map<Integer, Integer> countMapAsInt = seq.groupBy(i -> i % 5).count();
```
如果你要做GroupBy然后对每个分组求和
```java
Map<Integer, Integer> sumMap;
//stream
sumMap = stream.collect(Collectors.groupingBy(i -> i % 5, Collectors.summingInt(i -> i)));
//seq
sumMap = seq.groupBy(i -> i % 5).sumInt(i -> i);
```
如果你要做GroupBy并且希望每个分组的元素都先转为`String`然后按数字`split`再组合为一个`List`，再合并。
例如3个元素的分组`[11, 21, 31, 41]`转为`[1, 1, 2, 1, 3, 1, 4, 1]`。实现该操作需要用到函数式编程里的`reduce`或者`fold`的概念。首先写出`int->List<String>`的函数。
```java
Map<Integer, List<String>> map;
//function
Function<Integer, List<String>> function = i -> Arrays.asList(Integer.toString(i).split(""));
//stream
map = stream.collect(Collectors.groupingBy(i -> i % 5,
    Collectors.mapping(function,
        Collectors.reducing(new ArrayList<>(), (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        }))));
```
对于`Seq`，我设计出一个新的操作逻辑`foldBy`，类似于`fold`，但是更灵活，可以十分简单快速的实现上述需求
```java
//seq
map = seq.groupBy(i -> i % 5).foldBy(ArrayList::new, (ls, i) -> ls.addAll(function.apply(i)));
```
根据以上终端操作的对比可以看出，让`Stream`广为诟病的一个最重要的缺点就是它那极度冗长啰嗦的`Collectors`，可以说是为了函数式而函数式，非常之丑陋。好在这个硬伤不管是在`Kotlin`里还是`Seq`里都得到了彻底的解决。而借助`foldBy`的设计，`Seq`相比`Sequence`提供了更为灵活的能力，有兴趣的朋友可以自行对比实验。

### 流式操作
在终端操作之外，`Stream`成功的一点在于引入了流式或者说链式操作，可以不断的进行`map`，`filter`等标准的函数式变换。对于这些能力，`Seq`自然也是支持的，同时还有许多从`kotlin`里借鉴(抄)来的好东西。
```java
stream
    .map(i -> i + 1)
    .skip(1)
    .limit(8)
    .filter(i -> i % 2 == 0)
    .peek(System.out::println)
    .distinct()
    .flatMap(i -> Stream.of(i, i + 1, i + 2));

seq
    .map(i -> i + 1)
    //等价于skip
    .drop(1)
    //等价于limit
    .take(8)
    .filter(i -> i % 2 == 0)
    //相当于filter(Objects:nonNull)
    .filterNotNull()
    //等价于peek
    .onEach(System.out::println)
    .flatMap(i -> Seq.of(i, i + 1, i + 2))
    .distinct()
    //stream没有对应功能
    .distinctBy(i -> i % 10)
    //stream没有对应功能
    .dropWhile(i < 0)
    //stream没有对应功能
    .takeWhile(i > 10)
    //缓存为一个list，便于二次使用
    //sequence没有对应功能
    .cache();
```

## Seq对比Sequence
待续（虽然暂时没有但是不影响使用哈）。

# 如何使用(Maven)
添加`jitpack`库，然后在`dependencies`应用
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://www.jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    ...
    <dependency>
        <groupId>com.github.wolray</groupId>
        <artifactId>kt-like</artifactId>
        <version>1.0.0</version>
    </dependency>
    ...
</dependencies>
```
