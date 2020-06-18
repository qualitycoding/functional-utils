package me.shaftesbury.utils.functional.monad;

import me.shaftesbury.utils.functional.monad.String.ConformingString;
import me.shaftesbury.utils.functional.monad.String.ConformingString.ConformingStringBuilder;
import me.shaftesbury.utils.functional.monad.String.MissingValueException;
import me.shaftesbury.utils.functional.monad.String.NonBlankString;
import me.shaftesbury.utils.functional.monad.String.NonEmptyString;
import me.shaftesbury.utils.functional.monad.String.NonNullString;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.junit.jupiter.api.Assertions.assertAll;

class StringTest {
    @Nested
    class HasValue {
        @Test
        void isNotNull() {
            final java.lang.String input = "hkh";
            assertThat(NonNullString.of(input).hasValue()).isTrue();
        }

        @Test
        void isNull() {
            assertThat(NonNullString.of(null).hasValue()).isFalse();
        }

        @Test
        void isNotEmpty() {
            final java.lang.String input = "hkh";
            assertThat(NonEmptyString.of(input).hasValue()).isTrue();
        }

        @Test
        void isEmpty() {
            assertThat(NonEmptyString.of("").hasValue()).isFalse();
        }

        @Test
        void isNotBlank() {
            final java.lang.String input = "hkh";
            assertThat(NonBlankString.of(input).hasValue()).isTrue();
        }

        @Test
        void isBlank() {
            assertThat(NonBlankString.of("   ").hasValue()).isFalse();
        }
    }

    @Nested
    class Get {
        @Test
        void get() {
            assertThat(NonBlankString.of("hgh").get()).isEqualTo("hgh");
        }

        @Test
        void getThrows() {
            assertThatExceptionOfType(MissingValueException.class).isThrownBy(() -> NonBlankString.of("   ").get()).withMessage("Cannot get() a String that has no value");
        }
    }

    @Nested
    class GetOrEmpty {
        @Test
        void returnsOptionOf() {
            VavrAssertions.assertThat(NonBlankString.of("hgh").getOrEmpty()).contains("hgh");
        }

        @Test
        void returnsEmpty() {
            VavrAssertions.assertThat(NonBlankString.of("  ").getOrEmpty()).isEmpty();
        }
    }

    @Nested
    class GetOrElse {
        @Test
        void returnsValue() {
            assertThat(NonBlankString.of("hgh").getOrElse(() -> "bob")).isEqualTo("hgh");
        }

        @Test
        void returnsEmpty() {
            assertThat(NonBlankString.of("  ").getOrElse(() -> "bob")).isEqualTo("bob");
        }
    }

    @Nested
    class GetOrElseThrow {
        @Test
        void returnsValue() {
            assertThat(NonBlankString.of("hgh").getOrElseThrow(RuntimeException::new)).isEqualTo("hgh");
        }

