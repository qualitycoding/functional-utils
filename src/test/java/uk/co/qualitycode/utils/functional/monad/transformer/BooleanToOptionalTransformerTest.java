package uk.co.qualitycode.utils.functional.monad.transformer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BooleanToOptionalTransformerTest {
    @Test
    void ofTrueReturnsSome() {
        assertThat(BooleanToOptionalTransformer.of(true).isPresent()).isTrue();
    }

    @Test
    void ofFalseReturnsNone() {
        assertThat(BooleanToOptionalTransformer.of(false).isPresent()).isFalse();
    }
}
