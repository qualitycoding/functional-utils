package me.shaftesbury.utils.functional.primitive.integer;

import me.shaftesbury.utils.functional.Option;
import me.shaftesbury.utils.functional.OptionNoValueAccessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static me.shaftesbury.utils.functional.Functional.in;
import static me.shaftesbury.utils.functional.Functional.then;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;

public class FunctionalTest {
    public static Func_int_int DoublingGenerator = a -> 2 * a;

    @Test void initTest1() {
        final IntList output = Functional.init(DoublingGenerator, 5);
        assertThat(output.toArray()).containsExactly(2, 4, 6, 8, 10);
    }

    @Test void rangeTest1() {
        final IntList output = Functional.init(Functional.range(0), 5);
        assertThat(output.toArray()).containsExactly(0, 1, 2, 3, 4);
    }

    @Test void mapTest1() {
        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
        final Collection<String> output = Functional.map(Functional.dStringify(), input);
        assertThat(output.toArray()).containsExactly(new String[]{"1", "2", "3", "4", "5"});
    }

    @Test void mapiTest1() {
        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
        final Collection<Pair<Integer, String>> output = Functional.mapi((pos, i) -> Pair.of(pos, Integer.toString(i)), input);
        assertThat(me.shaftesbury.utils.functional.Functional.map(Pair::getRight, output).toArray()).containsExactly(new String[]{"1", "2", "3", "4", "5"});
        assertThat(Functional.map(Pair::getLeft, output).toArray()).containsExactly(0, 1, 2, 3, 4);
    }

//    @Test
//    public void seqMapiTest1()
//    {
//        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
//        final Collection<Pair<Integer,String>> output = Functional.toList(Functional.seq.mapi(new BiFunction<Integer, Integer, Pair<Integer, String>>() {
//
//            public Pair<Integer, String> apply(final Integer pos, final Integer i) {
//                return Pair.of(pos, i.toString());
//            }
//        }, input));
//        assertThat().containsExactly(new String[]{"1","2","3","4","5"},Functional.map(new Function<Pair<Integer,String>, String>() {
//
//            public String apply(final Pair<Integer,String> o) {
//                return o.getValue1();
//            }
//        },output).toArray());
//        assertThat().containsExactly(new Integer[]{0,1,2,3,4},Functional.map(new Function<Pair<Integer,String>, Integer>() {
//
//            public Integer apply(final Pair<Integer,String> o) {
//                return o.getLeft();
//            }
//        },output).toArray());
//    }

//    @Test
//    public void seqMapiTest2()
//    {
//        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
//        final Iterable<Pair<Integer, String>> mapi = Functional.seq.mapi(new BiFunction<Integer, Integer, Pair<Integer, String>>() {
//
//            public Pair<Integer, String> apply(final Integer pos, final Integer i) {
//                return Pair.of(pos, i.toString());
//            }
//        }, input);
//        final Iterator<Pair<Integer, String>> iterator = mapi.iterator();
//
//        for(int i=0;i<10;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        Pair<Integer, String> next = iterator.next();
//        assertThat().isEqualTo("1",next.getValue1());
//        next = iterator.next();
//        assertThat().isEqualTo("2",next.getValue1());
//        next = iterator.next();
//        assertThat().isEqualTo("3",next.getValue1());
//        next = iterator.next();
//        assertThat().isEqualTo("4",next.getValue1());
//        next = iterator.next();
//        assertThat().isEqualTo("5",next.getValue1());
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }

    public static Func_int_int TriplingGenerator = a -> 3 * a;

    public static Func_int_int QuadruplingGenerator = a -> 4 * a;

    private static boolean BothAreEven(final int a, final int b) {
        return Functional.isEven.apply(a) && Functional.isEven.apply(b);
    }


    @Test void forAll2Test1() {
        final IntList l = Functional.init(DoublingGenerator, 5);
        final IntList m = Functional.init(QuadruplingGenerator, 5);
        try {
            assertThat(Functional.forAll2(FunctionalTest::BothAreEven, l, m)).isTrue();
        } catch (final Exception e) {
            fail();
        }
    }

//    @Test
//    public void forAll2NoExceptionTest1()
//    {
//        final IntList l = Functional.init(DoublingGenerator, 5);
//        final IntList m = Functional.init(QuadruplingGenerator, 5);
//        try
//        {
//            assertThat().isTrue()(Functional.noException.forAll2(
//                    new BiFunction<Integer, Integer, Boolean>() {
//
//                        public Boolean apply(final Integer a, final Integer b) {
//                            return BothAreEven(a, b);
//                        }
//                    }, l, m).Some());
//        }
//        catch (final Exception e)
//        {
//            Assert.fail();
//        }
//    }

    private static boolean BothAreLessThan10(final int a, final int b) {
        return a < 10 && b < 10;
    }

    private static Func2_int_int_T<Boolean> dBothAreLessThan10 = FunctionalTest::BothAreLessThan10;

    @Test void forAll2Test2() {
        final IntList l = Functional.init(DoublingGenerator, 5);
        final IntList m = Functional.init(TriplingGenerator, 5);

        assertThat(Functional.forAll2(dBothAreLessThan10, l, m)).isFalse();
    }

    @Test void forAll2Test3() {
        final IntList l = Functional.init(DoublingGenerator, 5);
        final IntList m = Functional.init(QuadruplingGenerator, 7);

        assertThatExceptionOfType(Exception.class).isThrownBy(() ->
                Functional.forAll2(FunctionalTest::BothAreEven, l, m));
    }

//    @Test
//    public void forAll2NoExceptionTest2()
//    {
//        final IntList l = Functional.init(DoublingGenerator, 5);
//        final IntList m = Functional.init(TriplingGenerator, 5);
//
//        assertThat().isFalse()(Functional.noException.forAll2(dBothAreLessThan10
//                , l, m).Some());
//    }

