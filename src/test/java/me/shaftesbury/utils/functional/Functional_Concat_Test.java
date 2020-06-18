package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;
public class Functional_Concat_Test {
    public Functional_Concat_Test() {
    }

    @Test
    void concatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
        assertThat(Functional.concat(input, input)).containsExactlyElementsOf(expected);
    }

    @Test
    void seqConcatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqConcatTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Integer, Integer> doubler = i -> i * 2;
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5", "2", "4", "6", "8", "10");

        final Iterable<String> strs = Functional.seq.map(Functional.dStringify(), input);
        final Iterable<String> output = Functional.seq.concat(strs, Functional.seq.map(Functional.dStringify(), Functional.seq.map(doubler, input)));

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
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
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
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
            assertThat(iterator.hasNext()).isTrue();

        String next = iterator.next();
        assertThat(next).isEqualTo("1");
        next = iterator.next();
        assertThat(next).isEqualTo("2");
        next = iterator.next();
        assertThat(next).isEqualTo("3");
        next = iterator.next();
        assertThat(next).isEqualTo("4");
        next = iterator.next();
        assertThat(next).isEqualTo("5");
        next = iterator.next();
        assertThat(next).isEqualTo("2");
        next = iterator.next();
        assertThat(next).isEqualTo("4");
        next = iterator.next();
        assertThat(next).isEqualTo("6");
        next = iterator.next();
        assertThat(next).isEqualTo("8");
        next = iterator.next();
        assertThat(next).isEqualTo("10");
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void setConcatTest1() {
        final Set<Integer> input = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        final Function<Integer, Integer> doubler = i -> i * 2;
        final Set<String> expected = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "8", "10"));

        final Set<String> strs = Functional.set.map(Functional.dStringify(), input);
        final Set<String> output = Functional.set.concat(strs, Functional.set.map(Functional.dStringify(), Functional.set.map(doubler, input)));

        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }

}