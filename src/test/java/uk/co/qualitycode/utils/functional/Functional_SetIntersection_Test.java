package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_SetIntersection_Test {
    @Test
    void setIntersectionTest1() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

        final Set<Integer> intersection = Functional.Set.intersection(input1, input2);
        assertThat(input1).containsAll(intersection);
        assertThat(input2).containsAll(intersection);
    }

    @Test
    void setIntersectionTest2() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));

        final Set<Integer> intersection = Functional.Set.intersection(input1, input2);
        assertThat(input1).containsAll(intersection);
        assertThat(input2).containsAll(intersection);
    }

    @Test
    void setIntersectionTest3() {
        final Set<Integer> input1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Set<Integer> input2 = new HashSet<>(Arrays.asList(6, 7, 8));

        final Set<Integer> intersection = Functional.Set.intersection(input1, input2);
        assertThat(intersection).isEmpty();
    }
}
