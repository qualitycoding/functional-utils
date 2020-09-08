package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class Functional_ForAll_Test {
    @Test
    void forAll2ReturnsTrue() {
        final List<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final List<Integer> m = Functional.init(FunctionalTest.quadruplingGenerator, 5);
        assertThat(Functional.forAll2(FunctionalTest::bothAreEven, l, m)).isTrue();
    }

    @Test
    void forAll2ReturnsFalse() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);

        assertThat(Functional.forAll2(FunctionalTest::bothAreEven, l, m)).isFalse();
    }

    @Test
    void forAll2ThrowsWhenTheSequencesHaveDifferentLengths() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(FunctionalTest.quadruplingGenerator, 7);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll2(FunctionalTest::bothAreEven, l, m))
                .withMessage("forAll2(BiFunction<A,B,Boolean>,Iterable<A>,Iterable<B>): Cannot compare two sequences with different numbers of elements");
    }
}
