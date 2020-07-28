package uk.co.qualitycoding.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
public class Functional_TakeWhile_Test {
    public Functional_TakeWhile_Test() {
    }

    @Test
    void curriedTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final List<Integer> output = Functional.takeWhile(Functional.isEven).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void takeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final List<Integer> output = Functional.takeWhile(Functional.isEven, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1);
            final List<Integer> output = Functional.takeWhile(Functional.isOdd, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
            final List<Integer> output = Functional.takeWhile(i -> i <= 4, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.takeWhile(i -> i <= 6, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void takeWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.takeWhile(null, input));
    }

    @Test
    void seqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(Functional.isEven, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1);
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(Functional.isOdd, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(i -> i <= 4, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(i -> i <= 6, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void callHasNextAfterFinishedInSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1);
            final Iterable<Integer> output = Functional.seq.takeWhile(Functional.isOdd, l);
            final Iterator<Integer> iterator = output.iterator();
            try {
                assertThat(iterator.next()).isEqualTo(expected.get(0));
            } catch (final NoSuchElementException e) {
                fail("Shouldn't reach this point");
            }
            assertThat(iterator.hasNext()).isFalse();
            assertThat(iterator.hasNext()).isFalse();
        }
    }

    @Test
    void callNextAfterFinishedInSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1);
            final Iterable<Integer> output = Functional.seq.takeWhile(Functional.isOdd, l);
            final Iterator<Integer> iterator = output.iterator();
            try {
                assertThat(iterator.next()).isEqualTo(expected.get(0));
            } catch (final NoSuchElementException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(iterator::next);
        }
    }

    @Test
    void cantRemoveFromSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final Iterable<Integer> output = Functional.seq.takeWhile(Functional.isOdd, l);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }
    }

    @Test
    void cantRestartIteratorFromSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final Iterable<Integer> output = Functional.seq.takeWhile(Functional.isOdd, l);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }

    @Test
    void curriedSeqTakeWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final List<Integer> output = Functional.toList(Functional.seq.takeWhile(Functional.isEven).apply(l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqTakeWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.seq.takeWhile(null, input));
    }

    @Test
    void seqTakeWhileTest3() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        int counter = 10;
        final Iterable<Integer> integers = Functional.seq.takeWhile(Functional.constant(true), input);
        final Iterator<Integer> iterator = integers.iterator();
        while (counter >= 0) {
            assertThat(iterator.hasNext()).isTrue();
            --counter;
        }
        int next = iterator.next();
        assertThat(next).isEqualTo(1);
        next = iterator.next();
        assertThat(next).isEqualTo(2);
        next = iterator.next();
        assertThat(next).isEqualTo(3);
        next = iterator.next();
        assertThat(next).isEqualTo(4);
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }
        fail("Should not reach this point");
    }
}
