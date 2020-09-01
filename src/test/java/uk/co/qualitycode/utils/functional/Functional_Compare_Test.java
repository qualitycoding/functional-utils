package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.primitive.integer.Func_int_int;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.co.qualitycode.utils.functional.Functional.count;
import static uk.co.qualitycode.utils.functional.Functional.greaterThan;
import static uk.co.qualitycode.utils.functional.Functional.greaterThanOrEqual;
import static uk.co.qualitycode.utils.functional.Functional.isEven;
import static uk.co.qualitycode.utils.functional.Functional.isOdd;
import static uk.co.qualitycode.utils.functional.Functional.lessThan;
import static uk.co.qualitycode.utils.functional.Functional.lessThanOrEqual;
import static uk.co.qualitycode.utils.functional.Functional.sum;

class Functional_Compare_Test {
    public static Func_int_int doublingGenerator_f = a -> 2 * a;
    public static Function<Integer, Integer> doublingGenerator = a -> 2 * a;
    public static Function<Integer, Integer> triplingGenerator = a -> 3 * a;
    public static Function<Integer, Integer> quadruplingGenerator = a -> 4 * a;

    static boolean bothAreEven(final int a, final int b) {
        return isEven.apply(a) && isEven.apply(b);
    }

    static boolean bothAreLessThan10(final int a, final int b) {
        return a < 10 && b < 10;
    }

    static BiFunction<Integer, Integer, Boolean> dBothAreLessThan10 = Functional_Compare_Test::bothAreLessThan10;

    @Test
    void isEvenReturnsTrueForEvenFalseForOdd() {
        assertAll(
                () -> assertThat(isEven.apply(1)).isFalse(),
                () -> assertThat(isEven.apply(2)).isTrue()
        );
    }

    @Test
    void isOddReturnsFalseForEvenTrueForOdd() {
        assertAll(
                () -> assertThat(isOdd.apply(1)).isTrue(),
                () -> assertThat(isOdd.apply(2)).isFalse()
        );
    }

    @Test
    void countReturnsTheStatePlusOne() {
        assertThat(count.apply(1, 30000)).isEqualTo(2);
    }

    @Test
    void sumReturnsTheTotal() {
        assertThat(sum.apply(1, 2)).isEqualTo(3);
    }

    @Test
    void greaterThanReturnsBoolean() {
        assertAll(
                () -> assertThat(greaterThan(5).apply(6)).isTrue(),
                () -> assertThat(greaterThan(5).apply(5)).isFalse(),
                () -> assertThat(greaterThan(5).apply(4)).isFalse()
        );
    }

    @Test
    void greaterThanOrEqualReturnsBoolean() {
        assertAll(
                () -> assertThat(greaterThanOrEqual(5).apply(6)).isTrue(),
                () -> assertThat(greaterThanOrEqual(5).apply(5)).isTrue(),
                () -> assertThat(greaterThanOrEqual(5).apply(4)).isFalse()
        );
    }

    @Test
    void lessThanReturnsBoolean() {
        assertAll(
                () -> assertThat(lessThan(5).apply(6)).isFalse(),
                () -> assertThat(lessThan(5).apply(5)).isFalse(),
                () -> assertThat(lessThan(5).apply(4)).isTrue()
        );
    }

    @Test
    void lessThanOrEqualReturnsBoolean() {
        assertAll(
                () -> assertThat(lessThanOrEqual(5).apply(6)).isFalse(),
                () -> assertThat(lessThanOrEqual(5).apply(5)).isTrue(),
                () -> assertThat(lessThanOrEqual(5).apply(4)).isTrue()
        );
    }
}
