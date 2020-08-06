package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_SetAsymmetricDifference_Test {
    @Test
    void setAsymmetricDifferenceTest1() {
        final Set<Integer> input1 = new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<Integer>(Arrays.asList(4, 5, 6, 7, 8));
        final Set<Integer> expected = new HashSet<Integer>(Arrays.asList(1, 2, 3));

        final Set<Integer> diff = Functional.set.asymmetricDifference(input1, input2);
        assertThat(diff.containsAll(expected)).isTrue();
        assertThat(expected.containsAll(diff)).isTrue();
        assertThat(input2.containsAll(diff)).isFalse();
    }

    @Test
    void setAsymmetricDifferenceTest2() {
        final Set<Integer> input1 = new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<Integer>(Arrays.asList(6, 7, 8));
        final Set<Integer> expected = Collections.emptySet();

        final Set<Integer> diff = Functional.set.asymmetricDifference(input1, input2);
        assertThat(diff.containsAll(input1)).isTrue();
        assertThat(input1.containsAll(diff)).isTrue();
    }
}
