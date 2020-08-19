package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;
import uk.co.qualitycode.utils.functional.monad.OptionNoValueAccessException;
import uk.co.qualitycode.utils.functional.primitive.integer.Func_int_int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.co.qualitycode.utils.functional.Functional.isEven;
import static uk.co.qualitycode.utils.functional.Functional.join;

class FunctionalTest {
    public static Func_int_int doublingGenerator_f = a -> 2 * a;
    public static Function<Integer, Integer> doublingGenerator = a -> 2 * a;
    public static Function<Integer, Integer> triplingGenerator = a -> 3 * a;
    public static Function<Integer, Integer> quadruplingGenerator = a -> 4 * a;

    @Test
    void convertFlatMapFnFromOptional() {
        final Function<Integer, Optional<Integer>> f1 = Optional::of;
        final Function<Integer, io.vavr.control.Option<Integer>> f2 = io.vavr.control.Option::some;

        final int expected = 2;
        final Optional<Integer> i1 = Optional.of(expected);
        final io.vavr.control.Option<Integer> i2 = io.vavr.control.Option.some(expected);
        assertThat(i1.flatMap(f1)).hasValue(expected);
        assertThat(i2.flatMap(f2)).contains(expected);
        assertThat(Functional.ConvertFlatMapOptionalToOption.convert(f1).apply(expected)).contains(expected);
    }

    @Test
    void convertFlatMapFnFromOption() {
        final Function<Integer, io.vavr.control.Option<Integer>> f1 = io.vavr.control.Option::of;
        final Function<Integer, Optional<Integer>> f2 = Optional::of;

        final int expected = 2;
        final io.vavr.control.Option<Integer> i1 = io.vavr.control.Option.some(expected);
        final Optional<Integer> i2 = Optional.of(expected);
        assertThat(i1.flatMap(f1)).contains(expected);
        assertThat(i2.flatMap(f2)).hasValue(expected);
        assertThat(Functional.ConvertFlatMapOptionToOptional.convert(f1).apply(expected)).contains(expected);
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
        assertThat(c).containsExactlyElementsOf(b);
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
                Functional.choose(i -> i % 2 == 0 ? Option.of(i) : Option.none(), li);

        final Integer[] expected = new Integer[]{6, 12};
        assertThat(o).containsExactly(expected);
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

        public int i() {
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
        final Tuple2<Double, List<myInt>> output = Functional.foldAndChoose(
                (state, day) -> {
                    final Double value = day.i() % 2 == 0 ? (Double) ((double) (day.i() / 2)) : null;
                    return value != null
                            ? new Tuple2<>(value, Option.none())
                            : new Tuple2<>(state, Option.of(day));
                }, 10.0, openedDays2);

        assertThat(output._1()).isEqualTo(last);
        final List<Integer> keys = new ArrayList<>(missingPricesPerDate.keySet());
        Collections.sort(keys);
        assertThat(Functional.map(myInt::i, output._2())).containsExactlyElementsOf(keys);
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
        for (final int i : ait) output.add(i);
        assertThat(output).containsExactlyElementsOf(expected);
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
    void recFoldvsMapTest1() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = join(",", Functional.rec.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.rec.fold(FunctionalTest::csv, "", li);
        assertThat(s2).isEqualTo(s1);
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
    void extractFirstOfPair() {
        final List<Tuple2<Integer, String>> input = new ArrayList<>();
        for (int i = 0; i < 5; ++i) input.add(new Tuple2<>(i, Integer.toString(i)));
        final List<Integer> output = Functional.map(Functional.first(), input);
        final List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void extractSecondOfPair() {
        final List<Tuple2<Integer, String>> input = new ArrayList<>();
        for (int i = 0; i < 5; ++i) input.add(new Tuple2<>(i, Integer.toString(i)));
        final List<String> output = Functional.map(Functional.second(), input);
        final List<String> expected = Arrays.asList("0", "1", "2", "3", "4");
        assertThat(output).containsExactlyElementsOf(expected);
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
        assertThat(output).containsExactlyElementsOf(expected);
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
    void cantRemoveFromArrayIterableTest() {
        final Integer[] ints = new Integer[]{1, 2, 3, 4, 5};
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ArrayIterable.create(ints).iterator().remove());
    }
}
