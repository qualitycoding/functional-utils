package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.Matcher.findMatch;
import static uk.co.qualitycode.utils.functional.Functional.Matcher.matcher;
import static uk.co.qualitycode.utils.functional.Functional.Matcher.matchers;

class Functional_Matcher_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> findMatch(new Object()).from(null).orElse(mock(Function.class)))
                .withMessage("findMatch(A).from(Matches<A,B>).orElse(Function<A,B>): cases must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> findMatch(new Object()).from(mock(Functional.Matcher.Matches.class)).orElse(null))
                .withMessage("findMatch(A).from(Matches<A,B>).orElse(Function<A,B>): defaultCase must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> matcher(null, mock(Function.class)))
                .withMessage("matcher(Predicate<A>,Function<A,B>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> matcher(mock(Predicate.class), null))
                .withMessage("matcher(Predicate<A>,Function<A,B>): result must not be null");
    }

    @Test
    void switchMatches() {
        assertThat(
                findMatch(10)
                        .from(matchers(
                                matcher(Functional.lessThan(5), Functional.constant(-1)),
                                matcher(Functional.greaterThan(5), Functional.constant(1))))
                        .orElse(Functional.constant(0)))
                .isEqualTo(1);
    }

    @Test
    void switchDefaultCase() {
        assertThat(
                findMatch(10)
                        .from(matchers(
                                matcher(Functional.lessThan(10), Functional.constant(-1)),
                                matcher(Functional.greaterThan(10), Functional.constant(1))))
                        .orElse(Functional.constant(0)))
                .isEqualTo(0);
    }
}
