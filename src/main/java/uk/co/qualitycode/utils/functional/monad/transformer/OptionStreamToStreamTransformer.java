package uk.co.qualitycode.utils.functional.monad.transformer;

import uk.co.qualitycode.utils.functional.OptionStream;

import java.util.Optional;
import java.util.stream.Stream;

public class OptionStreamToStreamTransformer {
    public static <T> OptionStream<T> $(final Stream<Optional<T>> streamOfOptionals) {
        return new OptionStream<>(streamOfOptionals);
    }
}
