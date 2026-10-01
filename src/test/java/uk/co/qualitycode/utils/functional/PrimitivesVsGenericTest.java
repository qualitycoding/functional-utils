package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Collections;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator_f;

/**
 * The primitive int implementations must agree with the generic ones.
 */
class PrimitivesVsGenericTest {
    @ParameterizedTest
    @ValueSource(ints = {1, 1_000, 100_000})
    void primitiveAndGenericJoinAgree(final int howMany) {
        final String expected = String.join("", Collections.nCopies(howMany, "10"));

        final String primitive = uk.co.qualitycode.utils.functional.primitive.integer.Functional.join(
                "", uk.co.qualitycode.utils.functional.primitive.integer.Functional.init(10, howMany));
        final String generic = Functional.join("", Functional.init(Functional.constant(10), howMany));

        assertThat(primitive).isEqualTo(expected);
        assertThat(generic).isEqualTo(expected);
    }

    @ParameterizedTest(name = "the last of 2, 4, ... {0}*2 below {0}/2 is {1}")
    @CsvSource({"10, 4", "1000, 498", "100000, 49998"})
    void primitiveAndGenericFindLastAgree(final int howMany, final int expected) {
        final Predicate<Integer> belowHalf = a -> a < howMany / 2;

        final int primitive = uk.co.qualitycode.utils.functional.primitive.integer.Functional.findLast(
                a -> a < howMany / 2,
                uk.co.qualitycode.utils.functional.primitive.integer.Functional.init(doublingGenerator_f, howMany));
        final Option<Integer> generic = Functional.findLast(belowHalf, Functional.init(doublingGenerator, howMany));

        assertThat(primitive).isEqualTo(expected);
        assertThat(generic).isEqualTo(Option.of(expected));
    }
}
