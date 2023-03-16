package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.findLast;
import static uk.co.qualitycode.utils.functional.assertions.OptionAssert.assertThat;

class Functional_FindLast_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findLast(null, mock(Iterable.class)))
                        .withMessage("findLast(Predicate<A>,Iterable<A>): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findLast(x -> true, (Iterable) null))
                        .withMessage("findLast(Predicate<A>,Iterable<A>): input must not be null"),

                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findLast(null, mock(List.class)))
                        .withMessage("findLast(Predicate<A>,List<A>): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findLast(x -> true, (List) null))
                        .withMessage("findLast(Predicate<A>,List<A>): input must not be null"),

                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> findLast(null).apply(mock(Iterable.class)))
                        .withMessage("findLast(Predicate<A>): f must not be null")
        );
    }

    @Nested
    class FindLastFromListWithFunction {
        @Test
        void findLastWithFunctionReturnsEmpty() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast(Functional::isOdd, l)).isEmpty();
        }

        @Test
        void findLastWithFunctionReturnsValue() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast(Functional::isEven, l)).hasValue(10);
        }

        @Test
        void curriedFindLastWithFunctionReturnsValue() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            final Function<Iterable<Integer>, Option<Integer>> lastFunc = findLast(Functional::isEven);
            assertThat(lastFunc.apply(l)).hasValue(10);
        }
    }

    @Nested
    class FindLastFromIterableWithFunction {
        @Test
        void findLastIterableWithFunctionReturnsEmpty() {
            final Iterable<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast(Functional::isOdd, l)).isEmpty();
        }

        @Test
        void findLastIterableWithFunctionReturnsValue() {
            final Iterable<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast(Functional::isEven, l)).hasValue(10);
        }

        @Test
        void curriedFindLastIterableWithFunctionReturnsValue() {
            final Iterable<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            final Function<Iterable<Integer>, Option<Integer>> lastFunc = findLast(Functional::isEven);
            assertThat(lastFunc.apply(l)).hasValue(10);
        }
    }

    @Nested
    class FindLastFromListWithPredicate {
        @Test
        void findLastWithFunctionReturnsEmpty() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast((Predicate<Integer>) Functional::isOdd, l)).isEmpty();
        }

        @Test
        void findLastWithFunctionReturnsValue() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast((Predicate<Integer>) Functional::isEven, l)).hasValue(10);
        }

        @Test
        void curriedFindLastWithFunctionReturnsValue() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            final Function<Iterable<Integer>, Option<Integer>> lastFunc = findLast((Predicate<Integer>) Functional::isEven);
            assertThat(lastFunc.apply(l)).hasValue(10);
        }
    }

    @Nested
    class FindLastFromIterableWithPredicate {
        @Test
        void findLastIterableWithFunctionReturnsEmpty() {
            final Iterable<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast((Predicate<Integer>) Functional::isOdd, l)).isEmpty();
        }

        @Test
        void findLastIterableWithFunctionReturnsValue() {
            final Iterable<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast((Predicate<Integer>) Functional::isEven, l)).hasValue(10);
        }

        @Test
        void curriedFindLastIterableWithFunctionReturnsValue() {
            final Iterable<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            final Function<Iterable<Integer>, Option<Integer>> lastFunc = findLast((Predicate<Integer>) Functional::isEven);
            assertThat(lastFunc.apply(l)).hasValue(10);
        }
    }
}
