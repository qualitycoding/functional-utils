package uk.co.qualitycode.utils.functional.assertions;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OptionAssertTest<T> {
    @Test
    void hasValue() {
        final Object t = new Object();
        OptionAssert.assertThat(Option.of(t)).hasValue(t);

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> OptionAssert.assertThat(Option.none()).hasValue(new Object()))
                .withMessage("option has no value");
    }

    @Test
    void isEmpty() {
        OptionAssert.assertThat(Option.none()).isEmpty();

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> OptionAssert.assertThat(Option.of(1)).isEmpty())
                .withMessage("option has a value '1' but is expected to be empty");
    }
}
