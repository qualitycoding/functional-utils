package me.shaftesbury.utils.functional.monad.transformer;

import io.vavr.control.Option;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class OptionToListTransformerTest {

    @Nested
    class OfOption {
        @Test
        void precondition() {
            assertThatNullPointerException().isThrownBy(() -> OptionToListTransformer.of((Option) null)).withMessage("value must not be null");
        }

        @Test
        void ofOption() {
            final Object value = new Object();
            final Option<Object> option = Option.of(value);

            assertThat(OptionToListTransformer.of(option)).containsExactly(value);
        }

        @Test
        void ofNone() {
            assertThat(OptionToListTransformer.of(Option.none())).isEmpty();
        }
    }

    @Nested
    class OfOptional {
        @Test
        void precondition() {
            assertThatNullPointerException().isThrownBy(() -> OptionToListTransformer.of((Optional) null)).withMessage("value must not be null");
        }

        @Test
        void ofOptional() {
            final Object value = new Object();
            final Optional<Object> option = Optional.of(value);

            assertThat(OptionToListTransformer.of(option)).containsExactly(value);
        }

        @Test
        void ofNone() {
            assertThat(OptionToListTransformer.of(Optional.empty())).isEmpty();
        }
    }
}