//    @Test
//    public void forAll2NoExceptionTest3()
//    {
//        final IntList l = Functional.init(DoublingGenerator, 5);
//        final IntList m = Functional.init(QuadruplingGenerator, 7);
//
//        assertThat().isTrue()(Functional.noException.forAll2(
//                new BiFunction<Integer, Integer, Boolean>() {
//
//                    public Boolean apply(final Integer a, final Integer b) {
//                        return BothAreEven(a, b);
//                    }
//                }, l, m).isNone());
//    }

    @Test void compositionTest1A() {
        final IntList i = new IntList(new int[]{1, 2, 3, 45, 56, 6});

        final boolean allOdd = Functional.forAll(Functional.isOdd, i);
        final boolean notAllOdd = Functional.exists(Functional.not(Functional.isOdd), i);

        assertThat(allOdd).isFalse();
        assertThat(notAllOdd).isTrue();
    }

    @Test void compositionTest2() {
        final IntList l = Functional.init(DoublingGenerator, 5);
        final IntList m = Functional.init(TriplingGenerator, 5);
        assertThat(Functional.forAll2(Functional.not2(dBothAreLessThan10), l, m)).isFalse();
        // equivalent to BothAreGreaterThanOrEqualTo10

        final int lowerLimit = 1;
        final int upperLimit = 16;
        assertThat(
                Functional.forAll2(
                        Functional.not2(
                                (a, b) -> a > lowerLimit && b > lowerLimit
                        ), l, m)).isFalse();
        assertThat(
                Functional.forAll2(
                        Functional.not2(
                                (a, b) -> a > upperLimit && b > upperLimit
                        ), l, m)).isTrue();
    }

    @Test void partitionTest1() {
        final IntList m = Functional.init(TriplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isOdd, m);

        final Integer[] left = {3, 9, 15};
        final Integer[] right = {6, 12};
        assertThat(r.getLeft().toArray()).containsExactly(left);
        assertThat(r.getRight().toArray()).containsExactly(right);
    }

    @Test void partitionTest2() {
        final IntList l = Functional.init(DoublingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isEven, l);
        assertThat(r.getLeft().toArray()).containsExactly(l.toArray(new Integer[0]));
        assertThat(r.getRight().toArray()).containsExactly(new Integer[]{});
    }

    @Test void partitionTest3() {
        final IntList l = Functional.init(DoublingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isEven, l);
        assertThat(r.getLeft().toArray()).containsExactly(Functional.filter(Functional.isEven, l).toArray(new Integer[0]));
    }

    @Test void toStringTest1() {
        final IntList li = Functional.init(DoublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        //String s = String.Join(",", ls);
        assertThat(ls.toArray()).containsExactly(new String[]{"2", "4", "6", "8", "10"});
    }

    @Test void chooseTest1B() throws OptionNoValueAccessException {
        final IntList li = Functional.init(TriplingGenerator, 5);
        final Collection<String> o = Functional.choose((Func_int_T<Option<String>>) i -> i % 2 == 0 ? Option.toOption(Integer.toString(i)) : Option.None(), li);
        final String[] expected = {"6", "12"};
        assertThat(expected).containsExactlyElementsOf(o);
    }

    @Test void chooseTest2A() //throws OptionNoValueAccessException
    {
        Map<Integer, String> o = null;
        final IntList li = Functional.init(TriplingGenerator, 5);
        o = Functional.toDictionary(Functional.identity(), Functional.dStringify(),
                Functional.filter(i -> i % 2 == 0, li));
        final Map<Integer, String> expected = new HashMap<>();
        expected.put(6, "6");
        expected.put(12, "12");
        assertThat(o).hasSize(expected.size());
        for (final Integer expectedKey : expected.keySet()) {
            assertThat(o).containsKey(expectedKey);
            final String expectedValue = expected.get(expectedKey);
            //assertThat().isEqualTo(expectedValue,o.get(expectedKey),"Expected '"+expectedValue+"' but got '"+o.get(expectedKey)+"'");
            assertThat(o.get(expectedKey)).isEqualTo(expectedValue);
        }
    }

//    private final static <B, C>boolean Fn(final B b, final C c)
//    {
//        return b.equals(c);
//    }
//
//    private final static <B, C>Function<C, Boolean> curried_fn(final B b)
//    {
//        return new Function<C, Boolean>() {
//
//            public Boolean apply(final C c) {
//                return Fn(b,c);
//            }
//        };
//    }
//
//    @Test
//    public void curriedFnTest1()
//    {
//        final boolean test1a = Fn(1, 2);
//        final boolean test1b = curried_fn(1).apply(2);
//        assertThat().isEqualTo(test1a, test1b);
//    }
//
//    private static int adder_int(int left, int right) { return left + right; }
//
//    private static Function<Integer, Integer> curried_adder_int(final int c)
//    {
//        return new Function<Integer, Integer>() {
//
//            public Integer apply(final Integer p) {
//                return adder_int(c, p);
//            }
//        };
//    }
//
//    @Test
//    public void curriedFnTest2()
//    {
//        final List<Integer> a = Arrays.asList(1, 2, 3, 4, 5);
//        final IntList b = Functional.map(
//                new Function<Integer, Integer>() {
//
//                    public Integer apply(final Integer a1) {
//                        return adder_int(2, a1);
//                    }
//                }, a);
//        final IntList c = Functional.map(curried_adder_int(2), a);
//        assertThat().containsExactly(b.toArray(), c.toArray());
//    }

    private static String csv(final String state, final int a) {
        return StringUtils.isEmpty(state) ? "" + a : state + "," + a;
    }

    @Test void foldvsMapTest1() {
        final IntList li = Functional.init(DoublingGenerator, 5);
        final String s1 = me.shaftesbury.utils.functional.Functional.join(",", Functional.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.fold(FunctionalTest::csv, "", li);
        assertThat(s1).isEqualTo(s2);
    }

    private final Function<IntList, String> concatenate =
            l -> Functional.fold(FunctionalTest::csv, "", l);

    @Test void fwdPipelineTest1() {
        final IntList li = Functional.init(DoublingGenerator, 5);
        final String s1 = in(li, concatenate);
        assertThat(s1).isEqualTo("2,4,6,8,10");
    }

    private final Function<IntList, IntList> evens_f =
            l -> Functional.filter(Functional.isEven, l);

    @Test void fwdPipelineTest2() {
        final IntList li = Functional.init(TriplingGenerator, 5);
        final IntList evens = me.shaftesbury.utils.functional.Functional.in(li, evens_f);
        final String s1 = in(evens, concatenate);
        final String s2 = in(li, then(evens_f, concatenate));
        assertThat(s1).isEqualTo("6,12");
        assertThat(s1).isEqualTo(s2);
    }

    @Test void compositionTest3() {
        final IntList li = Functional.init(TriplingGenerator, 5);
        final String s = in(li, then(evens_f, concatenate));
        assertThat(s).isEqualTo("6,12");
    }

    @Test void compositionTest4() {
        final IntList li = Functional.init(TriplingGenerator, 5);
        final String s = then(evens_f, concatenate).apply(li);
        assertThat(s).isEqualTo("6,12");
    }

    @Test void indentTest1() {
        final int level = 5;
        final String expectedResult = "     ";

        String indentedName = "";
        for (int i = 0; i < level; ++i) {
            indentedName += " ";
        }
        assertThat(indentedName).isEqualTo(expectedResult);

        final Collection<String> indentation = Functional.init(
                (Func_int_T<String>) integer -> " ", level);
        assertThat(me.shaftesbury.utils.functional.Functional.join("", indentation)).isEqualTo("     ");
    }

    @Test void chooseTest3A() throws OptionNoValueAccessException {
        final IntList li = Functional.init(TriplingGenerator, 5);
        final IntList o =
                Functional.choose(
                        (Func_int_Option_int) i -> i % 2 == 0 ? Option_int.toOption(i) : Option_int.None(), li);

        assertThat(o.toArray()).containsExactly(6, 12);
    }

    /*
    //        [Test]
    //        public void tryGetValueTest1()
    //        {
    //            var d = new Dictionary<string, int>();
    //            d["one"] = 1;
    //            Assert.AreEqual(1, FunctionalHelpers.TryGetValue_nullable("one", d));
    //        }
    //
    // */
//
//    /*
//            [Test]
//        public void tryGetValueTest2()
//        {
//            var d = new Dictionary<string, int>();
//            d["one"] = 1;
//            Assert.IsNull(FunctionalHelpers.TryGetValue_nullable("two", d));
//        }
//
//     */
///*
//        [Test]
//        public void tryGetValueTest3()
//        {
//            var d = new Dictionary<string, string>();
//            d["one"] = "ONE";
//            Assert.AreEqual("ONE", FunctionalHelpers.TryGetValue("one", d).Some);
//        }
//
// */
//
//    /*
//            [Test]
//        public void tryGetValueTest4()
//        {
//            var d = new Dictionary<string, string>();
//            d["one"] = "ONE";
//            Assert.IsTrue(FunctionalHelpers.TryGetValue("two", d).None);
//        }
//
//     */
//
//    /*
//            [Test]
//        public void tryGetValueTest5()
//        {
//            var d = new Dictionary<string, List<int>>();
//            var l = new List<int>(new[] {1, 2, 3});
//            d["one"] = l;
//            Assert.AreEqual(l, FunctionalHelpers.TryGetValue("one", d).Some);
//        }
//*/
//    /*
//        [Test]
//        public void tryGetValueTest6()
//        {
//            var d = new Dictionary<string, string>();
//            d["one"] = "ONE";
//            Assert.IsTrue(FunctionalHelpers.TryGetValue("two", d).None);
//        }
//
//     */
//
    private class myInt {
        private final int _i;

        public myInt(final int i) {
            _i = i;
        }

        public final int i() {
            return _i;
        }
    }


    @Test void foldAndChooseTest1() {
        final Map<Integer, Double> missingPricesPerDate = new Hashtable<>();
        final IntList openedDays = Functional.init(TriplingGenerator, 5);
        Double last = 10.0;
        final IntIterator iterator = openedDays.iterator();
        while (iterator.hasNext()) {
            final int day = iterator.next();
            final Double value = day % 2 == 0 ? (Double) ((double) (day / 2)) : null;
            if (value != null)
                last = value;
            else
                missingPricesPerDate.put(day, last);
        }

        final Collection<myInt> openedDays2 = Functional.init(
                (Func_int_T<myInt>) a -> new myInt(3 * a), 5);
        final Pair<Double, List<myInt>> output =
                me.shaftesbury.utils.functional.Functional.foldAndChoose((state, day) -> {
                    final Double value = day.i() % 2 == 0 ? (Double) ((double) (day.i() / 2)) : null;
                    return value != null
                            ? Pair.of(value, Option.None())
                            : Pair.of(state, Option.toOption(day));
                }, 10.0, openedDays2);

        assertThat(output.getLeft()).isEqualTo(last);
        final List<Integer> keys = new ArrayList<>(missingPricesPerDate.keySet());
        Collections.sort(keys);
        assertThat(Functional.map(myInt::i, output.getRight()).toArray(new Integer[0]))
                .containsExactlyElementsOf(keys);
    }

    @Test void joinTest1() {
        final IntList ids = Functional.init(TriplingGenerator, 5);
        final String expected = "3,6,9,12,15";
        assertThat(me.shaftesbury.utils.functional.Functional.join(",", Functional.map(Functional.dStringify(), ids))).isEqualTo(expected);
        assertThat(Functional.join(",", ids)).isEqualTo(expected);
    }

    @Test void joinTest2() {
        final IntList ids = Functional.init(TriplingGenerator, 5);
        final String expected = "'3','6','9','12','15'";
        final Func_int_T<String> f =
                id -> "'" + id + "'";
        assertThat(me.shaftesbury.utils.functional.Functional.join(",", Functional.map(f, ids))).isEqualTo(expected);
        assertThat(Functional.join(",", ids, f)).isEqualTo(expected);
    }

    @Test void betweenTest1() {
        final int lowerBound = 2, upperBound = 4;
        assertThat(Functional.between(lowerBound, upperBound, 3)).isTrue();
    }

    @Test void betweenTest2() {
        final int lowerBound = 2, upperBound = 4;
        assertThat(Functional.between(lowerBound, upperBound, 1)).isFalse();
    }

//    @Test
//    public void betweenTest3()
//    {
//        final double lowerBound = 2.5, upperBound = 2.6;
//        assertThat().isTrue()(Functional.between(lowerBound, upperBound, 2.55));
//    }

    @Test void testIsEven_withEvenNum() {
        assertThat(Functional.isEven.apply(2)).isTrue();
    }

    //    @Test
//    public void testIn()
//    {
//        final int a = 10;
//        assertThat().isTrue()(Functionalin(a, Functional.isEven));
//    }
//
//
//    /*@Test
//    public void testThen()
//    {
//        // mult(two,three).then(add(four)) =>
//        // then(mult(two,three),add(four))
//        // 2 * 3 + 4 = 10
//        Integer two = 2;
//        Integer three = 3;
//        Integer four = 4;
//        Functional.then(new Function<Integer,Integer>()
//        {
//
//            public Integer apply(final Integer i) { return }
//        })
//    } */
//
//    @Test
//    public void seqFilterTest1()
//    {
//        final IntList l = Functional.init(DoublingGenerator,5);
//        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);
//
//        AssertIterable.assertIterableEquals(new ArrayList<Integer>(), oddElems);
//    }
//
//    @Test
//    public void seqFilterTest2()
//    {
//        final IntList l = Functional.init(DoublingGenerator,5);
//        final Iterable<Integer> evenElems = Functional.seq.filter(Functional.isEven, l);
//
//        final IntList expected = new int[]{2,4,6,8,10});
//        AssertIterable.assertIterableEquals(expected, evenElems);
//    }
//
//    @Test
//    public void seqFilterTest3()
//    {
//        final IntList l = Functional.init(DoublingGenerator,5);
//        final Integer limit = 5;
//        final Iterable<Integer> highElems = Functional.seq.filter(
//                new Function<Integer,Boolean>()
//                {
//
//                    public Boolean apply(final Integer a) { return a > limit;}
//                }, l);
//
//        final IntList expected = new int[]{6,8,10});
//        AssertIterable.assertIterableEquals(expected,highElems);
//    }
//
//    @Test
//    public void seqFilterTest4()
//    {
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Integer limit = 10;
//        final Iterable<Integer> output = Functional.seq.filter(
//                new Function<Integer,Boolean>()
//                {
//                     public Boolean apply(final Integer a) {return a > limit;}
//                }, li);
//
//        assertThat().isFalse()(output.iterator().hasNext());
//    }
//
//    @Test
//    public void seqFilterTest5()
//    {
//        final IntList li = Functional.init(DoublingGenerator, 10);
//        final IntList expected = new int[]{4,8,12,16,20});
//        final Iterable<Integer> output = Functional.seq.filter(
//                new Function<Integer,Boolean>()
//                {
//                     public Boolean apply(final Integer a) {return a % 4 ==0;}
//                }, li);
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqFilterTest6()
//    {
//        final IntList input = Functional.init(DoublingGenerator, 10);
//        final Iterable<Integer> output = Functional.seq.filter(
//                new Function<Integer,Boolean>()
//                {
//                     public Boolean apply(final Integer a) {return a % 4 ==0;}
//                }, input);
//        final Iterator<Integer> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        int next = iterator.next();
//        assertThat().isEqualTo(4,next);
//        next = iterator.next();
//        assertThat().isEqualTo(8,next);
//        next = iterator.next();
//        assertThat().isEqualTo(12,next);
//        next = iterator.next();
//        assertThat().isEqualTo(16,next);
//        next = iterator.next();
//        assertThat().isEqualTo(20,next);
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
    @Test void findLastTest1() {
        final IntList l = Functional.init(DoublingGenerator, 5);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> Functional.findLast(Functional.isOdd, l));
    }

    @Test void findLastTest2() {
        final IntList l = Functional.init(DoublingGenerator, 5);
        assertThat(Functional.findLast(Functional.isEven, l)).isEqualTo(10);
    }
//
//    @Test
//    public void findLastNoExceptionTest1()
//    {
//        final List<Integer> l = new ArrayList<Integer>(Functional.init(DoublingGenerator, 5));
//        assertThat().isTrue()(Functional.noException.findLast(Functional.isOdd, l).isNone());
//    }
//
//    @Test
//    public void findLastNoExceptionTest2()
//    {
//        final List<Integer> l = new ArrayList<Integer>(Functional.init(DoublingGenerator, 5));
//        assertThat().isEqualTo((Integer)10, Functional.noException.findLast(Functional.isEven, l).Some());
//    }
//
//    @Test(expected=NoSuchElementException.class)
//    public void findLastIterableTest1()
//    {
//        final IntIterable l = Functional.init(DoublingGenerator, 5);
//        assertThat().isEqualTo( 5, Functional.findLast(Functional.isOdd, l));
//    }
//
//    @Test
//    public void findLastIterableTest2()
//    {
//        final Iterable<Integer> l = new ArrayList<Integer>(Functional.init(DoublingGenerator, 5));
//        assertThat().isEqualTo((Integer)10, Functional.findLast(Functional.isEven, l));
//    }

    //    @Test
//    public void seqMapTest1()
//    {
//        final List<Integer> input = new int[]{1,2,3,4,5}); //Enumerable.Range(1, 5).ToList();
//        final List<String> expected = Arrays.asList(new String[] {"1", "2", "3", "4", "5"});
//        final Iterable<String> output = Functional.seq.map(Functional.<Integer>dStringify(), input);
//        final Iterator<String> it = output.iterator();
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(it.hasNext());
//
//        for(int i=0; i<expected.size(); ++i)
//            assertThat().isEqualTo(expected.get(i), it.next());
//
//        assertThat().isFalse()(it.hasNext());
//        try {
//            it.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void toArrayTest1()
//    {
//        final List<Integer> input = new int[]{1,2,3,4,5});
//        final Iterable<String> strs = Functional.seq.map(Functional.<Integer>dStringify(), input);
//        final List<String> expected = Arrays.asList(new String[]{"1", "2", "3", "4", "5"});
//
//        final Object[] output = Functional.toArray(strs);
//
//        assertThat().isEqualTo(expected.size(),output.length);
//        for(int i=0; i<expected.size(); ++i)
//            assertThat().isEqualTo(expected.get(i), output[i]);
//    }
//
    @Test void lastTest1() {
        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
        assertThat((long) Functional.last(input)).isEqualTo(5);
    }

//    @Test
//    public void lastTest2()
//    {
//        final List<Integer> input = new int[]{1,2,3,4,5};
//        final Iterable<String> strs = Functional.seq.map(Functional.<Integer>dStringify(), input);
//        assertThat().isEqualTo("5", Functional.last(strs));
//    }

    @Test void lastTest3() {
        final IntList input = new IntList(new int[]{});
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.last(input));
    }


    @Test void concatTest1() {
        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
        final int[] expected = new int[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5};
        assertThat(Functional.concat(input, input).toArray()).containsExactly(expected);
    }

    //    @Test