        @Test
        void returnsEmpty() {
            assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> NonBlankString.of("  ").getOrElseThrow(() -> new RuntimeException("busted"))).withMessage("busted");
        }
    }

    @Nested
    class AsNonNull {
        @Test
        void nonNull() {
            final NonNullString s = NonNullString.of("A");
            assertThat(s.asNonNull()).isEqualTo(s);
        }

        @Test
        void nonEmpty() {
            final NonEmptyString s = NonEmptyString.of("A");
            assertThat(s.asNonNull()).isEqualTo(s);
        }

        @Test
        void nonBlank() {
            final NonBlankString s = NonBlankString.of("A");
            assertThat(s.asNonNull()).isEqualTo(s);
        }
    }

    @Nested
    class AsNonEmpty {
        @Test
        void nonEmpty() {
            final NonEmptyString s = NonEmptyString.of("A");
            assertThat(s.asNonEmpty()).isEqualTo(s);
        }

        @Test
        void nonBlank() {
            final NonBlankString s = NonBlankString.of("A");
            assertThat(s.asNonEmpty()).isEqualTo(s);
        }
    }

    @Nested
    class AsNonBlank {
        @Test
        void nonBlank() {
            final NonBlankString s = NonBlankString.of("A");
            assertThat(s.asNonBlank()).isEqualTo(s);
        }
    }

    @Nested
    class Filter {
        @Test
        void withFunction() {
            assertThat(NonBlankString.of("jhj").filter(Objects::nonNull)).isEqualTo(NonBlankString.of("jhj"));
        }

        @Test
        void withPredicate() {
            assertThat(NonBlankString.of("jhj").filter((java.util.function.Predicate) x -> Objects.nonNull(x))).isEqualTo(NonBlankString.of("jhj"));
        }

        @Test
        void throwsWithFunction() {
            assertThatNullPointerException().isThrownBy(() -> assertThat(NonBlankString.of("jkh").filter((Function) null))).withMessage("predicate must not be null");
        }

        @Test
        void throwsWithPredicate() {
            assertThatNullPointerException().isThrownBy(() -> assertThat(NonBlankString.of("jkh").filter((Function) null))).withMessage("predicate must not be null");
        }
    }

    @Nested
    class Map {
        @Test
        void map() {
            final NonBlankString s = NonBlankString.of("kggsdf");
            assertThat(s.map(java.lang.String::toUpperCase)).isEqualTo(s);
        }

        @Test
        void mapThrows() {
            assertThatNullPointerException().isThrownBy(() -> NonBlankString.of("jhglgl").map(null)).withMessage("tfm must not be null");
        }
    }

    @Nested
    class FlatMap {
        @Test
        void flatMap() {
            final NonBlankString s = NonBlankString.of("kggsdf");
            assertThat(s.flatMap(s1 -> NonBlankString.of(s1.toUpperCase()))).isEqualTo(s);
        }

        @Test
        void flatMapThrows() {
            assertThatNullPointerException().isThrownBy(() -> NonBlankString.of("jhglgl").flatMap(null)).withMessage("tfm must not be null");
        }
    }

    @Nested
    class ConformingStringTests {
        @Test
        void createWithoutValueThrows() {
            assertThatExceptionOfType(MissingValueException.class).isThrownBy(() -> new ConformingStringBuilder().build())
                    .withMessage("Cannot build a ConformingString with no value");
        }

        @Test
        void createWithDefaultRule() {
            final ConformingString string = new ConformingStringBuilder().withValue("yes").build();
            assertThat(string.get()).isEqualTo("yes");
        }

        @Test
        void createWithRule() {
            final ConformingString string = new ConformingStringBuilder().withRule(s -> true).withValue("yes").build();
            assertThat(string.get()).isEqualTo("yes");
        }

        @Test
        void createWithRuleAndValueThatDoesNotMatch() {
            final ConformingString string = new ConformingStringBuilder().withRule(s -> false).withValue("yes").build();
            assertThat(string.hasValue()).isFalse();
        }

        @Test
        void of() {
            final Function<java.lang.String, Boolean> rule = s -> true;
            final java.lang.String value = "a";
            final ConformingString string = ConformingString.of(rule, value);
            assertAll(
                    () -> assertThat(string.getRule()).isEqualTo(rule),
                    () -> assertThat(string.get()).isEqualTo(value)
            );
        }

        @Test
        void getRule() {
            final Function<java.lang.String, Boolean> rule = s -> true;
            final ConformingString string = new ConformingStringBuilder().withRule(rule).withValue("value").build();
            assertThat(string.getRule()).isEqualTo(rule);
        }

        @Test
        void getRuleWhenValueDoesNotConform() {
            final Function<java.lang.String, Boolean> rule = s -> false;
            final ConformingString string = new ConformingStringBuilder().withRule(rule).withValue("value").build();
            assertThat(string.getRule()).isEqualTo(rule);
        }
    }
}
