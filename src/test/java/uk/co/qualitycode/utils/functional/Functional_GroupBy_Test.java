package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_GroupBy_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
            .isThrownBy(()->Functional.groupBy(null, mock(Iterable.class)))
            .withMessage("groupBy(Function<T,U>,Iterable<T>): keyFn must not be null");
        assertThatIllegalArgumentException()
            .isThrownBy(()->Functional.groupBy(mock(Function.class), null))
            .withMessage("groupBy(Function<T,U>,Iterable<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(()->Functional.groupBy(null))
                .withMessage("groupBy(Function<T,U>): keyFn must not be null");
    }

    @Test
    void groupByOddVsEvenInt() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Map<Boolean, List<Integer>> output = Functional.groupBy(Functional::isEven, input);
        final Map<Boolean, List<Integer>> expected = new HashMap<>();
        expected.put(false, Arrays.asList(1, 3, 5, 7, 9));
        expected.put(true, Arrays.asList(2, 4, 6, 8, 10));
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void groupByFirstChar() {
        final List<String> input = Arrays.asList("aa", "bab", "aac", "def");
        final Map<String, List<String>> output = Functional.groupBy(s -> s.substring(0, 1), input);
        final Map<String, List<String>> expected = new HashMap<>();
        expected.put("a", Arrays.asList("aa", "aac"));
        expected.put("b", Arrays.asList("bab"));
        expected.put("d", Arrays.asList("def"));
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void curriedGroupByFirstChar() {
        final List<String> input = Arrays.asList("aa", "bab", "aac", "def");
        final Map<String, List<String>> output = Functional.groupBy((String s) -> s.substring(0, 1)).apply(input);
        final Map<String, List<String>> expected = new HashMap<>();
        expected.put("a", Arrays.asList("aa", "aac"));
        expected.put("b", Arrays.asList("bab"));
        expected.put("d", Arrays.asList("def"));
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
