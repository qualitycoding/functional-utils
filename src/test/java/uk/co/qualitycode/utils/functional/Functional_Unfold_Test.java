package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class Functional_Unfold_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.unfold(null, mock(Predicate.class), new Object()))
                .withMessage("unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): unspooler must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.unfold(mock(Function.class), null, new Object()))
                .withMessage("unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): finished must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.unfold((Function<? super Object, Tuple2<Object, Object>>)null, mock(Predicate.class)))
                .withMessage("unfold(Function<B,Tuple2<A,B>>,Predicate<B>): unspooler must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.unfold((Function<? super Object, Tuple2<Object, Object>>)mock(Function.class), null))
                .withMessage("unfold(Function<B,Tuple2<A,B>>,Predicate<B>): finished must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.unfold(null))
                .withMessage("unfold(Function<B,Tuple2<A,B>>): unspooler must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.unfold(null, new Object()))
                .withMessage("unfold(Function<B,Option<Tuple2<A,B>>>,B): unspooler must not be null");
    }

    @Test
    void unfoldSequenceOfIntegersUsingPredicate() {
        final int seed = 1;
        final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
        final Predicate<Integer> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.unfold(doubler, finished, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedUnfoldSequenceOfIntegersUsingPredicateWithSeed() {
        final int seed = 1;
        final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
        final Predicate<Integer> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.unfold(doubler, finished).withSeed(seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedUnfoldSequenceOfIntegersUsingPredicateWithFinishedWithSeed() {
        final int seed = 1;
        final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
        final Predicate<Integer> finished = integer -> integer > 10;

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.unfold(doubler).withFinished(finished).withSeed(seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void unfoldUsingDoublingGenerator() {
        final int seed = 1;
        final Function<Integer, Option<Tuple2<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.none() : Option.of(new Tuple2<>(integer * 2, integer + 1));

        final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        final List<Integer> output = Functional.unfold(doubler, seed);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Nested
    class Lazy {
        @Test
        void seqUnfoldTest1() {
            final int seed = 0;
            final Function<Integer, Tuple2<Integer, Integer>> unspool = integer -> new Tuple2<>(integer + 1, integer + 1);
            final Predicate<Integer> finished = integer -> integer == 10;

            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.unfold(unspool, finished, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cantRemoveFromSeqUnfoldTest1() {
            final int seed = 0;
            final Function<Integer, Tuple2<Integer, Integer>> unspool = integer -> new Tuple2<>(integer + 1, integer + 1);
            final Predicate<Integer> finished = integer -> integer == 10;

            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.unfold(unspool, finished, seed);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqUnfoldTest1() {
            final int seed = 0;
            final Function<Integer, Tuple2<Integer, Integer>> unspool = integer -> new Tuple2<>(integer + 1, integer + 1);
            final Predicate<Integer> finished = integer -> integer == 10;

            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.unfold(unspool, finished, seed);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }

        @Test
        void seqUnfoldTest2() {
            final int seed = 0;
            final Function<Integer, Tuple2<Integer, Integer>> unspool = integer -> new Tuple2<>(integer + 1, integer + 1);
            final Predicate<Integer> finished = integer -> integer == 10;

            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            final Iterable<Integer> output = Functional.Lazy.unfold(unspool, finished, seed);
            final Iterator<Integer> iterator = output.iterator();

            for (int i = 0; i < 20; ++i)
                assertThat(iterator.hasNext()).isTrue();

            for (final int element : expected) {
                final int next = iterator.next();
                assertThat(next).isEqualTo(element);
            }

            assertThat(iterator.hasNext()).isFalse();
            try {
                iterator.next();
            } catch (final NoSuchElementException e) {
                return;
            }

            fail("Should not reach this point");
        }

        @Test
        void seqUnfoldAsDoublingGeneratorTest1() {
            final int seed = 1;
            final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
            final Predicate<Integer> finished = integer -> integer > 10;

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler, finished, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void seqUnfoldAsDoublingGeneratorTest2() {
            final int seed = 1;
            final Function<Integer, Option<Tuple2<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.none() : Option.of(new Tuple2<>(integer * 2, integer + 1));

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cantRemoveFromSeqUnfoldAsDoublingGeneratorTest2() {
            final int seed = 1;
            final Function<Integer, Option<Tuple2<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.none() : Option.of(new Tuple2<>(integer * 2, integer + 1));

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler, seed);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqUnfoldAsDoublingGeneratorTest2() {
            final int seed = 1;
            final Function<Integer, Option<Tuple2<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.none() : Option.of(new Tuple2<>(integer * 2, integer + 1));

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler, seed);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }

        @Test
        void seqUnfoldAsDoublingGeneratorTest3() {
            final int seed = 1;
            final Function<Integer, Option<Tuple2<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.none() : Option.of(new Tuple2<>(integer * 2, integer + 1));

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler, seed);
            final Iterator<Integer> iterator = output.iterator();

            for (int i = 0; i < 20; ++i)
                assertThat(iterator.hasNext()).isTrue();

            for (final int element : expected) {
                final int next = iterator.next();
                assertThat(next).isEqualTo(element);
            }

            assertThat(iterator.hasNext()).isFalse();
            try {
                iterator.next();
            } catch (final NoSuchElementException e) {
                return;
            }

            fail("Should not reach this point");
        }
    }

    @Nested
    class Rec {
        @Test
        void recUnfoldAsDoublingGeneratorTest1() {
            final int seed = 1;
            final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
            final Predicate<Integer> finished = integer -> integer > 10;

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final List<Integer> output = Functional.rec.unfold(doubler, finished, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void recUnfoldAsDoublingGeneratorTest2() {
            final int seed = 1;
            final Function<Integer, Option<Tuple2<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.none() : Option.of(new Tuple2<>(integer * 2, integer + 1));

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final List<Integer> output = Functional.rec.unfold(doubler, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }
}