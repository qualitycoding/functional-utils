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
import static uk.co.qualitycode.utils.functional.OptionAssert.assertThat;

class Functional_FindLast_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Function) null, mock(Iterable.class))).withMessage("f must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Function) x -> true, (Iterable) null)).withMessage("input must not be null"),

                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Function) null, mock(List.class))).withMessage("f must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Function) x -> true, (List) null)).withMessage("input must not be null"),

                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Predicate) null, mock(Iterable.class))).withMessage("f must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Predicate) x -> true, (Iterable) null)).withMessage("input must not be null"),

                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Predicate) null, mock(List.class))).withMessage("f must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Predicate) x -> true, (List) null)).withMessage("input must not be null"),

                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Predicate) null).apply(mock(Iterable.class))).withMessage("f must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Predicate) null).apply(mock(List.class))).withMessage("f must not be null"),

                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Function) null).apply(mock(Iterable.class))).withMessage("f must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> findLast((Function) null).apply(mock(List.class))).withMessage("f must not be null")
        );
    }

    @Nested
    class FindLastFromListWithFunction {
        @Test
        void findLastWithFunctionReturnsEmpty() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast(Functional.isOdd, l)).isEmpty();
        }

        @Test
        void findLastWithFunctionReturnsValue() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast(Functional.isEven, l)).hasValue(10);
        }

        @Test
        void curriedFindLastWithFunctionReturnsValue() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            final Function<Iterable<Integer>, Option<Integer>> lastFunc = findLast(Functional.isEven);
            assertThat(lastFunc.apply(l)).hasValue(10);
        }
    }

    @Nested
    class FindLastFromIterableWithFunction {
        @Test
        void findLastIterableWithFunctionReturnsEmpty() {
            final Iterable<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast(Functional.isOdd, l)).isEmpty();
        }

        @Test
        void findLastIterableWithFunctionReturnsValue() {
            final Iterable<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast(Functional.isEven, l)).hasValue(10);
        }

        @Test
        void curriedFindLastIterableWithFunctionReturnsValue() {
            final Iterable<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            final Function<Iterable<Integer>, Option<Integer>> lastFunc = findLast(Functional.isEven);
            assertThat(lastFunc.apply(l)).hasValue(10);
        }
    }

    @Nested
    class FindLastFromListWithPredicate {
        @Test
        void findLastWithFunctionReturnsEmpty() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast((Predicate<Integer>)x->Functional.isOdd.apply(x), l)).isEmpty();
        }

        @Test
        void findLastWithFunctionReturnsValue() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast((Predicate<Integer>)x->Functional.isEven.apply(x), l)).hasValue(10);
        }

        @Test
        void curriedFindLastWithFunctionReturnsValue() {
            final List<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            final Function<Iterable<Integer>, Option<Integer>> lastFunc = findLast((Predicate<Integer>)x->Functional.isEven.apply(x));
            assertThat(lastFunc.apply(l)).hasValue(10);
        }
    }

    @Nested
    class FindLastFromIterableWithPredicate {
        @Test
        void findLastIterableWithFunctionReturnsEmpty() {
            final Iterable<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast((Predicate<Integer>)x->Functional.isOdd.apply(x), l)).isEmpty();
        }

        @Test
        void findLastIterableWithFunctionReturnsValue() {
            final Iterable<Integer> l = new ArrayList<>(Functional.init(FunctionalTest.doublingGenerator, 5));
            assertThat(findLast((Predicate<Integer>)x->Functional.isEven.apply(x), l)).hasValue(10);
        }

        @Test
        void curriedFindLastIterableWithFunctionReturnsValue() {
            final Iterable<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
            final Function<Iterable<Integer>, Option<Integer>> lastFunc = findLast((Predicate<Integer>)x->Functional.isEven.apply(x));
            assertThat(lastFunc.apply(l)).hasValue(10);
        }
    }
}
