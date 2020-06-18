package me.shaftesbury.utils.functional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Functional_Sort_Test {
    public Functional_Sort_Test() {
    }

    @Test
    void sortWithTest1() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final Collection<Integer> output = Functional.sortWith(Integer::compare, i);
        Assertions.assertThat(output).containsExactly(1, 4, 6, 7, 23);
    }

    @Test
    void sortWithTest2() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final Collection<Integer> j = Functional.sortWith((a, b) -> a - b, i);
        Assertions.assertThat(j).containsExactly(1, 4, 6, 7, 23);
    }

    @Test
    void sortWithTest3() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final Collection<Integer> j = Functional.sortWith(Functional.dSorter, i);
        Assertions.assertThat(j).containsExactly(1, 4, 6, 7, 23);
    }
}