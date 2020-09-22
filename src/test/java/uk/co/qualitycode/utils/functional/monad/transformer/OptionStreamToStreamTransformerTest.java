package uk.co.qualitycode.utils.functional.monad.transformer;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.OptionStream;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OptionStreamToStreamTransformerTest {
    @Test
    void factoryMethodIsInAPI() {
        final OptionStream<Integer> os = OptionStreamToStreamTransformer.$(Stream.of(Optional.of(1), Optional.empty()));
        assertThat(os).isNotNull();
    }
}
