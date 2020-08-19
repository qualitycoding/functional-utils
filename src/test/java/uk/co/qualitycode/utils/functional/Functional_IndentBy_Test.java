package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class Functional_IndentBy_Test {
    @Test
    void defaultParamsBehaviour_throwsWhenHowManyIsNegative() {
        assertThatIllegalArgumentException().isThrownBy(()-> Functional.indentBy(-1, "d", "hhljj"))
                .withMessage("Negative numbers must not be supplied as 'howMany'");
    }

    @Test
    void defaultParamsBehaviour_throwsWhenUnitOfIndentationIsNull() {
        assertThatIllegalArgumentException().isThrownBy(()-> Functional.indentBy(0, null, "hhljj"))
                .withMessage("unitOfIndentation must not be null");
    }

    @Test
    void defaultParamsBehaviour_throwsWhenUnitOfIndentThisIsNull() {
        assertThatIllegalArgumentException().isThrownBy(()-> Functional.indentBy(0, "d", null))
                .withMessage("indentThis must not be null");
    }

    @Test
    void indent() {
        final String actual = Functional.indentBy(5, "a", " string");

        assertThat(actual).isEqualTo("aaaaa string");
    }
}
