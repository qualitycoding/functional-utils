package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class Functional_GroupBy_Test {
    @Test
    void groupByOddVsEvenInt() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Map<Boolean, List<Integer>> output = Functional.groupBy(Functional.isEven, input);
        final Map<Boolean, List<Integer>> expected = new HashMap<>();
        expected.put(false, Arrays.asList(1, 3, 5, 7, 9));
        expected.put(true, Arrays.asList(2, 4, 6, 8, 10));
        assertThat(output.get(true)).containsExactlyElementsOf(expected.get(true));
        assertThat(output.get(false)).containsExactlyElementsOf(expected.get(false));
    }

    @Test
    void groupByStringFirstTwoChar() {
        final List<String> input = Arrays.asList("aa", "aab", "aac", "def");
        final Map<String, List<String>> output = Functional.groupBy(s -> s.substring(0, 1), input);
        final Map<String, List<String>> expected = new HashMap<>();
        expected.put("a", Arrays.asList("aa", "aab", "aac"));
        expected.put("d", Arrays.asList("def"));
        assertThat(output.get("a")).containsExactlyElementsOf(expected.get("a"));
        assertThat(output.get("d")).containsExactlyElementsOf(expected.get("d"));
        assertThat(new TreeSet<>(output.keySet())).containsExactlyElementsOf(new TreeSet<>(expected.keySet()));
    }
}
