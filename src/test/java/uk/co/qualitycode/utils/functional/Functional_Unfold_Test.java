package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
    class Lazy extends InfiniteIterableTest<Integer, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.unfold(null, mock(Predicate.class), new Object()))
                    .withMessage("Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): unspooler must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.unfold(mock(Function.class), null, new Object()))
                    .withMessage("Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): finished must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.unfold((Function<? super Object, Tuple2<Object, Object>>) null, mock(Predicate.class)))
                    .withMessage("Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>): unspooler must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.unfold((Function<? super Object, Tuple2<Object, Object>>) mock(Function.class), null))
                    .withMessage("Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>): finished must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.unfold(null))
                    .withMessage("Lazy.unfold(Function<B,Tuple2<A,B>>): unspooler must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.unfold(null, new Object()))
                    .withMessage("Lazy.unfold(Function<B,Option<Tuple2<A,B>>>,B): unspooler must not be null");
        }

        @Test
        void unfoldSequenceOfIntegersUsingPredicate() {
            final int seed = 1;
            final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
            final Predicate<Integer> finished = integer -> integer > 10;

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler, finished, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedUnfoldSequenceOfIntegersUsingPredicateWithSeed() {
            final int seed = 1;
            final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
            final Predicate<Integer> finished = integer -> integer > 10;

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler, finished).withSeed(seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedUnfoldSequenceOfIntegersUsingPredicateWithFinishedWithSeed() {
            final int seed = 1;
            final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
            final Predicate<Integer> finished = integer -> integer > 10;

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler).withFinished(finished).withSeed(seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void unfoldUsingDoublingGenerator() {
            final int seed = 1;
            final Function<Integer, Option<Tuple2<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.none() : Option.of(new Tuple2<>(integer * 2, integer + 1));

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final Iterable<Integer> output = Functional.Lazy.unfold(doubler, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Override
        protected Collection<Integer> initialValues() {
            return null;
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            final int seed = 1;
            final Function<Integer, Tuple2<Integer, Integer>> doubler = integer -> new Tuple2<>(integer * 2, integer + 1);
            final Predicate<Integer> finished = integer -> integer > 10;
            return Functional.Lazy.unfold(doubler, finished, seed);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 10;
        }
    }

    @Nested
    class Rec {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Rec.unfold(null, mock(Predicate.class), new Object()))
                    .withMessage("Rec.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): unspooler must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Rec.unfold(mock(Function.class), null, new Object()))
                    .withMessage("Rec.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): finished must not be null");

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
            final Iterable<Integer> output = Functional.Rec.unfold(doubler, finished, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void unfoldUsingDoublingGenerator() {
            final int seed = 1;
            final Function<Integer, Option<Tuple2<Integer, Integer>>> doubler = integer -> integer > 10 ? Option.none() : Option.of(new Tuple2<>(integer * 2, integer + 1));

            final List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
            final List<Integer> output = Functional.Rec.unfold(doubler, seed);
            assertThat(output).containsExactlyElementsOf(expected);
        }
    }
}