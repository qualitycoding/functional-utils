package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThat;

class Functional_LazyPartition_Test {
    private static final List<Integer> ONE_TO_SIX = Arrays.asList(1, 2, 3, 4, 5, 6);

    private static <T> io.vavr.collection.List<T> drain(final Iterable<T> sequence) {
        return io.vavr.collection.List.ofAll(sequence);
    }

    @Nested
    class MatchingHalf extends FiniteIterableTest<Integer, Integer, Integer> {
        @Override
        protected Iterable<Integer> initialValues() {
            return ONE_TO_SIX;
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.partition(Functional::isEven, l)._1();
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.partition(Predicate<T>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 3;
        }
    }

    @Nested
    class RemainingHalf extends FiniteIterableTest<Integer, Integer, Integer> {
        @Override
        protected Iterable<Integer> initialValues() {
            return ONE_TO_SIX;
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.partition(Functional::isEven, l)._2();
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.partition(Predicate<T>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 3;
        }
    }

    @Test
    void agreesWithTheEagerPartition() {
        final Tuple2<Iterable<Integer>, Iterable<Integer>> lazy = Functional.Lazy.partition(Functional::isEven, ONE_TO_SIX);
        final Tuple2<List<Integer>, List<Integer>> eager = Functional.partition(Functional::isEven, ONE_TO_SIX);

        assertThat(drain(lazy._1())).containsExactlyElementsOf(eager._1());
        assertThat(drain(lazy._2())).containsExactlyElementsOf(eager._2());
    }

    @Test
    void eitherHalfMayBeConsumedFirst() {
        final Tuple2<Iterable<Integer>, Iterable<Integer>> halves = Functional.Lazy.partition(Functional::isEven, ONE_TO_SIX);

        assertThat(drain(halves._2())).containsExactly(1, 3, 5);
        assertThat(drain(halves._1())).containsExactly(2, 4, 6);
    }

    @Test
    void halvesMayBeConsumedAlternately() {
        final Tuple2<Iterable<Integer>, Iterable<Integer>> halves = Functional.Lazy.partition(Functional::isEven, ONE_TO_SIX);
        final Iterator<Integer> evens = halves._1().iterator();
        final Iterator<Integer> odds = halves._2().iterator();

        assertThat(Arrays.asList(odds.next(), evens.next(), evens.next(), odds.next(), odds.next(), evens.next()))
                .containsExactly(1, 2, 4, 3, 5, 6);
    }

    @Test
    void evaluatesNothingUntilAHalfIsIterated() {
        final AtomicInteger evaluations = new AtomicInteger();

        Functional.Lazy.partition(i -> evaluations.incrementAndGet() > 0, ONE_TO_SIX);

        assertThat(evaluations).hasValue(0);
    }

    @Test
    void evaluatesThePredicateOncePerElement() {
        final AtomicInteger evaluations = new AtomicInteger();
        final Tuple2<Iterable<Integer>, Iterable<Integer>> halves =
                Functional.Lazy.partition(i -> evaluations.incrementAndGet() > 0 && i % 2 == 0, ONE_TO_SIX);

        drain(halves._1());
        drain(halves._2());

        assertThat(evaluations).hasValue(6);
    }

    @Test
    void readsAnInfiniteInputOnlyAsFarAsNeeded() {
        final AtomicInteger evaluations = new AtomicInteger();
        final Iterable<Integer> evens = Functional.Lazy.partition(
                i -> evaluations.incrementAndGet() > 0 && i % 2 == 0, Functional.Lazy.init(i -> i))._1();

        assertThat(drain(Functional.Lazy.take(3, evens))).containsExactly(2, 4, 6);
        assertThat(evaluations).hasValue(6);
    }

    @Test
    void keepsNullElements() {
        final Tuple2<Iterable<String>, Iterable<String>> halves =
                Functional.Lazy.partition(s -> s == null, Arrays.asList("a", null, "b"));

        assertThat(drain(halves._1())).containsExactly((String) null);
        assertThat(drain(halves._2())).containsExactly("a", "b");
    }

    @Test
    void curriedFormAppliesLater() {
        assertThat(drain(Functional.Lazy.<Integer>partition(Functional::isEven).apply(ONE_TO_SIX)._2())).containsExactly(1, 3, 5);
    }

    @Test
    void preconditions() {
        assertThatNullPointerException().isThrownBy(() -> Functional.Lazy.partition(null, ONE_TO_SIX))
                .withMessage("Lazy.partition(Predicate<T>,Iterable<T>): predicate must not be null");
        assertThatNullPointerException().isThrownBy(() -> Functional.Lazy.partition(Functional::isEven, (Iterable<Integer>) null))
                .withMessage("Lazy.partition(Predicate<T>,Iterable<T>): input must not be null");
        assertThatNullPointerException().isThrownBy(() -> Functional.Lazy.partition((java.util.function.Predicate<Integer>) null))
                .withMessage("Lazy.partition(Predicate<T>): predicate must not be null");
    }
}
