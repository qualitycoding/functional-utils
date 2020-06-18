package me.shaftesbury.utils.functional.monad.transformer;

import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class OptionToEitherTransformerTest {
    @Test
    void ofOptionThrowsWhenSuppliedNull() {
        assertThatNullPointerException().isThrownBy(() -> OptionToEitherTransformer.of((Option) null));
    }

    @Test
    void ofOptionalThrowsWhenSuppliedNull() {
        assertThatNullPointerException().isThrownBy(() -> OptionToEitherTransformer.of((Optional) null));
    }

    @Test
    void ofOption() {
        final Either<? extends Exception, java.lang.String> value = OptionToEitherTransformer.of(Option.some("value"));
        assertThat(value.get()).isEqualTo("value");
    }

    @Test
    void ofOptional() {
        final Either<? extends Exception, java.lang.String> value = OptionToEitherTransformer.of(Optional.of("value"));
        assertThat(value.get()).isEqualTo("value");
    }

    @Test
    void ofOptionReturnsLeftForNone() {
        final Either<? extends Exception, Object> value = OptionToEitherTransformer.of(Option.none());
        assertThat(value.isLeft()).isTrue();
    }

    @Test
    void ofOptionalReturnsLeftForNone() {
        final Either<? extends Exception, Object> value = OptionToEitherTransformer.of(Optional.empty());
        assertThat(value.isLeft()).isTrue();
    }
}
