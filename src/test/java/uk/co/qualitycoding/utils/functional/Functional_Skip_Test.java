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

public class Functional_Skip_Test {
    public Functional_Skip_Test() {
    }

    @Test
    void curriedSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.<Integer>skip(0).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skip(0, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.skip(1, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final List<Integer> output = Functional.skip(2, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(4, 5);
            final List<Integer> output = Functional.skip(3, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(5);
            final List<Integer> output = Functional.skip(4, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final List<Integer> output = Functional.skip(5, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final List<Integer> output = Functional.skip(6, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.skip(-1, input));
    }

    @Test
    void seqSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(0, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(1, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(2, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(4, 5);
            final Iterable<Integer> output = Functional.seq.skip(3, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(5);
            final Iterable<Integer> output = Functional.seq.skip(4, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final Iterable<Integer> output = Functional.seq.skip(5, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final Iterable<Integer> output = Functional.seq.skip(6, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void cantRemoveFromSeqSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(0, l);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }
    }

    @Test
    void cantRestartIteratorFromSeqSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skip(0, l);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }

    @Test
    void curriedSeqSkipTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.<Integer>skip(0).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqSkipTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.seq.skip(-1, input));
    }

    @Test
    void seqSkipTest3() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expected = Arrays.asList(3, 4, 5);
        final Iterable<Integer> output = Functional.seq.skip(2, l);
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
}
