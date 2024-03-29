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
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Filter_Test {

    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(null, mock(Iterable.class)))
                .withMessage("filter(Predicate<A>,Iterable<A>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(mock(Predicate.class), (Iterable) null))
                .withMessage("filter(Predicate<A>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(null, mock(Collection.class)))
                .withMessage("filter(Predicate<A>,Collection<A>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(mock(Predicate.class), null))
                .withMessage("filter(Predicate<A>,Collection<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(null))
                .withMessage("filter(Predicate<A>): predicate must not be null");
    }

    @Test
    void filterCollectionForOddElements() {
        final List<Integer> l = Functional.init(doublingGenerator, 5);
        final List<Integer> oddElems = Functional.filter(Functional::isOdd, l);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void filterIterableForOddElements() {
        final Iterable<Integer> l = Functional.Lazy.init(doublingGenerator, 5);
        final List<Integer> oddElems = Functional.filter(Functional::isOdd, l);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void curriedFilterForOddElements() {
        final List<Integer> l = Functional.init(doublingGenerator, 5);
        final List<Integer> oddElems = Functional.filter(Functional::isOdd).apply(l);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void filterCollectionForEvenElements() {
        final List<Integer> l = Functional.init(doublingGenerator, 5);
        final List<Integer> evenElems = Functional.filter(Functional::isEven, l);

        assertThat(evenElems).containsExactlyElementsOf(l);
    }

    @Test
    void filterIterableForEvenElements() {
        final List<Integer> evenElems = Functional.filter(Functional::isEven, Functional.Lazy.init(doublingGenerator, 5));

        assertThat(evenElems).containsExactlyElementsOf(Functional.Lazy.init(doublingGenerator, 5));
    }

    @Test
    void curriedFilterForEvenElements() {
        final List<Integer> l = Functional.init(doublingGenerator, 5);
        final List<Integer> evenElems = Functional.filter(Functional::isEven).apply(l);

        assertThat(evenElems).containsExactlyElementsOf(l);
    }

    @Nested
    class Lazy extends FiniteIterableTest<Integer, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.filter(null, mock(Iterable.class)))
                    .withMessage("Lazy.filter(Predicate<T>,Iterable<T>): predicate must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.filter(mock(Predicate.class), null))
                    .withMessage("Lazy.filter(Predicate<T>,Iterable<T>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.filter(null))
                    .withMessage("Lazy.filter(Predicate<T>): predicate must not be null");
        }

        @Test
        void filterReturnsEvens() {
            final Collection<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> evenElems = Functional.Lazy.filter(Functional::isEven, l);

            assertThat(evenElems).containsExactly(2, 4);
        }

        @Test
        void curriedFilterReturnsEvens() {
            final Collection<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> evenElems = Functional.Lazy.filter(Functional::isEven).apply(l);

            assertThat(evenElems).containsExactly(2, 4);
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.filter(Functional::isOdd, l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.filter(Predicate<T>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 3;
        }
    }

    @Nested
    class Rec {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Rec.filter(null, mock(Iterable.class)))
                    .withMessage("Rec.filter(Predicate<A>,Iterable<A>): predicate must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Rec.filter(mock(Predicate.class), (Iterable<?>) null))
                    .withMessage("Rec.filter(Predicate<A>,Iterable<A>): input must not be null");
        }

        @Test
        void filterForOddElements() {
            final Iterable<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> oddElems = Functional.Rec.filter(Functional::isOdd, l);

            assertThat(oddElems).isEmpty();
        }

        @Test
        void filterForEvenElements() {
            final Iterable<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> evenElems = Functional.Rec.filter(Functional::isEven, l);

            assertThat(evenElems).containsExactlyElementsOf(l);
        }
    }
}