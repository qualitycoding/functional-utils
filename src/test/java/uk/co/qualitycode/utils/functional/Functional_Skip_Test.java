package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.mock;

class Functional_Skip_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(-1, mock(Iterable.class)))
                .withMessage("skip(int,Iterable<T>): howMany must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(1, (Iterable)null))
                .withMessage("skip(int,Iterable<T>): input must not be negative");

         assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(-1, mock(List.class)))
                .withMessage("skip(int,List<T>): howMany must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(1, (List)null))
                .withMessage("skip(int,List<T>): input must not be negative");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(-1))
                .withMessage("skip(int): howMany must not be negative");
    }

    @Nested
    class DoIt {
        private List<Integer> input;

        @BeforeEach
        void setup() {
            input = Arrays.asList(1, 2, 3, 4, 5);
        }

        @Test
        void skipNone() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skip(0, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstOne() {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.skip(1, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstOneUsingIterable() {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.skip(1, (Iterable)input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstTwo() {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final List<Integer> output = Functional.skip(2, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstThree() {
            final List<Integer> expected = Arrays.asList(4, 5);
            final List<Integer> output = Functional.skip(3, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstFour() {
            final List<Integer> expected = Arrays.asList(5);
            final List<Integer> output = Functional.skip(4, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipAll() {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.skip(5, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipAllUsingIterable() {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.skip(5, (Iterable)input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedSkipNone() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.<Integer>skip(0).apply(input);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Nested
    class Seq {
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
                final List<Integer> expected = new ArrayList<>();
                final Iterable<Integer> output = Functional.seq.skip(5, l);
                assertThat(output).containsExactlyElementsOf(expected);
            }
            {
                final List<Integer> expected = new ArrayList<>();
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
}