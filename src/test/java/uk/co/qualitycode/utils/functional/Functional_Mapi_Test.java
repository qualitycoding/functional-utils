package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
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
    class Lazy {
        @Test
        void seqMapiTest1() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Tuple2<Integer, String>> output = Functional.Lazy.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), input);
            assertThat(Functional.unzip(output)).satisfies(o -> {
                assertThat(o._1).containsExactly(0, 1, 2, 3, 4);
                assertThat(o._2).containsExactly("1", "2", "3", "4", "5");
            });
        }

        @Test
        void cantRestartIteratorInSeqMapiTest1() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Tuple2<Integer, String>> output = Functional.Lazy.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), input);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }

        @Test
        void curriedSeqMapiTest1() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Tuple2<Integer, String>> output = Functional.Lazy.mapi((BiFunction<Integer, Integer, Tuple2<Integer, String>>) (pos, i) -> new Tuple2<>(pos, i.toString())).apply(input);
            assertThat(Functional.unzip(output)).satisfies(o -> {
                assertThat(o._1).containsExactly(0, 1, 2, 3, 4);
                assertThat(o._2).containsExactly("1", "2", "3", "4", "5");
            });
        }

        @Test
        void cantRemoveFromSeqMapiTest1() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Tuple2<Integer, String>> output = Functional.Lazy.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), input);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void seqMapiTest2() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Tuple2<Integer, String>> mapi = Functional.Lazy.mapi((pos, i) -> new Tuple2<>(pos, i.toString()), input);
            final Iterator<Tuple2<Integer, String>> iterator = mapi.iterator();

            for (int i = 0; i < 10; ++i)
                assertThat(iterator.hasNext()).isTrue();

            Tuple2<Integer, String> next = iterator.next();
            assertThat(next._2()).isEqualTo("1");
            next = iterator.next();
            assertThat(next._2()).isEqualTo("2");
            next = iterator.next();
            assertThat(next._2()).isEqualTo("3");
            next = iterator.next();
            assertThat(next._2()).isEqualTo("4");
            next = iterator.next();
            assertThat(next._2()).isEqualTo("5");

            assertThat(iterator.hasNext()).isFalse();
            try {
                iterator.next();
            } catch (final NoSuchElementException e) {
                return;
            }

            fail("Should not reach this point");
        }
    }
}