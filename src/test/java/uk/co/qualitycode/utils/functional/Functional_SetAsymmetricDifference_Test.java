package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_SetAsymmetricDifference_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.Set.asymmetricDifference(null, mock(Set.class)))
                .withMessage("Set.asymmetricDifference(Set<A>,Set<A>): input1 must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.Set.asymmetricDifference(mock(Set.class), null))
                .withMessage("Set.asymmetricDifference(Set<A>,Set<A>): input2 must not be null");
    }

    @Test
    void setAsymmetricDifferenceTest1() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));
        final Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2, 3));

        final Set<Integer> diff = Functional.Set.asymmetricDifference(input1, input2);
        assertThat(diff).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(input2).doesNotContainAnyElementsOf(diff);
    }

    @Test
    void setAsymmetricDifferenceTest2() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(6, 7, 8));

        final Set<Integer> diff = Functional.Set.asymmetricDifference(input1, input2);
        assertThat(diff).containsExactlyInAnyOrderElementsOf(input1);
    }

    @Test
    void setAsymmetricDifferenceTest3() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(6, 7, 8));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

        final Set<Integer> diff = Functional.Set.asymmetricDifference(input1, input2);
        assertThat(diff).containsExactlyInAnyOrderElementsOf(input1);
    }
}
