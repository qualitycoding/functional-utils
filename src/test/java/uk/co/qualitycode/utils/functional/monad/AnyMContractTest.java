package uk.co.qualitycode.utils.functional.monad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The contract every {@link AnyM} must satisfy: the three monad laws, and the derived operations agreeing with
 * {@link AnyM#flatMap} and {@link AnyM#unit}. A monad's test class extends this and supplies the hooks.
 *
 * @param <W> the witness of the monad under test
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AnyMContractTest<W> {
    /**
     * @return the monad's unit
     */
    protected abstract <T> AnyM<W, T> pure(T value);

    /**
     * @return one instance of every shape the monad can take (e.g. some and none; success and failure)
     */
    protected abstract Stream<AnyM<W, Integer>> shapes();

    /**
     * @return the "empty" or "failed" shape, which must short-circuit
     */
    protected abstract AnyM<W, Integer> shortCircuiting();

    /**
     * A function that produces both shapes depending on its input: a positive input {@code i} gives
     * {@code pure(2 * i)}; zero and negative inputs give the short-circuiting shape.
     */
    protected abstract Function<Integer, AnyM<W, Integer>> f();

    /**
     * Two monads are equivalent when they would be indistinguishable to a caller. Equality by default; override for
     * monads without value equality.
     */
    protected void assertEquivalent(final AnyM<W, ?> actual, final AnyM<W, ?> expected) {
        assertThat(actual).isEqualTo(expected);
    }

    private Function<Integer, AnyM<W, Integer>> g() {
        return i -> pure(i + 100);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 5})
    void leftIdentity(final int a) {
        assertEquivalent(pure(a).flatMap(f()), f().apply(a));
    }

    @ParameterizedTest
    @MethodSource("shapes")
    void rightIdentity(final AnyM<W, Integer> m) {
        assertEquivalent(m.flatMap(m::unit), m);
    }

    @ParameterizedTest
    @MethodSource("shapes")
    void associativity(final AnyM<W, Integer> m) {
        assertEquivalent(m.flatMap(f()).flatMap(g()), m.flatMap(x -> f().apply(x).flatMap(g())));
    }

    @ParameterizedTest
    @MethodSource("shapes")
    void mapAgreesWithFlatMapAndUnit(final AnyM<W, Integer> m) {
        assertEquivalent(m.map(i -> i * 3), m.flatMap(i -> m.unit(i * 3)));
    }

    @ParameterizedTest
    @MethodSource("shapes")
    void mapWithIdentityChangesNothing(final AnyM<W, Integer> m) {
        assertEquivalent(m.map(Function.identity()), m);
    }

    @ParameterizedTest
    @MethodSource("shapes")
    void apAppliesTheContainedFunction(final AnyM<W, Integer> m) {
        assertEquivalent(m.ap(pure((Function<Integer, Integer>) i -> i + 1)), m.map(i -> i + 1));
    }

    @Test
    void zipWithCombinesTwoValues() {
        assertEquivalent(pure(2).zipWith(pure(3), Integer::sum), pure(5));
    }

    @Test
    void zipWithShortCircuitsOnEitherSide() {
        assertEquivalent(shortCircuiting().zipWith(pure(3), Integer::sum), shortCircuiting());
        assertEquivalent(pure(2).zipWith(shortCircuiting(), Integer::sum), shortCircuiting());
    }

    @Test
    void flattenRemovesOneLevelOfNesting() {
        assertEquivalent(AnyM.flatten(pure(pure(7))), pure(7));
    }

    @Test
    void sequenceCollectsValuesInOrder() {
        assertEquivalent(AnyM.sequence(this::pure, Arrays.asList(pure(1), pure(2), pure(3))),
                pure(io.vavr.collection.List.of(1, 2, 3)));
    }

    @Test
    void sequenceOfNothingIsUnitOfTheEmptyList() {
        assertEquivalent(AnyM.<W, Integer>sequence(this::pure, Arrays.asList()), pure(io.vavr.collection.List.empty()));
    }

    @Test
    void sequenceShortCircuits() {
        assertEquivalent(AnyM.sequence(this::pure, Arrays.asList(pure(1), shortCircuiting(), pure(3))), shortCircuiting());
    }

    @Test
    void traverseMapsThenSequences() {
        assertEquivalent(AnyM.traverse(this::pure, Arrays.asList(1, 2), f()), pure(io.vavr.collection.List.of(2, 4)));
    }
}
