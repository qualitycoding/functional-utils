package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CircularArrayListTest
{
    @Test void CircularArrayListInitialiseTest1()
    {
        final List<Integer> input = Arrays.asList(1,2,3,4,5);
        final List<Integer> output = new CircularArrayList<>(input, 4);
        final List<Integer> expected = Arrays.asList(1,2,3,4);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test void CircularArrayListInitialiseTest2()
    {
        final List<Integer> input = Arrays.asList(1,2,3,4,5);
        final List<Integer> output = new CircularArrayList<>(input, 6);
        final List<Integer> expected = Arrays.asList(1,2,3,4,5,1);
        assertThat(output).containsExactlyElementsOf(expected);
    }
}
