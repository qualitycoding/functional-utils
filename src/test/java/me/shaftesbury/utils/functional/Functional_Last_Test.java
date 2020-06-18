package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
public class Functional_Last_Test {
    public Functional_Last_Test() {
    }

    @Test
    void lastTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        assertThat((long) Functional.last(input)).isEqualTo(5);
    }

    @Test
    void lastofArrayTest1() {
        final Integer[] input = new Integer[]{1, 2, 3, 4, 5};
        assertThat((long) Functional.last(input)).isEqualTo(5);
    }

    @Test
    void lastTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        assertThat(Functional.last(strs)).isEqualTo("5");
    }

    @Test
    void lastTest3() {
        final List<Integer> input = new ArrayList<Integer>();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Functional.last(input));
    }
}