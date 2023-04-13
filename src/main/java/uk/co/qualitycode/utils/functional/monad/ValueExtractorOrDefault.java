package uk.co.qualitycode.utils.functional.monad;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public final class ValueExtractorOrDefault {
    private final String defaultValue;

    public ValueExtractorOrDefault() {
        this("");
    }

    public ValueExtractorOrDefault(final String defaultValue) {
        this.defaultValue = requireNonNull(defaultValue, "defaultValue must not be null");
    }

    public <T> MapOrDefault<T> value(final T t) {
        return f -> () -> mapOrDefault(f, Optional.ofNullable(t));
    }

    public <T> MapOrDefault<T> value(final Optional<T> t) {
        requireNonNull(t, "t must not be null");
        return f -> () -> mapOrDefault(f, t);
    }

    public String valueOrDefault(final String value) {
        return mapOrDefault(Function.identity(), Optional.ofNullable(value));
    }

    public String valueOrDefault(final Optional<String> value) {
        requireNonNull(value, "value must not be null");
        return mapOrDefault(Function.identity(), value);
    }

    public interface MapOrDefault<T> {
        Or then(Function<T, String> f);

        interface Or {
            String orDefault();
        }
    }

    private <T> String mapOrDefault(final Function<T, String> f, final Optional<T> value) {
        return value.map(f).orElse(defaultValue);
    }
}
