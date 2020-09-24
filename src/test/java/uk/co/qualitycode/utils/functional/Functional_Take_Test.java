package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class Functional_Take_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.take(-1, mock(Iterable.class)))
                .withMessage("take(int,Iterable<T>): howMany must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.take(1, (Iterable)null))
                .withMessage("take(int,Iterable<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.take(-1, mock(List.class)))
                .withMessage("take(int,List<T>): howMany must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.take(1, (List)null))
                .withMessage("take(int,List<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.take(-1))
                .withMessage("take(int): howMany must not be negative");
    }

    @Test
    void takeThreeElementsFromIterable() {
        final Iterable<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.take(3, input);
        final List<Integer> expected = Arrays.asList(1, 2, 3);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void takeThreeElementsFromList() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.take(3, input);
        final List<Integer> expected = Arrays.asList(1, 2, 3);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedTakeThreeElements() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final List<Integer> output = Functional.<Integer>take(3).apply(input);
        final List<Integer> expected = Arrays.asList(1, 2, 3);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Nested
    class Lazy {
        @Test
        void seqTakeTest1() {
            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.take(3, input);
            final List<Integer> expected = Arrays.asList(1, 2, 3);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedSeqTakeTest1() {
            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.<Integer>take(3).apply(input);
            final List<Integer> expected = Arrays.asList(1, 2, 3);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cantRemoveFromSeqTakeTest1() {
            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.take(1, input);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqTakeTest1() {
            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.take(1, input);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }

        @Test
        void takeTooManyFromSeqTakeTest1() {
            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.take(1, input);
            final Iterator<Integer> iterator = output.iterator();
            Integer next;
            try {
                next = iterator.next();
            } catch (final NoSuchElementException e) {
                fail("Should not reach this point");
                next = null;
            }
            assertThat(next).isEqualTo(input.get(0));
            assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(iterator::next);
        }
    }
}
