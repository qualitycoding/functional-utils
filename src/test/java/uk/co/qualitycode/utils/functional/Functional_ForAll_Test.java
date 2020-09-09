package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_ForAll_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll(null, mock(Iterable.class)))
                .withMessage("forAll(Predicate<A>,Iterable<A>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll(mock(Predicate.class), (Iterable) null))
                .withMessage("forAll(Predicate<A>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll(null, mock(Collection.class)))
                .withMessage("forAll(Predicate<A>,Collection<A>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll(mock(Predicate.class), (Collection) null))
                .withMessage("forAll(Predicate<A>,Collection<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll(null))
                .withMessage("forAll(Predicate<A>): predicate must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll2(null, mock(Iterable.class), mock(Iterable.class)))
                .withMessage("forAll2(BiFunction<A,B,Boolean>,Iterable<A>,Iterable<B>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll2(mock(BiFunction.class), (Iterable) null, mock(Iterable.class)))
                .withMessage("forAll2(BiFunction<A,B,Boolean>,Iterable<A>,Iterable<B>): input1 must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.forAll2(mock(BiFunction.class), mock(Iterable.class), (Iterable) null))
                .withMessage("forAll2(BiFunction<A,B,Boolean>,Iterable<A>,Iterable<B>): input2 must not be null");
    }

    @Nested
    class ForAll {
        @Test
        void forAllWithIterableReturnsTrue() {
            final Iterable<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            assertThat(Functional.forAll(Functional::isEven, l)).isTrue();
        }

        @Test
        void forAllWithIterableReturnsFalse() {
            final Iterable<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);
            assertThat(Functional.forAll(Functional::isEven, m)).isFalse();
        }

        @Test
        void forAllWithCollectionReturnsTrue() {
            final List<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            assertThat(Functional.forAll(Functional::isEven, l)).isTrue();
        }

        @Test
        void forAllWithCollectionReturnsFalse() {
            final List<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);
            assertThat(Functional.forAll(Functional::isEven, m)).isFalse();
        }
    }

    @Nested
    class ForAll2 {
        @Test
        void forAll2ReturnsTrue() {
            final List<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            final List<Integer> m = Functional.init(FunctionalTest.quadruplingGenerator, 5);
            assertThat(Functional.forAll2(FunctionalTest::bothAreEven, l, m)).isTrue();
        }

        @Test
        void forAll2ReturnsFalse() {
            final List<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            final List<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);

            assertThat(Functional.forAll2(FunctionalTest::bothAreEven, l, m)).isFalse();
        }

        @Test
        void forAll2ThrowsWhenTheSequencesHaveDifferentLengths() {
            final List<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            final List<Integer> m = Functional.init(FunctionalTest.quadruplingGenerator, 7);

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.forAll2(FunctionalTest::bothAreEven, l, m))
                    .withMessage("forAll2(BiFunction<A,B,Boolean>,Iterable<A>,Iterable<B>): Cannot compare two sequences with different numbers of elements");
        }
    }
}