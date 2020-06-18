package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
public class Functional_Constant_Test {
    public Functional_Constant_Test() {
    }

    @Test
    void constantInitialiserTest1() {
        final int howMany = 6;
        final int initValue = -1;
        final Collection<Integer> l = Functional.init(Functional.constant(initValue), howMany);
        assertThat(l.size()).isEqualTo(howMany);
        for (final int i : l)
            assertThat(i).isEqualTo(initValue);
    }
}