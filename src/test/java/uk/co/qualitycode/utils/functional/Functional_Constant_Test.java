package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_Constant_Test {
    @Test
    void constantShouldReturnTheSuppliedValue() {
        final Object value = new Object();
        assertThat(Functional.constant(value))
                .satisfies(f -> assertThat(f.apply(2)).isSameAs(value));
    }

    @Test
    void useConstantInInitAsAnInitialiser() {
        final int howMany = 6;
        final int initValue = -1;
        final Collection<Integer> l = Functional.init(Functional.constant(initValue), howMany);
        assertThat(l).hasSize(howMany).containsOnly(initValue);
    }
}
