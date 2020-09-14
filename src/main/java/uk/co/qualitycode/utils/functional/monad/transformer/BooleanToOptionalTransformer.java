package uk.co.qualitycode.utils.functional.monad.transformer;

import java.util.Optional;

public class BooleanToOptionalTransformer {
    public static Optional<Boolean> of(final boolean value) {
        return value ? Optional.of(true) : Optional.empty();
    }
}
