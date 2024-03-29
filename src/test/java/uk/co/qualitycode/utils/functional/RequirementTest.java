package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.co.qualitycode.utils.functional.Requirement.require;

class RequirementTest {
    @Test
    void requireReturnsCheckedValue() {
        assertThat(require(5).toBe().between(1).and(6)).isEqualTo(5);
    }

    @Test
    void requireThrows() {
        assertThatExceptionOfType(RequirementException.class)
                .isThrownBy(() -> require(0).toBe().between(1).and(6))
                .withMessage("'0' is not between '1' and '6'");
    }

    @Test
    void requireNonNullReturnsCheckedValue() {
        final java.lang.String value = "ggl";
        assertThat(require(value).toBe().nonNull()).isEqualTo(value);
    }

    @Test
    void requireNonNullThrows() {
        assertThatExceptionOfType(RequirementException.class)
                .isThrownBy(() -> require(null).toBe().nonNull())
                .withMessage("value must not be null");
    }
}
