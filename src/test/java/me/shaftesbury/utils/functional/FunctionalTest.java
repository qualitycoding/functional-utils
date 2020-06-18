package me.shaftesbury.utils.functional;

import me.shaftesbury.utils.functional.monad.Option;
import me.shaftesbury.utils.functional.monad.OptionNoValueAccessException;
import me.shaftesbury.utils.functional.primitive.integer.Func_int_int;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.assertj.vavr.api.VavrAssertions;
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
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

import static me.shaftesbury.utils.functional.Functional.isEven;
import static me.shaftesbury.utils.functional.Functional.join;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.fail;

class FunctionalTest {
    public static Func_int_int doublingGenerator_f = a -> 2 * a;
    public static Function<Integer, Integer> doublingGenerator = a -> 2 * a;
    public static Function<Integer, Integer> triplingGenerator = a -> 3 * a;
    public static Function<Integer, Integer> quadruplingGenerator = a -> 4 * a;

    @Test
    void convertFlatMapFn() {
        final Function<Integer, Optional<Integer>> f1 = Optional::of;
        final Function<Integer, io.vavr.control.Option<Integer>> f2 = io.vavr.control.Option::some;

        final int expected = 2;
        final Optional<Integer> i1 = Optional.of(expected);
        final io.vavr.control.Option<Integer> i2 = io.vavr.control.Option.some(expected);
        assertThat(i1.flatMap(f1)).hasValue(expected);
        VavrAssertions.assertThat(i2.flatMap(f2)).contains(expected);
        VavrAssertions.assertThat(Functional.convertFlatMapFn(f1).apply(expected)).contains(expected);
    }

    static boolean bothAreEven(final int a, final int b) {
        return isEven.apply(a) && isEven.apply(b);
    }

    static boolean bothAreLessThan10(final int a, final int b) {
        return a < 10 && b < 10;
    }

    static BiFunction<Integer, Integer, Boolean> dBothAreLessThan10 = FunctionalTest::bothAreLessThan10;

    @Test
    void compositionTest1A() {
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
    void compositionTest2() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(triplingGenerator, 5);
        assertThat(Functional.forAll2(Functional.not2(dBothAreLessThan10), l, m)).isFalse();
        // equivalent to bothAreGreaterThanOrEqualTo10

        final int lowerLimit = 1;
        final int upperLimit = 16;
        assertThat(Functional.forAll2(Functional.not2((a, b) -> a > lowerLimit && b > lowerLimit), l, m)).isFalse();
        assertThat(Functional.forAll2(Functional.not2((a, b) -> a > upperLimit && b > upperLimit), l, m)).isTrue();
    }

    static final <B, C> boolean fn(final B b, final C c) {
        return b.equals(c);
    }

    static final <B, C> Function<C, Boolean> curried_fn(final B b) {
        return c -> fn(b, c);
    }

