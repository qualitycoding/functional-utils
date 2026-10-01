package uk.co.qualitycode.utils.functional.primitive.integer;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * The int specialisations of operations that previously existed only for containers of references.
 */
class FunctionalIntSpecialisationsTest {
    private final IntList xs = ints(1, 2, 3, 10, 4, 5);

    private static IntList ints(final int... values) {
        return new IntList(values);
    }

    @Nested
    class TakeWhile {
        @Test
        void takesTheLongestPrefixSatisfyingThePredicate() {
            assertThat(Functional.takeWhile(x -> x < 3, xs).toArray()).containsExactly(1, 2);
        }

        @Test
        void takesEverythingWhenThePredicateAlwaysHolds() {
            assertThat(Functional.takeWhile(x -> true, xs).toArray()).containsExactly(1, 2, 3, 10, 4, 5);
        }

        @Test
        void takesNothingFromAnEmptySequence() {
            assertThat(Functional.takeWhile(x -> true, ints()).toArray()).isEmpty();
        }

        @Test
        void stopsEvaluatingAtTheFirstFailure() {
            final AtomicInteger evaluations = new AtomicInteger();

            Functional.takeWhile(x -> evaluations.incrementAndGet() > 0 && x < 3, xs);

            assertThat(evaluations).hasValue(3);
        }

        @Test
        void acceptsASinglePassSequence() {
            assertThat(Functional.takeWhile(x -> x > 3, Iterators.reverse(xs)).toArray()).containsExactly(5, 4, 10);
        }

