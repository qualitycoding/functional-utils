package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_Join_Test {
    @Test
    void joinTest1a() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "3,6,9,12,15";
        assertThat(Functional.join(",").apply(Functional.map(Functional.dStringify(), ids))).isEqualTo(expected);
        assertThat(Functional.<Integer>join(",").apply(ids)).isEqualTo(expected);
    }

    @Test
    void joinTest1b() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "3,6,9,12,15";
        assertThat(Functional.join(",", Functional.map(Functional.dStringify(), ids))).isEqualTo(expected);
        assertThat(Functional.join(",", ids)).isEqualTo(expected);
    }

    @Test
    void joinWithNullDelimiter() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "3691215";
        assertThat(Functional.join(null, Functional.map(Functional.dStringify(), ids))).isEqualTo(expected);
        assertThat(Functional.join(null, ids)).isEqualTo(expected);
    }

    @Test
    void joinWithNullSequence() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        assertThat(Functional.join("", null)).isEqualTo("");
    }

    @Test
    void joinWithMapTest1() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "'3','6','9','12','15'";
        final Function<Integer, String> f = id -> "'" + id + "'";
        assertThat(Functional.join(",", ids, f)).isEqualTo(expected);
    }
}
