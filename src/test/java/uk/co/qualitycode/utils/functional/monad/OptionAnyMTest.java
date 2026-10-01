package uk.co.qualitycode.utils.functional.monad;

import java.util.function.Function;
import java.util.stream.Stream;

class OptionAnyMTest extends AnyMContractTest<Option.Witness> {
    @Override
    protected <T> AnyM<Option.Witness, T> pure(final T value) {
        return Option.of(value);
    }

    @Override
    protected Stream<AnyM<Option.Witness, Integer>> shapes() {
        return Stream.of(Option.of(3), Option.none());
    }

    @Override
    protected AnyM<Option.Witness, Integer> shortCircuiting() {
        return Option.none();
    }

    @Override
    protected Function<Integer, AnyM<Option.Witness, Integer>> f() {
        return i -> i > 0 ? Option.of(2 * i) : Option.none();
    }
}
