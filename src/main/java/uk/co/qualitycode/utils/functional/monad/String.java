package uk.co.qualitycode.utils.functional.monad;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import uk.co.qualitycode.utils.functional.monad.transformer.BooleanToOptionTransformer;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.vavr.Predicates.not;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static java.util.Objects.requireNonNull;
import static uk.co.qualitycode.utils.functional.UsingWrapper.using;

/**
 * A Monadic String
 */
public class String {
    private final io.vavr.control.Option<java.lang.String> value;

    private String(final io.vavr.control.Option<java.lang.String> value) {
        this.value = value;
    }

    public NonNullString asNonNull() {
        return new NonNullString(value.filter(Objects::nonNull));
    }

    public NonEmptyString asNonEmpty() {
        return new NonEmptyString(value.filter(not(Predicate.isEqual(""))));
    }

    public NonBlankString asNonBlank() {
        return new NonBlankString(value
                .filter(Objects::nonNull)
                .map(java.lang.String::trim)
                .filter(not(""::equals)));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        // There's a tweak to the auto-generated equals() here ....
        if (o == null || o.getClass().getEnclosingClass() != String.class) return false;

        final String string = (String) o;

        return new EqualsBuilder()
                .append(value, string.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .toHashCode();
    }

    @Override
    public java.lang.String toString() {
        return "String{" +
                "value=" + value +
                '}';
    }

    public boolean hasValue() {
        return value.isDefined();
    }

    public java.lang.String get() {
        return value.getOrElseThrow(() -> new MissingValueException("Cannot get() a String that has no value"));
    }

    public io.vavr.control.Option<java.lang.String> getOrEmpty() {
        return value;
    }

    public java.lang.String getOrElse(final Supplier<java.lang.String> supplier) {
        return value.getOrElse(supplier);
    }

    /**
     * @param supplier of a RuntimeException (because we don't like checked exceptions)
     * @return
     */
    public java.lang.String getOrElseThrow(final Supplier<? extends RuntimeException> supplier) {
        return value.getOrElseThrow(supplier);
    }

    public String filter(final Function<java.lang.String, Boolean> predicate) {
        requireNonNull(predicate, "predicate must not be null");
        return new String(value.filter(predicate::apply));
    }

    public String filter(final Predicate<java.lang.String> predicate) {
        requireNonNull(predicate, "predicate must not be null");
        return new String(value.filter(predicate::test));
    }

    public String map(final Function<java.lang.String, java.lang.String> tfm) {
        requireNonNull(tfm, "tfm must not be null");
        return new String(value.map(tfm));
    }

    public String flatMap(final Function<java.lang.String, String> tfm) {
        requireNonNull(tfm, "tfm must not be null");
        return value.map(tfm).getOrElse(new String(none()));
    }

    public static class NonNullString extends String {
        NonNullString(final io.vavr.control.Option<java.lang.String> value) {
            super(value);
        }

        public static NonNullString of(final java.lang.String value) {
            return new String(some(value)).asNonNull();
        }

        public NonNullString asNonNull() {
            return this;
        }
    }

    public static class NonEmptyString extends NonNullString {
        NonEmptyString(final io.vavr.control.Option<java.lang.String> value) {
            super(value);
        }

        public static NonEmptyString of(final java.lang.String value) {
            return new String(some(value)).asNonEmpty();
        }

        public NonEmptyString asNonNull() {
            return this;
        }

        public NonEmptyString asNonEmpty() {
            return this;
        }
    }

    public static class NonBlankString extends NonEmptyString {
        NonBlankString(final io.vavr.control.Option<java.lang.String> value) {
            super(value);
        }

        public static NonBlankString of(final java.lang.String value) {
            return new String(some(value)).asNonBlank();
        }

        public NonBlankString asNonNull() {
            return this;
        }

        public NonBlankString asNonEmpty() {
            return this;
        }

        public NonBlankString asNonBlank() {
            return this;
        }
    }

    public static class ConformingString extends String {
        private final Function<java.lang.String, Boolean> rule;

        private ConformingString(final Function<java.lang.String, Boolean> rule, final io.vavr.control.Option<java.lang.String> s) {
            super(s);
            this.rule = rule;
        }

        public static ConformingString of(final Function<java.lang.String, Boolean> rule, final java.lang.String s) {
            return new ConformingStringBuilder().withRule(rule).withValue(s).build();
        }

        public Function<java.lang.String, Boolean> getRule() {
            return rule;
        }

        public static class ConformingStringBuilder extends ConformingObjectBuilder<java.lang.String, ConformingString> {
            @Override
            public ConformingString build() {
                return getValue().
                        map(value -> using(getRule()).in(rule -> new ConformingString(rule, applyRule(rule, value))))
                        .getOrElseThrow(() -> new MissingValueException("Cannot build a ConformingString with no value"));
            }

            private io.vavr.control.Option<java.lang.String> applyRule(final Function<java.lang.String, Boolean> rule, final java.lang.String value) {
                return BooleanToOptionTransformer.of(rule.apply(value)).map(x -> value);
            }
        }

    }

    public static class MissingValueException extends RuntimeException {
        public MissingValueException(final java.lang.String message) {
            super(message);
        }
    }
}
