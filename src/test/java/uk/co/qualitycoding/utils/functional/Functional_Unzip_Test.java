package uk.co.qualitycoding.utils.functional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
public class Functional_Unzip_Test {
    public Functional_Unzip_Test() {
    }

    @Test
    void unzipTest1() {
        final Collection<Pair<String, Integer>> input =
                new ArrayList<Pair<String, Integer>>();
        input.add(Pair.of("1", 1));
        input.add(Pair.of("2", 2));

        final Pair<Collection<String>, Collection<Integer>> expected =
                Pair.of(
                        Arrays.asList("1", "2"),
                        Arrays.asList(1, 2));

        final Pair<List<String>, List<Integer>> output = Functional.unzip(input);

        assertThat(output.getLeft()).containsExactlyElementsOf(expected.getLeft());
        assertThat(output.getRight()).containsExactlyElementsOf(expected.getRight());
    }

    @Test
    void iterableUnzipTest1() {
        final Collection<Pair<String, Integer>> input =
                new ArrayList<Pair<String, Integer>>();
        input.add(Pair.of("1", 1));
        input.add(Pair.of("2", 2));

        final Pair<Collection<String>, Collection<Integer>> expected =
                Pair.of(
                        Arrays.asList("1", "2"),
                        Arrays.asList(1, 2));

        final Pair<List<String>, List<Integer>> output = Functional.unzip(IterableHelper.create(input));

        assertThat(output.getLeft()).containsExactlyElementsOf(expected.getLeft());
        assertThat(output.getRight()).containsExactlyElementsOf(expected.getRight());
    }

    @Test
    void unzip3Test1() {
        final Collection<Triple<String, Integer, String>> input =
                new ArrayList<Triple<String, Integer, String>>();
        input.add(Triple.of("1", 1, "L"));
        input.add(Triple.of("2", 2, "M"));
        input.add(Triple.of("3", 3, "K"));

        final Triple<Collection<String>, Collection<Integer>, Collection<String>> expected =
                Triple.of(
                        Arrays.asList("1", "2", "3"),
                        Arrays.asList(1, 2, 3),
                        Arrays.asList("L", "M", "K"));

        final Triple<List<String>, List<Integer>, List<String>> output = Functional.unzip3(input);

        assertThat(output.getLeft()).containsExactlyElementsOf(expected.getLeft());
        assertThat(output.getMiddle()).containsExactlyElementsOf(expected.getMiddle());
        assertThat(output.getRight()).containsExactlyElementsOf(expected.getRight());
    }

    @Test
    void iterableUnzip3Test1() {
        final Collection<Triple<String, Integer, String>> input =
                new ArrayList<Triple<String, Integer, String>>();
        input.add(Triple.of("1", 1, "L"));
        input.add(Triple.of("2", 2, "M"));
        input.add(Triple.of("3", 3, "K"));

        final Triple<Collection<String>, Collection<Integer>, Collection<String>> expected =
                Triple.of(
                        Arrays.asList("1", "2", "3"),
                        Arrays.asList(1, 2, 3),
                        Arrays.asList("L", "M", "K"));

        final Triple<List<String>, List<Integer>, List<String>> output = Functional.unzip3(IterableHelper.create(input));

        assertThat(output.getLeft()).containsExactlyElementsOf(expected.getLeft());
        assertThat(output.getMiddle()).containsExactlyElementsOf(expected.getMiddle());
        assertThat(output.getRight()).containsExactlyElementsOf(expected.getRight());
    }
}
