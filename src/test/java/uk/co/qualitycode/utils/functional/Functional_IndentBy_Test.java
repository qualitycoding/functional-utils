package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static uk.co.qualitycode.utils.functional.Functional.fold;
import static uk.co.qualitycode.utils.functional.Functional.indentBy;
import static uk.co.qualitycode.utils.functional.Functional.init;
import static uk.co.qualitycode.utils.functional.Functional.join;
import static uk.co.qualitycode.utils.functional.IndentBy.indent;
import static uk.co.qualitycode.utils.functional.UsingWrapper.using;

class Functional_IndentBy_Test {
    @Test
    void defaultParamsBehaviour_throwsWhenHowManyIsNegative() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> indentBy(-1, "d", "hhljj"))
                .withMessage("indentBy(int,String,String): Negative numbers must not be supplied as 'howMany'");
    }

    @Test
    void defaultParamsBehaviour_throwsWhenUnitOfIndentationIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> indentBy(0, null, "hhljj"))
                .withMessage("indentBy(int,String,String): unitOfIndentation must not be null");
    }

    @Test
    void defaultParamsBehaviour_throwsWhenUnitOfIndentThisIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> indentBy(0, "d", null))
                .withMessage("indentBy(int,String,String): indentThis must not be null");
    }

    @Test
    void indentString() {
        final String actual = indentBy(5, "a", " string");

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

        final Collection<String> indentation = init(integer -> " ", level);
        assertThat("     ").isEqualTo(join("", indentation));

        final String s = fold((state, str) -> state + str, "", indentation);
        assertThat(expectedResult).isEqualTo(s);

        final String s1 = using(indentation).in(l -> fold((state, str) -> state + str, "", l));
        assertThat(expectedResult).isEqualTo(s1);
    }

    @Test
    void indentTest2() {
        final int level = 5;
        final String expectedResult = "     BOB";
        assertThat(indentBy(level, " ", "BOB")).isEqualTo(expectedResult);
    }

    @Nested
    class Monadic {
        @Test
        void indentBy() {
            final String result = indent("a").with(5).of("0");
            assertThat(result).isEqualTo("00000a");
        }
    }
}
