package uk.co.qualitycoding.utils.functional;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
public class Functional_Between_Test {
    public Functional_Between_Test() {
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
    void betweenTest3() {
        final double lowerBound = 2.5, upperBound = 2.6;
        assertThat(Functional.between(lowerBound, upperBound, 2.55)).isTrue();
    }
}
