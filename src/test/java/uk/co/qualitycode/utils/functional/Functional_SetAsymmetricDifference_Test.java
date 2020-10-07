package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_SetAsymmetricDifference_Test {
    @Test
    void setAsymmetricDifferenceTest1() {
        final java.util.Set input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final java.util.Set input2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));
        final java.util.Set expected = new HashSet<>(Arrays.asList(1, 2, 3));

        final java.util.Set diff = Functional.Set.asymmetricDifference(input1, input2);
        assertThat(diff).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(input2).doesNotContainAnyElementsOf(diff);
    }

    @Test
    void setAsymmetricDifferenceTest2() {
        final java.util.Set input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final java.util.Set input2 = new HashSet<>(Arrays.asList(6, 7, 8));

        final java.util.Set diff = Functional.Set.asymmetricDifference(input1, input2);
        assertThat(diff).containsExactlyInAnyOrderElementsOf(input1);
    }

    @Test
    void setAsymmetricDifferenceTest3() {
        final java.util.Set input1 = new HashSet<>(Arrays.asList(6, 7, 8));
        final java.util.Set input2 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

        final java.util.Set diff = Functional.Set.asymmetricDifference(input1, input2);
        assertThat(diff).containsExactlyInAnyOrderElementsOf(input1);
    }
}
