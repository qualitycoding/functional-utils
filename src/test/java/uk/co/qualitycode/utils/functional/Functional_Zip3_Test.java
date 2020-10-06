package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple3;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.asStream;

class Functional_Zip3_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(null, mock(Iterable.class), mock(Iterable.class)))
                        .withMessage("zip3(Iterable<A>,Iterable<B>,Iterable<C>): input1 must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(mock(Iterable.class), null, mock(Iterable.class)))
                        .withMessage("zip3(Iterable<A>,Iterable<B>,Iterable<C>): input2 must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(mock(Iterable.class), mock(Iterable.class), null))
                        .withMessage("zip3(Iterable<A>,Iterable<B>,Iterable<C>): input3 must not be null"));
    }

    @Test
    void zip3WithThreeIterables() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Tuple3<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(new Tuple3<>(1, 'a', 1.0));
        expected.add(new Tuple3<>(2, 'b', 2.0));
        expected.add(new Tuple3<>(3, 'c', 2.5));
        expected.add(new Tuple3<>(4, 'd', 3.0));
        expected.add(new Tuple3<>(5, 'e', 3.5));

        final List<Tuple3<Integer, Character, Double>> output = Functional.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(()->output.add(new Tuple3<>(1,'l',2.0)));
    }

    @Test
    void cannotZip3UsingIterablesWithUnequalLengths() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> {
                            final Iterable<Integer> input1 = Arrays.asList(1, 2);
                            final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
                            final Iterable<Character> input3 = Arrays.asList('a', 'b', 'c', 'd', 'e');
                            Functional.zip3(input1, input2, input3);
                        })
                        .withMessage("zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> {
                            final Iterable<Integer> input1 = Arrays.asList(1, 2);
                            final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
                            final Iterable<Integer> input3 = Arrays.asList(1, 2);
                            Functional.zip3(input1, input2, input3);
                        })
                        .withMessage("zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> {
                            final Iterable<Integer> input1 = Arrays.asList(1, 2);
                            final Iterable<Integer> input2 = Arrays.asList(1, 2);
                            final Iterable<Character> input3 = Arrays.asList('a', 'b', 'c', 'd', 'e');
                            Functional.zip3(input1, input2, input3);
                        })
                        .withMessage("zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths"));
    }

    @Nested
    class Lazy extends FiniteIterableTest<Character, Double, Tuple3<Integer, Character, Double>> {
        @Test
        void preconditions() {
            assertAll(
                    () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip3(null, mock(Iterable.class), mock(Iterable.class)))
                            .withMessage("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): input1 must not be null"),
                    () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip3(mock(Iterable.class), null, mock(Iterable.class)))
                            .withMessage("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): input2 must not be null"),
                    () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip3(mock(Iterable.class), mock(Iterable.class), null))
                            .withMessage("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): input3 must not be null"));
        }

        @Test
        void zip3WithThreeIterables() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
            final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

            final Collection<Tuple3<Integer, Character, Double>> expected = new ArrayList<>();
            expected.add(new Tuple3<>(1, 'a', 1.0));
            expected.add(new Tuple3<>(2, 'b', 2.0));
            expected.add(new Tuple3<>(3, 'c', 2.5));
            expected.add(new Tuple3<>(4, 'd', 3.0));
            expected.add(new Tuple3<>(5, 'e', 3.5));

            final Iterable<Tuple3<Integer, Character, Double>> output = Functional.Lazy.zip3(input1, input2, input3);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cannotZip3UsingIterablesWithUnequalLengths() {
            assertAll(
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> {
                                final Iterable<Integer> input1 = Arrays.asList(1, 2);
                                final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
                                final Iterable<Character> input3 = Arrays.asList('a', 'b', 'c', 'd', 'e');
                                asStream(Functional.Lazy.zip3(input1, input2, input3)).collect(Collectors.toSet());
                            })
                            .withMessage("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> {
                                final Iterable<Integer> input1 = Arrays.asList(1, 2);
                                final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
                                final Iterable<Integer> input3 = Arrays.asList(1, 2);
                                asStream(Functional.Lazy.zip3(input1, input2, input3)).collect(Collectors.toSet());
                            })
                            .withMessage("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> {
                                final Iterable<Integer> input1 = Arrays.asList(1, 2);
                                final Iterable<Integer> input2 = Arrays.asList(1, 2);
                                final Iterable<Character> input3 = Arrays.asList('a', 'b', 'c', 'd', 'e');
                                asStream(Functional.Lazy.zip3(input1, input2, input3)).collect(Collectors.toSet());
                            })
                            .withMessage("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths"));
        }

        @Override
        protected Iterable<Double> initialValues() {
            return Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);
        }

        @Override
        protected Iterable<Tuple3<Integer, Character, Double>> testFunction(final Iterable<Double> input3) {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
            return Functional.Lazy.zip3(input1, input2, input3);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 5;
        }
    }
}