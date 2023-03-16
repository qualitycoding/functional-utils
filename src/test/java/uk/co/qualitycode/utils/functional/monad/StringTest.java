package uk.co.qualitycode.utils.functional.monad;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.String.ConformingString;
import uk.co.qualitycode.utils.functional.monad.String.ConformingString.ConformingStringBuilder;
import uk.co.qualitycode.utils.functional.monad.String.MissingValueException;
import uk.co.qualitycode.utils.functional.monad.String.NonBlankString;
import uk.co.qualitycode.utils.functional.monad.String.NonEmptyString;
import uk.co.qualitycode.utils.functional.monad.String.NonNullString;

import java.util.Objects;
import java.util.function.Predicate;

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
    class Equals {
        @Nested
        class Value {
            @Test
            void nonBlankStringEqualsNonBlankString() {
                assertThat(NonBlankString.of("gsdf")).isEqualTo(NonBlankString.of("gsdf"));
            }

            @Test
            void nonBlankStringEqualsNonEmptyString() {
                assertThat(NonBlankString.of("gsdf")).isEqualTo(NonEmptyString.of("gsdf"));
            }

            @Test
            void nonBlankStringEqualsNonNullString() {
                assertThat(NonBlankString.of("gsdf")).isEqualTo(NonNullString.of("gsdf"));
            }

            @Test
            void nonEmptyStringEqualsNonBlankString() {
                assertThat(NonEmptyString.of("gsdf")).isEqualTo(NonBlankString.of("gsdf"));
            }

            @Test
            void nonEmptyStringEqualsNonEmptyString() {
                assertThat(NonEmptyString.of("gsdf")).isEqualTo(NonEmptyString.of("gsdf"));
            }

            @Test
            void nonEmptyStringEqualsNonNullString() {
                assertThat(NonEmptyString.of("gsdf")).isEqualTo(NonNullString.of("gsdf"));
            }

            @Test
            void nonNullStringEqualsNonBlankString() {
                assertThat(NonNullString.of("gsdf")).isEqualTo(NonBlankString.of("gsdf"));
            }

            @Test
            void nonNullStringEqualsNonEmptyString() {
                assertThat(NonNullString.of("gsdf")).isEqualTo(NonEmptyString.of("gsdf"));
            }

            @Test
            void nonNullStringEqualsNonNullString() {
                assertThat(NonNullString.of("gsdf")).isEqualTo(NonNullString.of("gsdf"));
            }
        }

        @Nested
        class Empty {
            @Test
            void nonBlankStringEqualsNonBlankString() {
                assertThat(NonBlankString.of("    ")).isEqualTo(NonBlankString.of("    "));
            }

            @Test
            void nonBlankStringEqualsNonEmptyString() {
                assertThat(NonBlankString.of("    ")).isEqualTo(NonEmptyString.of(""));
            }

            @Test
            void nonBlankStringEqualsNonNullString() {
                assertThat(NonBlankString.of("    ")).isEqualTo(NonNullString.of(null));
            }

            @Test
            void nonEmptyStringEqualsNonBlankString() {
                assertThat(NonEmptyString.of("")).isEqualTo(NonBlankString.of("    "));
            }

            @Test
            void nonEmptyStringEqualsNonEmptyString() {
                assertThat(NonEmptyString.of("")).isEqualTo(NonEmptyString.of(""));
            }

            @Test
            void nonEmptyStringEqualsNonNullString() {
                assertThat(NonEmptyString.of("")).isEqualTo(NonNullString.of(null));
            }

            @Test
            void nonNullStringEqualsNonBlankString() {
                assertThat(NonNullString.of(null)).isEqualTo(NonBlankString.of("    "));
            }

            @Test
            void nonNullStringEqualsNonEmptyString() {
                assertThat(NonNullString.of(null)).isEqualTo(NonEmptyString.of(""));
            }

            @Test
            void nonNullStringEqualsNonNullString() {
                assertThat(NonNullString.of(null)).isEqualTo(NonNullString.of(null));
            }
        }
    }

    @Nested
    class NotEqual {
        @Nested
        class Value {
            @Test
            void nonBlankStringDoesNotEqualNonBlankString() {
                assertThat(NonBlankString.of("gsdf")).isNotEqualTo((NonBlankString.of("dj")));
            }

            @Test
            void nonBlankStringDoesNotEqualNonEmptyString() {
                assertThat(NonBlankString.of("gsdf")).isNotEqualTo((NonEmptyString.of("dj")));
            }

            @Test
            void nonBlankStringDoesNotEqualNonNullString() {
                assertThat(NonBlankString.of("gsdf")).isNotEqualTo((NonNullString.of("dj")));
            }

            @Test
            void nonEmptyStringDoesNotEqualNonBlankString() {
                assertThat(NonEmptyString.of("gsdf")).isNotEqualTo((NonBlankString.of("dj")));
            }

            @Test
            void nonEmptyStringDoesNotEqualNonEmptyString() {
                assertThat(NonEmptyString.of("gsdf")).isNotEqualTo((NonEmptyString.of("dj")));
            }

            @Test
            void nonEmptyStringDoesNotEqualNonNullString() {
                assertThat(NonEmptyString.of("gsdf")).isNotEqualTo((NonNullString.of("dj")));
            }

            @Test
            void nonNullStringDoesNotEqualNonBlankString() {
                assertThat(NonNullString.of("gsdf")).isNotEqualTo((NonBlankString.of("dj")));
            }

            @Test
            void nonNullStringDoesNotEqualNonEmptyString() {
                assertThat(NonNullString.of("gsdf")).isNotEqualTo((NonEmptyString.of("dj")));
            }

            @Test
            void nonNullStringDoesNotEqualNonNullString() {
                assertThat(NonNullString.of("gsdf")).isNotEqualTo((NonNullString.of("dj")));
            }
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
            assertThat(NonBlankString.of("hgh").getOrEmpty().toJavaOptional()).hasValue("hgh");
//            VavrAssertions.assertThat(NonBlankString.of("hgh").getOrEmpty()).contains("hgh");
        }

        @Test
        void returnsEmpty() {
            assertThat(NonBlankString.of("   ").getOrEmpty().toJavaOptional()).isEmpty();
//            VavrAssertions.assertThat(NonBlankString.of("  ").getOrEmpty()).isEmpty();
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
            assertThat(NonBlankString.of("jhj").filter(Objects::nonNull)).isEqualTo(NonBlankString.of("jhj"));
        }

        @Test
        void throwsWithFunction() {
            assertThatNullPointerException()
                    .isThrownBy(() -> assertThat(NonBlankString.of("jkh").filter(null)))
                    .withMessage("predicate must not be null");
        }

        @Test
        void throwsWithPredicate() {
            assertThatNullPointerException()
                    .isThrownBy(() -> assertThat(NonBlankString.of("jkh").filter(null)))
                    .withMessage("predicate must not be null");
        }
    }

    @Nested
    class Map {
        @Test
        void map() {
            final java.lang.String value = "kggsdf";
            final java.lang.String expectedValue = value.toUpperCase();
            final NonBlankString expected = NonBlankString.of(expectedValue);
            final NonBlankString s = NonBlankString.of(value);
            assertThat(s.map(java.lang.String::toUpperCase)).isEqualTo(expected);
        }

        @Test
        void mapThrows() {
            assertThatNullPointerException()
                    .isThrownBy(() -> NonBlankString.of("jhglgl").map(null))
                    .withMessage("tfm must not be null");
        }
    }

    @Nested
    class FlatMap {
        @Test
        void flatMap() {
            final java.lang.String value = "kggsdf";
            final java.lang.String expectedValue = value.toUpperCase();
            final NonBlankString expected = NonBlankString.of(expectedValue);
            final NonBlankString s = NonBlankString.of(value);
            assertThat(s.flatMap(s1 -> NonBlankString.of(s1.toUpperCase()))).isEqualTo(expected);
        }

        @Test
        void flatMapThrows() {
            assertThatNullPointerException()
                    .isThrownBy(() -> NonBlankString.of("jhglgl").flatMap(null))
                    .withMessage("tfm must not be null");
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
            final Predicate<java.lang.String> rule = s -> true;
            final java.lang.String value = "a";
            final ConformingString string = ConformingString.of(rule, value);
            assertAll(
                    () -> assertThat(string.getRule()).isEqualTo(rule),
                    () -> assertThat(string.get()).isEqualTo(value)
            );
        }

        @Test
        void getRule() {
            final Predicate<java.lang.String> rule = s -> true;
            final ConformingString string = new ConformingStringBuilder().withRule(rule).withValue("value").build();
            assertThat(string.getRule()).isEqualTo(rule);
        }

        @Test
        void getRuleWhenValueDoesNotConform() {
            final Predicate<java.lang.String> rule = s -> false;
            final ConformingString string = new ConformingStringBuilder().withRule(rule).withValue("value").build();
            assertThat(string.getRule()).isEqualTo(rule);
        }
    }
}
