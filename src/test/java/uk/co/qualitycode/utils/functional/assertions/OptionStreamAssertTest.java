package uk.co.qualitycode.utils.functional.assertions;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.OptionStream;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.co.qualitycode.utils.functional.monad.transformer.OptionStreamToStreamTransformer.$;

class OptionStreamAssertTest<T> {

    @Test
    void containsExactly() {
        {
            final OptionStream<Integer> optionStream = $(Stream.of(Optional.of(1), Optional.empty(), Optional.of(2)));
            OptionStreamAssert.assertThat(optionStream).containsExactly(1, 2);
        }
        {
            final OptionStream<Integer> optionStream = $(Stream.of(Optional.of(1), Optional.empty(), Optional.of(2)));
            assertThatExceptionOfType(AssertionError.class)
                    .isThrownBy(() -> OptionStreamAssert.assertThat(optionStream).containsExactly(2, 1))
                    .withMessage("\n" +
                            "Actual and expected have the same elements but not in the same order, at index 0 actual element was:\n" +
                            "  <1>\n" +
                            "whereas expected element was:\n" +
                            "  <2>\n");
        }
    }
}
