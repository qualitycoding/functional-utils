package me.shaftesbury.utils.functional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class Functional_ToArray_Test {
    public Functional_ToArray_Test() {
    }

    @Test
    void toArrayTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");

        final Object[] output = Functional.toArray(strs);

        Assertions.assertThat(output.length).isEqualTo(expected.size());
        for (int i = 0; i < expected.size(); ++i)
            Assertions.assertThat(output[i]).isEqualTo(expected.get(i));
    }

    @Test
    void collectionToArrayTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final List<String> strs = Functional.map(Functional.dStringify(), input);
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");

        final Object[] output = Functional.toArray(strs);

        Assertions.assertThat(output.length).isEqualTo(expected.size());
        for (int i = 0; i < expected.size(); ++i)
            Assertions.assertThat(output[i]).isEqualTo(expected.get(i));
    }
}