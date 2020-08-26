package uk.co.qualitycode.utils.functional.monad;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.co.qualitycode.utils.functional.monad.Requirement.require;

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
}