    @Test
    void curriedFnTest1() {
        final boolean test1a = fn(1, 2);
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
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = join(",", Functional.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.fold(FunctionalTest::csv, "", li);
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void curriedFoldvsMapTest1() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = join(",", Functional.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.fold(FunctionalTest::csv, "").apply(li);
        assertThat(s2).isEqualTo(s1);
    }

    final Function<Collection<Integer>, String> concatenate = l -> Functional.fold(FunctionalTest::csv, "", l);

    final Function<Collection<Integer>, Collection<Integer>> evens_f = l -> Functional.filter(isEven, l);

    @Test
    void compositionTest3() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final String s = Functional.in(li, Functional.then(evens_f, concatenate));
        assertThat(s).isEqualTo("6,12");
    }

    @Test
    void compositionTest4() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
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
        assertThat("     ").isEqualTo(join("", indentation));

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
    void chooseTest3A() throws OptionNoValueAccessException {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
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
        final Collection<Integer> openedDays = Functional.init(triplingGenerator, 5);
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
    void testIsEven_withEvenNum() {
        assertThat(isEven.apply(2)).isTrue();
    }

    @Test
    void testIn() {
        final Integer a = 10;
        assertThat(Functional.in(a, isEven)).isTrue();
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
    void arrayIterableTest1() {
        final Integer[] input = new Integer[]{1, 2, 3, 4, 5};
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);

        final ArrayIterable<Integer> ait = ArrayIterable.create(input);
        final List<Integer> output = new ArrayList<>();
        for (final Integer i : ait) output.add(i);
        assertThat(output.toArray()).isEqualTo(expected.toArray());
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
    public void caseTest2()
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

    /*[Test]
    public void curryTest1()
    {
        Function<int, int, bool> f = (i, j) => i > j;
        Function<int, Function<int, bool>> g = i => j => f(i, j);
        bool t = 10.in(g(5));
        Assert.IsFalse(t);
    }*/

    /*[Test]
    public void curryTest2()
    {
        Function<int, int, bool> f = (i, j) => i < j;
        Function<int, Function<int, bool>> g = i => j => f(i, j);
        bool t = 10.in(g(5));
        Assert.IsTrue(t);
    }*/

    /*[Test]
    public void compositionTest1()
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

        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(input1, input2, input3));
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

        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(input1, input2, input3));
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

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
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
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.find((Function<String, Boolean>) s -> s.equals(trueMatch), ls)).isEqualTo(trueMatch);
    }

    @Test
    void curriedFindTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        final Function<Iterable<String>, String> findFunc = Functional.find(s -> s.equals(trueMatch));
        assertThat(findFunc.apply(ls)).isEqualTo(trueMatch);
    }

    @Test
    void findTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.find(s -> s.equals(falseMatch), ls));
    }

    @Test
    void findNoExceptionTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.find(s -> s.equals(trueMatch), ls).Some()).isEqualTo(trueMatch);
    }

    @Test
    void findNoExceptionTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.find(s -> s.equals(falseMatch), ls).isNone()).isTrue();
    }

    @Test
    void findIndexTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.findIndex(s -> s.equals(trueMatch), ls)).isEqualTo(2);
    }

    @Test
    void findIndexTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.findIndex(s -> s.equals(falseMatch), ls));
    }

    @Test
    void findIndexNoExceptionTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.findIndex(s -> s.equals(trueMatch), ls).Some()).isEqualTo((Integer) 2);
    }

    @Test
    void findIndexNoExceptionTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.findIndex(s -> s.equals(falseMatch), ls).isNone()).isTrue();
    }

    @Test
    void pickTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        assertThat(Functional.pick((Function<Integer, Option<String>>) a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None(), li)).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void curriedPickTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Function<Iterable<Integer>, String> pickFunc = Functional.pick(a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None());
        assertThat(pickFunc.apply(li)).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void pickTest2() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.pick(a -> a == falseMatch ? Option.toOption(a.toString()) : Option.None(), li));
    }

    @Test
    void pickNoExceptionTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        assertThat(Functional.noException.pick(a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None(), li).Some()).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void pickNoExceptionTest2() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
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
        final Iterable<Integer> output = Functional.init(doublingGenerator, 5);
        final List<Integer> output_ints = Functional.toList(output);
        assertThat(output_ints).isEqualTo(Arrays.asList(2, 4, 6, 8, 10));
    }

    public static Function<Integer, List<Integer>> repeat(final int howMany) {
        return integer -> Functional.init((Function<Integer, Integer>) counter -> integer, howMany);
    }

    @Test
    void curriedCollectTest1() {
        final List<Integer> input = Functional.init(doublingGenerator, 5);
        final List<Integer> output = Functional.collect(repeat(3)).apply(input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void collectTest1() {
        final List<Integer> input = Functional.init(doublingGenerator, 5);
        final List<Integer> output = Functional.collect(repeat(3), input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqCollectTest1() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqCollectTest1() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3)).apply(input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqCollectTest2() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
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
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void seqCollectTest3() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
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
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Pair<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(input, 2);
        final List<Integer> expectedList = Arrays.asList(2, 4);
        final List<Integer> expectedRemainder = Arrays.asList(6, 8, 10);
        assertThat(output.getLeft()).containsExactlyElementsOf(expectedList);
        assertThat(output.getRight()).containsExactlyElementsOf(expectedRemainder);
    }

    @Test
    void takeNandYieldTest2() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Pair<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(input, 0);
        final List<Integer> expectedList = Arrays.asList();
        final List<Integer> expectedRemainder = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(output.getLeft()).containsExactlyElementsOf(expectedList);
        assertThat(output.getRight()).containsExactlyElementsOf(expectedRemainder);
    }

    @Test
    void recFilterTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
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
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = join(",", Functional.rec.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.rec.fold(
                FunctionalTest::csv, "", li);
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void ifTest1() {
        final Integer input = 1;
        final Iterable2<Integer> i = IterableHelper.asList(0, 1, 2);
        final Iterable2<Integer> result = i.map(ii -> Functional.If(input, Functional.greaterThan(ii), doublingGenerator, triplingGenerator));
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
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> oddElems = Functional.set.filter(Functional.isOdd, sl);

        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void setFilterTest2() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> evenElems = Functional.set.filter(isEven, sl);

        final Collection<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(expected.containsAll(evenElems)).isTrue();
        assertThat(evenElems.containsAll(expected)).isTrue();
    }

    @Test
    void setFilterTest3() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Integer limit = 5;
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> highElems = Functional.set.filter(a -> a > limit, sl);

        final Collection<Integer> expected = Arrays.asList(6, 8, 10);
        assertThat(expected.containsAll(highElems)).isTrue();
        assertThat(highElems.containsAll(expected)).isTrue();
    }

    @Test
    void setFilterTest4() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Integer limit = 10;
        final Set<Integer> sl = new HashSet<>(li);
        final Set<Integer> output = Functional.set.filter(a -> a > limit, sl);

        assertThat(output.iterator().hasNext()).isFalse();
    }

    @Test
    void setFilterTest5() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 10);
        final Collection<Integer> expected = Arrays.asList(4, 8, 12, 16, 20);
        final Set<Integer> sl = new HashSet<>(li);
        final Set<Integer> output = Functional.set.filter(a -> a % 4 == 0, sl);

        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }

    @Test
    void setCollectTest1() {
        final Iterable<Integer> input = Functional.init(doublingGenerator, 5);
        final Set<Integer> output = Functional.set.collect(repeat(3), input);
        final Set<Integer> expected = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));

        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }

    @Test
    void setCollectTest2() {
        final Iterable<Integer> input = Functional.init(doublingGenerator, 5);
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
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.append(i, l);
        final List<Integer> expected = Arrays.asList(1, 2, 4, 6, 8, 10);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void tryToRemoveFromAnIteratorTest1() {
        final Integer i = 1;
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.append(i, l);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
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
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
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
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
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
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void filterInTermsOfFoldTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.inTermsOfFold.filter(Functional.isOdd, l);
        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void initInTermsOfUnfoldTest1() {
        final Collection<Integer> output = Functional.inTermsOfFold.init(integer -> integer * 2, 5);
        assertThat(output).containsExactly(2, 4, 6, 8, 10);
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
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.skip(-1, input));
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
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
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
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.seq.skip(-1, input));
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
            final List<Integer> output = Functional.skipWhile(isEven).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(isEven, l);
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
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.skipWhile(null, input));
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
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(isEven, l));
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
            final Iterable<Integer> output = Functional.seq.skipWhile(isEven, l);
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
            final Iterable<Integer> output = Functional.seq.skipWhile(isEven, l);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }
    }

    @Test
    void cantRestartIteratorFromseqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skipWhile(isEven, l);
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
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(isEven).apply(l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqSkipWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.seq.skipWhile(null, input));
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
        final Map<Boolean, List<Integer>> output = Functional.groupBy(isEven, input);
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
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.append(i, l);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Should not reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }
}