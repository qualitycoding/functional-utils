package uk.co.qualitycode.utils.functional.monad;

import org.assertj.core.api.IterableAssert;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.Functional;
import uk.co.qualitycode.utils.functional.Iterable2;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class OptionTest {
    @Nested
    class Creation {
        @Test
        void none() {
            final Option<Object> actual = Option.none();
        }

        @Test
        void ofObject() {
            final Option<Object> actual = Option.of(new Object());
        }

        @Test
        void ofNullObject() {
            final Option<Object> actual = Option.of((Object) null);
        }

        @Test
        void ofOptional() {
            final Option<Object> actual = Option.of(Optional.of(new Object()));
        }

        @Test
        void ofOption() {
            final Option<Object> actual = Option.of(io.vavr.control.Option.of(new Object()));
        }
    }

    @Nested
    class Convert {
        @Test
        void someToJavaOptional() {
            final Optional<Object> t = Optional.of(new Object());
            assertThat(Option.of(t).toJavaOptional()).isEqualTo(t);
        }

        @Test
        void noneToJavaOptional() {
            assertThat(Option.none().toJavaOptional()).isEmpty();
        }

        @Test
        void someToVavrOption() {
            final io.vavr.control.Option<Object> t = io.vavr.control.Option.of(new Object());
            assertThat(Option.of(t).toVavrOption()).isSameAs(t);
        }

        @Test
        void noneToVavrOption() {
            VavrAssertions.assertThat(Option.none().toVavrOption()).isEmpty();
        }
    }

    @Nested
    class Bind {
        @Test
        void isSome() {
            final Option<Integer> a = Option.of(1);
            final Option<Integer> b = a.bind(i -> Option.of(i * 2));
            assertThat(b.toJavaOptional()).hasValue(2);
        }

        @Test
        void isNone() {
            final Option<Integer> a = Option.none();
            final Option<Integer> b = a.bind(i -> Option.of(i * 2));
            assertThat(b.toJavaOptional()).isEmpty();
        }

        @Test
        void optionBindTest3() {
            final Iterable2<Integer> input = Iterable2.asList(1, 2, 3, 4, 5, 6);
            final java.util.List<Integer> expected = Arrays.asList(2, 4, 6);

            // Note that this really ought to be an example of 'choose' but we use bind here to exercise the code ;-)

            final Iterable2<Option<Integer>> output = input.map(
                    integer -> Option.of(integer).bind(
                            i -> Functional.isEven.apply(i) ? Option.of(i) : Option.none()));


            final Iterable2<Integer> choose = output.choose(Function.identity());
            final IterableAssert<Integer> integerIterableAssert = assertThat(choose);
            integerIterableAssert.containsExactlyElementsOf(expected);
        }
    }

    private static final BiFunction<Integer, Integer, Integer> plus = Integer::sum;

    @Test
    void optionLiftTest1() {
        final Option<Integer> a = Option.of(10);
        final Option<Integer> b = Option.of(100);
        final Option<Integer> c = Option.lift(plus, a, b);

        assertThat(c.isSome()).isTrue();
        assertThat(c.get()).isEqualTo(Integer.valueOf(110));
    }

    @Test
    void optionLiftTest2() {
        final Option<Integer> a = Option.of(10);
        final Option<Integer> b = Option.none();
        final Option<Integer> c = Option.lift(plus, a, b);

        assertThat(c.isNone()).isTrue();
    }

    @Test
    void optionLiftTest3() {
        final Option<Integer> a = Option.none();
        final Option<Integer> b = Option.of(10);
        final Option<Integer> c = Option.lift(plus, a, b);

        assertThat(c.isNone()).isTrue();
    }

    @Test
    void optionLiftTest4() {
        final Option<Integer> a = Option.none();
        final Option<Integer> b = Option.none();
        final Option<Integer> c = Option.lift(plus, a, b);

        assertThat(c.isNone()).isTrue();
    }

    @Test
    void optionSomeCreatorTestValueType1() {
        final int expected = 10;
        final Option<Integer> a = Option.of(expected);
        assertThat(a.isSome()).isTrue();
        assertThat(a.isNone()).isFalse();
        assertThat(a.get()).isEqualTo((Integer) expected);
    }

    @Test
    void optionSomeCreatorTestStringType1() {
        final java.lang.String expected = "ll";
        final Option<java.lang.String> a = Option.of(expected);
        assertThat(a.isSome()).isTrue();
        assertThat(a.isNone()).isFalse();
        assertThat(a.get()).isEqualTo(expected);
    }

    @Test
    void optionOfTestValueType2() {
        final Option<?> actual = Option.of((io.vavr.control.Option<?>) null);
        assertThat(actual.isNone()).isTrue();
    }

    @Test
    void optionOfTestValueType3() {
        final Option<?> actual = Option.of((Optional<?>) null);
        assertThat(actual.isNone()).isTrue();
    }
}
