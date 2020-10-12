package uk.co.qualitycode.utils.functional;

import io.vavr.control.Either;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.assertions.OptionAssert;
import uk.co.qualitycode.utils.functional.monad.Option;
import uk.co.qualitycode.utils.functional.primitive.integer.Func_int_int;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.co.qualitycode.utils.functional.Functional.isEven;
import static uk.co.qualitycode.utils.functional.Functional.stringify;

class FunctionalTest {
    public static Func_int_int doublingGenerator_f = a -> 2 * a;
    public static Function<Integer, Integer> doublingGenerator = a -> 2 * a;
    public static Function<Integer, Integer> triplingGenerator = a -> 3 * a;
    public static Function<Integer, Integer> quadruplingGenerator = a -> 4 * a;

    @Nested
    class ConvertFlatMap {
        @Test
        void preconditions() {
            assertAll(
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.ConvertFlatMapOptionalToFlatMapVavrOption.convert(null))
                            .withMessage("convert(Function<T,Optional<R>>): tfm must not be null"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.ConvertFlatMapVavrOptionToFlatMapOptional.convert(null))
                            .withMessage("convert(Function<T,Option<R>>): tfm must not be null"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.ConvertFlatMapVavrOptionToFlatMapOption.convert(null))
                            .withMessage("convert(Function<T,Option<R>>): tfm must not be null"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.ConvertFlatMapOptionalToFlatMapOption.convert(null))
                            .withMessage("convert(Function<T,Optional<R>>): tfm must not be null"));
        }

        @Test
        void convertOptionalAcceptsTfmThatReturnsNull() {
            assertThat(Functional.ConvertFlatMapOptionalToFlatMapVavrOption.convert(o -> null).apply(new Object())).isEmpty();
        }

        @Test
        void convertOptionAcceptsTfmThatReturnsNull() {
            assertThat(Functional.ConvertFlatMapVavrOptionToFlatMapOptional.convert(o -> null).apply(new Object())).isEmpty();
        }

        @Test
        void convertOptionalToOptionAcceptsTfmThatReturnsNull() {
            OptionAssert.assertThat(Functional.ConvertFlatMapOptionalToFlatMapOption.convert(o -> null).apply(new Object())).isEmpty();
        }

        @Test
        void convertOptionToOptionAcceptsTfmThatReturnsNull() {
            OptionAssert.assertThat(Functional.ConvertFlatMapVavrOptionToFlatMapOption.convert(o -> null).apply(new Object())).isEmpty();
        }

        @Test
        void convertFlatMapFnFromOptional() {
            final Function<Integer, Optional<Integer>> f1 = Optional::of;
            final Function<Integer, io.vavr.control.Option<Integer>> f2 = io.vavr.control.Option::some;

            final int expected = 2;
            final Optional<Integer> i1 = Optional.of(expected);
            final io.vavr.control.Option<Integer> i2 = io.vavr.control.Option.some(expected);
            assertThat(i1.flatMap(f1)).hasValue(expected);
            assertThat(i2.flatMap(f2)).contains(expected);
            assertThat(Functional.ConvertFlatMapOptionalToFlatMapVavrOption.convert(f1).apply(expected)).contains(expected);
        }

        @Test
        void convertFlatMapFnFromOption() {
            final Function<Integer, io.vavr.control.Option<Integer>> f1 = io.vavr.control.Option::of;
            final Function<Integer, Optional<Integer>> f2 = Optional::of;

            final int expected = 2;
            final io.vavr.control.Option<Integer> i1 = io.vavr.control.Option.some(expected);
            final Optional<Integer> i2 = Optional.of(expected);
            assertThat(i1.flatMap(f1)).contains(expected);
            assertThat(i2.flatMap(f2)).hasValue(expected);
            assertThat(Functional.ConvertFlatMapVavrOptionToFlatMapOptional.convert(f1).apply(expected)).contains(expected);
        }

        @Test
        void convertFlatMapFnFromOptionalToOption() {
            final Function<Integer, Optional<Integer>> f1 = Optional::of;
            final Function<Integer, Optional<Integer>> f2 = Optional::of;

            final int expected = 2;
            final Optional<Integer> i1 = Optional.of(expected);
            final Option<Integer> i2 = Option.of(expected);
            assertThat(i1.flatMap(f1)).hasValue(expected);
            assertThat(i2.toJavaOptional().flatMap(f2)).contains(expected);
            OptionAssert.assertThat(Functional.ConvertFlatMapOptionalToFlatMapOption.convert(f1).apply(expected)).hasValue(expected);
        }

        @Test
        void convertFlatMapFnFromOptionToOption() {
            final Function<Integer, io.vavr.control.Option<Integer>> f1 = io.vavr.control.Option::of;
            final Function<Integer, Optional<Integer>> f2 = Optional::of;

            final int expected = 2;
            final io.vavr.control.Option<Integer> i1 = io.vavr.control.Option.some(expected);
            final Optional<Integer> i2 = Optional.of(expected);
            assertThat(i1.flatMap(f1)).contains(expected);
            assertThat(i2.flatMap(f2)).hasValue(expected);
            OptionAssert.assertThat(Functional.ConvertFlatMapVavrOptionToFlatMapOption.convert(f1).apply(expected)).hasValue(expected);
        }
    }

    @Test
    void convertToString() {
        assertThat(stringify(1)).isEqualTo("1");

//        Mockito cannot verify toString()
//        toString() is too often used behind of scenes  (i.e. during String concatenation, in IDE debugging views). Verifying it may give inconsistent or hard to understand results. Not to mention that verifying toString() most likely hints awkward design (hard to explain in a short exception message. Trust me...)
//        However, it is possible to stub toString(). Stubbing toString() smells a bit funny but there are rare, legitimate use cases.

//        final Object obj = mock(Object.class);
//        stringify(obj);
//        verify(obj).toString();
    }

    @Test
    void factoryFunctionThrowsWhenGivenNullFunction() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.$(null))
                .withMessage("$(FunctionWithExceptionDeclaration<A,R,E>): fn must not be null");
    }

    @Test
    void factoryFunctionReturningEitherWrappingValue() {
        final Function<Object, Either<Exception, Object>> value = Functional.$(a -> a);
        final Object obj = new Object();

//        VavrAssertions.assertThat(value.apply(obj)).satisfies(v -> {
//            VavrAssertions.assertThat(v).isRight().containsOnRight(obj);
//        });

        final Either<Exception, Object> result = value.apply(obj);
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo(obj);
    }

    @Test
    void factoryFunctionReturningEitherWrappingException() {
        final Exception exception = new RuntimeException();
        final Function<Object, Either<Exception, Object>> value = Functional.$(a -> {
            throw exception;
        });

//        VavrAssertions.assertThat(value.apply(new Object())).satisfies(v -> {
//            VavrAssertions.assertThat(v).isLeft().containsOnLeft(exception);
//        });

        final Either<Exception, Object> result = value.apply(new Object());
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo(exception);
    }

    static boolean bothAreEven(final int a, final int b) {
        return isEven(a) && isEven(b);
    }

    static class myInt {
        private final int _i;

        public myInt(final int i) {
            _i = i;
        }

        public int i() {
            return _i;
        }
    }
}
