package me.shaftesbury.utils.functional.monad.transformer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BooleanToOptionTransformerTest {
    @Test
    void ofTrueReturnsSome() {
        assertThat(BooleanToOptionTransformer.of(true).isDefined()).isTrue();
    }

    @Test
    void ofFalseReturnsNone() {
        assertThat(BooleanToOptionTransformer.of(false).isEmpty()).isTrue();
    }
}
