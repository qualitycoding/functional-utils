package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;

public class Functional_SkipWhile_Test {
    public Functional_SkipWhile_Test() {
    }

    @Test
    void curriedSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional.isEven).apply(l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional.isEven, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional.isOdd, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final List<Integer> output = Functional.skipWhile(i -> i <= 2, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final List<Integer> output = Functional.skipWhile(i -> i <= 6, l);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void skipWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.skipWhile(null, input));
    }

    @Test
    void skipWhileTest3() {
        final List<Number> input = new ArrayList<Number>();
        for (int i = 1; i < 10; ++i)
            input.add(Integer.valueOf(i));

        final List<Number> output = Functional.skipWhile((Function<Object, Boolean>) number -> ((number instanceof Integer) && ((Integer) number % 2) == 1), input);

        final List<Integer> expected = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(Functional.isEven, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(Functional.isOdd, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(i -> i <= 2, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
        {
            final List<Integer> expected = new ArrayList<Integer>();
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(i -> i <= 6, l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqSkipWhileWithoutHasNextTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skipWhile(Functional.isEven, l);
            final Iterator<Integer> iterator = output.iterator();
            for (final int expct : expected)
                assertThat(iterator.next()).isEqualTo(expct);
        }
    }

    @Test
    void cantRemoveFromseqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skipWhile(Functional.isEven, l);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }
    }

    @Test
    void cantRestartIteratorFromseqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.seq.skipWhile(Functional.isEven, l);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }

    @Test
    void curriedSeqSkipWhileTest1() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.toList(Functional.seq.skipWhile(Functional.isEven).apply(l));
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void seqSkipWhileTest2() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.seq.skipWhile(null, input));
    }

    @Test
    void seqSkipWhileTest3() {
        final List<Number> input = new ArrayList<Number>();
        for (int i = 1; i < 10; ++i)
            input.add(i);

        final List<Number> output = Functional.toList(Functional.seq.skipWhile((Function<Object, Boolean>) number -> ((number instanceof Integer) && ((Integer) number % 2) == 1), input));

        final List<Integer> expected = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqSkipWhileTest4() {
        final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expected = Arrays.asList(3, 4, 5);
        final Iterable<Integer> output = Functional.seq.skipWhile(i -> i <= 2, l);
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