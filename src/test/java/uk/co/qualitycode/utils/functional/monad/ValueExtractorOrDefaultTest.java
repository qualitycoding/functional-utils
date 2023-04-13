package uk.co.qualitycode.utils.functional.monad;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ValueExtractorOrDefaultTest {

    private static final ValueExtractorOrDefault extractor = new ValueExtractorOrDefault();
    private static final ValueExtractorOrDefault extractorWithDefaultAssigned = new ValueExtractorOrDefault("juan");

    @Nested
    class TestingExtractorWithStrings {

        @Test
        void valueOrDefaultWillReturnValueWhenPresent() {
            final String value = "testValue";
            assertThat(extractor.valueOrDefault(value)).isEqualTo("testValue");
        }

        @Test
        void valueOrDefaultWillReturnDefaultWhenValueIsNotPresent() {
            final String value = null;
            assertThat(extractor.valueOrDefault(value)).isEqualTo("");
        }
    }

    @Nested
    class TestingExtractorWithOptionals {
        @Test
        void valueOrDefaultWillReturnValueWhenPresent() {
            final Optional<String> value = Optional.of("testValue");
            assertThat(extractor.valueOrDefault(value)).isEqualTo("testValue");
        }

        @Test
        void valueOrDefaultWillReturnDefaultWhenValueIsNotPresent() {
            final Optional<String> value = Optional.empty();
            assertThat(extractor.valueOrDefault(value)).isEqualTo("");
        }
    }

    @Nested
    class TestingExtractorWithDefaultWithStrings {

        @Test
        void valueOrDefaultWillReturnDefaultWhenValueIsNotPresent() {
            final String value = null;
            assertThat(extractorWithDefaultAssigned.valueOrDefault(value)).isEqualTo("juan");
        }
    }

    @Nested
    class TestingExtractorWithDefaultWithOptionals {

        @Test
        void valueOrDefaultWillReturnDefaultWhenValueIsNotPresent() {
            final Optional<String> value = Optional.empty();
            assertThat(extractorWithDefaultAssigned.valueOrDefault(value)).isEqualTo("juan");
        }
    }

}
