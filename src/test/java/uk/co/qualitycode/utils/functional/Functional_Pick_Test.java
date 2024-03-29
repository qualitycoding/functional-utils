package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Collection;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.pick;
import static uk.co.qualitycode.utils.functional.assertions.OptionAssert.assertThat;

class Functional_Pick_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> pick(null, mock(Iterable.class)))
                        .withMessage("pick(Function<A,Option<B>>, Iterable<A>): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> pick(Function.identity(), null))
                        .withMessage("pick(Function<A,Option<B>>, Iterable<A>): input must not be null"));
    }

    @Test
    void pickReturnsValue() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Option<Integer> pick = pick(a -> a.equals(trueMatch) ? Option.of(a) : Option.none(), li);
        assertThat(pick).hasValue(trueMatch);
    }

    @Test
    void pickReturnsNone() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(pick(a -> a.equals(falseMatch) ? Option.of(a) : Option.none(), li)).isEmpty();
    }

    @Test
    void curriedPickReturnsValue() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Function<Iterable<Integer>, Option<Integer>> pickFunc = pick(a -> a.equals(trueMatch) ? Option.of(a) : Option.none());
        assertThat(pickFunc.apply(li)).hasValue(trueMatch);
    }
}
