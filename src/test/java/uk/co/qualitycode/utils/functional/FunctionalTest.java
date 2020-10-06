package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.assertions.OptionAssert;
import uk.co.qualitycode.utils.functional.monad.Option;
import uk.co.qualitycode.utils.functional.primitive.integer.Func_int_int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.co.qualitycode.utils.functional.Functional.isEven;
import static uk.co.qualitycode.utils.functional.Functional.stringify;

class FunctionalTest {
    public static Func_int_int doublingGenerator_f = a -> 2 * a;
    public static Function<Integer, Integer> doublingGenerator = a -> 2 * a;
    public static Function<Integer, Integer> triplingGenerator = a -> 3 * a;
    public static Function<Integer, Integer> quadruplingGenerator = a -> 4 * a;

    @Nested
    class ConvertFlatMap {
        @Test
        void preconditions() {
            assertAll(
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.ConvertFlatMapOptionalToFlatMapVavrOption.convert(null))
                            .withMessage("convert(Function<T,Optional<R>>): tfm must not be null"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.ConvertFlatMapVavrOptionToFlatMapOptional.convert(null))
                            .withMessage("convert(Function<T,Option<R>>): tfm must not be null"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.ConvertFlatMapVavrOptionToFlatMapOption.convert(null))
                            .withMessage("convert(Function<T,Option<R>>): tfm must not be null"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.ConvertFlatMapOptionalToFlatMapOption.convert(null))
                            .withMessage("convert(Function<T,Optional<R>>): tfm must not be null"));
        }

        @Test
        void convertOptionalAcceptsTfmThatReturnsNull() {
            assertThat(Functional.ConvertFlatMapOptionalToFlatMapVavrOption.convert(o -> null).apply(new Object())).isEmpty();
        }

        @Test
        void convertOptionAcceptsTfmThatReturnsNull() {
            assertThat(Functional.ConvertFlatMapVavrOptionToFlatMapOptional.convert(o -> null).apply(new Object())).isEmpty();
        }

        @Test
        void convertOptionalToOptionAcceptsTfmThatReturnsNull() {
            OptionAssert.assertThat(Functional.ConvertFlatMapOptionalToFlatMapOption.convert(o -> null).apply(new Object())).isEmpty();
        }

        @Test
        void convertOptionToOptionAcceptsTfmThatReturnsNull() {
            OptionAssert.assertThat(Functional.ConvertFlatMapVavrOptionToFlatMapOption.convert(o -> null).apply(new Object())).isEmpty();
        }

        @Test
        void convertFlatMapFnFromOptional() {
            final Function<Integer, Optional<Integer>> f1 = Optional::of;
            final Function<Integer, io.vavr.control.Option<Integer>> f2 = io.vavr.control.Option::some;

            final int expected = 2;
            final Optional<Integer> i1 = Optional.of(expected);
            final io.vavr.control.Option<Integer> i2 = io.vavr.control.Option.some(expected);
            assertThat(i1.flatMap(f1)).hasValue(expected);
            assertThat(i2.flatMap(f2)).contains(expected);
            assertThat(Functional.ConvertFlatMapOptionalToFlatMapVavrOption.convert(f1).apply(expected)).contains(expected);
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
            assertThat(Functional.ConvertFlatMapVavrOptionToFlatMapOptional.convert(f1).apply(expected)).contains(expected);
        }

        @Test
        void convertFlatMapFnFromOptionalToOption() {
            final Function<Integer, Optional<Integer>> f1 = Optional::of;
            final Function<Integer, Optional<Integer>> f2 = Optional::of;

            final int expected = 2;
            final Optional<Integer> i1 = Optional.of(expected);
            final Option<Integer> i2 = Option.of(expected);
            assertThat(i1.flatMap(f1)).hasValue(expected);
            assertThat(i2.toJavaOptional().flatMap(f2)).contains(expected);
            OptionAssert.assertThat(Functional.ConvertFlatMapOptionalToFlatMapOption.convert(f1).apply(expected)).hasValue(expected);
        }

        @Test
        void convertFlatMapFnFromOptionToOption() {
            final Function<Integer, io.vavr.control.Option<Integer>> f1 = io.vavr.control.Option::of;
            final Function<Integer, Optional<Integer>> f2 = Optional::of;

            final int expected = 2;
            final io.vavr.control.Option<Integer> i1 = io.vavr.control.Option.some(expected);
            final Optional<Integer> i2 = Optional.of(expected);
            assertThat(i1.flatMap(f1)).contains(expected);
            assertThat(i2.flatMap(f2)).hasValue(expected);
            OptionAssert.assertThat(Functional.ConvertFlatMapVavrOptionToFlatMapOption.convert(f1).apply(expected)).hasValue(expected);
        }
    }

    @Test
    void convertToString() {
        assertThat(stringify(1)).isEqualTo("1");

//        Mockito cannot verify toString()
//        toString() is too often used behind of scenes  (i.e. during String concatenation, in IDE debugging views). Verifying it may give inconsistent or hard to understand results. Not to mention that verifying toString() most likely hints awkward design (hard to explain in a short exception message. Trust me...)
//        However, it is possible to stub toString(). Stubbing toString() smells a bit funny but there are rare, legitimate use cases.

//        final Object obj = mock(Object.class);
//        stringify(obj);
//        verify(obj).toString();
    }

    static boolean bothAreEven(final int a, final int b) {
        return isEven(a) && isEven(b);
    }

    private static final <B, C> boolean fn(final B b, final C c) {
        return b.equals(c);
    }

    private static final <B, C> Predicate<C> curried_fn(final B b) {
        return c -> fn(b, c);
    }

    private static int adder_int(final int left, final int right) {
        return left + right;
    }

    private static Function<Integer, Integer> curried_adder_int(final int c) {
        return p -> adder_int(c, p);
    }

    static String csv(final String state, final Integer a) {
        return StringUtils.isEmpty(state) ? a.toString() : state + "," + a;
    }

    final Function<Collection<Integer>, String> concatenate = l -> Functional.fold(FunctionalTest::csv, "", l);

    final Function<Collection<Integer>, Collection<Integer>> evens_f = l -> Functional.filter(Functional::isEven, l);

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

    static class myInt {
        private final int _i;

        public myInt(final int i) {
            _i = i;
        }

        public int i() {
            return _i;
        }
    }

    @Test
    void testIsEven_withEvenNum() {
        assertThat(isEven(2)).isTrue();
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
        final Predicate<Integer> testForPosInts = integer -> integer > state;

        final Function<Iterable<Integer>, List<Integer>> curriedTestForPosInts = Functional.filter(testForPosInts);

        final Collection<Integer> l = Arrays.asList(-3, -2, 0, 1, 5);
        final Collection<Integer> posInts = curriedTestForPosInts.apply(l);

        final Collection<Integer> expected = Arrays.asList(1, 5);
        assertThat(posInts).containsExactlyElementsOf(expected);
    }

    public static Function<Integer, List<Integer>> repeat(final int howMany) {
        return integer -> Functional.init(counter -> integer, howMany);
    }

    @Test
    void mapInTermsOfFoldTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<String> output = Functional.inTermsOfFold.map(Functional.stringify(), input);
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void filterInTermsOfFoldTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.inTermsOfFold.filter(Functional::isOdd, l);
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
    void cantRemoveFromArrayIterableTest() {
        final Integer[] ints = new Integer[]{1, 2, 3, 4, 5};
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ArrayIterable.create(ints).iterator().remove());
    }
}
