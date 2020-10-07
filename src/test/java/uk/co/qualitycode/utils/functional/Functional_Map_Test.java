package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.map;
import static uk.co.qualitycode.utils.functional.Functional.stringify;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Map_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(null, new ArrayList<>()))
                .withMessage("map(Function<A,B>,Collection<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(i -> i, null))
                .withMessage("map(Function<A,B>,Collection<A>): input must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(null, mock(Iterable.class)))
                .withMessage("map(Function<A,B>,Iterable<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(i -> i, (Iterable) null))
                .withMessage("map(Function<A,B>,Iterable<A>): input must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(null))
                .withMessage("map(Function<A,B>): f must not be null");

    }

    @Test
    void mapIterableOfIntsToStrings() {
        final List<String> output = map(stringify(), Functional.Lazy.init(doublingGenerator, 5));
        assertThat(output).containsExactly("2", "4", "6", "8", "10");
    }

    @Test
    void mapIntsToStrings() {
        final List<String> output = map(stringify(), Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void mapIterableReturnsImmutableList() {
        final List<String> output = map(stringify(), Functional.Lazy.init(doublingGenerator, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add("ighijh"));
    }

    @Test
    void mapIntsToStringsReturnsImmutableList() {
        final List<String> output = map(stringify(), Arrays.asList(1, 2, 3, 4, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add("ighijh"));
    }

    @Test
    void curriedMapIntsToStrings() {
        final Function<Iterable<Object>, Iterable<String>> mapFunc = map(stringify());
        final Iterable<String> output = mapFunc.apply(Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Nested
    class Lazy extends FiniteIterableTest<Function<Integer, String>, Integer, String> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.map(null, mock(Iterable.class)))
                    .withMessage("Lazy.map(Function<T,R>,Iterable<T>): f must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.map(mock(Function.class), null))
                    .withMessage("Lazy.map(Function<T,R>,Iterable<T>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.map(null))
                    .withMessage("Lazy.map(Function<T,R>): f must not be null");
        }

        @Test
        void mapIntsToStrings() {
            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
            final Iterable<String> output = Functional.Lazy.map(Functional.stringify(), input);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedMapIntsToStrings() {
            final Function<Iterable<Object>, Iterable<String>> mapFunc = Functional.Lazy.map(stringify());
            final Iterable<String> output = mapFunc.apply(Arrays.asList(1, 2, 3, 4, 5));
            assertThat(output).containsExactly("1", "2", "3", "4", "5");
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Functional.init(Function.identity(), 5);
        }

        @Override
        protected Iterable<String> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.map(stringify(), l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.map(Function<T,R>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 5;
        }
    }

    @Nested
    class Rec {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Rec.map(null, mock(Iterable.class)))
                    .withMessage("Rec.map(Function<A,B>,Iterable<A>): f must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Rec.map(i -> i, null))
                    .withMessage("Rec.map(Function<A,B>,Iterable<A>): input must not be null");
        }

        @Test
        void mapIntsToStrings() {
            final Iterable<String> output = Functional.Rec.map(stringify(), Arrays.asList(1, 2, 3, 4, 5));
            assertThat(output).containsExactly("1", "2", "3", "4", "5");
        }
    }
}
