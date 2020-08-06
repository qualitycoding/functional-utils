package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_Join_Test {
    @Test
    void joinTest1() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "3,6,9,12,15";
        assertThat(Functional.join(",", Functional.map(Functional.dStringify(), ids))).isEqualTo(expected);
        assertThat(Functional.join(",", ids)).isEqualTo(expected);
    }

    @Test
    void joinTest2() {
        final Collection<Integer> ids = Functional.init(FunctionalTest.triplingGenerator, 5);
        final String expected = "'3','6','9','12','15'";
        final Function<Integer, String> f = id -> "'" + id + "'";
        assertThat(Functional.join(",", Functional.map(f, ids))).isEqualTo(expected);
        assertThat(Functional.join(",", ids, f)).isEqualTo(expected);
    }
}
