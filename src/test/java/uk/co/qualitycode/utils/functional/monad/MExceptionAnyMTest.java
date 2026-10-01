package uk.co.qualitycode.utils.functional.monad;

import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MExceptionAnyMTest extends AnyMContractTest<MException.Witness> {
    private static MException<Integer> failure(final java.lang.String message) {
        return MException.toMException(() -> {
            throw new IllegalStateException(message);
        });
    }

    @Override
    protected <T> AnyM<MException.Witness, T> pure(final T value) {
        return MException.of(value);
    }

    @Override
    protected Stream<AnyM<MException.Witness, Integer>> shapes() {
        return Stream.of(MException.of(3), failure("shape"));
    }

    @Override
    protected AnyM<MException.Witness, Integer> shortCircuiting() {
        return failure("short-circuit");
    }

    @Override
    protected Function<Integer, AnyM<MException.Witness, Integer>> f() {
        return i -> i > 0 ? MException.of(2 * i) : failure("not positive: " + i);
    }

    /**
     * MException has no value equality: two are equivalent when both succeed with equal values, or both fail with
     * the same kind of exception and message.
     */
    @Override
    protected void assertEquivalent(final AnyM<MException.Witness, ?> actual, final AnyM<MException.Witness, ?> expected) {
        assertThat(describe(MException.narrow(actual))).isEqualTo(describe(MException.narrow(expected)));
    }

    private static java.lang.String describe(final MException<?> m) {
        return m.getException()
                .map(ex -> "failed with " + ex.getClass().getName() + ": " + ex.getMessage())
                .getOrElse(() -> "succeeded with " + m.read());
    }

    @Test
    void anExceptionThrownWhileMappingBecomesAFailure() {
        final MException<Integer> mapped = MException.of(1).map(i -> {
            throw new IllegalArgumentException("in map");
        });

        assertThat(mapped.getException().get()).hasMessage("in map");
    }

    @Test
    void sequenceReportsTheFirstFailureInInputOrder() {
        final MException<io.vavr.collection.List<Integer>> result = MException.narrow(
                AnyM.sequence(MException::of, java.util.Arrays.asList(MException.of(1), failure("first"), failure("second"))));

        assertThat(result.getException().get()).hasMessage("first");
    }
}
