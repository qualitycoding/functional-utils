package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_Switch_Test {
    @Test
    void switchTest1() {
        assertThat(Functional.switchBetween(10, Arrays.asList(Functional.toCase(Functional.lessThan(5), Functional.constant(-1)), Functional.toCase(Functional.greaterThan(5), Functional.constant(1))), Functional.constant(0))).isEqualTo(1);
    }

    @Test
    void switchDefaultCaseTest1() {
        assertThat(Functional.switchBetween(Integer.valueOf(10), Arrays.asList(Functional.toCase(Functional.lessThan(Integer.valueOf(5)), Functional.constant(Integer.valueOf(-1))), Functional.toCase(Functional.greaterThan(Integer.valueOf(15)), Functional.constant(Integer.valueOf(1)))), Functional.constant(Integer.valueOf(0)))).isEqualTo(Integer.valueOf(0));
    }
}
