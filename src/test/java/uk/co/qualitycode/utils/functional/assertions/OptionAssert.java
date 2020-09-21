package uk.co.qualitycode.utils.functional.assertions;

import uk.co.qualitycode.utils.functional.monad.Option;

public class OptionAssert<T> {
    private Option<T> option;

    public OptionAssert(final Option<T> option) {
        this.option = option;
    }

    public static <T> OptionAssert<T> assertThat(final Option<T> option) {
        return new OptionAssert<>(option);
    }

    public OptionAssert<T> hasValue(final T value) {
        final T v = option.getOrElseThrow(() -> new AssertionError("option has no value"));
        if (v.equals(value))
            return this;
        throw new AssertionError(String.format("option has value '%s' but was expected to have '%s'", v, value));
    }

    public OptionAssert<T> isEmpty() {
        option.toJavaOptional().ifPresent(v -> {
            throw new AssertionError(String.format("option has a value '%s' but is expected to be empty", v));
        });
        return this;
    }
}
