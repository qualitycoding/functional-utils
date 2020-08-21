package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class Functional_FindIndex_Test {
    @Test
    void findIndexTestWithPredicate() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.findIndex(trueMatch::equals, li)).isEqualTo(2);
    }
    @Test
    void findIndexTestWithFunction() {
        final Integer trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Function<Integer,Boolean> equals = trueMatch::equals;
        assertThat(Functional.findIndex(equals, li)).isEqualTo(2);
    }

    @Test
    void findIndexTestThrowsWhenThereIsNoMatchWithPredicate() {
        final Integer falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.findIndex(falseMatch::equals, li));
    }

    @Test
    void findIndexTestThrowsWhenThereIsNoMatchWithFunction() {
        final Integer falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Function<Integer,Boolean> equals = falseMatch::equals;
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.findIndex(equals, li));
    }
}
