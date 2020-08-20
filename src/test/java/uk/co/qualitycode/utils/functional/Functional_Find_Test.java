package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import static uk.co.qualitycode.utils.functional.Functional.find;
import static uk.co.qualitycode.utils.functional.Functional.init;
import static uk.co.qualitycode.utils.functional.OptionAssert.assertThat;

class Functional_Find_Test {
    @Test
    void findTestUsingFunction() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = init(FunctionalTest.doublingGenerator, 5);

        final Function<Integer,Boolean> equals = trueMatch::equals;

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
        final Function<Integer,Boolean> equals = trueMatch::equals;

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
