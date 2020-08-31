package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

class Functional_Join_Test {
    @Test
    void joinWithComma() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "3,6,9,12,15";
        assertThat(Functional.join(",", ids)).isEqualTo(expected);
    }

    @Test
    void curriedJoinWithComma() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "3,6,9,12,15";
        assertThat(Functional.<Integer>join(",").apply(ids)).isEqualTo(expected);
    }

    @Test
    void joinWithNullDelimiter() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "3691215";
        assertThat(Functional.join(null, ids)).isEqualTo(expected);
    }

    @Test
    void joinWithNullSequence() {
        assertThat(Functional.join("", null)).isEqualTo("");
    }

    @Test
    void joinWithMapPreconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> Functional.join(" ", new ArrayList<>(), null))
                        .withMessage("join(String,Iterable<T>,Function<T,String>): tfm must not be null"));
    }

    @Test
    void joinNullDelimiterReturnsConcatenatedInput() {
        assertThat(Functional.join(null, Arrays.asList(1,2,3), i->i.toString())).isEqualTo("123");
    }

    @Test
    void joinNullInputReturnsEmptyString() {
        assertThat(Functional.join(" ", null, i -> "hdfgh")).isEqualTo("");
    }

    @Test
    void joinWithMap() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "'3','6','9','12','15'";
        final Function<Integer, String> f = id -> "'" + id + "'";
        assertThat(Functional.join(",", ids, f)).isEqualTo(expected);
    }
}
