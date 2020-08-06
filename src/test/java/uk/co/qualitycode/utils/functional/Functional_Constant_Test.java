package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_Constant_Test {
    @Test
    void constantInitialiserTest1() {
        final int howMany = 6;
        final int initValue = -1;
        final Collection<Integer> l = Functional.init(Functional.constant(initValue), howMany);
        assertThat(l).hasSize(howMany);
        for (final int i : l)
            assertThat(i).isEqualTo(initValue);
    }
}
