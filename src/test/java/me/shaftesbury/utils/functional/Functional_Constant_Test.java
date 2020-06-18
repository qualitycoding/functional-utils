package me.shaftesbury.utils.functional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class Functional_Constant_Test {
    public Functional_Constant_Test() {
    }

    @Test
    void constantInitialiserTest1() {
        final int howMany = 6;
        final int initValue = -1;
        final Collection<Integer> l = Functional.init(Functional.constant(initValue), howMany);
        Assertions.assertThat(l.size()).isEqualTo(howMany);
        for (final int i : l)
            Assertions.assertThat(i).isEqualTo(initValue);
    }
}