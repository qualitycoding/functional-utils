package me.shaftesbury.utils.functional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

public class Functional_Exists_Test {
    public Functional_Exists_Test() {
    }

    @Test
    void existsTest() {
        final Collection<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional.isEven, i);
        final boolean allOdd = Functional.exists(Functional.isOdd, i);

        Assertions.assertThat(allOdd).isFalse();
        Assertions.assertThat(anEven).isTrue();
    }

    @Test
    void curriedExistsTest() {
        final Collection<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional.isEven).apply(i);
        final boolean allOdd = Functional.exists(Functional.isOdd).apply(i);

        Assertions.assertThat(allOdd).isFalse();
        Assertions.assertThat(anEven).isTrue();
    }
}