//    public void seqConcatTest1()
//    {
//        final List<Integer> input = new int[]{1,2,3,4,5});
//        final Function<Integer,Integer> doubler = new Function<Integer, Integer>() {
//
//            public Integer apply(final Integer i) {
//                return i * 2;
//            }
//        };
//        final List<String> expected = Arrays.asList(new String[]{"1","2","3","4","5","2","4","6","8","10"});
//
//        final Iterable<String> strs = Functional.seq.map(Functional.<Integer>dStringify(), input);
//        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.<Integer>dStringify(), Functional.seq.map(doubler, input)));
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqConcatTest2()
//    {
//        final List<Integer> input = new int[]{1,2,3,4,5});
//        final Function<Integer,Integer> doubler = new Function<Integer, Integer>() {
//
//            public Integer apply(final Integer i) {
//                return i * 2;
//            }
//        };
//        final List<String> expected = Arrays.asList(new String[]{"1","2","3","4","5","2","4","6","8","10"});
//
//        final Iterable<String> strs = Functional.seq.map(Functional.<Integer>dStringify(), input);
//        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.<Integer>dStringify(), Functional.seq.map(doubler, input)));
//        final Iterator<String> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        String next = iterator.next();
//        assertThat().isEqualTo("1",next);
//        next = iterator.next();
//        assertThat().isEqualTo("2",next);
//        next = iterator.next();
//        assertThat().isEqualTo("3",next);
//        next = iterator.next();
//        assertThat().isEqualTo("4",next);
//        next = iterator.next();
//        assertThat().isEqualTo("5",next);
//        next = iterator.next();
//        assertThat().isEqualTo("2",next);
//        next = iterator.next();
//        assertThat().isEqualTo("4",next);
//        next = iterator.next();
//        assertThat().isEqualTo("6",next);
//        next = iterator.next();
//        assertThat().isEqualTo("8",next);
//        next = iterator.next();
//        assertThat().isEqualTo("10",next);
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void arrayIterableTest1()
//    {
//        final Integer[] input = new Integer[]{1,2,3,4,5};
//        final List<Integer> expected = new int[]{1,2,3,4,5});
//
//        final ArrayIterable<Integer> ait = ArrayIterable.create(input);
//        final List<Integer> output = new ArrayList<Integer>();
//        for(final Integer i:ait) output.add(i);
//        assertThat().containsExactly(expected.toArray(), output.toArray());
//    }
//
//
//    @Test
//    public void seqChooseTest1()
//    {
//        final IntList li = Functional.init(TriplingGenerator,5);
//        final Iterable<String> output = Functional.seq.choose(
//                new Function<Integer,Option<String>>()
//                {
//
//                        public Option<String> apply(final Integer i)
//                        {
//                            return i%2 == 0 ? Option.toOption(i.toString()) : Option.<String>None();
//                        }
//                }, li);
//
//        final Collection<String> expected = Arrays.asList(new String[]{"6", "12"});
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqChooseTest2()
//    {
//        final IntList li = Functional.init(TriplingGenerator, 5);
//        final Iterable<String> output = Functional.seq.choose(
//                new Function<Integer, Option<String>>() {
//
//                    public Option<String> apply(final Integer i) {
//                        return i % 2 == 0 ? Option.toOption(i.toString()) : Option.<String>None();
//                    }
//                }, li);
//        final Iterator<String> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        String next = iterator.next();
//        assertThat().isEqualTo("6",next);
//        next = iterator.next();
//        assertThat().isEqualTo("12",next);
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void seqInitTest1()
//    {
//        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator, 5);
//        AssertIterable.assertIterableEquals(new int[]{2,4,6,8,10}), output);
//    }
//
//    @Test
//    public void seqInitTest3()
//    {
//        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator, 5);
//        final Iterator<Integer> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        int next = iterator.next();
//        assertThat().isEqualTo(2,next);
//        next = iterator.next();
//        assertThat().isEqualTo(4,next);
//        next = iterator.next();
//        assertThat().isEqualTo(6,next);
//        next = iterator.next();
//        assertThat().isEqualTo(8,next);
//        next = iterator.next();
//        assertThat().isEqualTo(10,next);
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
    @Test void fwdPipelineTest3() {
        final IntList input = Functional.init(DoublingGenerator, 5);
        final Collection<String> output = in(input,
                (Function<IntList, Collection<String>>) integers -> Functional.map(Functional.dStringify(), integers));

        final Collection<String> expected = Arrays.asList("2", "4", "6", "8", "10");
        assertThat(output).containsExactlyElementsOf(expected);
    }
