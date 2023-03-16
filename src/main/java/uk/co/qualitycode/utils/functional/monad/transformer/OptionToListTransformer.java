package uk.co.qualitycode.utils.functional.monad.transformer;

import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class OptionToListTransformer {
    public static <T> io.vavr.collection.List<T> of(final Option<T> value) {
        requireNonNull(value, "value must not be null");
        return value.isDefined() ? List.of(value.get()) : List.empty();
    }

    public static <T> java.util.List<T> of(final Optional<T> value) {
        requireNonNull(value, "value must not be null");
        return value.isPresent() ? Collections.singletonList(value.get()) : Collections.emptyList();
    }
}