        @Test
        void rejectsNullInput() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.takeWhile(x -> true, (IntIterable) null))
                    .withMessage("Functional.takeWhile(Predicate_int,IntIterable): input must not be null");
        }
    }

    @Nested
    class SkipWhile {
        @Test
        void dropsTheLongestPrefixSatisfyingThePredicate() {
            assertThat(Functional.skipWhile(x -> x < 3, xs).toArray()).containsExactly(3, 10, 4, 5);
        }

        @Test
        void dropsEverythingWhenThePredicateAlwaysHolds() {
            assertThat(Functional.skipWhile(x -> true, xs).toArray()).isEmpty();
        }

        @Test
        void rejectsANullPredicate() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.skipWhile((Predicate_int) null, xs))
                    .withMessage("Functional.skipWhile(Predicate_int,IntIterable): predicate must not be null");
        }
    }

    @Test
    void groupByPreservesOrderWithinEachGroup() {
        final Map<Integer, IntList> groups = Functional.groupBy(x -> x % 3, xs);

        assertThat(groups).containsOnlyKeys(0, 1, 2);
        assertThat(groups.get(0).toArray()).containsExactly(3);
        assertThat(groups.get(1).toArray()).containsExactly(1, 10, 4);
        assertThat(groups.get(2).toArray()).containsExactly(2, 5);
    }

    @Nested
    class Unfold {
        @Test
        void emitsAValuePerStateUntilFinished() {
            assertThat(Functional.unfold(s -> s * s, s -> s + 1, s -> s * s >= 30, 1).toArray())
                    .containsExactly(1, 4, 9, 16, 25);
        }

        @Test
        void isEmptyWhenTheSeedIsAlreadyFinished() {
            assertThat(Functional.unfold(s -> s, s -> s + 1, s -> true, 0).toArray()).isEmpty();
        }
    }

    @Nested
    class Zipping {
        @Test
        void zipPairsElements() {
            assertThat(Functional.zip(ints(1, 2), ints(3, 4))).containsExactly(new Tuple2<>(1, 3), new Tuple2<>(2, 4));
        }

        @Test
        void zipRejectsDifferentLengths() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.zip(ints(1, 2), ints(3)))
                    .withMessage("Functional.zip(IntIterable,IntIterable): cannot zip sequences of different lengths");
        }

        @Test
        void zip3TriplesElements() {
            assertThat(Functional.zip3(ints(1, 2), ints(3, 4), ints(5, 6)))
                    .containsExactly(new Tuple3<>(1, 3, 5), new Tuple3<>(2, 4, 6));
        }

        @Test
        void zip3RejectsDifferentLengths() {
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(ints(1, 2), ints(3, 4), ints(5)));
        }

        @Test
        void zippedPairsAreUnmodifiable() {
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> Functional.zip(ints(1), ints(2)).add(new Tuple2<>(3, 4)));
        }

        @Test
        void unzipSeparatesPairs() {
            final Tuple2<IntList, IntList> unzipped = Functional.unzip(Arrays.asList(new Tuple2<>(1, 2), new Tuple2<>(3, 4)));

            assertThat(unzipped._1().toArray()).containsExactly(1, 3);
            assertThat(unzipped._2().toArray()).containsExactly(2, 4);
        }

        @Test
        void unzip3SeparatesTriples() {
            final Tuple3<IntList, IntList, IntList> unzipped =
                    Functional.unzip3(Arrays.asList(new Tuple3<>(1, 2, 3), new Tuple3<>(4, 5, 6)));

            assertThat(unzipped._1().toArray()).containsExactly(1, 4);
            assertThat(unzipped._2().toArray()).containsExactly(2, 5);
            assertThat(unzipped._3().toArray()).containsExactly(3, 6);
        }

        @Test
        void unzipUndoesZip() {
            final Tuple2<IntList, IntList> roundTrip = Functional.unzip(Functional.zip(ints(1, 2, 3), ints(4, 5, 6)));

            assertThat(roundTrip._1().toArray()).containsExactly(1, 2, 3);
            assertThat(roundTrip._2().toArray()).containsExactly(4, 5, 6);
        }
    }

    @Nested
    class FlatMap {
        @Test
        void concatenatesTheSequenceForEachElement() {
            assertThat(Functional.flatMap(x -> ints(x, -x), ints(1, 2)).toArray()).containsExactly(1, -1, 2, -2);
        }

        @Test
        void isEmptyWhenEveryElementMapsToNothing() {
            assertThat(Functional.flatMap(x -> ints(), xs).toArray()).isEmpty();
        }
    }

    @Test
    void foldAndChooseFoldsWhileChoosing() {
        final Tuple2<Integer, IntList> result = Functional.foldAndChoose(
                (Integer sum, int x) -> new Tuple2<>(sum + x, x % 2 == 0 ? Option_int.of(x) : Option_int.none()), 0, xs);

        assertThat(result._1()).isEqualTo(25);
        assertThat(result._2().toArray()).containsExactly(2, 10, 4);
    }

    @Nested
    class SortWith {
        @Test
        void sortsWithTheComparison() {
            assertThat(Functional.sortWith((x, y) -> Integer.compare(y, x), xs).toArray()).containsExactly(10, 5, 4, 3, 2, 1);
        }

        @Test
        void isStable() {
            assertThat(Functional.sortWith((x, y) -> Integer.compare(x % 2, y % 2), xs).toArray())
                    .containsExactly(2, 10, 4, 1, 3, 5);
        }

        @Test
        void leavesTheInputUnchanged() {
            Functional.sortWith((x, y) -> Integer.compare(y, x), xs);

            assertThat(xs.toArray()).containsExactly(1, 2, 3, 10, 4, 5);
        }
    }

    @Test
    void anIntComparatorWorksAsAnIntBinaryOperator() {
        final java.util.function.IntBinaryOperator difference = (Func2_int_int_int) (x, y) -> x - y;

        assertThat(difference.applyAsInt(7, 2)).isEqualTo(5);
    }

    @Nested
    class EagerIntToInt {
        @Test
        void mapWithATypedLambdaTransformsToInts() {
            assertThat(Functional.map((int x) -> x * x, ints(1, 2, 3)).toArray()).containsExactly(1, 4, 9);
        }

        @Test
        void mapiPassesTheZeroBasedIndex() {
            assertThat(Functional.mapi((int i, int x) -> i * 100 + x, ints(5, 6, 7)).toArray()).containsExactly(5, 106, 207);
        }

        @Test
        void takeStopsAtTheEnd() {
            assertThat(Functional.take(2, ints(1, 2, 3)).toArray()).containsExactly(1, 2);
            assertThat(Functional.take(5, ints(1, 2)).toArray()).containsExactly(1, 2);
        }

        @Test
        void skipDropsTheFirstElements() {
            assertThat(Functional.skip(2, ints(1, 2, 3)).toArray()).containsExactly(3);
            assertThat(Functional.skip(5, ints(1, 2)).toArray()).isEmpty();
        }

        @Test
        void appendAddsAtTheEnd() {
            assertThat(Functional.append(9, ints(1, 2)).toArray()).containsExactly(1, 2, 9);
        }

        @Test
        void rejectNegativeCounts() {
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.take(-1, ints(1)))
                    .withMessage("Functional.take(int,IntIterable): howMany must not be negative");
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.skip(-1, ints(1)))
                    .withMessage("Functional.skip(int,IntIterable): howMany must not be negative");
        }
    }
}
