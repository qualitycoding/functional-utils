package uk.co.qualitycode.utils.functional.monad.transformer;

import io.vavr.control.Option;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class OptionToSetTransformerTest {

    @Nested
    class OfOption {
        @Test
        void precondition() {
            assertThatNullPointerException()
                    .isThrownBy(() -> OptionToSetTransformer.of((Option) null))
                    .withMessage("value must not be null");
        }

        @Test
        void ofOption() {
            final Object value = new Object();
            final Option<Object> option = Option.of(value);

            assertThat(OptionToSetTransformer.of(option)).containsExactly(value);
        }

        @Test
        void ofNone() {
            assertThat(OptionToSetTransformer.of(Option.none())).isEmpty();
        }
    }

    @Nested
    class OfOptional {
        @Test
        void precondition() {
            assertThatNullPointerException()
                    .isThrownBy(() -> OptionToSetTransformer.of((Optional) null))
                    .withMessage("value must not be null");
        }

        @Test
        void ofOptional() {
            final Object value = new Object();
            final Optional<Object> option = Optional.of(value);

            assertThat(OptionToSetTransformer.of(option)).containsExactly(value);
        }

        @Test
        void ofNone() {
            assertThat(OptionToSetTransformer.of(Optional.empty())).isEmpty();
        }
    }
}
