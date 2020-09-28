package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_Mapi_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.mapi(null, mock(Collection.class)))
                .withMessage("mapi(BiFunction<Integer,A,B>,Collection<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.mapi(mock(BiFunction.class), (Collection) null))
                .withMessage("mapi(BiFunction<Integer,A,B>,Collection<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.mapi(null, mock(Iterable.class)))
                .withMessage("mapi(BiFunction<Integer,A,B>,Iterable<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.mapi(mock(BiFunction.class), (Iterable) null))
                .withMessage("mapi(BiFunction<Integer,A,B>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.mapi(null))
                .withMessage("mapi(BiFunction<Integer,A,B>): f must not be null");
    }

    @Test
    void mapiIterableReturnsListOfPosAndTfm() {
        final List<Tuple2<Integer, String>> output = Functional.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), (Iterable) Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly(new Tuple2<>(0, "1"), new Tuple2<>(1, "2"), new Tuple2<>(2, "3"), new Tuple2<>(3, "4"), new Tuple2<>(4, "5"));
    }

    @Test
    void mapiReturnsListOfPosAndTfm() {
        final List<Tuple2<Integer, String>> output = Functional.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly(new Tuple2<>(0, "1"), new Tuple2<>(1, "2"), new Tuple2<>(2, "3"), new Tuple2<>(3, "4"), new Tuple2<>(4, "5"));
    }

    @Test
    void mapiIterableReturnsImmutableList() {
        final List<Tuple2<Integer, String>> output = Functional.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), (Iterable) Arrays.asList(1, 2, 3, 4, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(new Tuple2<>(1, "1")));
    }

    @Test
    void mapiReturnsImmutableList() {
        final List<Tuple2<Integer, String>> output = Functional.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), Arrays.asList(1, 2, 3, 4, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(new Tuple2<>(1, "1")));
    }

    @Test
    void curriedMapiReturnsListOfPosAndTfm() {
        final List<Tuple2<Integer, String>> output = Functional.mapi((pos, i) -> new Tuple2<>(pos, i.toString())).apply(Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly(new Tuple2<>(0, "1"), new Tuple2<>(1, "2"), new Tuple2<>(2, "3"), new Tuple2<>(3, "4"), new Tuple2<>(4, "5"));
    }

    @Nested
    class Lazy extends IterableResultTest<BiFunction<Integer, Integer, Integer>, Integer, Tuple2<Integer, String>> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.mapi(null, mock(Iterable.class)))
                    .withMessage("Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>): f must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.mapi(mock(BiFunction.class), null))
                    .withMessage("Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.mapi(null))
                    .withMessage("Lazy.mapi(BiFunction<Integer,U,V>): f must not be null");
        }

        @Test
        void mapIntsToStringsWithPositions() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Tuple2<Integer, String>> output = Functional.Lazy.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), input);
            assertThat(Functional.unzip(output)).satisfies(o -> {
                assertThat(o._1).containsExactly(0, 1, 2, 3, 4);
                assertThat(o._2).containsExactly("1", "2", "3", "4", "5");
            });
        }

        @Test
        void curriedMapIntsToStringsWithPositions() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Tuple2<Integer, String>> output = Functional.Lazy.mapi((Integer pos, Integer i) -> new Tuple2<>(pos, i.toString())).apply(input);
            assertThat(Functional.unzip(output)).satisfies(o -> {
                assertThat(o._1).containsExactly(0, 1, 2, 3, 4);
                assertThat(o._2).containsExactly("1", "2", "3", "4", "5");
            });
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        protected Iterable<Tuple2<Integer, String>> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.mapi((Integer pos, Integer i) -> new Tuple2<>(pos, i.toString()), l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 5;
        }
    }
}