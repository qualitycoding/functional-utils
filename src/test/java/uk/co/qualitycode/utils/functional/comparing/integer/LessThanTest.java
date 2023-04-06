package uk.co.qualitycode.utils.functional.comparing.integer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static uk.co.qualitycode.utils.functional.comparing.integer.LessThan.lessThan;
import static org.assertj.core.api.Assertions.assertThat;

class LessThanTest {

    @Test
    void factory() {
        assertThat(lessThan(5)).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({"1,5,true", "5,5,false", "6,5,false"})
    void compareWith(final int value, final int testValue, final boolean expected) {
        assertThat(lessThan(testValue).compareWith(value)).isEqualTo(expected);
    }
}
