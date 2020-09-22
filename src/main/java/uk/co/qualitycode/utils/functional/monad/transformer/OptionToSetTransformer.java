package uk.co.qualitycode.utils.functional.monad.transformer;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;

import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class OptionToSetTransformer {
    public static <T> Set<T> of(final Option<T> value) {
        requireNonNull(value, "value must not be null");
        return value.isDefined() ? HashSet.of(value.get()) : HashSet.empty();
    }

    public static <T> java.util.Set<T> of(final Optional<T> value) {
        requireNonNull(value, "value must not be null");
        return value.isPresent() ? Collections.singleton(value.get()) : Collections.emptySet();
    }
}
