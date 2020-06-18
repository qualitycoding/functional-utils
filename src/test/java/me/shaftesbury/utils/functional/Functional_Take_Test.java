package me.shaftesbury.utils.functional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class Functional_Take_Test {
    public Functional_Take_Test() {
    }

    @Test
    void takeTooManyItemsTest() {
        Assertions.assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.take(100, Functional.init(FunctionalTest.doublingGenerator, 10)));
    }

    @Test
    void takeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.take(o, input), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Assertions.assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.<Integer>take(o).apply(input), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Assertions.assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void takeNoExceptionTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.noException.take(o, input), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Assertions.assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void takeNoExceptionTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.noException.take(o + 5, input), input);
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Assertions.assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.toList(Functional.seq.take(o, input)), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Assertions.assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.toList(Functional.seq.<Integer>take(o).apply(input)), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Assertions.assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.take(1, input);
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.take(1, input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            Assertions.fail("Shouldn't reach this point");
        }
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void takeTooManyFromSeqTakeTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.take(1, input);
        final Iterator<Integer> iterator = output.iterator();
        Integer next;
        try {
            next = iterator.next();
        } catch (final NoSuchElementException e) {
            Assertions.fail("Should not reach this point");
            next = null;
        }
        Assertions.assertThat(next).isEqualTo(input.get(0));
        Assertions.assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(iterator::next);
    }

    @Test
    void seqTakeTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.collect((Function<Integer, List<Integer>>) o -> Functional.toList(Functional.seq.take(o, input)), input);
        final List<Integer> expected = Arrays.asList(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            Assertions.assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            Assertions.assertThat(next).isEqualTo(element);
        }

        Assertions.assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        Assertions.fail("Should not reach this point");
    }
}