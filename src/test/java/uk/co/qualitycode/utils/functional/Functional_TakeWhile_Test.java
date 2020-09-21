package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class Functional_TakeWhile_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeWhile(null, mock(List.class)))
                .withMessage("takeWhile(Predicate<T>,List<T>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeWhile(mock(Predicate.class), null))
                .withMessage("takeWhile(Predicate<T>,List<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeWhile(null))
                .withMessage("takeWhile(Predicate<T>): predicate must not be null");
    }

    @Nested
    class DoIt {
        private List<Integer> input;

        @BeforeEach
        void setup() {
            input = Arrays.asList(1, 2, 3, 4, 5);
        }

        @Test
        void takeWhileReturnsEmpty() {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.takeWhile(Functional::isEven, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void takeWhileReturnsAnElement() {
            final List<Integer> expected = Arrays.asList(1);
            final List<Integer> output = Functional.takeWhile(Functional::isOdd, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void takeWhileReturnsSomeElements() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
            final List<Integer> output = Functional.takeWhile(i -> i <= 4, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void takeWhileReturnsAllElements() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.takeWhile(i -> i <= 6, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedTakeWhile() {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.takeWhile(Functional::isEven).apply(input);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Nested
    class Seq {
        @Test
        void seqTakeWhileTest1() {
            final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
            {
                final List<Integer> expected = new ArrayList<>();
                final Iterable<Integer> output = Functional.seq.takeWhile(Functional::isEven, l);
                assertThat(output).containsExactlyElementsOf(expected);
            }
            {
                final List<Integer> expected = Arrays.asList(1);
                final Iterable<Integer> output = Functional.seq.takeWhile(Functional::isOdd, l);
                assertThat(output).containsExactlyElementsOf(expected);
            }
            {
                final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
                final Iterable<Integer> output = Functional.seq.takeWhile(i -> i <= 4, l);
                assertThat(output).containsExactlyElementsOf(expected);
            }
            {
                final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
                final Iterable<Integer> output = Functional.seq.takeWhile(i -> i <= 6, l);
                assertThat(output).containsExactlyElementsOf(expected);
            }
        }

        @Test
        void callHasNextAfterFinishedInSeqTakeWhileTest1() {
            final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
            {
                final List<Integer> expected = Arrays.asList(1);
                final Iterable<Integer> output = Functional.seq.takeWhile(Functional::isOdd, l);
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
                final Iterable<Integer> output = Functional.seq.takeWhile(Functional::isOdd, l);
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
                final Iterable<Integer> output = Functional.seq.takeWhile(Functional::isOdd, l);
                assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
            }
        }

        @Test
        void cantRestartIteratorFromSeqTakeWhileTest1() {
            final List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
            {
                final Iterable<Integer> output = Functional.seq.takeWhile(Functional::isOdd, l);
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
                final List<Integer> expected = new ArrayList<>();
                final Iterable<Integer> output = Functional.seq.takeWhile(Functional::isEven).apply(l);
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
            final Iterable<Integer> integers = Functional.seq.takeWhile(x -> Functional.constant(true).apply(x), input);
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
}