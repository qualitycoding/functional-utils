package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_TakeNAndYield_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeNAndYield(-1, mock(Iterable.class)))
                .withMessage("takeNAndYield(int,Iterable<A>): howMany must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.takeNAndYield(1, null))
                .withMessage("takeNAndYield(int,Iterable<A>): input must not be null");
    }

    @Test
    void takeMoreElementsThanSupplied() {
        final Iterable<Integer> input = Functional.Lazy.init(FunctionalTest.doublingGenerator, 5);
        final Tuple2<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(20, input);
        final List<Integer> expectedList = Arrays.asList(2, 4, 6, 8, 10);
        final List<Integer> expectedRemainder = Collections.emptyList();
        assertThat(output).satisfies(__-> {
            assertThat(output._1()).containsExactlyElementsOf(expectedList);
            assertThat(output._2()).containsExactlyElementsOf(expectedRemainder);
        });
    }

    @Test
    void takeNAndYield() {
        final Iterable<Integer> input = Functional.Lazy.init(FunctionalTest.doublingGenerator, 5);
        final Tuple2<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(2, input);
        final List<Integer> expectedList = Arrays.asList(2, 4);
        final List<Integer> expectedRemainder = Arrays.asList(6, 8, 10);
        assertThat(output).satisfies(__-> {
            assertThat(output._1()).containsExactlyElementsOf(expectedList);
            assertThat(output._2()).containsExactlyElementsOf(expectedRemainder);
        });
    }

    @Test
    void takeNoElementsAndYieldAllInput() {
        final Iterable<Integer> input = Functional.Lazy.init(FunctionalTest.doublingGenerator, 5);
        final Tuple2<List<Integer>, Iterable<Integer>> output = Functional.takeNAndYield(0, input);
        final List<Integer> expectedList = Arrays.asList();
        final List<Integer> expectedRemainder = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(output).satisfies(__-> {
            assertThat(output._1()).containsExactlyElementsOf(expectedList);
            assertThat(output._2()).containsExactlyElementsOf(expectedRemainder);
        });
    }
}
