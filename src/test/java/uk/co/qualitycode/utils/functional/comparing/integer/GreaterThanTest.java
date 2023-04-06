package uk.co.qualitycode.utils.functional.comparing.integer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static uk.co.qualitycode.utils.functional.comparing.integer.GreaterThan.greaterThan;
import static org.assertj.core.api.Assertions.assertThat;

class GreaterThanTest {

    @Test
    void factory() {
        assertThat(greaterThan(5)).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({"1,5,false", "5,5,false", "6,5,true"})
    void compareWith(final int value, final int testValue, final boolean expected) {
        assertThat(greaterThan(testValue).compareWith(value)).isEqualTo(expected);
    }
}