//
//    @Test
//    public void fwdPipelineTest4()
//    {
//        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
//        final Iterable<String> output = Functional.in(input,
//                new Function<Iterable<Integer>, Iterable<String>>() {
//
//                    public Iterable<String> apply(final Iterable<Integer> integers) {
//                        try {
//                            return Functional.seq.map(Functional.<Integer>dStringify(), integers);
//                        } catch (final Exception e) {
//                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                            return null; // Argh!!!
//                        }
//                    }
//                });
//
//        final Collection<String> expected = Arrays.asList(new String[] {"2", "4", "6", "8", "10"});
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void fwdPipelineTest5()
//    {
//        final Iterable<Integer> l = Functional.seq.init(DoublingGenerator, 5);
//        final Iterable<Integer> oddElems = Functional.in(l,
//                new Function<Iterable<Integer>, Iterable<Integer>>() {
//
//                    public Iterable<Integer> apply(final Iterable<Integer> ints) {
//                        return Functional.filter(Functional.isOdd, ints);
//                    }
//                });
//
//        assertThat().isFalse()(oddElems.iterator().hasNext());
//    }
//    /*
//    private class Test1
//    {
//        public readonly int i;
//
//        public Test1(int j)
//        {
//            i = j;
//        }
//    }
//
//    private static Function<object, string> fn1()
//    {
//        return delegate(object o)
//        {
//            if (o.GetType() == typeof (Test1))
//                return fn2(o as Test1);
//            if (o.GetType() == typeof (string))
//                return fn3(o as string);
//            return null;
//        };
//    }
//
//    private static string fn2(Test1 i)
//    {
//        return i.i.ToString();
//    }
//
//    private static string fn3(string s)
//    {
//        return s;
//    }
//
//    [Test]
//    public void fwdPipelineTest6()
//    {
//        Function<object, string> fn = fn1();
//        var i = new Test1(10);
//        const string s = "test";
//        Assert.AreEqual("10", i.in(fn));
//        Assert.AreEqual("test", s.in(fn));
//    } */
//
//    @Test
//    public void seqInitTest2()
//    {
//        final Iterable<Integer> output = Functional.seq.init(DoublingGenerator);
//        AssertIterable.assertIterableEquals(new int[]{2,4,6,8,10,12,14,16,18,20,22}),Functional.take(11,output));
//    }
//
//    @Test
//    public void takeTest1()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//        final List<Integer> output = Functional.collect(new Function<Integer, List<Integer>>() {
//
//            public List<Integer> apply(final Integer o) {
//                return Functional.take(o, input);
//            }
//        }, input);
//        final List<Integer> expected = Arrays.asList(1,1,2,1,2,3,1,2,3,4,1,2,3,4,5,1,2,3,4,5,6,1,2,3,4,5,6,7,1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,10);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void takeNoExceptionTest1()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//        final List<Integer> output = Functional.collect(new Function<Integer, List<Integer>>() {
//
//            public List<Integer> apply(final Integer o) {
//                return Functional.noException.take(o, input);
//            }
//        }, input);
//        final List<Integer> expected = Arrays.asList(1,1,2,1,2,3,1,2,3,4,1,2,3,4,5,1,2,3,4,5,6,1,2,3,4,5,6,7,1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,10);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void takeNoExceptionTest2()
//    {
//        final List<Integer> input = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
//        final List<Integer> output = Functional.collect(new Function<Integer, List<Integer>>() {
//
//            public List<Integer> apply(final Integer o) {
//                return Functional.noException.take(o+5, input);
//            }
//        }, input);
//        final List<Integer> expected = Arrays.asList(1,2,3,4,5,6,1,2,3,4,5,6,7,1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqTakeTest1()
//    {
//        final List<Integer> input = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
//        final List<Integer> output = Functional.collect(new Function<Integer, List<Integer>>() {
//
//            public List<Integer> apply(final Integer o) {
//                return Functional.toList(Functional.seq.take(o, input));
//            }
//        }, input);
//        final List<Integer> expected = Arrays.asList(1,1,2,1,2,3,1,2,3,4,1,2,3,4,5,1,2,3,4,5,6,1,2,3,4,5,6,7,1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,10);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqTakeTest2()
//    {
//        final List<Integer> input = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
//        final List<Integer> output = Functional.collect(new Function<Integer, List<Integer>>() {
//
//            public List<Integer> apply(final Integer o) {
//                return Functional.toList(Functional.seq.take(o, input));
//            }
//        }, input);
//        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//        final Iterator<Integer> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final int element : expected)
//        {
//            final int next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void takeWhileTest1()
//    {
//        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
//        {
//            final List<Integer> expected = new ArrayList<Integer>();
//            final List<Integer> output = Functional.takeWhile(Functional.isEven, l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(1);
//            final List<Integer> output = Functional.takeWhile(Functional.isOdd, l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(1,2,3,4);
//            final List<Integer> output = Functional.takeWhile(new Function<Integer, Boolean>() {
//                public Boolean apply(final Integer i) {
//                    return i <= 4;
//                }
//            }, l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
//            final List<Integer> output = Functional.takeWhile(new Function<Integer, Boolean>() {
//                public Boolean apply(final Integer i) {
//                    return i <= 6;
//                }
//            }, l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void takeWhileTest2()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
//        Functional.takeWhile(null, input);
//    }
//
//    @Test
//    public void seqTakeWhileTest1()
//    {
//        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
//        {
//            final List<Integer> expected = new ArrayList<Integer>();
//            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(Functional.isEven, l));
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(1);
//            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(Functional.isOdd, l));
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(1,2,3,4);
//            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(new Function<Integer, Boolean>() {
//                public Boolean apply(final Integer i) {
//                    return i <= 4;
//                }
//            }, l));
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
//            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(new Function<Integer, Boolean>() {
//                public Boolean apply(final Integer i) {
//                    return i <= 6;
//                }
//            }, l));
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void seqTakeWhileTest2()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
//        Functional.seq.takeWhile(null, input);
//    }
//
//    @Test
//    public void seqTakeWhileTest3()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
//        int counter=10;
//        final Iterable<Integer> integers = Functional.seq.takeWhile(Functional.constant(true), input);
//        final Iterator<Integer> iterator = integers.iterator();
//        while(counter>=0)
//        {
//            assertThat().isTrue()(iterator.hasNext());
//            --counter;
//        }
//        int next = iterator.next();
//        assertThat().isEqualTo(1,next);
//        next = iterator.next();
//        assertThat().isEqualTo(2,next);
//        next = iterator.next();
//        assertThat().isEqualTo(3,next);
//        next = iterator.next();
//        assertThat().isEqualTo(4, next);
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void iterableHasNextTest()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
//        int counter=10;
//        final Iterator<Integer> iterator = input.iterator();
//        while(counter>=0)
//        {
//            assertThat().isTrue()(iterator.hasNext());
//            --counter;
//        }
//        int next = iterator.next();
//        assertThat().isEqualTo(1,next);
//        next = iterator.next();
//        assertThat().isEqualTo(2,next);
//        next = iterator.next();
//        assertThat().isEqualTo(3,next);
//        next = iterator.next();
//        assertThat().isEqualTo(4,next);
//        assertThat().isFalse()(iterator.hasNext());
//    }
//
//    @Test
//    public void constantInitialiserTest1()
//    {
//        final int howMany = 6;
//        final int initValue = -1;
//        final IntList l = Functional.init(Functional.constant(initValue),howMany);
//        assertThat().isEqualTo(howMany, l.size());
//        for(final int i : l)
//            assertThat().isEqualTo(initValue, i);
//    }
//
//    /*[Test]
//    public void switchTest1()
//    {
//        Assert.AreEqual(1,
//                Functional.Switch(10,
//                        new[]
//        {
//            Case.ToCase((int a) => a < 5, a => -1),
//            Case.ToCase((int a) => a > 5, a => 1)
//        }, a => 0));
//    } */
//
//    /*[Test]
//    public void tryTest1()
//    {
//        int zero = 0;
//        int results = Functional.Try<int,int,DivideByZeroException>(10, a => a/zero, a => a);
//        Assert.AreEqual(10, results);
//    }*/
//    private class A
//    {
//        public String name;
//        public int id;
//    }
//    /*[Test]
//    public void caseTest2()
//    {
//        var c1 = new List<Function<A, object>> {(A a) => (object) a.name, (A a) => (object) a.id};
//
//        Function<A, IEnumerable<Function<int, object>>> c2 =
//                a => c1.Select<Function<A, object>, Function<int, object>>(f => j => f(a));
//
//        Function<A, IEnumerable<Functional.Case<int, object>>> cases =
//                a => c2(a).Select((f, i) => Case.ToCase(i.Equals, f));
//
//        var theA = new A {id = 1, name = "one"};
//
//        IEnumerable<object> results =
//                Enumerable.Range(0, 3).Select(i => Functional.Switch(i, cases(theA), aa => "oh dear"));
//        var expected = new object[] {"one", 1, "oh dear"};
//        CollectionAssert.AreEquivalent(expected, results);
//    }*/
//
//    /*[Test]
//    public void ignoreTest1()
//    {
//        var input = new[] {1, 2, 3, 4, 5};
//        var output = new List<string>();
//        Function<int, string> format = i => string.Format("Discarding {0}", i);
//        List<string> expected = new[] {1, 2, 3, 4, 5}.Select(format).ToList();
//        Function<int, bool> f = i =>
//        {
//            output.Add(format(i));
//            return true;
//        };
//        input.Select(f).Ignore();
//        CollectionAssert.AreEquivalent(expected, output);
//    }*/
//    @Test
//    public void chooseTest1() throws OptionNoValueAccessException
//    {
//        final IntList input = new int[] {1, 2, 3, 4, 5});
//        final IntList expected = new int[] {1, 3, 5});
//        final IntList output =
//                Functional.choose(
//                        new Function<Integer, Option<Integer>>() {
//
//                            public Option<Integer> apply(final Integer i) {
//                                return i%2 != 0 ? Option.toOption(i) : Option.<Integer>None();
//                            }
//                        }, input);
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void chooseTest2() throws OptionNoValueAccessException
//    {
//        final Collection<String> input = Arrays.asList(new String[] {"abc", "def"});
//        final Collection<Character> expected = Arrays.asList(new Character[]{'a'});
//        final Collection<Character> output =
//                Functional.choose(
//                        new Function<String, Option<Character>>() {
//
//                            public Option<Character> apply(final String str) {
//                                return str.startsWith("a") ? Option.toOption('a') : Option.<Character>None();
//                            }
//                        }
//                        ,input
//                );
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void chooseTest3() throws OptionNoValueAccessException
//    {
//        final IntList input = new int[] {1, 2, 3, 4, 5});
//        final IntList expected = new int[] {1, 3, 5});
//        final IntList output = Functional.choose(
//                new Function<Integer, Option<Integer>>() {
//
//                    public Option<Integer> apply(final Integer i) {
//                        return i%2 != 0 ? Option.toOption(i) : Option.<Integer>None();
//                    }
//                }, input)  ;
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    /*[Test]
//    public void curryTest1()
//    {
//        Function<int, int, bool> f = (i, j) => i > j;
//        Function<int, Function<int, bool>> g = i => j => f(i, j);
//        bool t = 10.in(g(5));
//        Assert.IsFalse(t);
//    }*/
//
//    /*[Test]
//    public void curryTest2()
//    {
//        Function<int, int, bool> f = (i, j) => i < j;
//        Function<int, Function<int, bool>> g = i => j => f(i, j);
//        bool t = 10.in(g(5));
//        Assert.IsTrue(t);
//    }*/
//
//    /*[Test]
//    public void compositionTest1()
//    {
//        Function<int, int, int> add = (x, y) => x + y;
//        Function<int, Function<int, int>> add1 = y => x => add(x, y);
//        Function<int, int, int> mult = (x, y) => x*y;
//        Function<int, Function<int, int>> mult1 = y => x => mult(x, y);
//        int expected = mult(add(1, 2), 3);
//        Assert.AreEqual(9, expected);
//        Assert.AreEqual(expected, 2.in(add1(1).then(mult1(3))));
//    }*/
//
//    @Test
//    public void unzipTest1()
//    {
//        final Collection<Pair<String,Integer>> input =
//                new ArrayList<Pair<String,Integer>>();
//        input.add(Pair.of("1", 1));
//        input.add(Pair.of("2", 2));
//
//        final Pair<Collection<String>,IntList> expected =
//                Pair.of(
//                        Arrays.asList(new String[]{"1", "2"}),
//                        new int[]{1,2}));
//
//        final Pair<List<String>,List<Integer>> output = Functional.unzip(input);
//
//        AssertIterable.assertIterableEquals(expected.getLeft(), output.getLeft());
//        AssertIterable.assertIterableEquals(expected.getValue1(), output.getValue1());
//    }
//
//    @Test
//    public void unzip3Test1()
//    {
//        final Collection<Triple<String,Integer,String>> input =
//                new ArrayList<Triple<String,Integer,String>>();
//        input.add(Triple.of("1", 1,"L"));
//        input.add(Triple.of("2", 2,"M"));
//        input.add(Triple.of("3", 3,"K"));
//
//        final Triple<Collection<String>,IntList,Collection<String>> expected =
//                Triple.of(
//                        Arrays.asList(new String[]{"1", "2","3"}),
//                        new int[]{1,2,3}),
//                        Arrays.asList(new String[]{"L","M","K"}));
//
//        final Triple<List<String>,List<Integer>,List<String>> output = Functional.unzip3(input);
//
//        AssertIterable.assertIterableEquals(expected.getLeft(), output.getLeft());
//        AssertIterable.assertIterableEquals(expected.getValue1(), output.getValue1());
//        AssertIterable.assertIterableEquals(expected.getRight(), output.getRight());
//    }
//
//    @Test
//    public void zipTest1()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//
//        final Collection<Pair<Integer,Character>> expected = new ArrayList<Pair<Integer, Character>>();
//        expected.add(Pair.of(1, 'a'));
//        expected.add(Pair.of(2, 'b'));
//        expected.add(Pair.of(3, 'c'));
//        expected.add(Pair.of(4, 'd'));
//        expected.add(Pair.of(5, 'e'));
//
//        final Collection<Pair<Integer,Character>> output = Functional.zip(input1, input2);
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void zipNoExceptionTest1()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//
//        final Collection<Pair<Integer,Character>> expected = new ArrayList<Pair<Integer, Character>>();
//        expected.add(Pair.of(1, 'a'));
//        expected.add(Pair.of(2, 'b'));
//        expected.add(Pair.of(3, 'c'));
//        expected.add(Pair.of(4, 'd'));
//        expected.add(Pair.of(5, 'e'));
//
//        final Collection<Pair<Integer,Character>> output = Functional.noException.zip(input1, input2);
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void zipNoExceptionTest2()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5, 6});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//
//        final Collection<Pair<Integer,Character>> expected = new ArrayList<Pair<Integer, Character>>();
//        expected.add(Pair.of(1, 'a'));
//        expected.add(Pair.of(2, 'b'));
//        expected.add(Pair.of(3, 'c'));
//        expected.add(Pair.of(4, 'd'));
//        expected.add(Pair.of(5, 'e'));
//
//        final Collection<Pair<Integer,Character>> output = Functional.noException.zip(input1, input2);
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqZipTest1()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//
//        final Collection<Pair<Integer,Character>> expected = new ArrayList<Pair<Integer, Character>>();
//        expected.add(Pair.of(1, 'a'));
//        expected.add(Pair.of(2, 'b'));
//        expected.add(Pair.of(3, 'c'));
//        expected.add(Pair.of(4, 'd'));
//        expected.add(Pair.of(5, 'e'));
//
//        final Collection<Pair<Integer,Character>> output = Functional.toList(Functional.seq.zip(input1, input2));
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqZipTest2()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5});
//        final Collection<Character> input2 = Arrays.asList(new Character[]{'a', 'b', 'c', 'd', 'e'});
//
//        final Collection<Pair<Integer,Character>> expected = new ArrayList<Pair<Integer, Character>>();
//        expected.add(Pair.of(1, 'a'));
//        expected.add(Pair.of(2, 'b'));
//        expected.add(Pair.of(3, 'c'));
//        expected.add(Pair.of(4, 'd'));
//        expected.add(Pair.of(5, 'e'));
//
//        final Collection<Pair<Integer,Character>> output = Functional.toList(Functional.seq.zip(input1, input2));
//        final Iterator<Pair<Integer,Character>> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final Pair<Integer,Character> element : expected)
//        {
//            final Pair<Integer,Character> next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void seqZipFnTest1()
//    {
//        final IntList input = new int[] {1, 2, 3, 4, 5});
//
//        final Collection<Pair<Integer,String>> expected = new ArrayList<Pair<Integer, String>>();
//        expected.add(Pair.of(1, "1"));
//        expected.add(Pair.of(2, "2"));
//        expected.add(Pair.of(3, "3"));
//        expected.add(Pair.of(4, "4"));
//        expected.add(Pair.of(5, "5"));
//
//        final List<Pair<Integer, String>> output = Functional.toList(Functional.seq.zip(Functional.<Integer>identity(), Functional.dStringify(), input));
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void zip3Test1()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//        final Collection<Double> input3 = Arrays.asList(new Double[] {1.0, 2.0, 2.5, 3.0, 3.5});
//
//        final Collection<Triple<Integer,Character,Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
//        expected.add(Triple.of(1, 'a', 1.0));
//        expected.add(Triple.of(2, 'b', 2.0));
//        expected.add(Triple.of(3, 'c', 2.5));
//        expected.add(Triple.of(4, 'd', 3.0));
//        expected.add(Triple.of(5, 'e', 3.5));
//
//        final Collection<Triple<Integer,Character,Double>> output = Functional.zip3(input1, input2, input3);
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void zip3NoExceptionTest1()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//        final Collection<Double> input3 = Arrays.asList(new Double[] {1.0, 2.0, 2.5, 3.0, 3.5});
//
//        final Collection<Triple<Integer,Character,Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
//        expected.add(Triple.of(1, 'a', 1.0));
//        expected.add(Triple.of(2, 'b', 2.0));
//        expected.add(Triple.of(3, 'c', 2.5));
//        expected.add(Triple.of(4, 'd', 3.0));
//        expected.add(Triple.of(5, 'e', 3.5));
//
//        final Collection<Triple<Integer,Character,Double>> output = Functional.noException.zip3(input1, input2, input3);
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void zip3NoExceptionTest2()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5, 6});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//        final Collection<Double> input3 = Arrays.asList(new Double[] {1.0, 2.0, 2.5, 3.0, 3.5});
//
//        final Collection<Triple<Integer,Character,Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
//        expected.add(Triple.of(1, 'a', 1.0));
//        expected.add(Triple.of(2, 'b', 2.0));
//        expected.add(Triple.of(3, 'c', 2.5));
//        expected.add(Triple.of(4, 'd', 3.0));
//        expected.add(Triple.of(5, 'e', 3.5));
//
//        final Collection<Triple<Integer,Character,Double>> output = Functional.noException.zip3(input1, input2, input3);
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqZip3Test1()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//        final Collection<Double> input3 = Arrays.asList(new Double[] {1.0, 2.0, 2.5, 3.0, 3.5});
//
//        final Collection<Triple<Integer,Character,Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
//        expected.add(Triple.of(1, 'a', 1.0));
//        expected.add(Triple.of(2, 'b', 2.0));
//        expected.add(Triple.of(3, 'c', 2.5));
//        expected.add(Triple.of(4, 'd', 3.0));
//        expected.add(Triple.of(5, 'e', 3.5));
//
//        final Collection<Triple<Integer,Character,Double>> output = Functional.toList(Functional.seq.zip3(input1, input2, input3));
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqZip3Test2()
//    {
//        final IntList input1 = new int[] {1, 2, 3, 4, 5});
//        final Collection<Character> input2 = Arrays.asList(new Character[] {'a', 'b', 'c', 'd', 'e'});
//        final Collection<Double> input3 = Arrays.asList(new Double[] {1.0, 2.0, 2.5, 3.0, 3.5});
//
//        final Collection<Triple<Integer,Character,Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
//        expected.add(Triple.of(1, 'a', 1.0));
//        expected.add(Triple.of(2, 'b', 2.0));
//        expected.add(Triple.of(3, 'c', 2.5));
//        expected.add(Triple.of(4, 'd', 3.0));
//        expected.add(Triple.of(5, 'e', 3.5));
//
//        final Collection<Triple<Integer,Character,Double>> output = Functional.toList(Functional.seq.zip3(input1, input2, input3));
//        final Iterator<Triple<Integer,Character,Double>> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final Triple<Integer,Character,Double> element : expected)
//        {
//            final Triple<Integer,Character,Double> next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    /*[ExpectedException(typeof(ArgumentException))]
//        [Test]
//    public void zip3Test2()
//    {
//        var input1 = new[] { 1, 2, 3, 4, 5 };
//        var input2 = new[] { 'a', 'b', 'd', 'e' };
//        var input3 = new[] { 1.0, 2.0, 2.5, 3.0, 3.5 };
//        var expected = new[]
//        {
//            Tuple.Create(1, 'a', 1.0), Tuple.Create(2, 'b', 2.0), Tuple.Create(3, 'c', 2.5),
//                    Tuple.Create(4, 'd', 3.0), Tuple.Create(5, 'e', 3.5)
//        }.ToList();
//
//        var output = Functional.Zip3(input1, input2, input3).ToList();
//
//        CollectionAssert.AreEquivalent(expected, output);
//    }*/
//
//    /*[ExpectedException(typeof(ArgumentException))]
//        [Test]
//    public void zip3Test3()
//    {
//        var input1 = new[] { 1, 2, 3, 4, 5 };
//        var input2 = new[] { 'a', 'b', 'c', 'd', 'e' };
//        var input3 = new[] { 1.0, 2.0, 2.5, 3.5 };
//        var expected = new[]
//        {
//            Tuple.Create(1, 'a', 1.0), Tuple.Create(2, 'b', 2.0), Tuple.Create(3, 'c', 2.5),
//                    Tuple.Create(4, 'd', 3.0), Tuple.Create(5, 'e', 3.5)
//        }.ToList();
//
//        var output = Functional.Zip3(input1, input2, input3).ToList();
//
//        CollectionAssert.AreEquivalent(expected, output);
//    }*/
//
//    @Test
//    public void findTest1()
//    {
//        final String trueMatch = "6";
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Collection<String> ls = Functional.map(Functional.<Integer>dStringify(), li);
//        assertThat().isEqualTo(trueMatch,
//                Functional.find(
//                        new Function<String, Boolean>() {
//
//                            public Boolean apply(final String s) {
//                                return s.equals(trueMatch);
//                            }
//                        }, ls));
//    }
//
//    @Test(expected = NoSuchElementException.class)
//    public void findTest2()
//    {
//        final String falseMatch = "7";
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Collection<String> ls = Functional.map(Functional.<Integer>dStringify(), li);
//        Functional.find(
//                new Function<String, Boolean>() {
//
//                    public Boolean apply(final String s) {
//                        return s.equals(falseMatch);
//                    }
//                }, ls);
//    }
//
//    @Test
//    public void findNoExceptionTest1()
//    {
//        final String trueMatch = "6";
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Collection<String> ls = Functional.map(Functional.<Integer>dStringify(), li);
//        assertThat().isEqualTo(trueMatch,
//                Functional.noException.find(
//                        new Function<String, Boolean>() {
//
//                            public Boolean apply(final String s) {
//                                return s.equals(trueMatch);
//                            }
//                        }, ls).Some());
//    }
//
//    @Test
//    public void findNoExceptionTest2()
//    {
//        final String falseMatch = "7";
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Collection<String> ls = Functional.map(Functional.<Integer>dStringify(), li);
//        assertThat().isTrue()(Functional.noException.find(
//                new Function<String, Boolean>() {
//
//                    public Boolean apply(final String s) {
//                        return s.equals(falseMatch);
//                    }
//                }, ls).isNone());
//    }
//
//    @Test
//    public void findIndexTest1()
//    {
//        final String trueMatch = "6";
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Collection<String> ls = Functional.map(Functional.<Integer>dStringify(), li);
//        assertThat().isEqualTo(2,
//                Functional.findIndex(
//                        new Function<String, Boolean>() {
//
//                            public Boolean apply(final String s) {
//                                return s.equals(trueMatch);
//                            }
//                        }, ls));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void findIndexTest2()
//    {
//        final String falseMatch = "7";
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Collection<String> ls = Functional.map(Functional.<Integer>dStringify(), li);
//        Functional.findIndex(
//                new Function<String, Boolean>() {
//
//                    public Boolean apply(final String s) {
//                        return s.equals(falseMatch);
//                    }
//                }, ls);
//    }
//
//    @Test
//    public void findIndexNoExceptionTest1()
//    {
//        final String trueMatch = "6";
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Collection<String> ls = Functional.map(Functional.<Integer>dStringify(), li);
//        assertThat().isEqualTo((Integer) 2,
//                Functional.noException.findIndex(
//                        new Function<String, Boolean>() {
//
//                            public Boolean apply(final String s) {
//                                return s.equals(trueMatch);
//                            }
//                        }, ls).Some());
//    }
//
//    @Test
//    public void findIndexNoExceptionTest2()
//    {
//        final String falseMatch = "7";
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Collection<String> ls = Functional.map(Functional.<Integer>dStringify(), li);
//        assertThat().isTrue()(Functional.noException.findIndex(
//                new Function<String, Boolean>() {
//
//                    public Boolean apply(final String s) {
//                        return s.equals(falseMatch);
//                    }
//                }, ls).isNone());
//    }
//
//    @Test
//    public void pickTest1()
//    {
//        final int trueMatch = 6;
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        assertThat().isEqualTo(((Integer) trueMatch).toString(),
//                Functional.pick(
//                        new Function<Integer, Option<String>>() {
//
//                            public Option<String> apply(final Integer a) {
//                                return a == trueMatch ? Option.toOption(a.toString()) : Option.<String>None();
//                            }
//                        }, li));
//    }
//
//    @Test(expected = NoSuchElementException.class)
//    public void pickTest2()
//    {
//        final int falseMatch = 7;
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        Functional.pick(
//                new Function<Integer, Option<String>>() {
//
//                    public Option<String> apply(final Integer a) {
//                        return a == falseMatch ? Option.toOption(a.toString()) : Option.<String>None();
//                    }
//                }, li);
//    }
//
//    @Test
//    public void pickNoExceptionTest1()
//    {
//        final int trueMatch = 6;
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        assertThat().isEqualTo(((Integer) trueMatch).toString(),
//                Functional.noException.pick(
//                        new Function<Integer, Option<String>>() {
//
//                            public Option<String> apply(final Integer a) {
//                                return a == trueMatch ? Option.toOption(a.toString()) : Option.<String>None();
//                            }
//                        }, li).Some());
//    }
//
//    @Test
//    public void pickNoExceptionTest2()
//    {
//        final int falseMatch = 7;
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        assertThat().isTrue()(Functional.noException.pick(
//                new Function<Integer, Option<String>>() {
//
//                    public Option<String> apply(final Integer a) {
//                        return a == falseMatch ? Option.toOption(a.toString()) : Option.<String>None();
//                    }
//                }, li).isNone());
//    }
//
//    @Test
//    public void curryFnTest1()
//    {
//        final int state=0;
//        final Function<Integer,Boolean> testForPosInts = new Function<Integer, Boolean>() {
//             public Boolean apply(final Integer integer) { return integer > state; }
//        };
//
//        final Function<Iterable<Integer>,List<Integer>> curriedTestForPosInts = Functional.filter(testForPosInts);
//
//        final IntList l = new int[]{-3,-2,0,1,5});
//        final IntList posInts = curriedTestForPosInts.apply(l);
//
//        final IntList expected = new int[]{1, 5});
//        AssertIterable.assertIterableEquals(expected, posInts);
//    }
//
//    @Test
//    public void mapDictTest1()
//    {
//        final IntList input = new int[]{1, 2, 3, 4, 5});
//        final Map<String,String> output = Functional.map_dict(
//                new Function<Integer, Map.Entry<String, String>>() {
//
//                    public Map.Entry<String, String> apply(final Integer i) {
//                        return new Map.Entry<String, String>() {
//                            public String setValue(String v) {
//                                throw new UnsupportedOperationException();
//                            }
//
//                            public String getValue() {
//                                return Functional.<Integer>dStringify().apply(i);
//                            }
//
//                            public String getKey() {
//                                return Functional.<Integer>dStringify().apply(i);
//                            }
//                        };
//                    }
//                }, input);
//
//        final List<String> keys = new ArrayList<String>(output.keySet());
//        Collections.sort(keys);
//        AssertIterable.assertIterableEquals(Arrays.asList(new String[]{"1", "2", "3", "4", "5"}), keys);
//    }
//
//    @Test
//    public void toListTest1()
//    {
//        final Iterable<Integer> output = Functional.init(DoublingGenerator, 5);
//        final List<Integer> output_ints = Functional.toList(output);
//        AssertIterable.assertIterableEquals(new int[]{2,4,6,8,10}), output_ints);
//    }
//
//    public static Function<Integer,List<Integer>> repeat(final int howMany)
//    {
//        return new Function<Integer, List<Integer>>() {
//
//            public List<Integer> apply(final Integer integer) {
//                return Functional.init(
//                        new Function<Integer, Integer>() {
//
//                            public Integer apply(final Integer counter) {
//                                return integer;
//                            }
//                        }, howMany);
//            }
//        };
//    }
//
//    @Test
//    public void collectTest1()
//    {
//        final List<Integer> input = Functional.init(DoublingGenerator, 5);
//        final List<Integer> output = Functional.collect(repeat(3), input);
//        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqCollectTest1()
//    {
//        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
//        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
//        final List<Integer> expected = Arrays.asList(2,2,2,4,4,4,6,6,6,8,8,8,10,10,10);
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void seqCollectTest2()
//    {
//        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
//        final Iterable<Integer> output1 = Functional.seq.collect(repeat(3), input);
//        final Iterable<Integer> output2 = output1;
//        final List<Integer> expected = Arrays.asList(2,2,2,4,4,4,6,6,6,8,8,8,10,10,10);
//        AssertIterable.assertIterableEquals(expected, output1);
//        AssertIterable.assertIterableEquals(expected, output2);
//    }
//
//    @Test
//    public void seqCollectTest3()
//    {
//        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
//        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
//        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
//        final Iterator<Integer> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final int element : expected)
//        {
//            final int next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void takeNandYieldTest1()
//    {
//        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
//        final Pair<List<Integer>,Iterable<Integer>> output = Functional.takeNAndYield(input, 2);
//        final List<Integer> expectedList = Arrays.asList(2,4);
//        final List<Integer> expectedRemainder = Arrays.asList(6, 8, 10);
//        AssertIterable.assertIterableEquals(expectedList,output.getLeft());
//        AssertIterable.assertIterableEquals(expectedRemainder, output.getValue1());
//    }
//
//    @Test
//    public void takeNandYieldTest2()
//    {
//        final Iterable<Integer> input = Functional.seq.init(DoublingGenerator, 5);
//        final Pair<List<Integer>,Iterable<Integer>> output = Functional.takeNAndYield(input, 0);
//        final List<Integer> expectedList = Arrays.asList();
//        final List<Integer> expectedRemainder = Arrays.asList(2,4,6,8,10);
//        AssertIterable.assertIterableEquals(expectedList,output.getLeft());
//        AssertIterable.assertIterableEquals(expectedRemainder,output.getValue1());
//    }
//
//    @Test
//    public void recFilterTest1()
//    {
//        final IntList l = Functional.init(DoublingGenerator, 5);
//        final Iterable<Integer> oddElems = Functional.rec.filter(Functional.isOdd, l);
//
//        AssertIterable.assertIterableEquals(new ArrayList<Integer>(), oddElems);
//    }
//
//    @Test
//    public void recMapTest1()
//    {
//        final IntList input = new int[]{1, 2, 3, 4, 5});
//        final Iterable<String> output = Functional.rec.map(Functional.<Integer>dStringify(), input);
//        AssertIterable.assertIterableEquals(Arrays.asList("1", "2", "3", "4", "5"), output);
//    }
//
//    @Test
//    public void recFoldvsMapTest1()
//    {
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final String s1 = Functional.join(",", Functional.rec.map(Functional.<Integer>dStringify(), li));
//        assertThat().isEqualTo("2,4,6,8,10", s1);
//        final String s2 = Functional.rec.fold(
//                new BiFunction<String, Integer, String>() {
//
//                    public String apply(final String s1, final Integer s2) {
//                        return csv(s1, s2);
//                    }
//                }, "", li);
//        assertThat().isEqualTo(s1, s2);
//    }
//
//    @Test
//    public void ifTest1()
//    {
//        final Integer input = 1;
//        final Iterable2<Integer> i = IterableHelper.asList(0, 1, 2);
//        final Iterable2<Integer> result = i.map(
//                new Function<Integer, Integer>() {
//                    public Integer apply(final Integer ii) {
//                        return Functional.If(input, Functional.greaterThan(ii), DoublingGenerator, TriplingGenerator);
//                    }
//                });
//        final List<Integer> expected = Arrays.asList(2, 3, 3);
//        AssertIterable.assertIterableEquals(expected, result);
//    }
//
//    @Test
//    public void switchTest1()
//    {
//        assertThat().isEqualTo(new Integer(1),
//                Functional.Switch(new Integer(10),
//                        Arrays.asList(
//
//                                Functional.toCase(Functional.lessThan(new Integer(5)), Functional.constant(new Integer(-1))),
//                                Functional.toCase(Functional.greaterThan(new Integer(5)), Functional.constant(new Integer(1)))
//                        ), Functional.constant(new Integer(0))));
//    }
//
//    @Test
//    public void setFilterTest1()
//    {
//        final IntList l = Functional.init(DoublingGenerator,5);
//        final Set<Integer> sl = new HashSet<Integer>(l);
//        final Set<Integer> oddElems = Functional.set.filter(Functional.isOdd, sl);
//
//        AssertIterable.assertIterableEquals(new ArrayList<Integer>(), oddElems);
//    }
//
//    @Test
//    public void setFilterTest2()
//    {
//        final IntList l = Functional.init(DoublingGenerator,5);
//        final Set<Integer> sl = new HashSet<Integer>(l);
//        final Set<Integer> evenElems = Functional.set.filter(Functional.isEven, sl);
//
//        final IntList expected = new int[]{2,4,6,8,10});
//        assertThat().isTrue()(expected.containsAll(evenElems));
//        assertThat().isTrue()(evenElems.containsAll(expected));
//    }
//
//    @Test
//    public void setFilterTest3()
//    {
//        final IntList l = Functional.init(DoublingGenerator,5);
//        final Integer limit = 5;
//        final Set<Integer> sl = new HashSet<Integer>(l);
//        final Set<Integer> highElems = Functional.set.filter(
//                new Function<Integer,Boolean>()
//                {
//
//                    public Boolean apply(final Integer a) { return a > limit;}
//                }, sl);
//
//        final IntList expected = new int[]{6,8,10});
//        assertThat().isTrue()(expected.containsAll(highElems));
//        assertThat().isTrue()(highElems.containsAll(expected));
//    }
//
//    @Test
//    public void setFilterTest4()
//    {
//        final IntList li = Functional.init(DoublingGenerator, 5);
//        final Integer limit = 10;
//        final Set<Integer> sl = new HashSet<Integer>(li);
//        final Set<Integer> output = Functional.set.filter(
//                new Function<Integer, Boolean>() {
//
//                    public Boolean apply(final Integer a) {
//                        return a > limit;
//                    }
//                }, sl);
//
//        assertThat().isFalse()(output.iterator().hasNext());
//    }
//
//    @Test
//    public void setFilterTest5()
//    {
//        final IntList li = Functional.init(DoublingGenerator, 10);
//        final IntList expected = new int[]{4, 8, 12, 16, 20});
//        final Set<Integer> sl = new HashSet<Integer>(li);
//        final Set<Integer> output = Functional.set.filter(
//                new Function<Integer,Boolean>()
//                {
//                     public Boolean apply(final Integer a) {return a % 4 ==0;}
//                }, sl);
//
//        assertThat().isTrue()(expected.containsAll(output));
//        assertThat().isTrue()(output.containsAll(expected));
//    }
//
//    @Test
//    public void setCollectTest1()
//    {
//        final Iterable<Integer> input = Functional.init(DoublingGenerator, 5);
//        final Set<Integer> output = Functional.set.collect(repeat(3), input);
//        final Set<Integer> expected = new HashSet<Integer>(Arrays.asList(2,4,6,8,10));
//
//        assertThat().isTrue()(expected.containsAll(output));
//        assertThat().isTrue()(output.containsAll(expected));
//    }
//
//    @Test
//    public void setCollectTest2()
//    {
//        final Iterable<Integer> input = Functional.init(DoublingGenerator, 5);
//        final Set<Integer> output1 = Functional.set.collect(repeat(3), input);
//        final Set<Integer> output2 = output1;
//        final Set<Integer> expected = new HashSet<Integer>(Arrays.asList(2,4,6,8,10));
//
//        assertThat().isTrue()(expected.containsAll(output1));
//        assertThat().isTrue()(output1.containsAll(expected));
//        assertThat().isTrue()(expected.containsAll(output2));
//        assertThat().isTrue()(output2.containsAll(expected));
//    }
//
//    @Test
//    public void setMapTest1()
//    {
//        final Set<Integer> input = new HashSet<Integer>(new int[]{1, 2, 3, 4, 5}));
//        final Set<String> output = Functional.set.map(Functional.<Integer>dStringify(), input);
//        final Set<String> expected = new HashSet<String>(Arrays.asList("1","2","3","4","5"));
//        assertThat().isTrue()(expected.containsAll(output));
//        assertThat().isTrue()(output.containsAll(expected));
//    }
//
//    @Test
//    public void setConcatTest1()
//    {
//        final Set<Integer> input = new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
//        final Function<Integer,Integer> doubler = new Function<Integer, Integer>() {
//
//            public Integer apply(final Integer i) {
//                return i * 2;
//            }
//        };
//        final Set<String> expected = new HashSet<String>(Arrays.asList("1","2","3","4","5","6","8","10"));
//
//        final Set<String> strs = Functional.set.map(Functional.<Integer>dStringify(), input);
//        final Set<String> output = Functional.set.concat(strs, Functional.set.map(Functional.<Integer>dStringify(), Functional.set.map(doubler, input)));
//
//        assertThat().isTrue()(expected.containsAll(output));
//        assertThat().isTrue()(output.containsAll(expected));
//    }
//
//    @Test
//    public void setIntersectionTest1()
//    {
//        final Set<Integer> input1 = new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
//        final Set<Integer> input2 = new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
//
//        final Set<Integer> intersection = Functional.set.intersection(input1,input2);
//        assertThat().isTrue()(intersection.containsAll(input1));
//        assertThat().isTrue()(input1.containsAll(intersection));
//    }
//
//    @Test
//    public void setIntersectionTest2()
//    {
//        final Set<Integer> input1 = new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
//        final Set<Integer> input2 = new HashSet<Integer>(Arrays.asList(4,5,6,7,8));
//        final Set<Integer> expected = new HashSet<Integer>(Arrays.asList(4,5));
//
//        final Set<Integer> intersection = Functional.set.intersection(input1, input2);
//        assertThat().isTrue()(intersection.containsAll(expected));
//        assertThat().isTrue()(expected.containsAll(intersection));
//    }
//
//    @Test
//    public void setIntersectionTest3()
//    {
//        final Set<Integer> input1 = new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
//        final Set<Integer> input2 = new HashSet<Integer>(Arrays.asList(6,7,8));
//        final Set<Integer> expected = Collections.emptySet();
//
//        final Set<Integer> intersection = Functional.set.intersection(input1, input2);
//        assertThat().isTrue()(intersection.containsAll(expected));
//        assertThat().isTrue()(expected.containsAll(intersection));
//    }
//
//    @Test
//    public void setAsymmetricDifferenceTest1()
//    {
//        final Set<Integer> input1 = new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
//        final Set<Integer> input2 = new HashSet<Integer>(Arrays.asList(4,5,6,7,8));
//        final Set<Integer> expected = new HashSet<Integer>(Arrays.asList(1,2,3));
//
//        final Set<Integer> diff = Functional.set.asymmetricDifference(input1, input2);
//        assertThat().isTrue()(diff.containsAll(expected));
//        assertThat().isTrue()(expected.containsAll(diff));
//        assertThat().isFalse()(input2.containsAll(diff));
//    }
//
//    @Test
//    public void setAsymmetricDifferenceTest2()
//    {
//        final Set<Integer> input1 = new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
//        final Set<Integer> input2 = new HashSet<Integer>(Arrays.asList(6,7,8));
//        final Set<Integer> expected = Collections.emptySet();
//
//        final Set<Integer> diff = Functional.set.asymmetricDifference(input1, input2);
//        assertThat().isTrue()(diff.containsAll(input1));
//        assertThat().isTrue()(input1.containsAll(diff));
//    }
//
//    @Test
//    public void appendTest1()
//    {
//        final Integer i = 1;
//        final IntList l = Functional.init(DoublingGenerator,5);
//        final Iterable<Integer> output = Functional.append(i, l);
//        final List<Integer> expected = Arrays.asList(1, 2, 4, 6, 8, 10);
//
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void unfoldTest1()
//    {
//        final Integer seed = 0;
//        final Function<Integer,Pair<Integer,Integer>> unspool = new Function<Integer, Pair<Integer, Integer>>() {
//
//            public Pair<Integer, Integer> apply(final Integer integer) {
//                return Pair.of(integer+1,integer+1);
//            }
//        };
//        final Function<Integer,Boolean> finished = new Function<Integer, Boolean>() {
//
//            public Boolean apply(final Integer integer) {
//                return integer==10;
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//        final List<Integer> output = Functional.unfold(unspool,finished,seed);
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void unfoldAsDoublingGeneratorTest1()
//    {
//        final Integer seed = 1;
//        final Function<Integer,Pair<Integer,Integer>> doubler = new Function<Integer, Pair<Integer, Integer>>() {
//
//            public Pair<Integer, Integer> apply(final Integer integer) {
//                return Pair.of(integer * 2, integer+1);
//            }
//        };
//        final Function<Integer,Boolean> finished = new Function<Integer, Boolean>() {
//
//            public Boolean apply(final Integer integer) {
//                return integer>10;
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(2,4,6,8,10,12,14,16,18,20);
//        final List<Integer> output = Functional.unfold(doubler,finished,seed);
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void unfoldAsDoublingGeneratorTest2()
//    {
//        final Integer seed = 1;
//        final Function<Integer,Option<Pair<Integer,Integer>>> doubler = new Function<Integer, Option<Pair<Integer, Integer>>>() {
//
//            public Option<Pair<Integer, Integer>> apply(final Integer integer) {
//                return integer>10 ? Option.<Pair<Integer, Integer>>None() : Option.toOption(Pair.of(integer * 2, integer+1));
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(2,4,6,8,10,12,14,16,18,20);
//        final List<Integer> output = Functional.unfold(doubler,seed);
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqUnfoldTest1()
//    {
//        final Integer seed = 0;
//        final Function<Integer,Pair<Integer,Integer>> unspool = new Function<Integer, Pair<Integer, Integer>>() {
//
//            public Pair<Integer, Integer> apply(final Integer integer) {
//                return Pair.of(integer+1,integer+1);
//            }
//        };
//        final Function<Integer,Boolean> finished = new Function<Integer, Boolean>() {
//
//            public Boolean apply(final Integer integer) {
//                return integer==10;
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
//        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqUnfoldTest2()
//    {
//        final Integer seed = 0;
//        final Function<Integer,Pair<Integer,Integer>> unspool = new Function<Integer, Pair<Integer, Integer>>() {
//
//            public Pair<Integer, Integer> apply(final Integer integer) {
//                return Pair.of(integer+1,integer+1);
//            }
//        };
//        final Function<Integer,Boolean> finished = new Function<Integer, Boolean>() {
//
//            public Boolean apply(final Integer integer) {
//                return integer==10;
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
//        final Iterator<Integer> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final int element : expected)
//        {
//            final int next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void seqUnfoldAsDoublingGeneratorTest1()
//    {
//        final Integer seed = 1;
//        final Function<Integer,Pair<Integer,Integer>> doubler = new Function<Integer, Pair<Integer, Integer>>() {
//
//            public Pair<Integer, Integer> apply(final Integer integer) {
//                return Pair.of(integer * 2, integer+1);
//            }
//        };
//        final Function<Integer,Boolean> finished = new Function<Integer, Boolean>() {
//
//            public Boolean apply(final Integer integer) {
//                return integer>10;
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(2,4,6,8,10,12,14,16,18,20);
//        final Iterable<Integer> output = Functional.seq.unfold(doubler,finished,seed);
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqUnfoldAsDoublingGeneratorTest2()
//    {
//        final Integer seed = 1;
//        final Function<Integer,Option<Pair<Integer,Integer>>> doubler = new Function<Integer, Option<Pair<Integer, Integer>>>() {
//
//            public Option<Pair<Integer, Integer>> apply(final Integer integer) {
//                return integer>10 ? Option.<Pair<Integer, Integer>>None() : Option.toOption(Pair.of(integer * 2, integer+1));
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(2,4,6,8,10,12,14,16,18,20);
//        final Iterable<Integer> output = Functional.seq.unfold(doubler,seed);
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqUnfoldAsDoublingGeneratorTest3()
//    {
//        final Integer seed = 1;
//        final Function<Integer,Option<Pair<Integer,Integer>>> doubler = new Function<Integer, Option<Pair<Integer, Integer>>>() {
//
//            public Option<Pair<Integer, Integer>> apply(final Integer integer) {
//                return integer>10 ? Option.<Pair<Integer, Integer>>None() : Option.toOption(Pair.of(integer * 2, integer+1));
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(2,4,6,8,10,12,14,16,18,20);
//        final Iterable<Integer> output = Functional.seq.unfold(doubler,seed);
//        final Iterator<Integer> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final int element : expected)
//        {
//            final int next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void recUnfoldAsDoublingGeneratorTest1()
//    {
//        final Integer seed = 1;
//        final Function<Integer,Pair<Integer,Integer>> doubler = new Function<Integer, Pair<Integer, Integer>>() {
//
//            public Pair<Integer, Integer> apply(final Integer integer) {
//                return Pair.of(integer * 2, integer+1);
//            }
//        };
//        final Function<Integer,Boolean> finished = new Function<Integer, Boolean>() {
//
//            public Boolean apply(final Integer integer) {
//                return integer>10;
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(2,4,6,8,10,12,14,16,18,20);
//        final List<Integer> output = Functional.rec.unfold(doubler, finished, seed);
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void recUnfoldAsDoublingGeneratorTest2()
//    {
//        final Integer seed = 1;
//        final Function<Integer,Option<Pair<Integer,Integer>>> doubler = new Function<Integer, Option<Pair<Integer, Integer>>>() {
//
//            public Option<Pair<Integer, Integer>> apply(final Integer integer) {
//                return integer>10 ? Option.<Pair<Integer, Integer>>None() : Option.toOption(Pair.of(integer * 2, integer+1));
//            }
//        };
//
//        final List<Integer> expected = Arrays.asList(2,4,6,8,10,12,14,16,18,20);
//        final List<Integer> output = Functional.rec.unfold(doubler, seed);
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void mapInTermsOfFoldTest1()
//    {
//        final IntList input = new int[]{1, 2, 3, 4, 5});
//        final Collection<String> output = Functional.inTermsOfFold.map(Functional.<Integer>dStringify(), input);
//        assertThat().containsExactly(new String[]{"1", "2", "3", "4", "5"}, output.toArray());
//    }
//
//    @Test
//    public void filterInTermsOfFoldTest1()
//    {
//        final IntList l = Functional.init(DoublingGenerator, 5);
//        final Iterable<Integer> oddElems = Functional.inTermsOfFold.filter(Functional.isOdd, l);
//        AssertIterable.assertIterableEquals(new ArrayList<Integer>(), oddElems);
//    }
//
//    @Test
//    public void initInTermsOfUnfoldTest1()
//    {
//        final IntList output = Functional.inTermsOfFold.init(new Function<Integer, Integer>() {
//
//            public Integer apply(final Integer integer) {
//                return integer * 2;
//            }
//        }, 5);
//        assertThat().containsExactly(new Integer[]{2, 4, 6, 8, 10}, output.toArray());
//    }
//
//    @Test
//    public void skipTest1()
//    {
//        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
//        {
//            final List<Integer> expected = Arrays.asList(1,2,3,4,5);
//            final List<Integer> output = Functional.skip(0,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(2,3,4,5);
//            final List<Integer> output = Functional.skip(1,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(3,4,5);
//            final List<Integer> output = Functional.skip(2,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(4,5);
//            final List<Integer> output = Functional.skip(3,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(5);
//            final List<Integer> output = Functional.skip(4,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = new ArrayList<Integer>();
//            final List<Integer> output = Functional.skip(5,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = new ArrayList<Integer>();
//            final List<Integer> output = Functional.skip(6,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void skipTest2()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
//        Functional.skip(-1, input);
//    }
//
//    @Test
//    public void seqSkipTest1()
//    {
//        final List<Integer> l = Arrays.asList(1,2,3,4,5);
//        {
//            final List<Integer> expected = Arrays.asList(1,2,3,4,5);
//            final Iterable<Integer> output = Functional.seq.skip(0, l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(2,3,4,5);
//            final Iterable<Integer> output = Functional.seq.skip(1,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(3,4,5);
//            final Iterable<Integer> output = Functional.seq.skip(2,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(4,5);
//            final Iterable<Integer> output = Functional.seq.skip(3,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(5);
//            final Iterable<Integer> output = Functional.seq.skip(4,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = new ArrayList<Integer>();
//            final Iterable<Integer> output = Functional.seq.skip(5,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = new ArrayList<Integer>();
//            final Iterable<Integer> output = Functional.seq.skip(6,l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void seqSkipTest2()
//    {
//        final List<Integer> input = Arrays.asList(1,2,3,4);
//        Functional.seq.skip(-1, input);
//    }
//
//    @Test
//    public void seqSkipTest3()
//    {
//        final List<Integer> l = Arrays.asList(1,2,3,4,5);
//        final List<Integer> expected = Arrays.asList(3,4,5);
//        final Iterable<Integer> output = Functional.seq.skip(2,l);
//        final Iterator<Integer> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final int element : expected)
//        {
//            final int next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void skipWhileTest1()
//    {
//        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
//        {
//            final List<Integer> expected = Arrays.asList(1,2,3,4,5);
//            final List<? extends Integer> output = Functional.skipWhile(Functional.isEven, l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(2,3,4,5);
//            final List<? extends Integer> output = Functional.skipWhile(Functional.isOdd, l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(3,4,5);
//            final List<? extends Integer> output = Functional.skipWhile(new Function<Integer, Boolean>() {
//                public Boolean apply(final Integer i) {
//                    return i <=2;
//                }
//            }, l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = new ArrayList<Integer>();
//            final List<? extends Integer> output = Functional.skipWhile(new Function<Integer,Boolean>() { public Boolean apply(final Integer i) { return i<=6;} },l);
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void skipWhileTest2()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
//        Functional.skipWhile(null, input);
//    }
//
//    @Test
//    public void skipWhileTest3()
//    {
//        final List<Number> input = new ArrayList<Number>();
//        for(int i=1;i<10;++i)
//            input.add(Integer.valueOf(i));
//
//        final List<? extends Number> output = Functional.skipWhile(new Function<Object, Boolean>() {
//
//            public Boolean apply(final Object number) {
//                return ((number instanceof Integer) && ((Integer)number % 2) == 1);
//            }
//        }, input);
//
//        final List<Integer> expected = Arrays.asList(2,3,4,5,6,7,8,9);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqSkipWhileTest1()
//    {
//        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
//        {
//            final List<Integer> expected = Arrays.asList(1,2,3,4,5);
//            final List<? extends Integer> output = Functional.toList(Functional.seq.skipWhile(Functional.isEven, l));
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(2,3,4,5);
//            final List<? extends Integer> output = Functional.toList(Functional.seq.skipWhile(Functional.isOdd, l));
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = Arrays.asList(3,4,5);
//            final List<? extends Integer> output = Functional.toList(Functional.seq.skipWhile(new Function<Integer, Boolean>() {
//                public Boolean apply(final Integer i) {
//                    return i <= 2;
//                }
//            }, l));
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//        {
//            final List<Integer> expected = new ArrayList<Integer>();
//            final List<? extends Integer> output = Functional.toList(Functional.seq.skipWhile(new Function<Integer, Boolean>() {
//                public Boolean apply(final Integer i) {
//                    return i <= 6;
//                }
//            }, l));
//            AssertIterable.assertIterableEquals(expected, output);
//        }
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void seqSkipWhileTest2()
//    {
//        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
//        Functional.seq.skipWhile(null, input);
//    }
//
//    @Test
//    public void seqSkipWhileTest3()
//    {
//        final List<Number> input = new ArrayList<Number>();
//        for(int i=1;i<10;++i)
//            input.add(Integer.valueOf(i));
//
//        final List<? extends Number> output = Functional.toList(Functional.seq.skipWhile(new Function<Object, Boolean>() {
//
//            public Boolean apply(final Object number) {
//                return ((number instanceof Integer) && ((Integer) number % 2) == 1);
//            }
//        }, input));
//
//        final List<Integer> expected = Arrays.asList(2,3,4,5,6,7,8,9);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqSkipWhileTest4()
//    {
//        final List<Integer> l = Arrays.asList(1,2,3,4,5);
//        final List<Integer> expected = Arrays.asList(3,4,5);
//        final Iterable<Integer> output = Functional.seq.skipWhile(new Function<Integer, Boolean>() {
//            public Boolean apply(final Integer i) {
//                return i <= 2;
//            }
//        }, l);
//        final Iterator<Integer> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final int element : expected)
//        {
//            final int next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void extractFirstOfPair()
//    {
//        final List<Pair<Integer,String>> input = new ArrayList<Pair<Integer,String>>();
//        for(int i=0;i<5;++i) input.add(Pair.of(i, new Integer(i).toString()));
//        final List<Integer> output = Functional.map(Functional.<Integer,String>first(),input);
//        final List<Integer> expected = Arrays.asList(0,1,2,3,4);
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void extractSecondOfPair()
//    {
//        final List<Pair<Integer,String>> input = new ArrayList<Pair<Integer,String>>();
//        for(int i=0;i<5;++i) input.add(Pair.of(i, new Integer(i).toString()));
//        final List<String> output = Functional.map(Functional.<Integer,String>second(),input);
//        final List<String> expected = Arrays.asList("0","1","2","3","4");
//        AssertIterable.assertIterableEquals(expected, output);
//    }
//
//    @Test
//    public void groupByOddVsEvenInt()
//    {
//        final List<Integer> input = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
//        final Map<Boolean,List<Integer>> output = Functional.groupBy(Functional.isEven,input);
//        final Map<Boolean,List<Integer>> expected = new HashMap<Boolean, List<Integer>>();
//        expected.put(false,Arrays.asList(1,3,5,7,9));
//        expected.put(true, Arrays.asList(2, 4, 6, 8, 10));
//        AssertIterable.assertIterableEquals(expected.get(true), output.get(true));
//        AssertIterable.assertIterableEquals(expected.get(false),output.get(false));
//    }
//
//    @Test
//    public void groupByStringFirstTwoChar()
//    {
//        final List<String> input = Arrays.asList("aa","aab","aac","def");
//        final Map<String,List<String>> output = Functional.groupBy(new Function<String, String>() {
//
//            public String apply(final String s) {
//                return s.substring(0,1);
//            }
//        },input);
//        final Map<String,List<String>> expected = new HashMap<String, List<String>>();
//        expected.put("a",Arrays.asList("aa","aab","aac"));
//        expected.put("d",Arrays.asList("def"));
//        AssertIterable.assertIterableEquals(expected.get("a"), output.get("a"));
//        AssertIterable.assertIterableEquals(expected.get("d"),output.get("d"));
//        AssertIterable.assertIterableEquals(new TreeSet<String>(expected.keySet()),new TreeSet<String>(output.keySet()));
//    }
//
//    @Test
//    public void partitionRangesOfInt()
//    {
//        final int noElems = 13;
//        final int noPartitions = 5;
//        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems,noPartitions);
//
//        final List<Integer> expectedStart = Arrays.asList(0,3,6,9,11);
//        final List<Integer> expectedEnd = Arrays.asList(3,6,9,11,13);
//        final List<Pair<Integer,Integer>> expected = Functional.zip(expectedStart,expectedEnd);
//
//        final List<Pair<Integer,Integer>> output = Functional.map(new Function<Functional.Range<Integer>, Pair<Integer,Integer>>() {
//
//            public Pair<Integer, Integer> apply(final Functional.Range<Integer> range) {
//                return Pair.of(range.from(), range.to());
//            }
//        }, partitions);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void exactlyEvenPartitionRangesOfInt()
//    {
//        final int noElems = 10;
//        final int noPartitions = 5;
//        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems,noPartitions);
//
//        final List<Integer> expectedStart = Arrays.asList(0,2,4,6,8);
//        final List<Integer> expectedEnd = Arrays.asList(2,4,6,8,10);
//        final List<Pair<Integer,Integer>> expected = Functional.zip(expectedStart,expectedEnd);
//
//        final List<Pair<Integer,Integer>> output = Functional.map(new Function<Functional.Range<Integer>, Pair<Integer,Integer>>() {
//
//            public Pair<Integer, Integer> apply(final Functional.Range<Integer> range) {
//                return Pair.of(range.from(), range.to());
//            }
//        }, partitions);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void partitionFewerRangesOfIntThanPartitionsRequested()
//    {
//        final int noElems = 7;
//        final int noPartitions = 10;
//        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems,noPartitions);
//
//        final List<Integer> expectedStart = Arrays.asList(0,1,2,3,4,5,6);
//        final List<Integer> expectedEnd = Arrays.asList(1,2,3,4,5,6,7);
//        final List<Functional.Range<Integer>> expected =
//                Functional.concat(
//                    Functional.map(
//                        new Function<Pair<Integer,Integer>, Functional.Range<Integer>>() {
//                            public Functional.Range<Integer> apply(final Pair<Integer,Integer> pair)
//                            {
//                                return new Functional.Range<Integer>(pair.getLeft(), pair.getValue1());
//                            }
//                        }, Functional.zip(expectedStart, expectedEnd)),
//                    Functional.init(Functional.constant(new Functional.Range<Integer>(7,7)),3));
//
//        AssertIterable.assertIterableEquals(expected,partitions);
//    }
//
//    @Test
//    public void partitionRangesOfString()
//    {
//        final int noElems = 13;
//        final int noPartitions = 5;
//        final List<Functional.Range<String>> partitions =
//                Functional.partition(
//                        new Function<Integer, String>() {
//                            public String apply(final Integer i) {
//                                return new Integer(i - 1).toString();
//                            }
//                        },
//                        noElems, noPartitions);
//
//        final List<String> expectedStart = Arrays.asList("0","3","6","9","11");
//        final List<String> expectedEnd = Arrays.asList("3","6","9","11","13");
//        final List<Pair<String,String>> expected = Functional.zip(expectedStart,expectedEnd);
//
//        final List<Pair<String,String>> output = Functional.map(new Function<Functional.Range<String>, Pair<String,String>>() {
//
//            public Pair<String, String> apply(final Functional.Range<String> range) {
//                return Pair.of(range.from(), range.to());
//            }
//        }, partitions);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void partitionWithEmptySource()
//    {
//        Functional.partition(0,1);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void partitionWithZeroOutputRanges()
//    {
//        Functional.partition(1,0);
//    }
//
//    @Test
//    public void seqPartitionRangesOfString()
//    {
//        final int noElems = 13;
//        final int noPartitions = 5;
//        final List<Functional.Range<String>> partitions = Functional.toList(
//                Functional.seq.partition(
//                        new Function<Integer, String>() {
//                            public String apply(final Integer i) {
//                                return new Integer(i - 1).toString();
//                            }
//                        },
//                        noElems, noPartitions));
//
//        final List<String> expectedStart = Arrays.asList("0","3","6","9","11");
//        final List<String> expectedEnd = Arrays.asList("3","6","9","11","13");
//        final List<Pair<String,String>> expected = Functional.zip(expectedStart,expectedEnd);
//
//        final List<Pair<String,String>> output = Functional.map(new Function<Functional.Range<String>, Pair<String,String>>() {
//
//            public Pair<String, String> apply(final Functional.Range<String> range) {
//                return Pair.of(range.from(), range.to());
//            }
//        }, partitions);
//
//        AssertIterable.assertIterableEquals(expected,output);
//    }
//
//    @Test
//    public void seqPartitionRangesOfString2()
//    {
//        final int noElems = 13;
//        final int noPartitions = 5;
//        final Iterable<Functional.Range<String>> output =
//                Functional.seq.partition(
//                        new Function<Integer, String>() {
//                            public String apply(final Integer i) {
//                                return new Integer(i - 1).toString();
//                            }
//                        },
//                        noElems, noPartitions);
//
//        final List<String> expectedStart = Arrays.asList("0","3","6","9","11");
//        final List<String> expectedEnd = Arrays.asList("3","6","9","11","13");
//        final List<Pair<String,String>> expected_ = Functional.zip(expectedStart,expectedEnd);
//
//        final List<Functional.Range < String >> expected = Functional.map(new Function<Pair<String, String>, Functional.Range < String >> ()
//        {
//
//            public Functional.Range < String > apply(final Pair<String, String> pair) {
//                return new Functional.Range<String>(pair.getLeft(),pair.getValue1());
//            }
//        }, expected_);
//        final Iterator<Functional.Range<String>> iterator = output.iterator();
//
//        for(int i=0;i<20;++i)
//            assertThat().isTrue()(iterator.hasNext());
//
//        for(final Functional.Range<String> element : expected)
//        {
//            final Functional.Range<String> next = iterator.next();
//            assertThat().isEqualTo(element,next);
//        }
//
//        assertThat().isFalse()(iterator.hasNext());
//        try {
//            iterator.next();
//        } catch(final NoSuchElementException e) {
//            return;
//        }
//
//        Assert.fail("Should not reach this point");
//    }
//
//    @Test
//    public void toMutableDictionaryTest()
//    {
//        final Map<Integer,String> expected = new HashMap<Integer,String>();
//        expected.put(6, "6");
//        expected.put(12, "12");
//        final Map<Integer,String> output = Functional.toMutableDictionary(expected);
//        assertThat().isTrue()(expected.entrySet().containsAll(output.entrySet()));
//        assertThat().isTrue()(output.entrySet().containsAll(expected.entrySet()));
//        output.put(24, "24");
//        assertThat().isTrue()(output.containsKey(24));
//        assertThat().isTrue()(output.containsValue("24"));
//   }
//
//    @Test
//    public void toMutableListTest()
//    {
//        final List<String> expected = Arrays.asList("0","3","6","9","11");
//        final List<String> output = Functional.toMutableList(expected);
//        AssertIterable.assertIterableEquals(output,expected);
//        output.add("24");
//        assertThat().isTrue()(output.contains("24"));
//    }
//
//    @Test
//    public void toMutableSetTest()
//    {
//        final List<String> expected = Arrays.asList("0","3","6","9","11");
//        final Set<String> output = Functional.toMutableSet(expected);
//        assertThat().isTrue()(expected.containsAll(output));
//        assertThat().isTrue()(output.containsAll(expected));
//        output.add("24");
//        assertThat().isTrue()(output.contains("24"));
//    }
}