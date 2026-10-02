package uk.co.qualitycode.utils.functional.primitive.integer;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.entry;

/**
 * Operations of the primitive-int {@link Functional} that previously had no live tests.
 */
class FunctionalOperationsTest {
    private final IntList ints = new IntList(new int[]{3, 8, 5, 10});
    private final List<Integer> oneToFive = Arrays.asList(1, 2, 3, 4, 5);

    @Nested
    class Searching {
        @Test
        void findReturnsTheFirstMatch() {
            assertThat(Functional.find(x -> x % 2 == 0, ints)).isEqualTo(8);
        }

        @Test
        void findThrowsWhenNothingMatches() {
            assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.find(x -> x > 100, ints));
        }

        @Test
        void findIndexReturnsThePositionOfTheFirstMatch() {
            assertThat(Functional.findIndex(x -> x % 2 == 0, ints)).isEqualTo(1);
        }

        @Test
        void findIndexThrowsWhenNothingMatches() {
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.findIndex(x -> x > 100, ints));
        }

        @Test
        void pickReturnsTheFirstSomeValue() {
            final String picked = Functional.pick(x -> x > 4 ? Option.of("p" + x) : Option.none(), ints);

            assertThat(picked).isEqualTo("p8");
        }

        @Test
        void pickThrowsWhenEveryValueIsNone() {
            assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.pick(x -> Option.none(), ints));
        }
    }

    @Nested
    class Comparisons {
        @ParameterizedTest(name = "{0} vs 5: > {1}, >= {2}, < {3}, <= {4}")
        @CsvSource({"4, false, false, true, true", "5, false, true, false, true", "6, true, true, false, false"})
        void comparePredicatesAgainstTheirBound(final int value, final boolean gt, final boolean gte, final boolean lt, final boolean lte) {
            assertThat(Functional.greaterThan(5).test(value)).isEqualTo(gt);
            assertThat(Functional.greaterThanOrEqual(5).test(value)).isEqualTo(gte);
            assertThat(Functional.lessThan(5).test(value)).isEqualTo(lt);
            assertThat(Functional.lessThanOrEqual(5).test(value)).isEqualTo(lte);
        }
    }

    @Nested
    class SimpleFunctions {
        @Test
        void constantIgnoresItsArgument() {
            assertThat(Functional.constant("k").apply(42)).isEqualTo("k");
        }

        @Test
        void identityReturnsItsArgument() {
            assertThat(Functional.identity().apply(7)).isEqualTo(7);
        }

        @Test
        void countIncrementsTheState() {
            assertThat(Functional.count(4, 99)).isEqualTo(5);
        }
    }

    @Nested
    class Slicing {
        @Test
        void takeReturnsTheFirstElements() {
            assertThat(Functional.take(2, oneToFive)).containsExactly(1, 2);
        }

        @Test
        void takeThrowsWhenTooFewElements() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> Functional.take(9, oneToFive))
                    .withMessage("Cannot take 9 elements from input list with fewer elements");
        }

        @Test
        void takeWhileStopsAtTheFirstFailure() {
            assertThat(Functional.takeWhile((Integer x) -> x < 3, oneToFive)).containsExactly(1, 2);
        }

        @Test
        void skipDropsTheFirstElements() {
            assertThat(Functional.skip(2, oneToFive)).containsExactly(3, 4, 5);
        }

        @Test
        void skipWhileDropsUntilTheFirstFailure() {
            assertThat(Functional.skipWhile((Integer x) -> x < 3, oneToFive)).containsExactly(3, 4, 5);
        }

        @Test
        void takeNAndYieldSplitsTheSequence() {
            final Tuple2<List<Integer>, Iterable<Integer>> split = Functional.takeNAndYield(oneToFive, 2);

            assertThat(split._1()).containsExactly(1, 2);
            assertThat(Functional.toList(split._2())).containsExactly(3, 4, 5);
        }

        @Test
        void takeNAndYieldReturnsAnUnmodifiableList() {
            final List<Integer> taken = Functional.takeNAndYield(oneToFive, 2)._1();

            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> taken.add(6));
        }
    }

    @Nested
    class Building {
        @Test
        void appendAddsTheItemAtTheEnd() {
            assertThat(Functional.append(9, oneToFive)).containsExactly(1, 2, 3, 4, 5, 9);
        }

        @Test
        void appendCanBeTraversedRepeatedly() {
            final Iterable<Integer> appended = Functional.append(9, oneToFive);

            assertThat(Functional.toList(appended)).isEqualTo(Functional.toList(appended));
        }

        @Test
        void collectConcatenatesTheResults() {
            assertThat(Functional.collect((Integer x) -> Arrays.asList(x, -x), Arrays.asList(1, 2))).containsExactly(1, -1, 2, -2);
        }

        @Test
        void unfoldUntilFinished() {
            assertThat(Functional.unfold((Integer b) -> new Tuple2<>("v" + b, b + 1), (Integer b) -> b >= 3, 0))
                    .containsExactly("v0", "v1", "v2");
        }

        @Test
        void unfoldUntilNone() {
            assertThat(Functional.unfold(
                    (Integer b) -> b >= 3 ? Option.<Tuple2<String, Integer>>none() : Option.of(new Tuple2<>("v" + b, b + 1)), 0))
                    .containsExactly("v0", "v1", "v2");
        }
    }

    @Nested
    class Zipping {
        @Test
        void zipPairsElements() {
            assertThat(Functional.zip(Arrays.asList(1, 2), Arrays.asList("a", "b")))
                    .containsExactly(new Tuple2<>(1, "a"), new Tuple2<>(2, "b"));
        }

        @Test
        void zipRejectsDifferentLengths() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.zip(Arrays.asList(1, 2), Collections.singletonList("a")))
                    .withMessage("Functional.zip(Iterable<A>,Iterable<B>): l1 and l2 have differing numbers of elements");
        }

        @Test
        void zip3TriplesElements() {
            assertThat(Functional.zip3(Arrays.asList(1, 2), Arrays.asList("a", "b"), Arrays.asList(true, false)))
                    .containsExactly(new Tuple3<>(1, "a", true), new Tuple3<>(2, "b", false));
        }
    }

    @Nested
    class Collecting {
        @Test
        void groupByCollectsElementsUnderTheirKey() {
            final Map<Integer, List<Integer>> groups = Functional.groupBy((Integer x) -> x % 2, oneToFive);

            assertThat(groups).containsOnly(entry(0, Arrays.asList(2, 4)), entry(1, Arrays.asList(1, 3, 5)));
        }

        @Test
        void toDictionaryMapsEachElement() {
            assertThat(Functional.<Integer, String, Integer>toDictionary(x -> "k" + x, x -> x * 10, ints))
                    .containsOnly(entry("k3", 30), entry("k8", 80), entry("k5", 50), entry("k10", 100));
        }

        @Test
        void toListAndToSetAreUnmodifiable() {
            final List<Integer> list = Functional.toList(oneToFive);
            final Set<Integer> set = Functional.toSet(Arrays.asList(1, 1, 2));

            assertThat(list).containsExactly(1, 2, 3, 4, 5);
            assertThat(set).containsExactlyInAnyOrder(1, 2);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> list.add(6));
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> set.add(3));
        }

        @Test
        void mutableCopiesAreIndependentOfTheirSource() {
            final List<Integer> list = Functional.toMutableList(oneToFive);
            final Set<Integer> set = Functional.toMutableSet(oneToFive);
            final Map<String, Integer> map = Functional.toMutableDictionary(Collections.singletonMap("a", 1));

            list.add(6);
            set.add(6);
            map.put("b", 2);

            assertThat(list).containsExactly(1, 2, 3, 4, 5, 6);
            assertThat(set).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6);
            assertThat(map).containsOnly(entry("a", 1), entry("b", 2));
            assertThat(oneToFive).containsExactly(1, 2, 3, 4, 5);
        }
    }

    @Test
    void predicatesComposeWithTheComparisons() {
        final Predicate<Integer> between = Functional.greaterThan(2).and(Functional.lessThan(5));

        assertThat(oneToFive).filteredOn(between).containsExactly(3, 4);
    }
}
