package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.qualitycode.utils.functional.Functional.join;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;
import static uk.co.qualitycode.utils.functional.FunctionalTest.triplingGenerator;

class Functional_Fold_Test {

    public static final BiFunction<String, Integer, String> csv = (state, a) -> state + "," + a;

    @Test
    void foldIntegers() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = "0,2,4,6,8,10";

        final String s2 = Functional.fold(csv, "0", li);

        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void curriedFoldIntegers() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = "0,2,4,6,8,10";

        final String s2 = Functional.fold(csv, "0").apply(li);

        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void curriedFoldIntegers2() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = "0,2,4,6,8,10";

        final String s2 = Functional.fold(csv).withInitialValue("0").apply(li);

        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void foldvsMapTest1() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = join(",", Functional.map(Functional.stringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.fold(FunctionalTest::csv, "", li);
        assertThat(s2).isEqualTo(s1);
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

    static boolean isEven(final int i) {
        return i % 2 == 0;
    }

    @Test
    void foldAndChoose() {
        final Collection<Integer> openedDays = Functional.init(triplingGenerator, 5);

        final Hashtable<Integer, Double> missingPricesPerDate= new Hashtable<>();
        Double previous=10.0;
        for (final int day : openedDays) {
            final Option<Double> value = isEven(day) ? Option.of((double) (day / 2)) : Option.none();
            if (value.isSome())
                previous = value.get();
            else
                missingPricesPerDate.put(day, previous);
        }

        final Collection<FunctionalTest.myInt> openedDays2 = Functional.init(
                a -> new FunctionalTest.myInt(3 * a), 5);
        final Tuple2<Double, List<FunctionalTest.myInt>> output = Functional.foldAndChoose(
                (state, day) -> {
                    final Double value = day.i() % 2 == 0 ? (Double) ((double) (day.i() / 2)) : null;
                    return value != null
                            ? new Tuple2<>(value, Option.none())
                            : new Tuple2<>(state, Option.of(day));
                }, 10.0, openedDays2);

        assertThat(output._1()).isEqualTo(previous);
        final List<Integer> keys = new ArrayList<>(missingPricesPerDate.keySet());
        Collections.sort(keys);
        assertThat(Functional.map(FunctionalTest.myInt::i, output._2())).containsExactlyElementsOf(keys);
//        assertThatExceptionOfType(UnsupportedOperationException.class)
//                .isThrownBy(()->output._2().add(new FunctionalTest.myInt(0)));
    }
}
