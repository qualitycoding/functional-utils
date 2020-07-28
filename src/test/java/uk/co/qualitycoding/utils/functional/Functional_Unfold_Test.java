package uk.co.qualitycoding.utils.functional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import uk.co.qualitycoding.utils.functional.monad.Option;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;

public class Functional_Unfold_Test {
    public Functional_Unfold_Test() {
    }

    @Test
    void unfoldTest1() {
        final int seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.unfold(unspool, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void unfoldAsDoublingGeneratorTest1() {
        final int seed = 1;
        final Function<Integer, Pair<Integer, Integer>> doubler = integer -> Pair.of(integer * 2, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.unfold(doubler, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void unfoldAsDoublingGeneratorTest2() {
        final int seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.unfold(doubler, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqUnfoldTest1() {
        final int seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqUnfoldTest1() {
        final int seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqUnfoldTest1() {
        final int seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqUnfoldTest2() {
        final int seed = 0;
        final Function<Integer, Pair<Integer, Integer>> unspool = integer -> Pair.of(integer + 1, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer == 10;

        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Iterable<Integer> output = Functional.seq.unfold(unspool, finished, seed);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void seqUnfoldAsDoublingGeneratorTest1() {
        final int seed = 1;
        final Function<Integer, Pair<Integer, Integer>> doubler = integer -> Pair.of(integer * 2, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqUnfoldAsDoublingGeneratorTest2() {
        final int seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqUnfoldAsDoublingGeneratorTest2() {
        final int seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, seed);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqUnfoldAsDoublingGeneratorTest2() {
        final int seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, seed);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqUnfoldAsDoublingGeneratorTest3() {
        final int seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final Iterable<Integer> output = Functional.seq.unfold(doubler, seed);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final int element : expected) {
            final int next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void recUnfoldAsDoublingGeneratorTest1() {
        final int seed = 1;
        final Function<Integer, Pair<Integer, Integer>> doubler = integer -> Pair.of(integer * 2, integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.rec.unfold(doubler, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void recUnfoldAsDoublingGeneratorTest2() {
        final int seed = 1;
        final Function<Integer, Option<Pair<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.None() : Option.toOption(Pair.of(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.rec.unfold(doubler, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }
}
