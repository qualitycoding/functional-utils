package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_SkipWhile_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skipWhile(null, mock(List.class)))
                .withMessage("skipWhile(Predicate<T>,List<T>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skipWhile(mock(Predicate.class), (List)null))
                .withMessage("skipWhile(Predicate<T>,List<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skipWhile(null, mock(Iterable.class)))
                .withMessage("skipWhile(Predicate<T>,Iterable<T>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skipWhile(mock(Predicate.class), (Iterable)null))
                .withMessage("skipWhile(Predicate<T>,Iterable<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skipWhile(null))
                .withMessage("skipWhile(Predicate<T>): predicate must not be null");
    }

    @Nested
    class DoIt {
        private List<Integer> input;

        @BeforeEach
        void setup() {
            input = Arrays.asList(1, 2, 3, 4, 5);
        }

        @Test
        void skipWhileDropsNone() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional::isEven, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipWhileDropsFirst() {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional::isOdd, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipWhileDropsFirstUsingIterable() {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional::isOdd, (Iterable)input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipWhileDropsSecond() {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final List<Integer> output = Functional.skipWhile(i -> i <= 2, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipWhileDropsAll() {
            final List<Integer> expected = new ArrayList<>();
            final List<Integer> output = Functional.skipWhile(i -> i <= 6, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedSkipWhileDropsNone() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> output = Functional.skipWhile(Functional::isEven).apply(input);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }

    @Nested
    class Lazy extends FiniteIterableTest<Integer, Integer, Integer> {
        private List<Integer> input;

        @BeforeEach
        void setup() {
            input = Arrays.asList(1, 2, 3, 4, 5);
        }

        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.skipWhile(null, mock(Iterable.class)))
                    .withMessage("Lazy.skipWhile(Predicate<T>,Iterable<T>): predicate must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.skipWhile(mock(Predicate.class), (Iterable) null))
                    .withMessage("Lazy.skipWhile(Predicate<T>,Iterable<T>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.skipWhile(null))
                    .withMessage("Lazy.skipWhile(Predicate<T>): predicate must not be null");
        }

        @Test
        void multipleCallsToHasNextDoesNotAdvancePosition() {
            final Iterable<Integer> output = Functional.Lazy.skipWhile(Functional::isEven, input);
            final Iterator<Integer> iterator = output.iterator();
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next()).isEqualTo(1);
        }

        @Test
        void skipWhileDropsNone() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.skipWhile(Functional::isEven, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipWhileDropsFirst() {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.skipWhile(Functional::isOdd, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipWhileDropsFirstUsingIterable() {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.skipWhile(Functional::isOdd, (Iterable) input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipWhileDropsSecond() {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.skipWhile(i -> i <= 2, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipWhileDropsAll() {
            final Iterable<Integer> output = Functional.Lazy.skipWhile(i -> true, input);
            assertThat(output).isEmpty();
        }

        @Test
        void curriedSkipWhileDropsNone() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.skipWhile(Functional::isEven).apply(input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.skipWhile(i -> i <= 2, input);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.skipWhile(Predicate<T>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 3;
        }
    }
}