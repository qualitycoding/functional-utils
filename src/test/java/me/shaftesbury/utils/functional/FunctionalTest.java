package me.shaftesbury.utils.functional;

import me.shaftesbury.utils.functional.primitive.integer.Func_int_int;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
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
import static org.assertj.core.api.Assertions.fail;

class FunctionalTest {
    static Function<Integer, Integer> DoublingGenerator = a -> 2 * a;
    static Func_int_int doublingGenerator = a -> 2 * a;

    @Test
    void initTest1() {
        final Collection<Integer> output = Functional.init(DoublingGenerator, 5);
        assertThat(output.toArray()).isEqualTo(new Integer[]{2, 4, 6, 8, 10});
    }

    @Test
    void rangeTest1() {
        final Collection<Integer> output = Functional.init(Functional.range(0), 5);
        assertThat(output.toArray()).isEqualTo(new Integer[]{0, 1, 2, 3, 4});
    }

    @Test
    void mapTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<String> output = Functional.map(Functional.dStringify(), input);
        assertThat(output.toArray()).isEqualTo(new String[]{"1", "2", "3", "4", "5"});
    }

    @Test
    void curriedMapTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Iterable<Integer>, List<String>> mapFunc = Functional.map(Functional.dStringify());
        final Collection<String> output = mapFunc.apply(input);
        assertThat(output.toArray()).isEqualTo(new String[]{"1", "2", "3", "4", "5"});
    }

    @Test
    void mapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        assertThat(Functional.map(Pair::getRight, output).toArray()).isEqualTo(new String[]{"1", "2", "3", "4", "5"});
        assertThat(Functional.map(Pair::getLeft, output).toArray()).isEqualTo(new Integer[]{0, 1, 2, 3, 4});
    }

    @Test
    void curriedMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Iterable<Integer>, List<Pair<Integer, String>>> mapiFunc = Functional.mapi((pos, i) -> Pair.of(pos, i.toString()));
        final Collection<Pair<Integer, String>> output = mapiFunc.apply(input);
        assertThat(Functional.map(Pair::getRight, output).toArray()).isEqualTo(new String[]{"1", "2", "3", "4", "5"});
        assertThat(Functional.map(Pair::getLeft, output).toArray()).isEqualTo(new Integer[]{0, 1, 2, 3, 4});
    }

    @Test
    void seqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.toList(Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input));
        assertThat(Functional.map(Pair::getRight, output).toArray()).isEqualTo(new String[]{"1", "2", "3", "4", "5"});
        assertThat(Functional.map(Pair::getLeft, output).toArray()).isEqualTo(new Integer[]{0, 1, 2, 3, 4});
    }

    @Test
    void cantRestartIteratorInSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> output = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void curriedSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.toList(Functional.seq.mapi((BiFunction<Integer, Integer, Pair<Integer, String>>) (pos, i) -> Pair.of(pos, i.toString())).apply(input));
        assertThat(Functional.map(Pair::getRight, output).toArray()).isEqualTo(new String[]{"1", "2", "3", "4", "5"});
        assertThat(Functional.map(Pair::getLeft, output).toArray()).isEqualTo(new Integer[]{0, 1, 2, 3, 4});
    }

    @Test
    void cantRemoveFromSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> output = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void seqMapiTest2() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> mapi = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        final Iterator<Pair<Integer, String>> iterator = mapi.iterator();

        for (int i = 0; i < 10; ++i)
            assertThat(iterator.hasNext()).isTrue();

        Pair<Integer, String> next = iterator.next();
        assertThat(next.getRight()).isEqualTo("1");
        next = iterator.next();
        assertThat(next.getRight()).isEqualTo("2");
        next = iterator.next();
        assertThat(next.getRight()).isEqualTo("3");
        next = iterator.next();
        assertThat(next.getRight()).isEqualTo("4");
        next = iterator.next();
        assertThat(next.getRight()).isEqualTo("5");

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void sortWithTest1() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final Collection<Integer> output = Functional.sortWith(Integer::compare, i);
        assertThat(output.toArray()).isEqualTo(new Integer[]{1, 4, 6, 7, 23});
    }

    @Test
    void sortWithTest2() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final Collection<Integer> j = Functional.sortWith((a, b) -> a - b, i);
        assertThat(j.toArray()).isEqualTo(new Integer[]{1, 4, 6, 7, 23});
    }

    @Test
    void sortWithTest3() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final Collection<Integer> j = Functional.sortWith(Functional.dSorter, i);
        assertThat(j.toArray()).isEqualTo(new Integer[]{1, 4, 6, 7, 23});
    }

    public static Function<Integer, Integer> TriplingGenerator = a -> 3 * a;

    public static Function<Integer, Integer> QuadruplingGenerator = a -> 4 * a;

    private static boolean BothAreEven(final int a, final int b) {
        return Functional.isEven.apply(a) && Functional.isEven.apply(b);
    }


    @Test
    void forAll2Test1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Collection<Integer> m = Functional.init(QuadruplingGenerator, 5);
        assertThat(Functional.forAll2(FunctionalTest::BothAreEven, l, m)).isTrue();
    }

    @Test
    void forAll2NoExceptionTest1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Collection<Integer> m = Functional.init(QuadruplingGenerator, 5);
        assertThat(Functional.noException.forAll2(FunctionalTest::BothAreEven, l, m).Some()).isTrue();
    }

    private static boolean BothAreLessThan10(final int a, final int b) {
        return a < 10 && b < 10;
    }

    private static BiFunction<Integer, Integer, Boolean> dBothAreLessThan10 = FunctionalTest::BothAreLessThan10;

    @Test
    void forAll2Test2() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Collection<Integer> m = Functional.init(TriplingGenerator, 5);

        assertThat(Functional.forAll2(dBothAreLessThan10, l, m)).isFalse();
    }

    @Test
    void forAll2Test3() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Collection<Integer> m = Functional.init(QuadruplingGenerator, 7);

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> Functional.forAll2(FunctionalTest::BothAreEven, l, m));
    }

    @Test
    void forAll2NoExceptionTest2() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Collection<Integer> m = Functional.init(TriplingGenerator, 5);

        assertThat(Functional.noException.forAll2(dBothAreLessThan10, l, m).Some()).isFalse();
    }

    @Test
    void forAll2NoExceptionTest3() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Collection<Integer> m = Functional.init(QuadruplingGenerator, 7);

        assertThat(Functional.noException.forAll2(FunctionalTest::BothAreEven, l, m).isNone()).isTrue();
    }

    @Test
    void CompositionTest1A() {
        final Collection<Integer> i = Arrays.asList(1, 2, 3, 45, 56, 6);

        final boolean allOdd = Functional.forAll(Functional.isOdd, i);
        final boolean notAllOdd = Functional.exists(Functional.not(Functional.isOdd), i);

        assertThat(allOdd).isFalse();
        assertThat(notAllOdd).isTrue();
    }

    @Test
    void curriedCompositionTest1A() {
        final Collection<Integer> i = Arrays.asList(1, 2, 3, 45, 56, 6);

        final boolean allOdd = Functional.forAll(Functional.isOdd).apply(i);
        final boolean notAllOdd = Functional.exists(Functional.not(Functional.isOdd)).apply(i);

        assertThat(allOdd).isFalse();
        assertThat(notAllOdd).isTrue();
    }

    @Test
    void existsTest() {
        final Collection<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional.isEven, i);
        final boolean allOdd = Functional.exists(Functional.isOdd, i);

        assertThat(allOdd).isFalse();
        assertThat(anEven).isTrue();
    }

    @Test
    void curriedExistsTest() {
        final Collection<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional.isEven).apply(i);
        final boolean allOdd = Functional.exists(Functional.isOdd).apply(i);

        assertThat(allOdd).isFalse();
        assertThat(anEven).isTrue();
    }

    @Test
    void CompositionTest2() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Collection<Integer> m = Functional.init(TriplingGenerator, 5);
        assertThat(Functional.forAll2(Functional.not2(dBothAreLessThan10), l, m)).isFalse();
        // equivalent to BothAreGreaterThanOrEqualTo10

        final int lowerLimit = 1;
        final int upperLimit = 16;
        assertThat(
                Functional.forAll2(
                        Functional.not2((a, b) -> a > lowerLimit && b > lowerLimit
                        ), l, m)).isFalse();
        assertThat(
                Functional.forAll2(
                        Functional.not2((a, b) -> a > upperLimit && b > upperLimit
                        ), l, m)).isTrue();
    }

    @Test
    void partitionTest1() {
        final Collection<Integer> m = Functional.init(TriplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isOdd, m);

        final Integer[] left = {3, 9, 15};
        final Integer[] right = {6, 12};
        assertThat(r.getLeft().toArray()).isEqualTo(left);
        assertThat(r.getRight().toArray()).isEqualTo(right);
    }

    @Test
    void curriedPartitionTest1() {
        final Collection<Integer> m = Functional.init(TriplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isOdd).apply(m);

        final Integer[] left = {3, 9, 15};
        final Integer[] right = {6, 12};
        assertThat(r.getLeft().toArray()).isEqualTo(left);
        assertThat(r.getRight().toArray()).isEqualTo(right);
    }

    @Test
    void iterablePartitionTest1() {
        final Iterable<Integer> m = Functional.seq.init(TriplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isOdd, m);

        final Integer[] left = {3, 9, 15};
        final Integer[] right = {6, 12};
        assertThat(r.getLeft().toArray()).isEqualTo(left);
        assertThat(r.getRight().toArray()).isEqualTo(right);
    }

    @Test
    void partitionTest2() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isEven, l);
        assertThat(r.getLeft().toArray()).isEqualTo(l.toArray());
        assertThat(r.getRight().toArray()).isEqualTo(new Integer[]{});
    }

    @Test
    void partitionTest3() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isEven, l);
        assertThat(r.getLeft().toArray()).isEqualTo(Functional.filter(Functional.isEven, l).toArray());
    }

    @Test
    void toStringTest1() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        //String s = String.Join(",", ls);
        assertThat(ls.toArray()).isEqualTo(new String[]{"2", "4", "6", "8", "10"});
    }

    @Test
    void ChooseTest1B() throws OptionNoValueAccessException {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Collection<String> o = Functional.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);
        final String[] expected = {"6", "12"};
        assertThat(expected).isEqualTo(o.toArray());
    }

    @Test
    void curriedChooseTest1B() throws OptionNoValueAccessException {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Collection<String> o = Functional.choose((Function<Integer, Option<String>>) i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None()).apply(li);
        final String[] expected = {"6", "12"};
        assertThat(expected).isEqualTo(o.toArray());
    }

    @Test
    void ChooseTest2A() //throws OptionNoValueAccessException
    {
        Map<Integer, String> o = null;
        try {
            final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
            o = Functional.toDictionary(Functional.identity(), Functional.dStringify(),
                    Functional.choose(i -> i % 2 == 0 ? Option.toOption(i) : Option.None(), li));
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
    void CurriedFnTest1() {
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
    void CurriedFnTest2() {
        final List<Integer> a = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Integer> b = Functional.map(a1 -> adder_int(2, a1), a);
        final Collection<Integer> c = Functional.map(curried_adder_int(2), a);
        assertThat(c.toArray()).isEqualTo(b.toArray());
    }

    private static String csv(final String state, final Integer a) {
        return StringUtils.isEmpty(state) ? a.toString() : state + "," + a;
    }

    @Test
    void foldvsMapTest1() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final String s1 = Functional.join(",", Functional.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.fold(FunctionalTest::csv, "", li);
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void curriedFoldvsMapTest1() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final String s1 = Functional.join(",", Functional.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.fold(FunctionalTest::csv, "").apply(li);
        assertThat(s2).isEqualTo(s1);
    }

    private final Function<Collection<Integer>, String> concatenate = l -> Functional.fold(FunctionalTest::csv, "", l);

    @Test
    void fwdPipelineTest1() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final String s1 = Functional.in(li, concatenate);
        assertThat(s1).isEqualTo("2,4,6,8,10");
    }

    private final Function<Collection<Integer>, Collection<Integer>> evens_f = l -> Functional.filter(Functional.isEven, l);

    @Test
    void fwdPipelineTest2() {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Collection<Integer> evens = Functional.in(li, evens_f);
        final String s1 = Functional.in(evens, concatenate);
        final String s2 = Functional.in(li, Functional.then(evens_f, concatenate));
        assertThat(s1).isEqualTo("6,12");
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void CompositionTest3() {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final String s = Functional.in(li, Functional.then(evens_f, concatenate));
        assertThat(s).isEqualTo("6,12");
    }

    @Test
    void CompositionTest4() {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final String s = Functional.then(evens_f, concatenate).apply(li);
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

        final Collection<String> indentation = Functional.init(integer -> " ", level);
        assertThat("     ").isEqualTo(Functional.join("", indentation));

        final String s = Functional.fold((state, str) -> state + str, "", indentation);
        assertThat(expectedResult).isEqualTo(s);

        final Function<Collection<String>, String> folder = l -> Functional.fold((BiFunction<String, String, String>) (state, str) -> state + str, "", l);

        final String s1 = Functional.in(indentation, folder);
        assertThat(expectedResult).isEqualTo(s1);
    }

    @Test
    void indentTest2() {
        final int level = 5;
        final String expectedResult = "     BOB";
        assertThat(Functional.indentBy(level, " ", "BOB")).isEqualTo(expectedResult);
    }

    @Test
    void ChooseTest3A() throws OptionNoValueAccessException {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Collection<Integer> o =
                Functional.choose(i -> i % 2 == 0 ? Option.toOption(i) : Option.None(), li);

        final Integer[] expected = new Integer[]{6, 12};
        assertThat(o.toArray()).isEqualTo(expected);
    }

/*
        [Test]
        public void tryGetValueTest1()
        {
            var d = new Dictionary<string, int>();
            d["one"] = 1;
            Assert.AreEqual(1, FunctionalHelpers.TryGetValue_nullable("one", d));
        }

 */

    /*
            [Test]
        public void tryGetValueTest2()
        {
            var d = new Dictionary<string, int>();
            d["one"] = 1;
            Assert.IsNull(FunctionalHelpers.TryGetValue_nullable("two", d));
        }

     */
/*
        [Test]
        public void tryGetValueTest3()
        {
            var d = new Dictionary<string, string>();
            d["one"] = "ONE";
            Assert.AreEqual("ONE", FunctionalHelpers.TryGetValue("one", d).Some);
        }

 */

    /*
            [Test]
        public void tryGetValueTest4()
        {
            var d = new Dictionary<string, string>();
            d["one"] = "ONE";
            Assert.IsTrue(FunctionalHelpers.TryGetValue("two", d).None);
        }

     */

    /*
            [Test]
        public void tryGetValueTest5()
        {
            var d = new Dictionary<string, List<int>>();
            var l = new List<int>(new[] {1, 2, 3});
            d["one"] = l;
            Assert.AreEqual(l, FunctionalHelpers.TryGetValue("one", d).Some);
        }
*/
    /*
        [Test]
        public void tryGetValueTest6()
        {
            var d = new Dictionary<string, string>();
            d["one"] = "ONE";
            Assert.IsTrue(FunctionalHelpers.TryGetValue("two", d).None);
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
        final Collection<Integer> openedDays = Functional.init(TriplingGenerator, 5);
        Double last = 10.0;
        for (final int day : openedDays) {
            final Double value = day % 2 == 0 ? (Double) ((double) (day / 2)) : null;
            if (value != null)
                last = value;
            else
                missingPricesPerDate.put(day, last);
        }

        final Collection<myInt> openedDays2 = Functional.init(
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
        assertThat(Functional.map(myInt::i, output.getRight()).toArray()).containsExactlyElementsOf(keys);
    }

    @Test
    void joinTest1() {
        final Collection<Integer> ids = Functional.init(TriplingGenerator, 5);
        final String expected = "3,6,9,12,15";
        assertThat(Functional.join(",", Functional.map(Functional.dStringify(), ids))).isEqualTo(expected);
        assertThat(Functional.join(",", ids)).isEqualTo(expected);
    }

    @Test
    void joinTest2() {
        final Collection<Integer> ids = Functional.init(TriplingGenerator, 5);
        final String expected = "'3','6','9','12','15'";
        final Function<Integer, String> f = id -> "'" + id + "'";
        assertThat(Functional.join(",", Functional.map(f, ids))).isEqualTo(expected);
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


    /*@Test void testThen()
    {
        // mult(two,three).then(add(four)) =>
        // then(mult(two,three),add(four))
        // 2 * 3 + 4 = 10
        Integer two = 2;
        Integer three = 3;
        Integer four = 4;
        Functional.then(new Function<Integer,Integer>()
        {

            public Integer apply(final Integer i) { return }
        })
    } */

    @Test
    void seqFilterTest1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);

        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void curriedSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd).apply(l);

        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void cantRemoveFromSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->oddElems.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);
        try {
            oddElems.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(oddElems::iterator);
    }

    @Test
    void seqFilterTest2() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> evenElems = Functional.seq.filter(Functional.isEven, l);

        final Collection<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(evenElems).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest3() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Integer limit = 5;
        final Iterable<Integer> highElems = Functional.seq.filter(a -> a > limit, l);

        final Collection<Integer> expected = Arrays.asList(6, 8, 10);
        assertThat(highElems).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest4() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Integer limit = 10;
        final Iterable<Integer> output = Functional.seq.filter(a -> a > limit, li);

        assertThat(output.iterator().hasNext()).isFalse();
    }

    @Test
    void seqFilterTest5() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 10);
        final Collection<Integer> expected = Arrays.asList(4, 8, 12, 16, 20);
        final Iterable<Integer> output = Functional.seq.filter(
                a -> a % 4 == 0, li);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest6() {
        final Collection<Integer> input = Functional.init(DoublingGenerator, 10);
        final Iterable<Integer> output = Functional.seq.filter(a -> a % 4 == 0, input);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        int next = iterator.next();
        assertThat(next).isEqualTo(4);
        next = iterator.next();
        assertThat(next).isEqualTo(8);
        next = iterator.next();
        assertThat(next).isEqualTo(12);
        next = iterator.next();
        assertThat(next).isEqualTo(16);
        next = iterator.next();
        assertThat(next).isEqualTo(20);

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void findLastTest1() {
        final List<Integer> l = new ArrayList<>(Functional.init(DoublingGenerator, 5));
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(()->Functional.findLast(Functional.isOdd, l));
    }

    @Test
    void findLastTest2() {
        final List<Integer> l = new ArrayList<>(Functional.init(DoublingGenerator, 5));
        assertThat(Functional.findLast(Functional.isEven, l)).isEqualTo((Integer) 10);
    }

    @Test
    void curriedFindLastTest2() {
        final List<Integer> l = new ArrayList<>(Functional.init(DoublingGenerator, 5));
        final Function<List<Integer>, Integer> lastFunc = Functional.findLast(Functional.isEven);
        assertThat(lastFunc.apply(l)).isEqualTo((Integer) 10);
    }

    @Test
    void findLastNoExceptionTest1() {
        final List<Integer> l = Functional.init(DoublingGenerator, 5);
        assertThat(Functional.noException.findLast(Functional.isOdd, l).isNone()).isTrue();
    }

    @Test
    void findLastNoExceptionTest2() {
        final List<Integer> l = Functional.init(DoublingGenerator, 5);
        assertThat(Functional.noException.findLast(Functional.isEven, l).Some()).isEqualTo((Integer) 10);
    }

    @Test
    void iterableFindLastNoExceptionTest1() {
        final Iterable<Integer> l = Functional.seq.init(DoublingGenerator, 5);
        assertThat(Functional.noException.findLast(Functional.isOdd, l).isNone()).isTrue();
    }

    @Test
    void iterableFindLastNoExceptionTest2() {
        final Iterable<Integer> l = Functional.seq.init(DoublingGenerator, 5);
        assertThat(Functional.noException.findLast(Functional.isEven, l).Some()).isEqualTo((Integer) 10);
    }

    @Test
    void lastNoExceptionTest1() {
        final List<Integer> l = new ArrayList<>(Functional.init(DoublingGenerator, 5));
        final Option<Integer> last = Functional.noException.last(l);
        assertThat(last.isSome()).isTrue();
        assertThat(last.Some()).isEqualTo((Integer) 10);
    }

    @Test
    void findLastIterableTest1() {
        final Iterable<Integer> l = new ArrayList<>(Functional.init(DoublingGenerator, 5));
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(()->Functional.findLast(Functional.isOdd, l));
    }

    @Test
    void findLastIterableTest2() {
        final Iterable<Integer> l = new ArrayList<>(Functional.init(DoublingGenerator, 5));
        assertThat(Functional.findLast(Functional.isEven, l)).isEqualTo((Integer) 10);
    }

    @Test
    void seqMapTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
        final Iterable<String> output = Functional.seq.map(Functional.dStringify(), input);
        final Iterator<String> it = output.iterator();
        for (int i = 0; i < 20; ++i)
            assertThat(it.hasNext()).isTrue();

        for (int i = 0; i < expected.size(); ++i)
            assertThat(it.next()).isEqualTo(expected.get(i));

        assertThat(it.hasNext()).isFalse();
        try {
            it.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void curriedSeqMapTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
        final Iterable<String> output = Functional.seq.map(Functional.<Integer>dStringify()).apply(input);
        final Iterator<String> it = output.iterator();
        for (int i = 0; i < 20; ++i)
            assertThat(it.hasNext()).isTrue();

        for (int i = 0; i < expected.size(); ++i)
            assertThat(it.next()).isEqualTo(expected.get(i));

        assertThat(it.hasNext()).isFalse();
        try {
            it.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void cantRemoveFromSeqMapTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
        final Iterable<String> output = Functional.seq.map(Functional.dStringify(), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqMapTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
        final Iterable<String> output = Functional.seq.map(Functional.dStringify(), input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void toArrayTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");

        final Object[] output = Functional.toArray(strs);

        assertThat(output.length).isEqualTo(expected.size());
        for (int i = 0; i < expected.size(); ++i)
            assertThat(output[i]).isEqualTo(expected.get(i));
    }

    @Test
    void collectionToArrayTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final List<String> strs = Functional.map(Functional.dStringify(), input);
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");

        final Object[] output = Functional.toArray(strs);

        assertThat(output.length).isEqualTo(expected.size());
        for (int i = 0; i < expected.size(); ++i)
            assertThat(output[i]).isEqualTo(expected.get(i));
    }

    @Test
    void lastTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        assertThat((long) Functional.last(input)).isEqualTo(5);
    }

    @Test
    void lastofArrayTest1() {
        final Integer[] input = new Integer[]{1, 2, 3, 4, 5};
        assertThat((long) Functional.last(input)).isEqualTo(5);
    }

    @Test
    void lastTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        assertThat(Functional.last(strs)).isEqualTo("5");
    }

    @Test
    void lastTest3() {
        final List<Integer> input = new ArrayList<>();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(()->Functional.last(input));
    }

    @Test
    void concatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
        assertThat(Functional.concat(input, input)).containsExactlyElementsOf(expected);
    }

    @Test
    void seqConcatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqConcatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqConcatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqConcatTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));
        final Iterator<String> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        String next = iterator.next();
        assertThat(next).isEqualTo("1");
        next = iterator.next();
        assertThat(next).isEqualTo("2");
        next = iterator.next();
        assertThat(next).isEqualTo("3");
        next = iterator.next();
        assertThat(next).isEqualTo("4");
        next = iterator.next();
        assertThat(next).isEqualTo("5");
        next = iterator.next();
        assertThat(next).isEqualTo("2");
        next = iterator.next();
        assertThat(next).isEqualTo("4");
        next = iterator.next();
        assertThat(next).isEqualTo("6");
        next = iterator.next();
        assertThat(next).isEqualTo("8");
        next = iterator.next();
        assertThat(next).isEqualTo("10");
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void arrayIterableTest1() {
        final Integer[] input = new Integer[]{1, 2, 3, 4, 5};
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);

        final ArrayIterable<Integer> ait = ArrayIterable.create(input);
        final List<Integer> output = new ArrayList<>();
        for (final Integer i : ait) output.add(i);
        assertThat(output.toArray()).isEqualTo(expected.toArray());
    }

    @Test
    void seqChooseTest1() {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);

        final Collection<String> expected = Arrays.asList("6", "12");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose((Function<Integer, Option<String>>) i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None()).apply(li);

        final Collection<String> expected = Arrays.asList("6", "12");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqChooseTest2() {
        final Collection<Integer> li = Functional.init(TriplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);
        final Iterator<String> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        String next = iterator.next();
        assertThat(next).isEqualTo("6");
        next = iterator.next();
        assertThat(next).isEqualTo("12");
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void seqInitTest1() {
        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator, 5);
        assertThat(output).containsExactly(2, 4, 6, 8, 10);
    }

    @Test
    void cantRemoveFromSeqInitTest1() {
        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator, 5);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void seqInitTest3() {
        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator, 5);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        int next = iterator.next();
        assertThat(next).isEqualTo(2);
        next = iterator.next();
        assertThat(next).isEqualTo(4);
        next = iterator.next();
        assertThat(next).isEqualTo(6);
        next = iterator.next();
        assertThat(next).isEqualTo(8);
        next = iterator.next();
        assertThat(next).isEqualTo(10);
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void fwdPipelineTest3() {
        final Collection<Integer> input = Functional.init(DoublingGenerator, 5);
        final Collection<String> output = Functional.in(input, (Function<Collection<Integer>, Collection<String>>) integers -> Functional.map(Functional.<Integer>dStringify(), integers));

        final Collection<String> expected = Arrays.asList("2", "4", "6", "8", "10");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void fwdPipelineTest4() {
        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
        final Iterable<String> output = Functional.in(input,
                integers -> {
                    try {
                        return Functional.seq.map(Functional.dStringify(), integers);
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
        final Iterable<Integer> l = Functional.seq.init(DoublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.in(l, (Function<Iterable<Integer>, Iterable<Integer>>) ints -> Functional.filter(Functional.isOdd, ints));

        assertThat(oddElems.iterator().hasNext()).isFalse();
    }
    /*
    private class Test1
    {
        public readonly int i;

        public Test1(int j)
        {
            i = j;
        }
    }

    private static Function<object, string> fn1()
    {
        return delegate(object o)
        {
            if (o.GetType() == typeof (Test1))
                return fn2(o as Test1);
            if (o.GetType() == typeof (string))
                return fn3(o as string);
            return null;
        };
    }

    private static string fn2(Test1 i)
    {
        return i.i.ToString();
    }

    private static string fn3(string s)
    {
        return s;
    }

    [Test]
    public void fwdPipelineTest6()
    {
        Function<object, string> fn = fn1();
        var i = new Test1(10);
        const string s = "test";
        Assert.AreEqual("10", i.in(fn));
        Assert.AreEqual("test", s.in(fn));
    } */

    @Test
    void seqInitTest2() {
        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator);
        assertThat(Functional.take(11, output)).containsExactly(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22);
    }

    @Test
    void cantRemoveFromSeqInitTest2() {
        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqInitTest2() {
        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void takeTooManyItemsTest() {
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(()->Functional.take(100, Functional.init(DoublingGenerator, 10)));
    }

    @Test
    void takeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.take(o, input), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.<Integer>take(o).apply(input), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void takeNoExceptionTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.noException.take(o, input), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void takeNoExceptionTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.noException.take(o + 5, input), input);
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.toList(Functional.seq.take(o, input)), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.toList(Functional.seq.<Integer>take(o).apply(input)), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.take(1, input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.take(1, input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void takeTooManyFromSeqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.take(1, input);
        final Iterator<Integer> iterator = output.iterator();
        Integer next;
        try {
            next = iterator.next();
        } catch (final NoSuchElementException e) {
            fail("Should not reach this point");
            next = null;
        }
        assertThat(next).isEqualTo(input.get(0));
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(iterator::next);
    }

    @Test
    void seqTakeTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.toList(Functional.seq.take(o, input)), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void curriedTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.takeWhile(Functional.isEven).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void takeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.takeWhile(Functional.isEven, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1);
            final List<Integer> output = Functional.takeWhile(Functional.isOdd, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
            final List<Integer> output = Functional.takeWhile(i -> i <= 4, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.takeWhile(i -> i <= 6, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void takeWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(()->Functional.takeWhile(null, input));
    }

    @Test
    void seqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(Functional.isEven, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1);
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(Functional.isOdd, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(i -> i <= 4, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(i -> i <= 6, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void callHasNextAfterFinishedInSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1);
            final Iterable<Integer> output = Functional.seq.takeWhile(Functional.isOdd, l);
            final Iterator<Integer> iterator = output.iterator();
            try {
                assertThat(iterator.next()).isEqualTo(expected.get(0));
            } catch (final NoSuchElementException e) {
                fail("Shouldn't reach this point");
            }
            assertThat(iterator.hasNext()).isFalse();
            assertThat(iterator.hasNext()).isFalse();
        }
    }

    @Test
    void callNextAfterFinishedInSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1);
            final Iterable<Integer> output = Functional.seq.takeWhile(Functional.isOdd, l);
            final Iterator<Integer> iterator = output.iterator();
            try {
                assertThat(iterator.next()).isEqualTo(expected.get(0));
            } catch (final NoSuchElementException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(iterator::next);
        }
    }

    @Test
    void cantRemoveFromSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final Iterable<Integer> output = Functional.seq.takeWhile(Functional.isOdd, l);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
        }
    }

    @Test
    void cantRestartIteratorFromSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final Iterable<Integer> output = Functional.seq.takeWhile(Functional.isOdd, l);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }

    @Test
    void curriedSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(Functional.isEven).apply(l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqTakeWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(()->Functional.seq.takeWhile(null, input));
    }

    @Test
    void seqTakeWhileTest3() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        int counter = 10;
        final Iterable<Integer> integers = Functional.seq.takeWhile(Functional.constant(true), input);
        final Iterator<Integer> iterator = integers.iterator();
        while (counter >= 0) {
            assertThat(iterator.hasNext()).isTrue();
            --counter;
        }
        int next = iterator.next();
        assertThat(next).isEqualTo(1);
        next = iterator.next();
        assertThat(next).isEqualTo(2);
        next = iterator.next();
        assertThat(next).isEqualTo(3);
        next = iterator.next();
        assertThat(next).isEqualTo(4);
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }
        fail("Should not reach this point");
    }

    @Test
    void iterableHasNextTest() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        int counter = 10;
        final Iterator<Integer> iterator = input.iterator();
        while (counter >= 0) {
            assertThat(iterator.hasNext()).isTrue();
            --counter;
        }
        int next = iterator.next();
        assertThat(next).isEqualTo(1);
        next = iterator.next();
        assertThat(next).isEqualTo(2);
        next = iterator.next();
        assertThat(next).isEqualTo(3);
        next = iterator.next();
        assertThat(next).isEqualTo(4);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void ConstantInitialiserTest1() {
        final int howMany = 6;
        final int initValue = -1;
        final Collection<Integer> l = Functional.init(Functional.constant(initValue), howMany);
        assertThat(l.size()).isEqualTo(howMany);
        for (final int i : l)
            assertThat(i).isEqualTo(initValue);
    }

    /*[Test]
    public void switchTest1()
    {
        Assert.AreEqual(1,
                Functional.Switch(10,
                        new[]
        {
            Case.ToCase((int a) => a < 5, a => -1),
            Case.ToCase((int a) => a > 5, a => 1)
        }, a => 0));
    } */

    /*[Test]
    public void tryTest1()
    {
        int zero = 0;
        int results = Functional.Try<int,int,DivideByZeroException>(10, a => a/zero, a => a);
        Assert.AreEqual(10, results);
    }*/
    private class A {
        public String name;
        public int id;
    }
    /*[Test]
    public void CaseTest2()
    {
        var c1 = new List<Function<A, object>> {(A a) => (object) a.name, (A a) => (object) a.id};

        Function<A, IEnumerable<Function<int, object>>> c2 =
                a => c1.Select<Function<A, object>, Function<int, object>>(f => j => f(a));

        Function<A, IEnumerable<Functional.Case<int, object>>> cases =
                a => c2(a).Select((f, i) => Case.ToCase(i.Equals, f));

        var theA = new A {id = 1, name = "one"};

        IEnumerable<object> results =
                Enumerable.Range(0, 3).Select(i => Functional.Switch(i, cases(theA), aa => "oh dear"));
        var expected = new object[] {"one", 1, "oh dear"};
        CollectionAssert.AreEquivalent(expected, results);
    }*/

    /*[Test]
    public void ignoreTest1()
    {
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
    void ChooseTest1() throws OptionNoValueAccessException {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Integer> expected = Arrays.asList(1, 3, 5);
        final Collection<Integer> output =
                Functional.choose(i -> i % 2 != 0 ? Option.toOption(i) : Option.None(), input);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void ChooseTest2() throws OptionNoValueAccessException {
        final Collection<String> input = Arrays.asList("abc", "def");
        final Collection<Character> expected = Arrays.asList('a');
        final Collection<Character> output =
                Functional.choose(str -> str.startsWith("a") ? Option.toOption('a') : Option.None()
                        , input
                );
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void ChooseTest3() throws OptionNoValueAccessException {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Integer> expected = Arrays.asList(1, 3, 5);
        final Collection<Integer> output = Functional.choose(
                i -> i % 2 != 0 ? Option.toOption(i) : Option.None(), input);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    /*[Test]
    public void CurryTest1()
    {
        Function<int, int, bool> f = (i, j) => i > j;
        Function<int, Function<int, bool>> g = i => j => f(i, j);
        bool t = 10.in(g(5));
        Assert.IsFalse(t);
    }*/

    /*[Test]
    public void CurryTest2()
    {
        Function<int, int, bool> f = (i, j) => i < j;
        Function<int, Function<int, bool>> g = i => j => f(i, j);
        bool t = 10.in(g(5));
        Assert.IsTrue(t);
    }*/

    /*[Test]
    public void CompositionTest1()
    {
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
    void iterableUnzipTest1() {
        final Collection<Pair<String, Integer>> input =
                new ArrayList<>();
        input.add(Pair.of("1", 1));
        input.add(Pair.of("2", 2));

        final Pair<Collection<String>, Collection<Integer>> expected =
                Pair.of(
                        Arrays.asList("1", "2"),
                        Arrays.asList(1, 2));

        final Pair<List<String>, List<Integer>> output = Functional.unzip(IterableHelper.create(input));

        assertThat(output.getLeft()).containsExactlyElementsOf(expected.getLeft());
        assertThat(output.getRight()).containsExactlyElementsOf(expected.getRight());
    }

    @Test
    void unzip3Test1() {
        final Collection<Triple<String, Integer, String>> input =
                new ArrayList<>();
        input.add(Triple.of("1", 1, "L"));
        input.add(Triple.of("2", 2, "M"));
        input.add(Triple.of("3", 3, "K"));

        final Triple<Collection<String>, Collection<Integer>, Collection<String>> expected =
                Triple.of(
                        Arrays.asList("1", "2", "3"),
                        Arrays.asList(1, 2, 3),
                        Arrays.asList("L", "M", "K"));

        final Triple<List<String>, List<Integer>, List<String>> output = Functional.unzip3(input);

        assertThat(output.getLeft()).containsExactlyElementsOf(expected.getLeft());
        assertThat(output.getMiddle()).containsExactlyElementsOf(expected.getMiddle());
        assertThat(output.getRight()).containsExactlyElementsOf(expected.getRight());
    }

    @Test
    void iterableUnzip3Test1() {
        final Collection<Triple<String, Integer, String>> input =
                new ArrayList<>();
        input.add(Triple.of("1", 1, "L"));
        input.add(Triple.of("2", 2, "M"));
        input.add(Triple.of("3", 3, "K"));

        final Triple<Collection<String>, Collection<Integer>, Collection<String>> expected =
                Triple.of(
                        Arrays.asList("1", "2", "3"),
                        Arrays.asList(1, 2, 3),
                        Arrays.asList("L", "M", "K"));

        final Triple<List<String>, List<Integer>, List<String>> output = Functional.unzip3(IterableHelper.create(input));

        assertThat(output.getLeft()).containsExactlyElementsOf(expected.getLeft());
        assertThat(output.getMiddle()).containsExactlyElementsOf(expected.getMiddle());
        assertThat(output.getRight()).containsExactlyElementsOf(expected.getRight());
    }

    @Test
    void zipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Pair<Integer, Character>> expected = new ArrayList<>();
        expected.add(Pair.of(1, 'a'));
        expected.add(Pair.of(2, 'b'));
        expected.add(Pair.of(3, 'c'));
        expected.add(Pair.of(4, 'd'));
        expected.add(Pair.of(5, 'e'));

        final Collection<Pair<Integer, Character>> output = Functional.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void iterableZipTest1() {
        final Iterable<Integer> input1 = Functional.seq.map(Functional.identity(), Arrays.asList(1, 2, 3, 4, 5));
        final Iterable<Character> input2 = Functional.seq.map(Functional.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));

        final Collection<Pair<Integer, Character>> expected = new ArrayList<>();
        expected.add(Pair.of(1, 'a'));
        expected.add(Pair.of(2, 'b'));
        expected.add(Pair.of(3, 'c'));
        expected.add(Pair.of(4, 'd'));
        expected.add(Pair.of(5, 'e'));

        final Collection<Pair<Integer, Character>> output = Functional.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void failingZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd');

        assertThatIllegalArgumentException().isThrownBy(()->Functional.zip(input1, input2));
    }

    @Test
    void zipTwoFuncsTest1() {
        final Collection<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

        final Collection<Pair<Integer, String>> expected = new ArrayList<>();
        expected.add(Pair.of(1, "1"));
        expected.add(Pair.of(2, "2"));
        expected.add(Pair.of(3, "3"));
        expected.add(Pair.of(4, "4"));
        expected.add(Pair.of(5, "5"));

        final List<Pair<Integer, String>> output = Functional.zip(Functional.identity(), Functional.dStringify(), ints);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zipNoExceptionTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Pair<Integer, Character>> expected = new ArrayList<>();
        expected.add(Pair.of(1, 'a'));
        expected.add(Pair.of(2, 'b'));
        expected.add(Pair.of(3, 'c'));
        expected.add(Pair.of(4, 'd'));
        expected.add(Pair.of(5, 'e'));

        final Collection<Pair<Integer, Character>> output = Functional.noException.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zipNoExceptionTest2() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5, 6);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Pair<Integer, Character>> expected = new ArrayList<>();
        expected.add(Pair.of(1, 'a'));
        expected.add(Pair.of(2, 'b'));
        expected.add(Pair.of(3, 'c'));
        expected.add(Pair.of(4, 'd'));
        expected.add(Pair.of(5, 'e'));

        final Collection<Pair<Integer, Character>> output = Functional.noException.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Pair<Integer, Character>> expected = new ArrayList<>();
        expected.add(Pair.of(1, 'a'));
        expected.add(Pair.of(2, 'b'));
        expected.add(Pair.of(3, 'c'));
        expected.add(Pair.of(4, 'd'));
        expected.add(Pair.of(5, 'e'));

        final Collection<Pair<Integer, Character>> output = Functional.toList(Functional.seq.zip(input1, input2));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Pair<Integer, Character>> expected = new ArrayList<>();
        expected.add(Pair.of(1, 'a'));
        expected.add(Pair.of(2, 'b'));
        expected.add(Pair.of(3, 'c'));
        expected.add(Pair.of(4, 'd'));
        expected.add(Pair.of(5, 'e'));

        final Collection<Pair<Integer, Character>> output = Functional.toList(Functional.seq.<Integer, Character>zip(input1).apply(input2));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Iterable<Pair<Integer, Character>> zip = Functional.seq.zip(input1, input2);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->zip.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Iterable<Pair<Integer, Character>> zip = Functional.seq.zip(input1, input2);
        try {
            zip.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(zip::iterator);
    }

    @Test
    void seqZipTest2() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Pair<Integer, Character>> expected = new ArrayList<>();
        expected.add(Pair.of(1, 'a'));
        expected.add(Pair.of(2, 'b'));
        expected.add(Pair.of(3, 'c'));
        expected.add(Pair.of(4, 'd'));
        expected.add(Pair.of(5, 'e'));

        final Collection<Pair<Integer, Character>> output = Functional.toList(Functional.seq.zip(input1, input2));
        final Iterator<Pair<Integer, Character>> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final Pair<Integer, Character> element : expected) {
            final Pair<Integer, Character> next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void seqZipFnTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

        final Collection<Pair<Integer, String>> expected = new ArrayList<>();
        expected.add(Pair.of(1, "1"));
        expected.add(Pair.of(2, "2"));
        expected.add(Pair.of(3, "3"));
        expected.add(Pair.of(4, "4"));
        expected.add(Pair.of(5, "5"));

        final List<Pair<Integer, String>> output = Functional.toList(Functional.seq.zip(Functional.identity(), Functional.dStringify(), input));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqZipFnTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

        final Iterable<Pair<Integer, String>> output = Functional.seq.zip(Functional.identity(), Functional.dStringify(), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqZipFnTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

        final Iterable<Pair<Integer, String>> output = Functional.seq.zip(Functional.identity(), Functional.dStringify(), input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
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

        final Collection<Triple<Integer, Character, Double>> output = Functional.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void failingZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        assertThatIllegalArgumentException().isThrownBy(()->Functional.zip3(input1, input2, input3));
    }

    @Test
    void iterableZip3Test1() {
        final Iterable<Integer> input1 = Functional.seq.map(Functional.identity(), Arrays.asList(1, 2, 3, 4, 5));
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void failingIterableZip3Test1() {
        final Iterable<Integer> input1 = Functional.seq.map(Functional.identity(), Arrays.asList(1, 2, 3, 4, 5));
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        assertThatIllegalArgumentException().isThrownBy(()->Functional.zip3(input1, input2, input3));
    }

    @Test
    void zip3NoExceptionTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.noException.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zip3NoExceptionTest2() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5, 6);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.noException.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.toList(Functional.seq.zip3(input1, input2, input3));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.toList(Functional.seq.<Integer, Character, Double>zip3(input1, input2).apply(input3));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Iterable<Triple<Integer, Character, Double>> output = Functional.seq.zip3(input1, input2, input3);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Iterable<Triple<Integer, Character, Double>> output = Functional.seq.zip3(input1, input2, input3);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqZip3Test2() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.toList(Functional.seq.zip3(input1, input2, input3));
        final Iterator<Triple<Integer, Character, Double>> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final Triple<Integer, Character, Double> element : expected) {
            final Triple<Integer, Character, Double> next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    /*[ExpectedException(typeof(ArgumentException))]
        [Test]
    public void zip3Test2()
    {
        var input1 = new[] { 1, 2, 3, 4, 5 };
        var input2 = new[] { 'a', 'b', 'd', 'e' };
        var input3 = new[] { 1.0, 2.0, 2.5, 3.0, 3.5 };
        var expected = new[]
        {
            Tuple.Create(1, 'a', 1.0), Tuple.Create(2, 'b', 2.0), Tuple.Create(3, 'c', 2.5),
                    Tuple.Create(4, 'd', 3.0), Tuple.Create(5, 'e', 3.5)
        }.ToList();

        var output = Functional.Zip3(input1, input2, input3).ToList();

        CollectionAssert.AreEquivalent(expected, output);
    }*/

    /*[ExpectedException(typeof(ArgumentException))]
        [Test]
    public void zip3Test3()
    {
        var input1 = new[] { 1, 2, 3, 4, 5 };
        var input2 = new[] { 'a', 'b', 'c', 'd', 'e' };
        var input3 = new[] { 1.0, 2.0, 2.5, 3.5 };
        var expected = new[]
        {
            Tuple.Create(1, 'a', 1.0), Tuple.Create(2, 'b', 2.0), Tuple.Create(3, 'c', 2.5),
                    Tuple.Create(4, 'd', 3.0), Tuple.Create(5, 'e', 3.5)
        }.ToList();

        var output = Functional.Zip3(input1, input2, input3).ToList();

        CollectionAssert.AreEquivalent(expected, output);
    }*/

    @Test
    void findTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.find((Function<String, Boolean>) s -> s.equals(trueMatch), ls)).isEqualTo(trueMatch);
    }

    @Test
    void curriedFindTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        final Function<Iterable<String>, String> findFunc = Functional.find(s -> s.equals(trueMatch));
        assertThat(findFunc.apply(ls)).isEqualTo(trueMatch);
    }

    @Test
    void findTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(()->Functional.find(s -> s.equals(falseMatch), ls));
    }

    @Test
    void findNoExceptionTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.find(s -> s.equals(trueMatch), ls).Some()).isEqualTo(trueMatch);
    }

    @Test
    void findNoExceptionTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.find(s -> s.equals(falseMatch), ls).isNone()).isTrue();
    }

    @Test
    void findIndexTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.findIndex(s -> s.equals(trueMatch), ls)).isEqualTo(2);
    }

    @Test
    void findIndexTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThatIllegalArgumentException().isThrownBy(()->Functional.findIndex(s -> s.equals(falseMatch), ls));
    }

    @Test
    void findIndexNoExceptionTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.findIndex(s -> s.equals(trueMatch), ls).Some()).isEqualTo((Integer) 2);
    }

    @Test
    void findIndexNoExceptionTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.findIndex(s -> s.equals(falseMatch), ls).isNone()).isTrue();
    }

    @Test
    void pickTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        assertThat(Functional.pick((Function<Integer, Option<String>>) a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None(), li)).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void curriedPickTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Function<Iterable<Integer>, String> pickFunc = Functional.pick(a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None());
        assertThat(pickFunc.apply(li)).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void pickTest2() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(()->Functional.pick(a -> a == falseMatch ? Option.toOption(a.toString()) : Option.None(), li));
    }

    @Test
    void pickNoExceptionTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        assertThat(Functional.noException.pick(a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None(), li).Some()).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void pickNoExceptionTest2() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        assertThat(Functional.noException.pick(a -> a == falseMatch ? Option.toOption(a.toString()) : Option.None(), li).isNone()).isTrue();
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
        final Map<String, String> output = Functional.map_dict(i -> new Map.Entry<String, String>() {
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
        final Iterable<Integer> output = Functional.init(DoublingGenerator, 5);
        final List<Integer> output_ints = Functional.toList(output);
        assertThat(output_ints).isEqualTo(Arrays.asList(2, 4, 6, 8, 10));
    }

    public static Function<Integer, List<Integer>> repeat(final int howMany) {
        return integer -> Functional.init((Function<Integer, Integer>) counter -> integer, howMany);
    }

    @Test
    void curriedCollectTest1() {
        final List<Integer> input = Functional.init(DoublingGenerator, 5);
        final List<Integer> output = Functional.collect(repeat(3)).apply(input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void CollectTest1() {
        final List<Integer> input = Functional.init(DoublingGenerator, 5);
        final List<Integer> output = Functional.collect(repeat(3), input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqCollectTest1() {
        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqCollectTest1() {
        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3)).apply(input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqCollectTest2() {
        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        try {
            final Iterator<Integer> iterator1 = output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void cantRemoveFromSeqCollectTest1() {
        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void seqCollectTest3() {
        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void takeNandYieldTest1() {
        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
        final Pair<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(input, 2);
        final List<Integer> expectedList = Arrays.asList(2, 4);
        final List<Integer> expectedRemainder = Arrays.asList(6, 8, 10);
        assertThat(output.getLeft()).containsExactlyElementsOf(expectedList);
        assertThat(output.getRight()).containsExactlyElementsOf(expectedRemainder);
    }

    @Test
    void takeNandYieldTest2() {
        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
        final Pair<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(input, 0);
        final List<Integer> expectedList = Arrays.asList();
        final List<Integer> expectedRemainder = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(output.getLeft()).containsExactlyElementsOf(expectedList);
        assertThat(output.getRight()).containsExactlyElementsOf(expectedRemainder);
    }

    @Test
    void recFilterTest1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.rec.filter(Functional.isOdd, l);

        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void recMapTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<String> output = Functional.rec.map(Functional.dStringify(), input);
        assertThat(output).isEqualTo(Arrays.asList("1", "2", "3", "4", "5"));
    }

    @Test
    void recFoldvsMapTest1() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final String s1 = Functional.join(",", Functional.rec.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.rec.fold(
                FunctionalTest::csv, "", li);
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void ifTest1() {
        final Integer input = 1;
        final Iterable2<Integer> i = IterableHelper.asList(0, 1, 2);
        final Iterable2<Integer> result = i.map(ii -> Functional.If(input, Functional.greaterThan(ii), DoublingGenerator, TriplingGenerator));
        final List<Integer> expected = Arrays.asList(2, 3, 3);
        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void switchTest1() {
        assertThat(Functional.Switch(10, Arrays.asList(Functional.toCase(Functional.lessThan(5), Functional.constant(-1)), Functional.toCase(Functional.greaterThan(5), Functional.constant(1))), Functional.constant(0))).isEqualTo(1);
    }

    @Test
    void switchDefaultCaseTest1() {
        assertThat(Functional.Switch(Integer.valueOf(10), Arrays.asList(Functional.toCase(Functional.lessThan(Integer.valueOf(5)), Functional.constant(Integer.valueOf(-1))), Functional.toCase(Functional.greaterThan(Integer.valueOf(15)), Functional.constant(Integer.valueOf(1)))), Functional.constant(Integer.valueOf(0)))).isEqualTo(Integer.valueOf(0));
    }

    @Test
    void setFilterTest1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> oddElems = Functional.set.filter(Functional.isOdd, sl);

        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void setFilterTest2() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> evenElems = Functional.set.filter(Functional.isEven, sl);

        final Collection<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(expected.containsAll(evenElems)).isTrue();
        assertThat(evenElems.containsAll(expected)).isTrue();
    }

    @Test
    void setFilterTest3() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Integer limit = 5;
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> highElems = Functional.set.filter(a -> a > limit, sl);

        final Collection<Integer> expected = Arrays.asList(6, 8, 10);
        assertThat(expected.containsAll(highElems)).isTrue();
        assertThat(highElems.containsAll(expected)).isTrue();
    }

    @Test
    void setFilterTest4() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 5);
        final Integer limit = 10;
        final Set<Integer> sl = new HashSet<>(li);
        final Set<Integer> output = Functional.set.filter(a -> a > limit, sl);

        assertThat(output.iterator().hasNext()).isFalse();
    }

    @Test
    void setFilterTest5() {
        final Collection<Integer> li = Functional.init(DoublingGenerator, 10);
        final Collection<Integer> expected = Arrays.asList(4, 8, 12, 16, 20);
        final Set<Integer> sl = new HashSet<>(li);
        final Set<Integer> output = Functional.set.filter(a -> a % 4 == 0, sl);

        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }

    @Test
    void setCollectTest1() {
        final Iterable<Integer> input = Functional.init(DoublingGenerator, 5);
        final Set<Integer> output = Functional.set.collect(repeat(3), input);
        final Set<Integer> expected = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));

        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }

    @Test
    void setCollectTest2() {
        final Iterable<Integer> input = Functional.init(DoublingGenerator, 5);
        final Set<Integer> output1 = Functional.set.collect(repeat(3), input);
        final Set<Integer> output2 = output1;
        final Set<Integer> expected = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));

        assertThat(expected.containsAll(output1)).isTrue();
        assertThat(output1.containsAll(expected)).isTrue();
        assertThat(expected.containsAll(output2)).isTrue();
        assertThat(output2.containsAll(expected)).isTrue();
    }

    @Test
    void setMapTest1() {
        final Set<Integer> input = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<String> output = Functional.set.map(Functional.dStringify(), input);
        final Set<String> expected = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));
        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }

    @Test
    void setConcatTest1() {
        final Set<Integer> input = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Function<Integer, Integer> doubler = i -> i * 2;
        final Set<String> expected = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "8", "10"));

        final Set<String> strs = Functional.set.map(Functional.dStringify(), input);
        final Set<String> output = Functional.set.concat(strs, Functional.set.map(Functional.dStringify(), Functional.set.map(doubler, input)));

        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }

    @Test
    void setIntersectionTest1() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

        final Set<Integer> intersection = Functional.set.intersection(input1, input2);
        assertThat(intersection.containsAll(input1)).isTrue();
        assertThat(input1.containsAll(intersection)).isTrue();
    }

    @Test
    void setIntersectionTest2() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));
        final Set<Integer> expected = new HashSet<>(Arrays.asList(4, 5));

        final Set<Integer> intersection = Functional.set.intersection(input1, input2);
        assertThat(intersection.containsAll(expected)).isTrue();
        assertThat(expected.containsAll(intersection)).isTrue();
    }

    @Test
    void setIntersectionTest3() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(6, 7, 8));
        final Set<Integer> expected = Collections.emptySet();

        final Set<Integer> intersection = Functional.set.intersection(input1, input2);
        assertThat(intersection.containsAll(expected)).isTrue();
        assertThat(expected.containsAll(intersection)).isTrue();
    }

    @Test
    void setAsymmetricDifferenceTest1() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));
        final Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2, 3));

        final Set<Integer> diff = Functional.set.asymmetricDifference(input1, input2);
        assertThat(diff.containsAll(expected)).isTrue();
        assertThat(expected.containsAll(diff)).isTrue();
        assertThat(input2.containsAll(diff)).isFalse();
    }

    @Test
    void setAsymmetricDifferenceTest2() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(6, 7, 8));
        final Set<Integer> expected = Collections.emptySet();

        final Set<Integer> diff = Functional.set.asymmetricDifference(input1, input2);
        assertThat(diff.containsAll(input1)).isTrue();
        assertThat(input1.containsAll(diff)).isTrue();
    }

    @Test
    void appendTest1() {
        final Integer i = 1;
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> output = Functional.append(i, l);
        final List<Integer> expected = Arrays.asList(1, 2, 4, 6, 8, 10);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void tryToRemoveFromAnIteratorTest1() {
        final Integer i = 1;
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> output = Functional.append(i, l);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void unfoldTest1() {
        final Integer seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.unfold(unspool, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void unfoldAsDoublingGeneratorTest1() {
        final Integer seed = 1;
        final Function<Integer, Pair<Integer, Integer>> doubler = integer -> Pair.of(integer * 2, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.unfold(doubler, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void unfoldAsDoublingGeneratorTest2() {
        final Integer seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.unfold(doubler, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqUnfoldTest1() {
        final Integer seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqUnfoldTest1() {
        final Integer seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqUnfoldTest1() {
        final Integer seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqUnfoldTest2() {
        final Integer seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void seqUnfoldAsDoublingGeneratorTest1() {
        final Integer seed = 1;
        final Function<Integer, Pair<Integer, Integer>> doubler = integer -> Pair.of(integer * 2, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqUnfoldAsDoublingGeneratorTest2() {
        final Integer seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqUnfoldAsDoublingGeneratorTest2() {
        final Integer seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, seed);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqUnfoldAsDoublingGeneratorTest2() {
        final Integer seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, seed);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqUnfoldAsDoublingGeneratorTest3() {
        final Integer seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, seed);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void recUnfoldAsDoublingGeneratorTest1() {
        final Integer seed = 1;
        final Function<Integer, Pair<Integer, Integer>> doubler = integer -> Pair.of(integer * 2, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.rec.unfold(doubler, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void recUnfoldAsDoublingGeneratorTest2() {
        final Integer seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.rec.unfold(doubler, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void mapInTermsOfFoldTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<String> output = Functional.inTermsOfFold.map(Functional.dStringify(), input);
        assertThat(output.toArray()).isEqualTo(new String[]{"1", "2", "3", "4", "5"});
    }

    @Test
    void filterInTermsOfFoldTest1() {
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.inTermsOfFold.filter(Functional.isOdd, l);
        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void initInTermsOfUnfoldTest1() {
        final Collection<Integer> output = Functional.inTermsOfFold.init(integer -> integer * 2, 5);
        assertThat(output.toArray()).isEqualTo(new Integer[]{2, 4, 6, 8, 10});
    }

    @Test
    void curriedSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.<Integer>skip(0).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skip(0, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.skip(1, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final List<Integer> output = Functional.skip(2, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(4, 5);
            final List<Integer> output = Functional.skip(3, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(5);
            final List<Integer> output = Functional.skip(4, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.skip(5, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.skip(6, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(()->Functional.skip(-1, input));
    }

    @Test
    void seqSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(0, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(1, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(2, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(4, 5);
            final Iterable<Integer> output = Functional.seq.skip(3, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(5);
            final Iterable<Integer> output = Functional.seq.skip(4, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<>();
            final Iterable<Integer> output = Functional.seq.skip(5, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<>();
            final Iterable<Integer> output = Functional.seq.skip(6, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void cantRemoveFromSeqSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(0, l);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
        }
    }

    @Test
    void cantRestartIteratorFromSeqSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(0, l);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }

    @Test
    void curriedSeqSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.<Integer>skip(0).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqSkipTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(()->Functional.seq.skip(-1, input));
    }

    @Test
    void seqSkipTest3() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expected = Arrays.asList(3, 4, 5);
        final Iterable<Integer> output = Functional.seq.skip(2, l);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void curriedSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional.isEven).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional.isEven, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional.isOdd, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final List<Integer> output = Functional.skipWhile(i -> i <= 2, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.skipWhile(i -> i <= 6, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(()->Functional.skipWhile(null, input));
    }

    @Test
    void skipWhileTest3() {
        final List<Number> input = new ArrayList<>();
        for (int i = 1; i < 10; ++i)
            input.add(Integer.valueOf(i));

        final List<Number> output = Functional.skipWhile((Function<Object, Boolean>) number -> ((number instanceof Integer) && ((Integer) number % 2) == 1), input);

        final List<Integer> expected = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(Functional.isEven, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(Functional.isOdd, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(i -> i <= 2, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(i -> i <= 6, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqSkipWhileWithoutHasNextTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skipWhile(Functional.isEven, l);
            final Iterator<Integer> iterator = output.iterator();
            for (final Integer expct : expected)
                assertThat(iterator.next()).isEqualTo(expct);
        }
    }

    @Test
    void cantRemoveFromseqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skipWhile(Functional.isEven, l);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->output.iterator().remove());
        }
    }

    @Test
    void cantRestartIteratorFromseqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skipWhile(Functional.isEven, l);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }

    @Test
    void curriedSeqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(Functional.isEven).apply(l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqSkipWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(()->Functional.seq.skipWhile(null, input));
    }

    @Test
    void seqSkipWhileTest3() {
        final List<Number> input = new ArrayList<>();
        for (int i = 1; i < 10; ++i)
            input.add(i);

        final List<Number> output = Functional.toList(Functional.seq.skipWhile((Function<Object, Boolean>) number -> ((number instanceof Integer) && ((Integer) number % 2) == 1), input));

        final List<Integer> expected = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqSkipWhileTest4() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expected = Arrays.asList(3, 4, 5);
        final Iterable<Integer> output = Functional.seq.skipWhile(i -> i <= 2, l);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void extractFirstOfPair() {
        final List<Pair<Integer, String>> input = new ArrayList<>();
        for (int i = 0; i < 5; ++i) input.add(Pair.of(i, Integer.toString(i)));
        final List<Integer> output = Functional.map(Functional.first(), input);
        final List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void extractSecondOfPair() {
        final List<Pair<Integer, String>> input = new ArrayList<>();
        for (int i = 0; i < 5; ++i) input.add(Pair.of(i, Integer.toString(i)));
        final List<String> output = Functional.map(Functional.second(), input);
        final List<String> expected = Arrays.asList("0", "1", "2", "3", "4");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void groupByOddVsEvenInt() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Map<Boolean, List<Integer>> output = Functional.groupBy(Functional.isEven, input);
        final Map<Boolean, List<Integer>> expected = new HashMap<>();
        expected.put(false, Arrays.asList(1, 3, 5, 7, 9));
        expected.put(true, Arrays.asList(2, 4, 6, 8, 10));
        assertThat(output.get(true)).containsExactlyElementsOf(expected.get(true));
        assertThat(output.get(false)).containsExactlyElementsOf(expected.get(false));
    }

    @Test
    void groupByStringFirstTwoChar() {
        final List<String> input = Arrays.asList("aa", "aab", "aac", "def");
        final Map<String, List<String>> output = Functional.groupBy(s -> s.substring(0, 1), input);
        final Map<String, List<String>> expected = new HashMap<>();
        expected.put("a", Arrays.asList("aa", "aab", "aac"));
        expected.put("d", Arrays.asList("def"));
        assertThat(output.get("a")).containsExactlyElementsOf(expected.get("a"));
        assertThat(output.get("d")).containsExactlyElementsOf(expected.get("d"));
        assertThat(new TreeSet<>(output.keySet())).containsExactlyElementsOf(new TreeSet<>(expected.keySet()));
    }

    @Test
    void partitionRangesOfIntAndStore() {
        final int noElems = 13;
        final int noPartitions = 5;
        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems, noPartitions);

        // Store the ranges in a map to exercise the hashCode()
        final Map<Functional.Range<Integer>, Pair<Integer, Integer>> map =
                Functional.toDictionary(
                        Functional.identity(),
                        range -> Pair.of(range.from(), range.to()), partitions);

        final List<Functional.Range<Integer>> extractedRanges = Functional.map(Map.Entry::getKey, map.entrySet());

        assertThat(extractedRanges.containsAll(partitions)).isTrue();
        assertThat(partitions.containsAll(extractedRanges)).isTrue();
    }

    @Test
    void seqPartitionRangesOfIntAndStore() {
        final int noElems = 13;
        final int noPartitions = 5;
        final List<Functional.Range<Integer>> partitions = Functional.toList(Functional.seq.partition(noElems, noPartitions));

        // Store the ranges in a map to exercise the hashCode()
        final Map<Integer, Functional.Range<Integer>> map = Functional.toDictionary(Functional.Range::from, Functional.identity(), partitions);

        final List<Functional.Range<Integer>> extractedRanges = Functional.map(Map.Entry::getValue, map.entrySet());

        assertThat(extractedRanges.containsAll(partitions)).isTrue();
        assertThat(partitions.containsAll(extractedRanges)).isTrue();
    }

    @Test
    void cantRemoveFromSeqPartitionRangesOfIntAndStore() {
        final int noElems = 13;
        final int noPartitions = 5;
        final Iterable<Functional.Range<Integer>> partitions = Functional.seq.partition(noElems, noPartitions);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> partitions.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqPartitionRangesOfIntAndStore() {
        final int noElems = 13;
        final int noPartitions = 5;
        final Iterable<Functional.Range<Integer>> partitions = Functional.seq.partition(noElems, noPartitions);
        try {
            partitions.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(partitions::iterator);
    }

    @Test
    void partitionRangesOfInt() {
        final int noElems = 13;
        final int noPartitions = 5;
        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems, noPartitions);

        final List<Integer> expectedStart = Arrays.asList(0, 3, 6, 9, 11);
        final List<Integer> expectedEnd = Arrays.asList(3, 6, 9, 11, 13);
        final List<Pair<Integer, Integer>> expected = Functional.zip(expectedStart, expectedEnd);

        final List<Pair<Integer, Integer>> output = Functional.map(range -> Pair.of(range.from(), range.to()), partitions);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void exactlyEvenPartitionRangesOfInt() {
        final int noElems = 10;
        final int noPartitions = 5;
        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems, noPartitions);

        final List<Integer> expectedStart = Arrays.asList(0, 2, 4, 6, 8);
        final List<Integer> expectedEnd = Arrays.asList(2, 4, 6, 8, 10);
        final List<Pair<Integer, Integer>> expected = Functional.zip(expectedStart, expectedEnd);

        final List<Pair<Integer, Integer>> output = Functional.map(range -> Pair.of(range.from(), range.to()), partitions);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void partitionFewerRangesOfIntThanPartitionsRequested() {
        final int noElems = 7;
        final int noPartitions = 10;
        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems, noPartitions);

        final List<Integer> expectedStart = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
        final List<Integer> expectedEnd = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        final List<Functional.Range<Integer>> expected =
                Functional.concat(
                        Functional.map(
                                pair -> new Functional.Range<>(pair.getLeft(), pair.getRight()), Functional.zip(expectedStart, expectedEnd)),
                        Functional.init(Functional.constant(new Functional.Range<>(7, 7)), 3));

        assertThat(partitions).containsExactlyElementsOf(expected);
    }

    @Test
    void partitionRangesOfString() {
        final int noElems = 13;
        final int noPartitions = 5;
        final List<Functional.Range<String>> partitions =
                Functional.partition(
                        i -> Integer.toString(i - 1),
                        noElems, noPartitions);

        final List<String> expectedStart = Arrays.asList("0", "3", "6", "9", "11");
        final List<String> expectedEnd = Arrays.asList("3", "6", "9", "11", "13");
        final List<Pair<String, String>> expected = Functional.zip(expectedStart, expectedEnd);

        final List<Pair<String, String>> output = Functional.map(range -> Pair.of(range.from(), range.to()), partitions);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void partitionWithEmptySource() {
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.partition(0, 1));
    }

    @Test
    void partitionWithZeroOutputRanges() {
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.partition(1, 0));
    }

    @Test
    void seqPartitionRangesOfString() {
        final int noElems = 13;
        final int noPartitions = 5;
        final List<Functional.Range<String>> partitions = Functional.toList(
                Functional.seq.partition(
                        i -> Integer.toString(i - 1),
                        noElems, noPartitions));

        final List<String> expectedStart = Arrays.asList("0", "3", "6", "9", "11");
        final List<String> expectedEnd = Arrays.asList("3", "6", "9", "11", "13");
        final List<Pair<String, String>> expected = Functional.zip(expectedStart, expectedEnd);

        final List<Pair<String, String>> output = Functional.map(range -> Pair.of(range.from(), range.to()), partitions);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqPartitionRangesOfString2() {
        final int noElems = 13;
        final int noPartitions = 5;
        final Iterable<Functional.Range<String>> output =
                Functional.seq.partition(
                        i -> Integer.toString(i - 1),
                        noElems, noPartitions);

        final List<String> expectedStart = Arrays.asList("0", "3", "6", "9", "11");
        final List<String> expectedEnd = Arrays.asList("3", "6", "9", "11", "13");
        final List<Pair<String, String>> expected_ = Functional.zip(expectedStart, expectedEnd);

        final List<Functional.Range<String>> expected = Functional.map(pair -> new Functional.Range<>(pair.getLeft(), pair.getRight()), expected_);
        final Iterator<Functional.Range<String>> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final Functional.Range<String> element : expected) {
            final Functional.Range<String> next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void toMutableDictionaryTest() {
        final Map<Integer, String> expected = new HashMap<>();
        expected.put(6, "6");
        expected.put(12, "12");
        final Map<Integer, String> output = Functional.toMutableDictionary(expected);
        assertThat(expected.entrySet().containsAll(output.entrySet())).isTrue();
        assertThat(output.entrySet().containsAll(expected.entrySet())).isTrue();
        output.put(24, "24");
        assertThat(output.containsKey(24)).isTrue();
        assertThat(output.containsValue("24")).isTrue();
    }

    @Test
    void toMutableListTest() {
        final List<String> expected = Arrays.asList("0", "3", "6", "9", "11");
        final List<String> output = Functional.toMutableList(expected);
        assertThat(expected).containsExactlyElementsOf(output);
        output.add("24");
        assertThat(output.contains("24")).isTrue();
    }

    @Test
    void toMutableSetTest() {
        final List<String> expected = Arrays.asList("0", "3", "6", "9", "11");
        final Set<String> output = Functional.toMutableSet(expected);
        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
        output.add("24");
        assertThat(output.contains("24")).isTrue();
    }

    @Test
    void iterableToMutableSetTest() {
        final List<String> expected = Arrays.asList("0", "3", "6", "9", "11");
        final Iterable<String> input = Functional.seq.map(Functional.identity(), expected);
        final Set<String> output = Functional.toMutableSet(input);
        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
        output.add("24");
        assertThat(output.contains("24")).isTrue();
    }

    @Test
    void countTest() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final int howMany = Functional.fold(Functional.count, 0, input);
        assertThat(howMany).isEqualTo(input.size());
    }

    @Test
    void sumTest() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final int sum = Functional.fold(Functional.sum, 0, input);
        assertThat(sum).isEqualTo(15);
    }

    @Test
    void greaterThanOrEqualTest() {
        final List<Integer> list1 = Arrays.asList(-5, -1, 0, 1, 5);
        final List<Integer> list2 = Arrays.asList(-1, 0, 1, 5);

        final List<Boolean> expected = Arrays.asList(
                false, false, false, false,
                true, false, false, false,
                true, true, false, false,
                true, true, true, false,
                true, true, true, true);

        final List<Boolean> output = Functional.collect(ths -> Functional.map(that -> Functional.greaterThanOrEqual(that).apply(ths), list2), list1);

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void lessThanOrEqualTest() {
        final List<Integer> list1 = Arrays.asList(-1, 0, 1, 5);
        final List<Integer> list2 = Arrays.asList(-5, -1, 0, 1, 5);

        final List<Boolean> expected = Arrays.asList(
                false, true, true, true, true,
                false, false, true, true, true,
                false, false, false, true, true,
                false, false, false, false, true
        );

        final List<Boolean> output = Functional.collect(ths -> Functional.map(that -> Functional.lessThanOrEqual(that).apply(ths), list2), list1);

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void enumerationToListTest1() {
        final int[] ints = new int[]{1, 2, 3, 4, 5};
        final Enumeration<Integer> enumeration = new Enumeration<Integer>() {
            int counter = 0;

            public boolean hasMoreElements() {
                return counter < ints.length;
            }


            public Integer nextElement() {
                return ints[counter++];
            }
        };

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);

        final List<Integer> output = Functional.toList(enumeration);

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void cantRemoveFromArrayIterableTest() {
        final Integer[] ints = new Integer[]{1, 2, 3, 4, 5};
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ArrayIterable.create(ints).iterator().remove());
    }

    @Test
    void appendIterableCanOnlyHaveOneIterator() {
        final Integer i = 1;
        final Collection<Integer> l = Functional.init(DoublingGenerator, 5);
        final Iterable<Integer> output = Functional.append(i, l);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Should not reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }
}