package me.shaftesbury.utils.functional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class Iterable2Test {
    private static Function<Integer, Integer> DoublingGenerator = a -> 2 * a;

    @Test
    void initTest1() {
        final Iterable2<Integer> output = IterableHelper.init(DoublingGenerator, 5);
        assertThat(output).containsExactly(2, 4, 6, 8, 10);
    }

    @Test
    void mapTest1() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Iterable2<String> output = input.map(Functional.dStringify());
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void sortWithTest1() {
        final Iterable2<Integer> i = IterableHelper.asList(1, 6, 23, 7, 4);
        final Iterable2<Integer> output = i.sortWith(Integer::compare);
        assertThat(output).containsExactly(1, 4, 6, 7, 23);
    }

    @Test
    void sortWithTest2() {
        final Iterable2<Integer> i = IterableHelper.asList(1, 6, 23, 7, 4);
        final Iterable2<Integer> j = i.sortWith(Comparator.comparingInt(a -> a));
        assertThat(j).containsExactly(1, 4, 6, 7, 23);
    }

    @Test
    void sortWithTest3() {
        final Iterable2<Integer> i = IterableHelper.asList(1, 6, 23, 7, 4);
        final Iterable2<Integer> j = i.sortWith(Functional.dSorter);
        assertThat(j).containsExactly(1, 4, 6, 7, 23);
    }

    private static Function<Integer, Integer> TriplingGenerator = a -> 3 * a;

    private static Function<Integer, Integer> QuadruplingGenerator = a -> 4 * a;

    private static boolean BothAreEven(final int a, final int b) {
        return Functional.isEven.apply(a) && Functional.isEven.apply(b);
    }

    @Test
    void forAll2Test1() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> m = IterableHelper.init(QuadruplingGenerator, 5);
        assertThat(l.forAll2(Iterable2Test::BothAreEven, m)).isTrue();
    }

    private static boolean BothAreLessThan10(final int a, final int b) {
        return a < 10 && b < 10;
    }

    private static BiFunction<Integer, Integer, Boolean> dBothAreLessThan10 = Iterable2Test::BothAreLessThan10;

    @Test
    void forAll2Test2() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> m = IterableHelper.init(TriplingGenerator, 5);

        assertThat(Functional.forAll2(dBothAreLessThan10, l, m)).isFalse();
    }

    @Test
    void forAll2Test3() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> m = IterableHelper.init(QuadruplingGenerator, 7);

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> Functional.forAll2(Iterable2Test::BothAreEven, l, m));
    }

    @Test
    void compositionTest1A() {
        final Iterable2<Integer> i = IterableHelper.asList(1, 2, 3, 45, 56, 6);

        final boolean allOdd = i.forAll(Functional.isOdd);
        final boolean notAllOdd = i.exists(Functional.not(Functional.isOdd));

        assertThat(allOdd).isFalse();
        assertThat(notAllOdd).isTrue();
    }

    @Test
    void compositionTest2() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> m = IterableHelper.init(TriplingGenerator, 5);
        assertThat(Functional.forAll2(Functional.not2(dBothAreLessThan10), l, m)).isFalse();
        // equivalent to BothAreGreaterThanOrEqualTo10
    }

    @Test
    void compositionTest2a() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> m = IterableHelper.init(TriplingGenerator, 5);
        final int lowerLimit = 1;
        final int upperLimit = 16;
        assertThat(Functional.forAll2(Functional.not2((a, b) -> a > lowerLimit && b > lowerLimit), l, m)).isFalse();
    }

    @Test
    void compositionTest2b() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> m = IterableHelper.init(TriplingGenerator, 5);
        final int lowerLimit = 1;
        final int upperLimit = 16;
        assertThat(Functional.forAll2(Functional.not2((a, b) -> a > upperLimit && b > upperLimit), l, m)).isTrue();
    }

    @Test
    void partitionTest1() {
        final Iterable2<Integer> m = IterableHelper.init(TriplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isOdd, m);

        final Integer[] left = {3, 9, 15};
        final Integer[] right = {6, 12};
        assertThat(r.getLeft().toArray()).containsExactly(left);
        assertThat(r.getRight().toArray()).containsExactly(right);
    }

    @Test
    void partitionTest1a() {
        final Iterable2<Integer> m = IterableHelper.init(TriplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = m.partition(Functional.isOdd);

        final Integer[] left = {3, 9, 15};
        final Integer[] right = {6, 12};
        assertThat(r.getLeft().toArray()).containsExactly(left);
        assertThat(r.getRight().toArray()).containsExactly(right);
    }

    @Test
    void partitionTest2() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isEven, l);
        final Iterable2<Integer> expected = IterableHelper.init(DoublingGenerator, 5);
        assertThat(r.getLeft()).containsExactlyElementsOf(expected);
        assertThat(r.getRight().toArray()).containsExactly(new Integer[]{});
    }

    @Test
    void partitionTest3() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isEven, l);
        final Iterable2<Integer> expected = IterableHelper.init(DoublingGenerator, 5).filter(Functional.isEven);
        assertThat(r.getLeft()).containsExactlyElementsOf(expected);
    }

    @Test
    void toStringTest1() {
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<String> ls = li.map(Functional.dStringify());
        //String s = String.Join(",", ls);
        assertThat(ls).containsExactlyElementsOf(IterableHelper.asList("2", "4", "6", "8", "10"));
    }

    @Test
    void chooseTest1B() throws OptionNoValueAccessException {
        final Iterable2<Integer> li = IterableHelper.init(TriplingGenerator, 5);
        final Iterable2<String> o = li.choose(
                i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None());
        final Iterable2<String> expected = IterableHelper.asList("6", "12");
        assertThat(expected).containsExactlyElementsOf(o);
    }

    @Test
    void chooseTest2A() //throws OptionNoValueAccessException
    {
        Map<Integer, String> o = null;
        try {
            final Iterable2<Integer> li = IterableHelper.init(TriplingGenerator, 5);
            o = Functional.toDictionary(Functional.identity(), Functional.dStringify(),
                    li.choose(
                            i -> i % 2 == 0 ? Option.toOption(i) : Option.None()));
        } catch (final Exception e) {
        }
        final Map<Integer, String> expected = new HashMap<>();
        expected.put(6, "6");
        expected.put(12, "12");
        assertThat(expected.size() == o.size()).isTrue();
        for (final Integer expectedKey : expected.keySet()) {
            assertThat(o.containsKey(expectedKey)).isTrue();
            final String expectedValue = expected.get(expectedKey);
            //assertThat("Expected '"+expectedValue+"' but got '"+o.get(expectedKey)+"'").isEqualTo(expectedValue,o.get(expectedKey));
            assertThat(o.get(expectedKey).equals(expectedValue)).isTrue();
        }
    }

    private final static <B, C> boolean Fn(final B b, final C c) {
        return b.equals(c);
    }

    private final static <B, C> Function<C, Boolean> curried_fn(final B b) {
        return c -> Fn(b, c);
    }

    @Test
    void curriedFnTest1() {
        final boolean test1a = Fn(1, 2);
        final boolean test1b = curried_fn(1).apply(2);
        assertThat(test1b).isEqualTo(test1a);
    }

    private static int adder_int(final int left, final int right) {
        return left + right;
    }

    private static Function<Integer, Integer> curried_adder_int(final int c) {
        return p -> adder_int(c, p);
    }

    @Test
    void curriedFnTest2() {
        final Iterable2<Integer> a = IterableHelper.asList(1, 2, 3, 4, 5);
        final Iterable2<Integer> b = a.map(
                a1 -> adder_int(2, a1));
        final Iterable2<Integer> c = a.map(curried_adder_int(2));
        assertThat(c).containsExactlyElementsOf(b);
    }

    private static String csv(final String state, final Integer a) {
        return StringUtils.isEmpty(state) ? a.toString() : state + "," + a;
    }

    @Test
    void foldvsMapTest1() {
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        final String s1 = Functional.join(",", li.map(Functional.dStringify()));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = IterableHelper.init(DoublingGenerator, 5).fold(
                Iterable2Test::csv, "");
        assertThat(s2).isEqualTo(s1);
    }

    private final Function<Iterable2<Integer>, String> concatenate =
            l -> l.fold(Iterable2Test::csv, "");

    @Test
    void fwdPipelineTest1() {
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        final String s1 = li.in(concatenate);
        assertThat(s1).isEqualTo("2,4,6,8,10");
    }

    private final UnaryFunction<Iterable2<Integer>, Iterable2<Integer>> evens_f =
            new UnaryFunction<Iterable2<Integer>, Iterable2<Integer>>() {

                public Iterable2<Integer> apply(final Iterable2<Integer> l) {
                    return l.filter(Functional.isEven);
                }
            };

    @Test
    void fwdPipelineTest2() {
        final String s1;
        {
            final Iterable2<Integer> li = IterableHelper.init(TriplingGenerator, 5);
            final Iterable2<Integer> evens = li.in(evens_f);
            s1 = evens.in(concatenate);
        }
        final Iterable2<Integer> li = IterableHelper.init(TriplingGenerator, 5);
        final String s2 = li.in(evens_f.then(concatenate));
        assertThat(s1).isEqualTo("6,12");
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void compositionTest3() {
        final Iterable2<Integer> li = IterableHelper.init(TriplingGenerator, 5);
        final String s = li.in(evens_f.then(concatenate));
        assertThat(s).isEqualTo("6,12");
    }

    @Test
    void compositionTest4() {
        final Iterable2<Integer> li = IterableHelper.init(TriplingGenerator, 5);
        final String s = evens_f.then(concatenate).apply(li);
        assertThat(s).isEqualTo("6,12");
    }

    @Test
    void indentTest1() {
        final int level = 5;
        final String expectedResult = "     ";

        String indentedName = "";
        for (int i = 0; i < level; ++i) {
            indentedName += " ";
        }
        assertThat(expectedResult).isEqualTo(indentedName);

        {
            final Iterable2<String> indentation = IterableHelper.init(
                    integer -> " ", level);
            assertThat("     ").isEqualTo(Functional.join("", indentation));
        }
        {
            final Iterable2<String> indentation = IterableHelper.init(
                    integer -> " ", level);
            final String s = indentation.fold(
                    (state, str) -> state + str, "");
            assertThat(expectedResult).isEqualTo(s);
        }
        {
            final Iterable2<String> indentation = IterableHelper.init(
                    integer -> " ", level);
            final Function<Iterable2<String>, String> folder =
                    l -> l.fold((state, str) -> state + str, "");

            final String s1 = indentation.in(folder);
            assertThat(expectedResult).isEqualTo(s1);
        }
    }

    @Test
    void indentTest2() {
        final int level = 5;
        final String expectedResult = "     BOB";
        assertThat(Functional.indentBy(level, " ", "BOB")).isEqualTo(expectedResult);
    }


    @Test
    void chooseTest3A() throws OptionNoValueAccessException {
        final Iterable2<Integer> li = IterableHelper.init(TriplingGenerator, 5);
        final Iterable2<Integer> o =
                li.choose(
                        i -> i % 2 == 0 ? Option.toOption(i) : Option.None());

        final Integer[] expected = new Integer[]{6, 12};
        assertThat(o).containsExactly(expected);
    }

/*
        [Test]
        public void tryGetValueTest1() {
            var d = new Dictionary<string, int>();
            d["one"] = 1;
            Assert.AreEqual(1, Iterable2Helpers.TryGetValue_nullable("one", d));
        }

 */

    /*
            [Test]
        public void tryGetValueTest2() {
            var d = new Dictionary<string, int>();
            d["one"] = 1;
            Assert.IsNull(Iterable2Helpers.TryGetValue_nullable("two", d));
        }

     */
/*
        [Test]
        public void tryGetValueTest3() {
            var d = new Dictionary<string, string>();
            d["one"] = "ONE";
            Assert.AreEqual("ONE", Iterable2Helpers.TryGetValue("one", d).Some);
        }

 */

    /*
            [Test]
        public void tryGetValueTest4() {
            var d = new Dictionary<string, string>();
            d["one"] = "ONE";
            Assert.IsTrue(Iterable2Helpers.TryGetValue("two", d).None);
        }

     */

    /*
            [Test]
        public void tryGetValueTest5() {
            var d = new Dictionary<string, List<int>>();
            var l = new List<int>(new[] {1, 2, 3});
            d["one"] = l;
            Assert.AreEqual(l, Iterable2Helpers.TryGetValue("one", d).Some);
        }
*/
    /*
        [Test]
        public void tryGetValueTest6() {
            var d = new Dictionary<string, string>();
            d["one"] = "ONE";
            Assert.IsTrue(Iterable2Helpers.TryGetValue("two", d).None);
        }

     */

    private class myInt {
        private final int _i;

        public myInt(final int i) {
            _i = i;
        }

        public final int i() {
            return _i;
        }
    }


    @Test
    void foldAndChooseTest1() {
        final Map<Integer, Double> missingPricesPerDate = new Hashtable<>();
        final Iterable2<Integer> openedDays = IterableHelper.init(TriplingGenerator, 5);
        Double last = 10.0;
        for (final int day : openedDays) {
            final Double value = day % 2 == 0 ? (Double) ((double) (day / 2)) : null;
            if (value != null)
                last = value;
            else
                missingPricesPerDate.put(day, last);
        }

        final Iterable2<myInt> openedDays2 = IterableHelper.init(
                a -> new myInt(3 * a), 5);
        final Pair<Double, List<myInt>> output = Functional.foldAndChoose(
                (state, day) -> {
                    final Double value = day.i() % 2 == 0 ? (Double) ((double) (day.i() / 2)) : null;
                    return value != null
                            ? Pair.of(value, Option.None())
                            : Pair.of(state, Option.toOption(day));
                }, 10.0, openedDays2);

        assertThat(output.getLeft()).isEqualTo(last);
        final List<Integer> keys = new ArrayList<>(missingPricesPerDate.keySet());
        Collections.sort(keys);
        assertThat(Functional.map(myInt::i, output.getRight()).toArray()).containsExactly(keys.toArray());
    }

    @Test
    void joinTest1() {
        final String expected = "3,6,9,12,15";
        {
            final Iterable2<Integer> ids = IterableHelper.init(TriplingGenerator, 5);
            assertThat(Functional.join(",", ids.map(Functional.dStringify()))).isEqualTo(expected);
        }
        final Iterable2<Integer> ids = IterableHelper.init(TriplingGenerator, 5);
        assertThat(")).isEqualTo(expected, ids.join(");
    }

    @Test
    void joinTest2() {
        final String expected = "'3','6','9','12','15'";
        final Function<Integer, String> f =
                id -> "'" + id + "'";
        {
            final Iterable2<Integer> ids = IterableHelper.init(TriplingGenerator, 5);
            assertThat(Functional.join(",", ids.map(f))).isEqualTo(expected);
        }
        final Iterable2<Integer> ids = IterableHelper.init(TriplingGenerator, 5);
        assertThat(Functional.join(",", ids, f)).isEqualTo(expected);
    }

    @Test
    void betweenTest1() {
        final int lowerBound = 2, upperBound = 4;
        assertThat(Functional.between(lowerBound, upperBound, 3)).isTrue();
    }

    @Test
    void betweenTest2() {
        final int lowerBound = 2, upperBound = 4;
        assertThat(Functional.between(lowerBound, upperBound, 1)).isFalse();
    }

    @Test
    void betweenTest3() {
        final double lowerBound = 2.5, upperBound = 2.6;
        assertThat(Functional.between(lowerBound, upperBound, 2.55)).isTrue();
    }

    @Test
    void testIsEven_withEvenNum() {
        assertThat(Functional.isEven.apply(2)).isTrue();
    }

    @Test
    void testIn() {
        final Integer a = 10;
        assertThat(Functional.in(a, Functional.isEven)).isTrue();
    }


    /*@Test void testThen() {
        // mult(two,three).then(add(four)) =>
        // then(mult(two,three),add(four))
        // 2 * 3 + 4 = 10
        Integer two = 2;
        Integer three = 3;
        Integer four = 4;
        Iterable2.then(new Function<Integer,Integer>() {

            public Integer apply(Integer i) { return }
        })
    } */


    @Test
    void findLastTest1() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.findLast(Functional.isOdd, l));
    }

    @Test
    void findLastTest2() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        assertThat(l.findLast(Functional.isEven)).isEqualTo((Integer) 10);
    }

    @Test
    void toArrayTest1() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Iterable2<String> strs = input.map(Functional.dStringify());
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");

        final Object[] output = strs.toArray();

        assertThat(output.length).isEqualTo(expected.size());
        for (int i = 0; i < expected.size(); ++i)
            assertThat(output[i]).isEqualTo(expected.get(i));
    }

    @Test
    void toSetTest1() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Set<Integer> integerSet = input.toSet();
        final Set<Integer> expected = new HashSet<>();
        expected.add(1);
        expected.add(2);
        expected.add(3);
        expected.add(4);
        expected.add(5);

        assertThat(expected.containsAll(integerSet)).isTrue();
        assertThat(integerSet.containsAll(expected)).isTrue();
    }

    @Test
    void toDictionaryTest() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Map<Integer, String> output = input.toDictionary(Functional.identity(), Functional.dStringify());

        final Map<Integer, String> expected = new HashMap<>();
        expected.put(1, "1");
        expected.put(2, "2");
        expected.put(3, "3");
        expected.put(4, "4");
        expected.put(5, "5");
        assertThat(expected.size() == output.size()).isTrue();
        for (final Integer expectedKey : expected.keySet()) {
            assertThat(output.containsKey(expectedKey)).isTrue();
            final String expectedValue = expected.get(expectedKey);
            //assertThat("Expected '"+expectedValue+"' but got '"+o.get(expectedKey)+"'").isEqualTo(expectedValue,o.get(expectedKey));
            assertThat(output.get(expectedKey).equals(expectedValue)).isTrue();
        }
    }

    @Test
    void lastTest1() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        assertThat((long) Functional.last(input)).isEqualTo(5);
    }

    @Test
    void lastTest1a() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        assertThat((long) input.last()).isEqualTo(5);
    }

    @Test
    void lastTest2() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Iterable<String> strs = input.map(Functional.dStringify());
        assertThat(Functional.last(strs)).isEqualTo("5");
    }

    @Test
    void concatTest1() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Iterable2<Integer> expected = IterableHelper.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
        assertThat(input.concat(input)).containsExactlyElementsOf(expected);
    }

    @Test
    void seqConcatTest1() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final Iterable2<String> expected = IterableHelper.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable2<String> strs = input.map(Functional.dStringify());
        final Iterable2<String> output = strs.concat(input.map(doubler).map(Functional.dStringify()));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void arrayIterableTest1() {
        final Integer[] input = new Integer[]{1, 2, 3, 4, 5};
        final Iterable2<Integer> expected = IterableHelper.asList(1, 2, 3, 4, 5);

        final ArrayIterable<Integer> ait = ArrayIterable.create(input);
        final List<Integer> output = new ArrayList<>();
        for (final Integer i : ait) output.add(i);
        assertThat(output).containsExactlyElementsOf(expected);
    }


    @Test
    void seqChooseTest1() {
        final Iterable2<Integer> li = IterableHelper.init(TriplingGenerator, 5);
        final Iterable2<String> output = li.choose(
                i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None());

        final Collection<String> expected = Arrays.asList("6", "12");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void fwdPipelineTest3() {
        final Iterable2<Integer> input = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<String> output = input.in(
                integers -> integers.map(Functional.dStringify()));

        final Collection<String> expected = Arrays.asList("2", "4", "6", "8", "10");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void fwdPipelineTest4() {
        final Iterable2<Integer> input = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<String> output = input.in(
                integers -> {
                    try {
                        return integers.map(Functional.dStringify());
                    } catch (final Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        return null; // Argh!!!
                    }
                });

        final Collection<String> expected = Arrays.asList("2", "4", "6", "8", "10");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void fwdPipelineTest5() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> oddElems = l.in(
                ints -> ints.filter(Functional.isOdd));

        assertThat(oddElems.iterator().hasNext()).isFalse();
    }
    /*
    private class Test1
    {
        public readonly int i;

        public Test1(int j) {
            i = j;
        }
    }

    private static Function<object, string> fn1() {
        return delegate(object o) {
            if (o.GetType() == typeof (Test1))
                return fn2(o as Test1);
            if (o.GetType() == typeof (string))
                return fn3(o as string);
            return null;
        };
    }

    private static string fn2(Test1 i) {
        return i.i.ToString();
    }

    private static string fn3(string s) {
        return s;
    }

    [Test]
    public void fwdPipelineTest6() {
        Function<object, string> fn = fn1();
        var i = new Test1(10);
        const string s = "test";
        Assert.AreEqual("10", i.in(fn));
        Assert.AreEqual("test", s.in(fn));
    } */

    @Test
    void seqInitTest2() {
        final Iterable2<Integer> output = IterableHelper.init(DoublingGenerator);
        assertThat(output.take(11)).containsExactly(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22);
    }

    @Test
    void constantInitialiserTest1() {
        final int howMany = 6;
        final int initValue = -1;
        final Iterable2<Integer> l = IterableHelper.init(Functional.constant(initValue), howMany);
        assertThat(l.toList().size()).isEqualTo(howMany);
        for (final int i : IterableHelper.init(Functional.constant(initValue), howMany))
            assertThat(i).isEqualTo(initValue);
    }

    /*[Test]
    public void switchTest1() {
        Assert.AreEqual(1,
                Iterable2.Switch(10,
                        new[]
        {
            Case.ToCase((int a) => a < 5, a => -1),
            Case.ToCase((int a) => a > 5, a => 1)
        }, a => 0));
    } */

    /*[Test]
    public void tryTest1() {
        int zero = 0;
        int results = Iterable2.Try<int,int,DivideByZeroException>(10, a => a/zero, a => a);
        Assert.AreEqual(10, results);
    }*/
    private class A {
        public String name;
        public int id;
    }
    /*[Test]
    public void caseTest2() {
        var c1 = new List<Function<A, object>> {(A a) => (object) a.name, (A a) => (object) a.id};

        Function<A, IEnumerable<Function<int, object>>> c2 =
                a => c1.Select<Function<A, object>, Function<int, object>>(f => j => f(a));

        Function<A, IEnumerable<Iterable2.Case<int, object>>> cases =
                a => c2(a).Select((f, i) => Case.ToCase(i.Equals, f));

        var theA = new A {id = 1, name = "one"};

        IEnumerable<object> results =
                Enumerable.Range(0, 3).Select(i => Iterable2.Switch(i, cases(theA), aa => "oh dear"));
        var expected = new object[] {"one", 1, "oh dear"};
        CollectionAssert.AreEquivalent(expected, results);
    }*/

    /*[Test]
    public void ignoreTest1() {
        var input = new[] {1, 2, 3, 4, 5};
        var output = new List<string>();
        Function<int, string> format = i => string.Format("Discarding {0}", i);
        List<string> expected = new[] {1, 2, 3, 4, 5}.Select(format).ToList();
        Function<int, bool> f = i =>
        {
            output.Add(format(i));
            return true;
        };
        input.Select(f).Ignore();
        CollectionAssert.AreEquivalent(expected, output);
    }*/
    @Test
    void chooseTest1() throws OptionNoValueAccessException {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Collection<Integer> expected = Arrays.asList(1, 3, 5);
        final Iterable2<Integer> output =
                input.choose(
                        i -> i % 2 != 0 ? Option.toOption(i) : Option.None());
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void chooseTest2() throws OptionNoValueAccessException {
        final Iterable2<String> input = IterableHelper.asList("abc", "def");
        final Collection<Character> expected = Arrays.asList('a');
        final Iterable2<Character> output =
                input.choose(
                        str -> str.startsWith("a") ? Option.toOption('a') : Option.None()
                );
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void chooseTest3() throws OptionNoValueAccessException {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Collection<Integer> expected = Arrays.asList(1, 3, 5);
        final Iterable2<Integer> output = input.choose(
                i -> i % 2 != 0 ? Option.toOption(i) : Option.None());

        assertThat(output).containsExactlyElementsOf(expected);
    }

    /*[Test]
    public void curryTest1() {
        Function<int, int, bool> f = (i, j) => i > j;
        Function<int, Function<int, bool>> g = i => j => f(i, j);
        bool t = 10.in(g(5));
        Assert.IsFalse(t);
    }*/

    /*[Test]
    public void curryTest2() {
        Function<int, int, bool> f = (i, j) => i < j;
        Function<int, Function<int, bool>> g = i => j => f(i, j);
        bool t = 10.in(g(5));
        Assert.IsTrue(t);
    }*/

    /*[Test]
    public void compositionTest1() {
        Function<int, int, int> add = (x, y) => x + y;
        Function<int, Function<int, int>> add1 = y => x => add(x, y);
        Function<int, int, int> mult = (x, y) => x*y;
        Function<int, Function<int, int>> mult1 = y => x => mult(x, y);
        int expected = mult(add(1, 2), 3);
        Assert.AreEqual(9, expected);
        Assert.AreEqual(expected, 2.in(add1(1).then(mult1(3))));
    }*/

    @Test
    void unzipTest1() {
        final Collection<Pair<String, Integer>> input =
                new ArrayList<>();
        input.add(Pair.of("1", 1));
        input.add(Pair.of("2", 2));

        final Pair<Collection<String>, Collection<Integer>> expected =
                Pair.of(
                        Arrays.asList("1", "2"),
                        Arrays.asList(1, 2));

        final Pair<List<String>, List<Integer>> output = Functional.unzip(input);

        assertThat(output.getLeft()).containsExactlyElementsOf(expected.getLeft());
        assertThat(output.getRight()).containsExactlyElementsOf(expected.getRight());
    }

    @Test
    void zipTest1() {
        final Iterable2<Integer> input1 = IterableHelper.asList(1, 2, 3, 4, 5);
        final Iterable2<Character> input2 = IterableHelper.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Pair<Integer, Character>> expected = new ArrayList<>();
        expected.add(Pair.of(1, 'a'));
        expected.add(Pair.of(2, 'b'));
        expected.add(Pair.of(3, 'c'));
        expected.add(Pair.of(4, 'd'));
        expected.add(Pair.of(5, 'e'));

        final Iterable2<Pair<Integer, Character>> output = input1.zip(input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Iterable2<Triple<Integer, Character, Double>> output = IterableHelper.create(input1).zip3(input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    /*[ExpectedException(typeof(ArgumentException))]
        [Test]
    public void zip3Test2() {
        var input1 = new[] { 1, 2, 3, 4, 5 };
        var input2 = new[] { 'a', 'b', 'd', 'e' };
        var input3 = new[] { 1.0, 2.0, 2.5, 3.0, 3.5 };
        var expected = new[]
        {
            Tuple.Create(1, 'a', 1.0), Tuple.Create(2, 'b', 2.0), Tuple.Create(3, 'c', 2.5),
                    Tuple.Create(4, 'd', 3.0), Tuple.Create(5, 'e', 3.5)
        }.ToList();

        var output = Iterable2.Zip3(input1, input2, input3).ToList();

        CollectionAssert.AreEquivalent(expected, output);
    }*/

    /*[ExpectedException(typeof(ArgumentException))]
        [Test]
    public void zip3Test3() {
        var input1 = new[] { 1, 2, 3, 4, 5 };
        var input2 = new[] { 'a', 'b', 'c', 'd', 'e' };
        var input3 = new[] { 1.0, 2.0, 2.5, 3.5 };
        var expected = new[]
        {
            Tuple.Create(1, 'a', 1.0), Tuple.Create(2, 'b', 2.0), Tuple.Create(3, 'c', 2.5),
                    Tuple.Create(4, 'd', 3.0), Tuple.Create(5, 'e', 3.5)
        }.ToList();

        var output = Iterable2.Zip3(input1, input2, input3).ToList();

        CollectionAssert.AreEquivalent(expected, output);
    }*/

    @Test
    void findTest1() {
        final String trueMatch = "6";
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<String> ls = li.map(Functional.dStringify());
        assertThat(ls.find(s -> s.equals(trueMatch))).isEqualTo(trueMatch);
    }

    @Test
    void findTest2() {
        final String falseMatch = "7";
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<String> ls = li.map(Functional.dStringify());
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> ls.find(s -> s.equals(falseMatch)));
    }

    @Test
    void findIndexTest1() {
        final String trueMatch = "6";
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<String> ls = li.map(Functional.dStringify());
        assertThat(ls.findIndex(s -> s.equals(trueMatch))).isEqualTo(2);
    }

    @Test
    void findIndexTest2() {
        final String falseMatch = "7";
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<String> ls = li.map(Functional.dStringify());
        assertThatIllegalArgumentException().isThrownBy(() -> ls.findIndex(s -> s.equals(falseMatch)));
    }

    @Test
    void pickTest1() {
        final int trueMatch = 6;
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        assertThat(li.pick((Function<Integer, Option<String>>) a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None())).isEqualTo(String.valueOf(trueMatch));
    }

    @Test
    void pickTest2() {
        final int falseMatch = 7;
        final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> li.pick(a -> a == falseMatch ? Option.toOption(a.toString()) : Option.None()));
    }

    @Test
    void curryFnTest1() {
        final int state = 0;
        final Function<Integer, Boolean> testForPosInts = integer -> integer > state;

        final Function<Iterable<Integer>, List<Integer>> curriedTestForPosInts = Functional.filter(testForPosInts);

        final Collection<Integer> l = Arrays.asList(-3, -2, 0, 1, 5);
        final Collection<Integer> posInts = curriedTestForPosInts.apply(l);

        final Collection<Integer> expected = Arrays.asList(1, 5);
        assertThat(posInts).containsExactlyElementsOf(expected);
    }

    @Test
    void mapDictTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Map<String, String> output = Functional.map_dict(
                i -> new Map.Entry<String, String>() {
                    public String setValue(final String v) {
                        throw new UnsupportedOperationException();
                    }

                    public String getValue() {
                        return Functional.<Integer>dStringify().apply(i);
                    }

                    public String getKey() {
                        return Functional.<Integer>dStringify().apply(i);
                    }
                }, input);

        final List<String> keys = new ArrayList<>(output.keySet());
        Collections.sort(keys);
        assertThat(keys).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void toListTest1() {
        final Iterable2<Integer> output = IterableHelper.init(DoublingGenerator, 5);
        final List<Integer> output_ints = output.toList();
        assertThat(output_ints).containsExactly(2, 4, 6, 8, 10);
    }

    public static Function<Integer, Iterable<Integer>> intToList(final int howMany) {
        return integer -> IterableHelper.init(counter -> integer, howMany);
    }

    @Test
    void collectTest1() {
        final Iterable2<Integer> input = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> output = input.collect(intToList(3));
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqCollectTest1() {
        final Iterable2<Integer> input = IterableHelper.init(DoublingGenerator, 5);
        final Iterable2<Integer> output = input.collect(intToList(3));
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void takeNandYieldTest1() {
        final Iterable2<Integer> input = IterableHelper.init(DoublingGenerator, 5);
        final Pair<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(input, 2);
        final List<Integer> expectedList = Arrays.asList(2, 4);
        final List<Integer> expectedRemainder = Arrays.asList(6, 8, 10);
        assertThat(output.getLeft()).containsExactlyElementsOf(expectedList);
        assertThat(output.getRight()).containsExactlyElementsOf(expectedRemainder);
    }

    @Test
    void takeNandYieldTest2() {
        final Iterable<Integer> input = IterableHelper.init(DoublingGenerator, 5);
        final Pair<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(input, 0);
        final List<Integer> expectedList = Arrays.asList();
        final List<Integer> expectedRemainder = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(output.getLeft()).containsExactlyElementsOf(expectedList);
        assertThat(output.getRight()).containsExactlyElementsOf(expectedRemainder);
    }

    @Test
    void recFilterTest1() {
        final Iterable2<Integer> l = IterableHelper.init(DoublingGenerator, 5);
        final Iterable<Integer> oddElems = l.filter(Functional.isOdd);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void recMapTest1() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5);
        final Iterable<String> output = input.map(Functional.dStringify());
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void recFoldvsMapTest1() {
        final String s1;
        final String s2;
        {
            final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
            s1 = Functional.join(",", li.map(Functional.dStringify()));
            assertThat(s1).isEqualTo("2,4,6,8,10");
        }
        {
            final Iterable2<Integer> li = IterableHelper.init(DoublingGenerator, 5);
            s2 = li.fold(Iterable2Test::csv, "");
        }
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void emptySeqTestHasNoElements() {
        final Iterable2<Integer> l = IterableHelper.createEmpty();

        int i = 0;
        final Iterator<Integer> it = l.iterator();
        while (it.hasNext()) ++i;

        assertThat(i).isEqualTo(0);
    }

    @Test
    void emptySeqTestEquals() {
        final Iterable2<Integer> l = IterableHelper.createEmpty();
        final Iterable2<Integer> ll = IterableHelper.createEmpty();

        assertThat(ll).containsExactlyElementsOf(l);
    }

    @Test
    void emptySeqTestFilter() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final Iterable2<Integer> l1 = l.filter(Functional.isEven); // this filter needed to be fed from a statically-typed variable
        final Iterable2<Integer> other = IterableHelper.createEmpty();

        assertThat(l1).containsExactlyElementsOf(other);
    }

    @Test
    void emptySeqTestMap() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final Iterable2<String> l1 = l.map(Functional.dStringify()); // this filter needed to be fed from a statically-typed variable
        final Iterable2<String> other = IterableHelper.createEmpty();

        assertThat(l1).containsExactlyElementsOf(other);
    }

    @Test
    void emptySeqTestChoose() {
        final Function<Integer, Option<String>> chooser = integer -> Option.toOption(integer.toString());
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final Iterable2<String> l1 = l.choose(chooser); // this filter needed to be fed from a statically-typed variable
        final Iterable2<String> other = IterableHelper.createEmpty();

        assertThat(l1).containsExactlyElementsOf(other);
    }

    @Test
    void emptySeqTestExists1() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final boolean b = l.exists(Functional.isEven); // this filter needed to be fed from a statically-typed variable

        assertThat(b).isFalse();
    }

    @Test
    void emptySeqTestExists2() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final boolean b = l.exists(Functional.isOdd); // this filter needed to be fed from a statically-typed variable

        assertThat(b).isFalse();
    }

    @Test
    void emptySeqTestForAll1() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final boolean b = l.forAll(Functional.isEven); // this filter needed to be fed from a statically-typed variable

        assertThat(b).isFalse();
    }

    @Test
    void emptySeqTestForAll2() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final boolean b = l.forAll(Functional.isOdd); // this filter needed to be fed from a statically-typed variable

        assertThat(b).isFalse();
    }

    @Test
    void emptySeqTestFold() {
        final BiFunction<String, Integer, String> folder = (s, integer) -> s + integer.toString();
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final String l1 = l.fold(folder, ""); // this filter needed to be fed from a statically-typed variable
        final Iterable2<String> other = IterableHelper.createEmpty();

        assertThat(l1).isEqualTo("");
    }

    @Test
    void emptySeqTestToList() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final List<Integer> l1 = l.toList(); // this filter needed to be fed from a statically-typed variable
        final Iterable2<Integer> other = IterableHelper.createEmpty();

        assertThat(l1).containsExactlyElementsOf(other);
    }

    @Test
    void emptySeqTestSortWith() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final Iterable2<Integer> l1 = l.sortWith((o1, o2) -> 0); // this filter needed to be fed from a statically-typed variable
        final Iterable2<Integer> other = IterableHelper.createEmpty();

        assertThat(l1).containsExactlyElementsOf(other);
    }

    @Test
    void emptySeqTestConcat() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final Iterable2<Integer> l1 = l.concat(IterableHelper.create(Arrays.asList(1, 2, 3))); // this filter needed to be fed from a statically-typed variable
        final Iterable2<Integer> other = IterableHelper.create(Arrays.asList(1, 2, 3));

        assertThat(l1).containsExactlyElementsOf(other);
    }

    @Test
    void emptySeqTestFind() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> l.find(integer -> true)); // this filter needed to be fed from a statically-typed variable
    }

    @Test
    void emptySeqTestFindIndex() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> l.findIndex(integer -> true)); // this filter needed to be fed from a statically-typed variable
    }

    @Test
    void emptySeqTestPick() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> l.pick(integer -> Option.toOption(integer.toString()))); // this filter needed to be fed from a statically-typed variable
    }

    @Test
    void emptySeqTestTake() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final Iterable2<Integer> l1 = l.take(10); // this filter needed to be fed from a statically-typed variable
        final Iterable2<Integer> other = IterableHelper.createEmpty();

        assertThat(l1).containsExactlyElementsOf(other);
    }

    @Test
    void emptySeqTestZip() {
        final Iterable2<Integer> k = IterableHelper.create(Arrays.asList(1, 2, 3));
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        assertThatIllegalArgumentException().isThrownBy(() -> l.zip(k)); // this filter needed to be fed from a statically-typed variable
    }

    @Test
    void emptySeqTestZip3() {
        final Iterable2<Integer> j = IterableHelper.create(Arrays.asList(1, 2, 3));
        final Iterable2<Integer> k = IterableHelper.create(Arrays.asList(1, 2, 3));
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        assertThatIllegalArgumentException().isThrownBy(() -> l.zip3(k, j)); // this filter needed to be fed from a statically-typed variable
    }

    @Test
    void emptySeqTestCollect() {
        final Iterable2<Integer> l = IterableHelper.createEmpty(); // for some reason the generic type inference failed and so
        final Iterable2<Integer> l1 = l.collect(integer -> Arrays.asList(1, 2, 3, integer)); // this filter needed to be fed from a statically-typed variable
        final Iterable2<Integer> other = IterableHelper.createEmpty();

        assertThat(l1).containsExactlyElementsOf(other);
    }

    @Test
    void emptySeqTestIn() {
        final Iterable2<Integer> l = IterableHelper.createEmpty();
        final String u = l.in(integers -> integers.fold((s, integer) -> s + integer.toString(), ""));
        assertThat(u).isEqualTo("");
    }

    @Test
    void groupByOddVsEvenInt() {
        final Iterable2<Integer> input = IterableHelper.create(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        final Map<Boolean, List<Integer>> output = input.groupBy(Functional.isEven);
        final Map<Boolean, List<Integer>> expected = new HashMap<>();
        expected.put(false, Arrays.asList(1, 3, 5, 7, 9));
        expected.put(true, Arrays.asList(2, 4, 6, 8, 10));
        assertThat(output.get(true)).containsExactlyElementsOf(expected.get(true));
        assertThat(output.get(false)).containsExactlyElementsOf(expected.get(false));
    }

    @Test
    void groupByStringFirstTwoChar() {
        final Iterable2<String> input = IterableHelper.create(Arrays.asList("aa", "aab", "aac", "def"));
        final Map<String, List<String>> output = input.groupBy(s -> s.substring(0, 1));
        final Map<String, List<String>> expected = new HashMap<>();
        expected.put("a", Arrays.asList("aa", "aab", "aac"));
        expected.put("d", Arrays.asList("def"));
        assertThat(output.get("a")).containsExactlyElementsOf(expected.get("a"));
        assertThat(output.get("d")).containsExactlyElementsOf(expected.get("d"));
        assertThat(new TreeSet<String>(output.keySet())).containsExactlyElementsOf(new TreeSet<String>(expected.keySet()));
    }

    @Test
    void seqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> output = IterableHelper.create(input).mapi((BiFunction<Integer, Integer, Pair<Integer, String>>) (pos, i) -> Pair.of(pos, i.toString()));
        assertThat(Functional.map(Pair::getRight, output).toArray()).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void takeWhileTest1() {
        final Iterable2<Integer> l = IterableHelper.create(Arrays.asList(1, 2, 3, 4, 5));
        final List<Integer> expected = new ArrayList<>();
        final Iterable<Integer> output = l.takeWhile(Functional.isEven);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void skipTest1() {
        final Iterable2<Integer> l = IterableHelper.create(Arrays.asList(1, 2, 3, 4, 5));

        final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
        final Iterable<Integer> output = l.skip(1);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void skipWhileTest1() {
        final Iterable2<Integer> l = IterableHelper.create(Arrays.asList(1, 2, 3, 4, 5));
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Integer> output = l.skipWhile(Functional.isEven);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void emptyListIterator() {
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> IterableHelper.createEmpty().iterator().next());
    }

    @Test
    void emptyListIteratorCantRemove() {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> IterableHelper.createEmpty().iterator().remove());
    }

    /**
     * These two are not equal because the real Iterable2 actually has an iterator which might not be subsequently
     * recreatable and so it is not generally possible to determine whether the real Iterable2 is empty or not.
     */
    @Test
    void isEmptyListEqualToListWithNoElements() {
        final Iterable2<Integer> emptyList = IterableHelper.createEmpty();
        final Iterable2<Integer> listWithNoElements = IterableHelper.create(new ArrayList<>());

        assertThat(listWithNoElements).isNotEqualTo(emptyList);
    }

    @Test
    void emptyListMapiTest() {
        assertThat(IterableHelper.createEmpty().mapi((integer, o) -> null)).isEmpty();
    }

    @Test
    void forAll2EmptyListTest() {
        assertThat(IterableHelper.createEmpty().forAll2((BiFunction<Object, Object, Boolean>) (o, o2) -> null, Arrays.asList(1))).isFalse();
    }

    @Test
    void toArrayEmptyListTest() {
        assertThat(IterableHelper.createEmpty().toArray()).containsExactly(new Integer[]{});
    }

    @Test void toSetEmptyListTest() {
        assertThat(IterableHelper.createEmpty().toSet()).isEqualTo(new HashSet<Integer>());
    }

    @Test void toDictionaryEmptyListTest() {
        assertThat(IterableHelper.<Integer>createEmpty().toDictionary((Function<Integer, Integer>) integer -> null, (Function<Integer, String>) integer -> null)).isEmpty();
    }

    @Test
    void lastOfEmptyListTest() {
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> IterableHelper.createEmpty().last());
    }

    @Test
    void emptyListTakeWhileTest() {
        assertThat(IterableHelper.<Integer>createEmpty().takeWhile(Functional.isOdd)).isEqualTo(IterableHelper.createEmpty());
    }

    @Test
    void emptyListSkipTest() {
        assertThat(IterableHelper.<Integer>createEmpty().skip(1)).isEqualTo(IterableHelper.createEmpty());
    }

    @Test
    void emptyListSkipWhileTest() {
        assertThat(IterableHelper.<Integer>createEmpty().skipWhile(Functional.isOdd)).isEqualTo(IterableHelper.createEmpty());
    }

    @Test
    void emptyListJoinTest() {
        assertThat(IterableHelper.createEmpty().join("a")).isEqualTo("");
    }

    @Test
    void emptyListFindLastTest() {
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> IterableHelper.<Integer>createEmpty().findLast(Functional.isOdd));
    }

    @Test
    void emptyListPartitionTest() {
        final Pair<List<Object>, List<Object>> pair = IterableHelper.createEmpty().partition(o -> null);
        assertThat(pair.getLeft().isEmpty()).isTrue();
        assertThat(pair.getRight().isEmpty()).isTrue();
    }

    @Test
    void emptyListGroupByTest() {
        final Map<Object, List<Object>> grp = IterableHelper.createEmpty().groupBy(o -> null);

        assertThat(grp.isEmpty()).isTrue();
    }

    @Test
    void emptyListInACollection() {
        final Map<Iterable2<Integer>, Integer> map = new HashMap<>();
        map.put(IterableHelper.createEmpty(), 0);
        assertThat(map.get(IterableHelper.createEmpty())).isEqualTo(Integer.valueOf(0));
    }

    @Test
    void emptyListToString() {
        assertThat(IterableHelper.createEmpty().toString()).isEqualTo("()");
    }
}