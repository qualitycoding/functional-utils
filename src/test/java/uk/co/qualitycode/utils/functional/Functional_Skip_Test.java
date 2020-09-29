package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_Skip_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(-1, mock(Iterable.class)))
                .withMessage("skip(int,Iterable<T>): howMany must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(1, (Iterable) null))
                .withMessage("skip(int,Iterable<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(-1, mock(List.class)))
                .withMessage("skip(int,List<T>): howMany must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.skip(1, (List) null))
                .withMessage("skip(int,List<T>): input must not be null");

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
    class Lazy extends FiniteIterableTest<Integer, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.skip(-1, mock(Iterable.class)))
                    .withMessage("Lazy.skip(int,Iterable<T>): howMany must not be negative");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.skip(1, (Iterable) null))
                    .withMessage("Lazy.skip(int,Iterable<T>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.skip(-1, mock(Iterable.class)))
                    .withMessage("Lazy.skip(int,Iterable<T>): howMany must not be negative");
        }

        private List<Integer> input;

        @BeforeEach
        void setup() {
            input = Arrays.asList(1, 2, 3, 4, 5);
        }

        @Test
        void skipNone() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.skip(0, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstOne() {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.skip(1, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstOneUsingIterable() {
            final List<Integer> expected = Arrays.asList(2, 3, 4, 5);
            final Iterable<Object> output = Functional.Lazy.skip(1, (Iterable) input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstTwo() {
            final List<Integer> expected = Arrays.asList(3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.skip(2, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstThree() {
            final List<Integer> expected = Arrays.asList(4, 5);
            final Iterable<Integer> output = Functional.Lazy.skip(3, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipFirstFour() {
            final List<Integer> expected = Arrays.asList(5);
            final Iterable<Integer> output = Functional.Lazy.skip(4, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipAll() {
            final List<Integer> expected = new ArrayList<>();
            final Iterable<Integer> output = Functional.Lazy.skip(5, input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void skipAllUsingIterable() {
            final List<Integer> expected = new ArrayList<>();
            final Iterable<Object> output = Functional.Lazy.skip(5, (Iterable) input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedSkipNone() {
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.<Integer>skip(0).apply(input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.skip(4, input);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.skip(int,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 1;
        }
    }
}