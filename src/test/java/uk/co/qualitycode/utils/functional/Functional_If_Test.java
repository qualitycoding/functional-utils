package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_If_Test {
    @Test
    void ifTest1() {
        final int input = 1;
        final Iterable2<Integer> i = Iterable2.asList(0, 1, 2);
        final Iterable2<Integer> result = i.map(ii -> Functional.if_fn(input, Functional.greaterThan(ii), FunctionalTest.doublingGenerator, FunctionalTest.triplingGenerator));
        final List<Integer> expected = Arrays.asList(2, 3, 3);
        assertThat(result).containsExactlyElementsOf(expected);
    }
}
