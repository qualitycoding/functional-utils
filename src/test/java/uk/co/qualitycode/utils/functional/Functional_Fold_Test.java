package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.join;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;
import static uk.co.qualitycode.utils.functional.FunctionalTest.triplingGenerator;

class Functional_Fold_Test {

    public static final BiFunction<String, Integer, String> csv = (state, a) -> state + "," + a;

    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.fold(null, new Object(), mock(Iterable.class)))
                .withMessage("fold(BiFunction<A,B,A>,A,Iterable<B>): folder must not be null");
        // null is an allowable initialValue
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.fold(mock(BiFunction.class), new Object(), (Iterable) null))
                .withMessage("fold(BiFunction<A,B,A>,A,Iterable<B>): input must not be null");

//        assertThatIllegalArgumentException()
//                .isThrownBy(() -> Functional.fold(null, new Object(), mock(Collection.class)))
//                .withMessage("fold(BiFunction<A,B,A>,A,Collection<B>): folder must not be null");
//        assertThatIllegalArgumentException()
//                .isThrownBy(() -> Functional.fold(mock(BiFunction.class), new Object(), (Collection) null))
//                .withMessage("fold(BiFunction<A,B,A>,A,Collection<B>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.fold(null, new Object()))
                .withMessage("fold(BiFunction<A,B,A>,A): folder must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.fold(null))
                .withMessage("fold(BiFunction<A,B,A>): folder must not be null");
    }

    @Test
    void foldIntegersStartingWithNull() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = ",2,4,6,8,10";

        final String s2 = Functional.fold((state, val) -> StringUtils.join(state, ",", val), null, li);

        assertThat(s2).isEqualTo(s1);
    }

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
    void curriedFoldIntegersSupplyingInitialValueSeparately() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = "0,2,4,6,8,10";

        final String s2 = Functional.fold(csv).withInitialValue("0").apply(li);

        assertThat(s2).isEqualTo(s1);
    }

    @Nested
    class FoldAndMapAreEquivalent {
        @Test
        void foldVsMap() {
            final Collection<Integer> li = Functional.init(doublingGenerator, 5);
            final String s1 = "0," + join(",", Functional.map(Functional.stringify(), li));
            assertThat(s1).isEqualTo("0,2,4,6,8,10");
            final String s2 = Functional.fold(csv, "0", li);
            assertThat(s2).isEqualTo(s1);
        }
    }

    static boolean isEven(final int i) {
        return i % 2 == 0;
    }

    @Test
    void foldAndChoose() {
        final Collection<Integer> openedDays = Functional.init(triplingGenerator, 5);

        final Hashtable<Integer, Double> missingPricesPerDate = new Hashtable<>();
        Double previous = 10.0;
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
