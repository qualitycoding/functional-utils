package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.co.qualitycode.utils.functional.Functional.find;
import static uk.co.qualitycode.utils.functional.Functional.init;
import static uk.co.qualitycode.utils.functional.assertions.OptionAssert.assertThat;

class Functional_Find_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> find(null, Collections.emptySet()))
                        .withMessage("find(Predicate<A>,Iterable<A>): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> find(x -> x.equals(new Object()), null))
                        .withMessage("find(Predicate<A>,Iterable<A>): input must not be null"));
    }

    @Test
    void findTestUsingFunction() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = init(FunctionalTest.doublingGenerator, 5);

        final Predicate<Integer> equals = trueMatch::equals;

        assertThat(find(equals, li)).hasValue(trueMatch);
    }

    @Test
    void findTestUsingPredicate() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = init(FunctionalTest.doublingGenerator, 5);

        final Predicate<Integer> equals = trueMatch::equals;

        assertThat(find(equals, li)).hasValue(trueMatch);
    }

    @Test
    void curriedFindTestUsingFunction() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = init(FunctionalTest.doublingGenerator, 5);
        final Predicate<Integer> equals = trueMatch::equals;

        final Function<Iterable<Integer>, Option<Integer>> result = find(equals);

        assertThat(result.apply(li)).hasValue(trueMatch);
    }

    @Test
    void curriedFindTestUsingPredicate() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = init(FunctionalTest.doublingGenerator, 5);
        final Predicate<Integer> equals = trueMatch::equals;

        final Function<Iterable<Integer>, Option<Integer>> result = find(equals);

        assertThat(result.apply(li)).hasValue(trueMatch);
    }

    @Test
    void findResultHasNoValue() {
        final Integer falseMatch = 7;
        final Collection<Integer> li = init(FunctionalTest.doublingGenerator, 5);

        final Option<Integer> actual = find(falseMatch::equals, li);

        assertThat(actual).isEmpty();
    }
}
