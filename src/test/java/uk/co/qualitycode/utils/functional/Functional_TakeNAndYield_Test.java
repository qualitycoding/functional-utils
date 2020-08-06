package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_TakeNAndYield_Test {
    @Test
    void takeNandYieldTest1() {
        final Iterable<Integer> input = Functional.seq.init(FunctionalTest.doublingGenerator, 5);
        final Tuple2<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(input, 2);
        final List<Integer> expectedList = Arrays.asList(2, 4);
        final List<Integer> expectedRemainder = Arrays.asList(6, 8, 10);
        assertThat(output._1()).containsExactlyElementsOf(expectedList);
        assertThat(output._2()).containsExactlyElementsOf(expectedRemainder);
    }

    @Test
    void takeNandYieldTest2() {
        final Iterable<Integer> input = Functional.seq.init(FunctionalTest.doublingGenerator, 5);
        final Tuple2<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(input, 0);
        final List<Integer> expectedList = Arrays.asList();
        final List<Integer> expectedRemainder = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(output._1()).containsExactlyElementsOf(expectedList);
        assertThat(output._2()).containsExactlyElementsOf(expectedRemainder);
    }
}
