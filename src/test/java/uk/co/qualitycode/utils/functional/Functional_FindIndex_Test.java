package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.findIndex;

class Functional_FindIndex_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findIndex(null, mock(Iterable.class)))
                        .withMessage("findIndex(Predicate<A>,Iterable<A>): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findIndex(x -> true, null))
                        .withMessage("findIndex(Predicate<A>,Iterable<A>): input must not be null")
        );
    }

    @Test
    void findIndexTestWithPredicate() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(findIndex(trueMatch::equals, li)).isEqualTo(2);
    }

    @Test
    void findIndexTestWithFunction() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Predicate<Integer> equals = trueMatch::equals;
        assertThat(findIndex(equals, li)).isEqualTo(2);
    }

    @Test
    void findIndexTestThrowsWhenThereIsNoMatchWithPredicate() {
        final Integer falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThatIllegalArgumentException().isThrownBy(() -> findIndex(falseMatch::equals, li));
    }

    @Test
    void findIndexTestThrowsWhenThereIsNoMatchWithFunction() {
        final Integer falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Predicate<Integer> equals = falseMatch::equals;
        assertThatIllegalArgumentException().isThrownBy(() -> findIndex(equals, li));
    }
}
