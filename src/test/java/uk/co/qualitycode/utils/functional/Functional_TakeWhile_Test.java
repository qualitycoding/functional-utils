package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_TakeWhile_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeWhile(null, mock(Iterable.class)))
                .withMessage("takeWhile(Predicate<T>,Iterable<T>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeWhile(mock(Predicate.class), (Iterable) null))
                .withMessage("takeWhile(Predicate<T>,Iterable<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeWhile(null, mock(List.class)))
                .withMessage("takeWhile(Predicate<T>,List<T>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeWhile(mock(Predicate.class), (List) null))
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
        void takeWhileReturnsAnElementUsingIterable() {
            final List<Integer> expected = Arrays.asList(1);
            final List<Integer> output = Functional.takeWhile(Functional::isOdd, (Iterable) input);
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
    class Lazy extends FiniteIterableTest<Integer, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.takeWhile(null, mock(Iterable.class)))
                    .withMessage("Lazy.takeWhile(Predicate<T>,Iterable<T>): predicate must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.takeWhile(mock(Predicate.class), (Iterable) null))
                    .withMessage("Lazy.takeWhile(Predicate<T>,Iterable<T>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.takeWhile(null))
                    .withMessage("Lazy.takeWhile(Predicate<T>): predicate must not be null");
        }

        private List<Integer> input;

        @BeforeEach
        void setup() {
            input = Arrays.asList(1, 2, 3, 4, 5);
        }

        @Test
        void takeWhileReturnsEmpty() {
            final List<Integer> expected = new ArrayList<>();
            final Iterable<Integer> output = Functional.Lazy.takeWhile(Functional::isEven, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void takeWhileReturnsAnElement() {
            final List<Integer> expected = Arrays.asList(1);
            final Iterable<Integer> output = Functional.Lazy.takeWhile(Functional::isOdd, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void takeWhileReturnsAnElementUsingIterable() {
            final List<Integer> expected = Arrays.asList(1);
            final Iterable<Integer> output = Functional.Lazy.takeWhile(Functional::isOdd, (Iterable) input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void takeWhileReturnsSomeElements() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
            final Iterable<Integer> output = Functional.Lazy.takeWhile(i -> i <= 4, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void takeWhileReturnsAllElements() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.takeWhile(i -> i <= 6, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedTakeWhile() {
            final List<Integer> expected = new ArrayList<>();
            final Iterable<Integer> output = Functional.Lazy.takeWhile(Functional::isEven).apply(input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.takeWhile(i -> i <= 4, input);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.takeWhile(Predicate<T>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 4;
        }
    }
}
