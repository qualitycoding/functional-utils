package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Append_Test {
    @Nested
    class Lazy {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.append(null, mock(Iterable.class)))
                    .withMessage("Lazy.append(T,Iterable<T>): value must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.append(new Object(), null))
                    .withMessage("Lazy.append(T,Iterable<T>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.append(null))
                    .withMessage("Lazy.append(T): value must not be null");
        }

        @Test
        void appendShouldPlaceNewValueAtTheEndOfTheSequence() {
            final int i = 6;
            final Collection<Integer> l = Functional.init(Function.identity(), 5);
            final Iterable<Integer> output = Functional.Lazy.append(i, l);
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedAppendShouldPlaceNewValueAtTheEndOfTheSequence() {
            final int i = 6;
            final Collection<Integer> l = Functional.init(Function.identity(), 5);
            final Iterable<Integer> output = Functional.Lazy.append(i).apply(l);
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void nextThrowsIfThereAreNoMoreElements() {
            final Collection<Integer> l = Collections.singleton(1);
            final Iterable<Integer> output = Functional.Lazy.append(10, l);

            final Iterator<Integer> iterator = output.iterator();
            iterator.next();
            iterator.next();
            assertThatExceptionOfType(RuntimeException.class)
                    .isThrownBy(iterator::next)
                    .withMessage("Lazy.append(T,Iterable<T>): Cannot seek beyond the end of the sequence");
        }

        @Test
        void canConsumeSequenceWithoutCallingHasNext() {
            final int i = 6;
            final Collection<Integer> l = Functional.init(Function.identity(), 5);
            final Iterable<Integer> output = Functional.Lazy.append(i, l);

            final Iterator<Integer> iterator = output.iterator();
            assertThatNoException()
                    .isThrownBy(() -> {
                        iterator.next();
                        iterator.next();
                        iterator.next();
                        iterator.next();
                        iterator.next();
                        iterator.next();
                    });
        }

        @Test
        void tryToRemoveFromAnIterator() {
            final int i = 1;
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> output = Functional.Lazy.append(i, l);
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> output.iterator().remove())
                    .withMessage("Lazy.append(T,Iterable<T>): it is not possible to remove elements from this sequence");
        }

        @Test
        void appendIterableCanOnlyHaveOneIterator() {
            final int i = 1;
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> output = Functional.Lazy.append(i, l);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Should not reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(output::iterator)
                    .withMessage("Lazy.append(T,Iterable<T>): This Iterable does not allow multiple Iterators");
        }
    }
}