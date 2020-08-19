package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static uk.co.qualitycode.utils.functional.Functional.join;

class Functional_IndentBy_Test {
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

    @Test
    void indentTest1() {
        final int level = 5;
        final String expectedResult = "     ";

        String indentedName = "";
        for (int i = 0; i < level; ++i) {
            indentedName += " ";
        }
        assertThat(expectedResult).isEqualTo(indentedName);

        final Collection<String> indentation = Functional.init(integer -> " ", level);
        assertThat("     ").isEqualTo(join("", indentation));

        final String s = Functional.fold((state, str) -> state + str, "", indentation);
        assertThat(expectedResult).isEqualTo(s);

        final Function<Collection<String>, String> folder = l -> Functional.fold((BiFunction<String, String, String>) (state, str) -> state + str, "", l);

        final String s1 = Functional.in(indentation, folder);
        assertThat(expectedResult).isEqualTo(s1);
    }

    @Test
    void indentTest2() {
        final int level = 5;
        final String expectedResult = "     BOB";
        assertThat(Functional.indentBy(level, " ", "BOB")).isEqualTo(expectedResult);
    }
}
