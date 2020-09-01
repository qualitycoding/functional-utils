package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.function.Function;
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
                        .isThrownBy(() -> findIndex((Function) null, mock(Iterable.class)))
                        .withMessage("findIndex(Function<A,Boolean>,Iterable<A>): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findIndex((Predicate) null, mock(Iterable.class)))
                        .withMessage("findIndex(Predicate<A>,Iterable<A>): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findIndex((Function) x -> true, null))
                        .withMessage("findIndex(Function<A,Boolean>,Iterable<A>): input must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findIndex((Predicate) x -> true, null))
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
        final Function<Integer, Boolean> equals = trueMatch::equals;
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
        final Function<Integer, Boolean> equals = falseMatch::equals;
        assertThatIllegalArgumentException().isThrownBy(() -> findIndex(equals, li));
    }
}
