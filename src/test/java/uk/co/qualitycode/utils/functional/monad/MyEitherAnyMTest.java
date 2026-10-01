package uk.co.qualitycode.utils.functional.monad;

import java.util.function.Function;
import java.util.stream.Stream;

class MyEitherAnyMTest extends AnyMContractTest<MyEither.Witness<java.lang.String>> {
    @Override
    protected <T> AnyM<MyEither.Witness<java.lang.String>, T> pure(final T value) {
        return MyEither.right(value);
    }

    @Override
    protected Stream<AnyM<MyEither.Witness<java.lang.String>, Integer>> shapes() {
        return Stream.of(MyEither.right(3), MyEither.left("left"));
    }

    @Override
    protected AnyM<MyEither.Witness<java.lang.String>, Integer> shortCircuiting() {
        return MyEither.left("short-circuit");
    }

    @Override
    protected Function<Integer, AnyM<MyEither.Witness<java.lang.String>, Integer>> f() {
        return i -> i > 0 ? MyEither.right(2 * i) : MyEither.left("not positive: " + i);
    }
}
