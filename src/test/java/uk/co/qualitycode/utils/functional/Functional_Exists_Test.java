package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_Exists_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.exists(null, mock(Iterable.class)))
                .withMessage("exists(Predicate<T>,Iterable<T>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.exists(mock(Predicate.class), (Iterable) null))
                .withMessage("exists(Predicate<T>,Iterable<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.exists(null, mock(Collection.class)))
                .withMessage("exists(Predicate<T>,Collection<T>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.exists(mock(Predicate.class), (Collection) null))
                .withMessage("exists(Predicate<T>,Collection<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.exists(null))
                .withMessage("exists(Predicate<T>): predicate must not be null");
    }

    @Test
    void existsInIterable() {
        final Iterable<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional::isEven, i);
        final boolean allOdd = Functional.exists(Functional::isOdd, i);

        assertThat(allOdd).isFalse();
        assertThat(anEven).isTrue();
    }

    @Test
    void existsInCollection() {
        final Collection<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional::isEven, i);
        final boolean allOdd = Functional.exists(Functional::isOdd, i);

        assertThat(allOdd).isFalse();
        assertThat(anEven).isTrue();
    }

    @Test
    void curriedExistsTest() {
        final Collection<Integer> i = Arrays.asList(2, 4, 6);

        final boolean anEven = Functional.exists(Functional::isEven).test(i);
        final boolean allOdd = Functional.exists(Functional::isOdd).test(i);

        assertThat(allOdd).isFalse();
        assertThat(anEven).isTrue();
    }

    @Nested
    class Not {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.not(null))
                    .withMessage("not(Predicate<A>): predicate must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.not2(null))
                    .withMessage("not2(BiPredicate<A,B>): predicate must not be null");
        }

        @Test
        void notReturnsInversePredicate() {
            final List<Integer> i = Arrays.asList(2, 4, 6);

            final boolean anEven = Functional.exists(Functional::isEven, i);
            final boolean notAnEven = Functional.exists(Functional.not(Functional::isEven), i);

            assertThat(notAnEven).isFalse();
            assertThat(anEven).isTrue();
        }

        @Test
        void not2ReturnsInversePredicate() {
            final List<Integer> i = Arrays.asList(2, 4, 6);

            final boolean anEven = Functional.exists(Functional::isEven, i);
            final boolean notAnEven = Functional.forAll2(Functional.not2(FunctionalTest::bothAreEven), i, i);

            assertThat(notAnEven).isFalse();
            assertThat(anEven).isTrue();
        }
    }
}
