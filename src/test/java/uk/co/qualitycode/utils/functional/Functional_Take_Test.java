package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
    class Lazy extends FiniteIterableTest<Integer, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.take(-1, mock(Iterable.class)))
                    .withMessage("Lazy.take(int,Iterable<T>): howMany must not be negative");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.take(1, (Iterable) null))
                    .withMessage("Lazy.take(int,Iterable<T>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.take(-1))
                    .withMessage("Lazy.take(int): howMany must not be negative");
        }

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

        @Override
        protected Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.take(3, l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.take(int,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 3;
        }
    }
}
