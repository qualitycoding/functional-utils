package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_Exists_Test {
    @Test
    void existsTest() {
        final Collection<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional::isEven, i);
        final boolean allOdd = Functional.exists(Functional::isOdd, i);

        assertThat(allOdd).isFalse();
        assertThat(anEven).isTrue();
    }

    @Test
    void curriedExistsTest() {
        final Collection<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional::isEven).test(i);
        final boolean allOdd = Functional.exists(Functional::isOdd).test(i);

        assertThat(allOdd).isFalse();
        assertThat(anEven).isTrue();
    }
}
