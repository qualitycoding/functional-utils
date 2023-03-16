package uk.co.qualitycode.utils.functional.monad.transformer;

public class BooleanToOptionTransformer {
    public static io.vavr.control.Option<Boolean> of(final boolean value) {
        return value ? io.vavr.control.Option.of(true) : io.vavr.control.Option.none();
    }
}
