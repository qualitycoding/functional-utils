package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static me.shaftesbury.utils.functional.Functional.range;
import static org.assertj.core.api.Assertions.assertThat;
class Functional_Range_Test {
    @Test
    void rangeHasLowerBound() {
        final Function<Integer, Integer> rangeGenerator = range(7);
        assertThat(rangeGenerator.apply(1)).as("First value in the range").isEqualTo(7);
        assertThat(rangeGenerator.apply(2)).as("Second value in the range").isEqualTo(8);
    }

}