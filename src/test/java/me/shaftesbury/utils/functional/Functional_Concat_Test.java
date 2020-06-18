package me.shaftesbury.utils.functional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class Functional_Concat_Test {
    public Functional_Concat_Test() {
    }

    @Test
    void concatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
        Assertions.assertThat(Functional.concat(input, input)).containsExactlyElementsOf(expected);
    }

    @Test
    void seqConcatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));

        Assertions.assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqConcatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));

        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqConcatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            Assertions.fail("Shouldn't reach this point");
        }
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqConcatTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));
        final Iterator<String> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            Assertions.assertThat(iterator.hasNext()).isTrue();

        String next = iterator.next();
        Assertions.assertThat(next).isEqualTo("1");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("2");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("3");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("4");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("5");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("2");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("4");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("6");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("8");
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo("10");
        Assertions.assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        Assertions.fail("Should not reach this point");
    }
}