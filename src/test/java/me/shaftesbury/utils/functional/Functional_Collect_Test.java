package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static me.shaftesbury.utils.functional.FunctionalTest.doublingGenerator;
import static me.shaftesbury.utils.functional.FunctionalTest.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;

public class Functional_Collect_Test {
    public Functional_Collect_Test() {
    }

    @Test
    void curriedCollectTest1() {
        final List<Integer> input = Functional.init(doublingGenerator, 5);
        final List<Integer> output = Functional.collect(repeat(3)).apply(input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void collectTest1() {
        final List<Integer> input = Functional.init(doublingGenerator, 5);
        final List<Integer> output = Functional.collect(repeat(3), input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqCollectTest1() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqCollectTest1() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3)).apply(input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqCollectTest2() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        try {
            final Iterator<Integer> iterator1 = output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void cantRemoveFromSeqCollectTest1() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void seqCollectTest3() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> output = Functional.seq.collect(repeat(3), input);
        final List<Integer> expected = Arrays.asList(2, 2, 2, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 10);
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
    void setCollectTest1() {
        final Iterable<Integer> input = Functional.init(doublingGenerator, 5);
        final Set<Integer> output = Functional.set.collect(repeat(3), input);
        final Set<Integer> expected = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));

        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }

    @Test
    void setCollectTest2() {
        final Iterable<Integer> input = Functional.init(doublingGenerator, 5);
        final Set<Integer> output1 = Functional.set.collect(repeat(3), input);
        final Set<Integer> output2 = output1;
        final Set<Integer> expected = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));

        assertThat(expected.containsAll(output1)).isTrue();
        assertThat(output1.containsAll(expected)).isTrue();
        assertThat(expected.containsAll(output2)).isTrue();
        assertThat(output2.containsAll(expected)).isTrue();
    }

    @Test
    void greaterThanOrEqualTest() {
        final List<Integer> list1 = Arrays.asList(-5, -1, 0, 1, 5);
        final List<Integer> list2 = Arrays.asList(-1, 0, 1, 5);

        final List<Boolean> expected = Arrays.asList(
                false, false, false, false,
                true, false, false, false,
                true, true, false, false,
                true, true, true, false,
                true, true, true, true);

        final List<Boolean> output = Functional.collect(ths -> Functional.map(that -> Functional.greaterThanOrEqual(that).apply(ths), list2), list1);

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void lessThanOrEqualTest() {
        final List<Integer> list1 = Arrays.asList(-1, 0, 1, 5);
        final List<Integer> list2 = Arrays.asList(-5, -1, 0, 1, 5);

        final List<Boolean> expected = Arrays.asList(
                false, true, true, true, true,
                false, false, true, true, true,
                false, false, false, true, true,
                false, false, false, false, true
        );

        final List<Boolean> output = Functional.collect(ths -> Functional.map(that -> Functional.lessThanOrEqual(that).apply(ths), list2), list1);

        assertThat(output).isEqualTo(expected);
    }
}