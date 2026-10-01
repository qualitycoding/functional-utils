package uk.co.qualitycode.utils.functional;

import java.util.Optional;
import java.util.stream.Stream;

public final class OptionStream<T> {
    private OptionStream() {
    }

    public static <T> Stream<T> $(final Stream<Optional<T>> streamOfOptionals) {
        return streamOfOptionals.filter(Optional::isPresent).map(Optional::get);
    }
}
