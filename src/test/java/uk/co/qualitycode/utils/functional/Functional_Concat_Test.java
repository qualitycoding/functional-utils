package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

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
    class Lazy extends IterableResultTest<Iterable<Integer>, Integer, Integer> {
        @Test
        void seqConcatTest1() {
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

    @Nested
    class Set {
        @Test
        void setConcatTest1() {
            final java.util.Set<Integer> input = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            final Function<Integer, Integer> doubler = i -> i * 2;
            final java.util.Set<String> expected = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "8", "10"));

            final java.util.Set<String> strs = Functional.set.map(Functional.stringify(), input);
            final java.util.Set<String> output = Functional.set.concat(strs, Functional.set.map(Functional.stringify(), Functional.set.map(doubler, input)));

            assertThat(expected.containsAll(output)).isTrue();
            assertThat(output.containsAll(expected)).isTrue();
        }
    }
}
