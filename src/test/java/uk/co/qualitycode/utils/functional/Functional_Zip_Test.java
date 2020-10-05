package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.asStream;

class Functional_Zip_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(null, Function.identity(), mock(Iterable.class)))
                .withMessage("zip(Function<A,B>,Function<A,B>,Iterable<A>): zipFunc1 must not be null");
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(Function.identity(), null, mock(Iterable.class)))
                .withMessage("zip(Function<A,B>,Function<A,B>,Iterable<A>): zipFunc2 must not be null");
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(Function.identity(), Function.identity(), null))
                .withMessage("zip(Function<A,B>,Function<A,B>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(null, mock(Function.class)))
                .withMessage("zip(Function<A,B>,Function<A,B>): zipFunc1 must not be null");
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(mock(Function.class), null))
                .withMessage("zip(Function<A,B>,Function<A,B>): zipFunc2 must not be null");

        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(null, mock(Iterable.class)))
                .withMessage("zip(Iterable<A>,Iterable<B>): input1 must not be null");
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(mock(Iterable.class), null))
                .withMessage("zip(Iterable<A>,Iterable<B>): input2 must not be null");

    }

    @Test
    void zipTwoFuncs() {
        final List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

        final List<Tuple2<Integer, String>> expected = new ArrayList<>();
        expected.add(new Tuple2<>(2, "1"));
        expected.add(new Tuple2<>(3, "2"));
        expected.add(new Tuple2<>(4, "3"));
        expected.add(new Tuple2<>(5, "4"));
        expected.add(new Tuple2<>(6, "5"));

        final List<Tuple2<Integer, String>> output = Functional.zip(i -> i + 1, Functional.stringify(), ints);

        assertThat(output).containsExactlyElementsOf(expected);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(new Tuple2<>(1, "hjh")));
    }

    @Test
    void curriedZipTwoFuncs() {
        final List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

        final List<Tuple2<Integer, String>> expected = new ArrayList<>();
        expected.add(new Tuple2<>(1, "1"));
        expected.add(new Tuple2<>(2, "2"));
        expected.add(new Tuple2<>(3, "3"));
        expected.add(new Tuple2<>(4, "4"));
        expected.add(new Tuple2<>(5, "5"));

        final List<Tuple2<Integer, String>> output = Functional.zip(Function.<Integer>identity(), Functional.stringify()).apply(ints);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zipTwoIterables() {
        final Iterable<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final List<Tuple2<Integer, Character>> expected = new ArrayList<>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final List<Tuple2<Integer, Character>> output = Functional.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(new Tuple2<>(1, 'd')));
    }

    @Test
    void cannotZipTwoIterablesWithUnequalLengths() {
        final Iterable<Integer> input1 = Arrays.asList(1, 2);
        final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.zip(input1, input2))
                .withMessage("zip(Iterable<A>,Iterable<B>): Cannot zip two iterables with different lengths");
    }

    @Test
    void zipTwoCollections() {
        final List<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final List<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final List<Tuple2<Integer, Character>> expected = new ArrayList<>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final List<Tuple2<Integer, Character>> output = Functional.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(new Tuple2<>(1, 'd')));
    }

    @Test
    void cannotZipTwoCollectionsWithUnequalLengths() {
        final List<Integer> input1 = Arrays.asList(1, 2);
        final Collection<Character> input2 = Collections.singleton('a');

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.zip(input1, input2))
                .withMessage("zip(Collection<A>,Collection<B>): The input sequences must have the same number of elements");
    }

    @Nested
    class Lazy extends FiniteIterableTest<Integer, Integer, Tuple2<Integer, String>> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip(null, Function.identity(), mock(Iterable.class)))
                    .withMessage("Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>): zipFunc1 must not be null");
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip(Function.identity(), null, mock(Iterable.class)))
                    .withMessage("Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>): zipFunc2 must not be null");
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip(Function.identity(), Function.identity(), null))
                    .withMessage("Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>): input must not be null");

            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip(null, mock(Function.class)))
                    .withMessage("Lazy.zip(Function<A,B>,Function<A,B>): zipFunc1 must not be null");
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip(mock(Function.class), null))
                    .withMessage("Lazy.zip(Function<A,B>,Function<A,B>): zipFunc2 must not be null");

            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip(null, mock(Iterable.class)))
                    .withMessage("Lazy.zip(Iterable<A>,Iterable<B>): input1 must not be null");
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.zip(mock(Iterable.class), null))
                    .withMessage("Lazy.zip(Iterable<A>,Iterable<B>): input2 must not be null");
        }

        @Test
        void zipTwoFuncs() {
            final List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

            final List<Tuple2<Integer, String>> expected = new ArrayList<>();
            expected.add(new Tuple2<>(2, "1"));
            expected.add(new Tuple2<>(3, "2"));
            expected.add(new Tuple2<>(4, "3"));
            expected.add(new Tuple2<>(5, "4"));
            expected.add(new Tuple2<>(6, "5"));

            final Iterable<Tuple2<Integer, String>> output = Functional.Lazy.zip(i -> i + 1, Functional.stringify(), ints);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedZipTwoFuncs() {
            final List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

            final List<Tuple2<Integer, String>> expected = new ArrayList<>();
            expected.add(new Tuple2<>(1, "1"));
            expected.add(new Tuple2<>(2, "2"));
            expected.add(new Tuple2<>(3, "3"));
            expected.add(new Tuple2<>(4, "4"));
            expected.add(new Tuple2<>(5, "5"));

            final Iterable<Tuple2<Integer, String>> output = Functional.Lazy.zip(Function.<Integer>identity(), Functional.stringify()).apply(ints);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void zipTwoIterables() {
            final Iterable<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

            final List<Tuple2<Integer, Character>> expected = new ArrayList<>();
            expected.add(new Tuple2<>(1, 'a'));
            expected.add(new Tuple2<>(2, 'b'));
            expected.add(new Tuple2<>(3, 'c'));
            expected.add(new Tuple2<>(4, 'd'));
            expected.add(new Tuple2<>(5, 'e'));

            final Iterable<Tuple2<Integer, Character>> output = Functional.Lazy.zip(input1, input2);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cannotZipTwoIterablesWithUnequalLengths() {
            final Iterable<Integer> input1 = Arrays.asList(1, 2);
            final Iterable<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

            final Iterable<Tuple2<Integer, Character>> zip = Functional.Lazy.zip(input1, input2);

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> asStream(zip).collect(Collectors.toSet()))
                    .withMessage("Lazy.zip(Iterable<A>,Iterable<B>): cannot zip two iterables with different lengths");
        }

        @Override
        protected Iterable<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        protected Iterable<Tuple2<Integer, String>> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.zip(i -> i + 1, Functional.stringify(), l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.zip(Function<A,B>,Function<A,C>,Iterable<A>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 5;
        }
    }
}
