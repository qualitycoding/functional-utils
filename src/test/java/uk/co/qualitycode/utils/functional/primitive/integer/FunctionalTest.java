package uk.co.qualitycode.utils.functional.primitive.integer;

import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;
import uk.co.qualitycode.utils.functional.monad.OptionNoValueAccessException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class FunctionalTest {
    public static final Func_int_int doublingGenerator = a -> 2 * a;

    @Test
    void rangeTest1() {
        final IntList output = Functional.init(Functional.range(0), 5);
        assertThat(output.toArray()).containsExactly(0, 1, 2, 3, 4);
    }

    private static boolean bothAreLessThan10(final int a, final int b) {
        return a < 10 && b < 10;
    }

    @Test
    void initTest1() {
        final IntList output = Functional.init(doublingGenerator, 5);
        assertThat(output.toArray()).containsExactly(2, 4, 6, 8, 10);
    }

    public static final Func_int_int triplingGenerator = a -> 3 * a;

    public static final Func_int_int quadruplingGenerator = a -> 4 * a;

    private static boolean bothAreEven(final int a, final int b) {
        return Functional.isEven(a) && Functional.isEven(b);
    }

    @Test
    void mapTest1() {
        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
        final Collection<String> output = Functional.map(Functional.dStringify(), input);
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void mapiTest1() {
        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
        final Collection<Tuple2<Integer, String>> output = Functional.mapi((final int pos, final int i) -> new Tuple2<>(pos, Integer.toString(i)), input);
        assertThat(uk.co.qualitycode.utils.functional.Functional.map(Tuple2::_2, output)).containsExactly("1", "2", "3", "4", "5");
        assertThat(Functional.map(Tuple2::_1, output).toArray()).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void forAll2Test1() {
        final IntList l = Functional.init(doublingGenerator, 5);
        final IntList m = Functional.init(quadruplingGenerator, 5);

        assertThat(Functional.forAll2(FunctionalTest::bothAreEven, l, m)).isTrue();
    }

    @Test
    void forAll2Test2() {
        final IntList l = Functional.init(doublingGenerator, 5);
        final IntList m = Functional.init(triplingGenerator, 5);

        assertThat(Functional.forAll2(FunctionalTest::bothAreLessThan10, l, m)).isFalse();
    }

    @Test
    void forAll2Test3() {
        final IntList l = Functional.init(doublingGenerator, 5);
        final IntList m = Functional.init(quadruplingGenerator, 7);

        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> Functional.forAll2(FunctionalTest::bothAreEven, l, m));
    }

    @Test
    void compositionTest1A() {
        final IntList i = new IntList(new int[]{1, 2, 3, 45, 56, 6});

        final boolean allOdd = Functional.forAll(Functional::isOdd, i);
        final boolean notAllOdd = Functional.exists(Functional.not(Functional::isOdd), i);

        assertThat(allOdd).isFalse();
        assertThat(notAllOdd).isTrue();
    }

    @Test
    void compositionTest2() {
        final IntList l = Functional.init(doublingGenerator, 5);
        final IntList m = Functional.init(triplingGenerator, 5);
        assertThat(Functional.forAll2(Functional.not2(FunctionalTest::bothAreLessThan10), l, m)).isFalse();
        // equivalent to BothAreGreaterThanOrEqualTo10

        final int lowerLimit = 1;
        final int upperLimit = 16;
        assertThat(Functional.forAll2(Functional.not2((a, b) -> a > lowerLimit && b > lowerLimit), l, m)).isFalse();
        assertThat(Functional.forAll2(Functional.not2((a, b) -> a > upperLimit && b > upperLimit), l, m)).isTrue();
    }

    @Test
    void partitionTest1() {
        final IntList m = Functional.init(triplingGenerator, 5);
        final Tuple2<List<Integer>, List<Integer>> r = Functional.partition(Functional::isOdd, m);

        final Integer[] left = {3, 9, 15};
        final Integer[] right = {6, 12};
        assertThat(r._1()).containsExactly(left);
        assertThat(r._2()).containsExactly(right);
    }

    @Test
    void partitionTest2() {
        final IntList l = Functional.init(doublingGenerator, 5);
        final Tuple2<List<Integer>, List<Integer>> r = Functional.partition(Functional::isEven, l);
        assertThat(r._1()).containsExactly(l.toArray(new Integer[0]));
        assertThat(r._2()).isEmpty();
    }

    @Test
    void partitionTest3() {
        final IntList l = Functional.init(doublingGenerator, 5);
        final Tuple2<List<Integer>, List<Integer>> r = Functional.partition(Functional::isEven, l);
        assertThat(r._1()).containsExactly(Functional.filter(Functional::isEven, l).toArray(new Integer[0]));
    }

    @Test
    void toStringTest1() {
        final IntList li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(ls).containsExactly("2", "4", "6", "8", "10");
    }

    @Test
    void chooseTest1B() throws OptionNoValueAccessException {
        final IntList li = Functional.init(triplingGenerator, 5);
        final Collection<String> o = Functional.choose((Func_int_T<Option<String>>) i -> i % 2 == 0 ? Option.of(Integer.toString(i)) : Option.none(), li);
        assertThat(o).containsExactly("6", "12");
    }

    private static String csv(final String state, final int a) {
        return StringUtils.isEmpty(state) ? "" + a : state + "," + a;
    }

    @Test
    void foldvsMapTest1() {
        final IntList li = Functional.init(doublingGenerator, 5);
        final String s1 = uk.co.qualitycode.utils.functional.Functional.join(",", Functional.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.fold(FunctionalTest::csv, "", li);
        assertThat(s1).isEqualTo(s2);
    }

    private final Function<IntList, String> concatenate =
            l -> Functional.fold(FunctionalTest::csv, "", l);

    private final Function<IntList, IntList> evens_f =
            l -> Functional.filter(Functional::isEven, l);

    @Test
    void indentTest1() {
        final int level = 5;
        final String expectedResult = "     ";

        String indentedName = "";
        for (int i = 0; i < level; ++i) {
            indentedName += " ";
        }
        assertThat(indentedName).isEqualTo(expectedResult);

        final Collection<String> indentation = Functional.init(
                (Func_int_T<String>) integer -> " ", level);
        assertThat(uk.co.qualitycode.utils.functional.Functional.join("", indentation)).isEqualTo("     ");
    }

    @Test
    void chooseTest3A() throws OptionNoValueAccessException {
        final IntList li = Functional.init(triplingGenerator, 5);
        final IntList o =
                Functional.choose(
                        (Func_int_Option_int) i -> i % 2 == 0 ? Option_int.toOption(i) : Option_int.none(), li);

        assertThat(o.toArray()).containsExactly(6, 12);
    }

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
        final IntList openedDays = Functional.init(triplingGenerator, 5);
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
        final Tuple2<Double, List<myInt>> output =
                uk.co.qualitycode.utils.functional.Functional.foldAndChoose((state, day) -> {
                    final Double value = day.i() % 2 == 0 ? (Double) ((double) (day.i() / 2)) : null;
                    return value != null
                            ? new Tuple2<>(value, Option.none())
                            : new Tuple2<>(state, Option.of(day));
                }, 10.0, openedDays2);

        assertThat(output._1()).isEqualTo(last);
        final List<Integer> keys = new ArrayList<>(missingPricesPerDate.keySet());
        Collections.sort(keys);
        assertThat(Functional.map(myInt::i, output._2()).toArray(new Integer[0]))
                .containsExactlyElementsOf(keys);
    }

    @Test
    void joinTest1() {
        final IntList ids = Functional.init(triplingGenerator, 5);
        final String expected = "3,6,9,12,15";
        assertThat(uk.co.qualitycode.utils.functional.Functional.join(",", Functional.map(Functional.dStringify(), ids))).isEqualTo(expected);
        assertThat(Functional.join(",", ids)).isEqualTo(expected);
    }

    @Test
    void joinTest2() {
        final IntList ids = Functional.init(triplingGenerator, 5);
        final String expected = "'3','6','9','12','15'";
        final Func_int_T<String> f =
                id -> "'" + id + "'";
        assertThat(uk.co.qualitycode.utils.functional.Functional.join(",", Functional.map(f, ids))).isEqualTo(expected);
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
    void testIsEven_withEvenNum() {
        assertThat(Functional.isEven(2)).isTrue();
    }

    @Test
    void findLastTest1() {
        final IntList l = Functional.init(doublingGenerator, 5);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> Functional.findLast(Functional::isOdd, l));
    }

    @Test
    void findLastTest2() {
        final IntList l = Functional.init(doublingGenerator, 5);
        assertThat(Functional.findLast(Functional::isEven, l)).isEqualTo(10);
    }

    @Test
    void lastTest1() {
        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
        assertThat((long) Functional.last(input)).isEqualTo(5);
    }

    @Test
    void lastTest3() {
        final IntList input = new IntList(new int[]{});
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.last(input));
    }

    @Test
    void concatTest1() {
        final IntList input = new IntList(new int[]{1, 2, 3, 4, 5});
        final int[] expected = new int[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5};
        assertThat(Functional.concat(input, input).toArray()).containsExactly(expected);
    }

}
