package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_Concat_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.concat(null, mock(Iterable.class)))
                .withMessage("concat(Iterable<T>,Iterable<T>): input1 must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.concat(mock(Iterable.class), null))
                .withMessage("concat(Iterable<T>,Iterable<T>): input2 must not be null");
    }

    @Test
    void concatTwoLists() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
        assertThat(Functional.concat(input, input)).containsExactlyElementsOf(expected);
    }

    @Nested
    class Lazy extends FiniteIterableTest<Iterable<Integer>, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.concat(null, mock(Iterable.class)))
                    .withMessage("Lazy.concat(Iterable<T>,Iterable<T>): input1 must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.concat(mock(Iterable.class), null))
                    .withMessage("Lazy.concat(Iterable<T>,Iterable<T>): input2 must not be null");
        }

        @Test
        void concatTwoSequencesOfIntegers() {
            final List<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final List<Integer> input2 = Arrays.asList(6, 7, 8, 9, 10);
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

            final Iterable<Integer> output = Functional.Lazy.concat(input1, input2);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.concat(Arrays.asList(1, 2, 3, 4, 5), l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.concat(Iterable<T>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 10;
        }
    }
}
