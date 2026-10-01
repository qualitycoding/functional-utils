package uk.co.qualitycode.utils.functional.monad.transformer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import io.vavr.control.Option;

class BooleanToOptionTransformerTest {
    @Test
    void ofTrueReturnsSome() {
        assertThat(BooleanToOptionTransformer.of(true)).isEqualTo(Option.some(true));
    }

    @Test
    void ofFalseReturnsNone() {
        assertThat(BooleanToOptionTransformer.of(false)).isEqualTo(Option.none());
    }
}
