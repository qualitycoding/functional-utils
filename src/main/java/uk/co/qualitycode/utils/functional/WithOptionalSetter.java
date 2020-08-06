package uk.co.qualitycode.utils.functional;

import java.util.Optional;
import java.util.function.Function;

public interface WithOptionalSetter<Builder> {
    default <T> Builder optionally(final Function<Builder, Function<T, Builder>> with, final Optional<T> opt) {
        return opt.map(value -> with.apply(self()).apply(value)).orElse(self());
    }

    default <T> Builder optionally(final Function<Builder, Function<T, Builder>> with, final io.vavr.control.Option<T> opt) {
        return optionally(with, opt.toJavaOptional());
    }

    Builder self();
}
