package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class Functional_Unzip_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.unzip(null))
                .withMessage("unzip(Iterable<Tuple2<A,B>>): input must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.unzip3(null))
                .withMessage("unzip3(Iterable<Tuple3<A,B,C>>): input must not be null");
    }

    @Test
    void unzipReturnsTwoLists() {
        final Collection<Tuple2<String, Integer>> input = new ArrayList<>();
        input.add(new Tuple2<>("1", 1));
        input.add(new Tuple2<>("2", 2));

        final Tuple2<Collection<String>, Collection<Integer>> expected =
                new Tuple2<>(
                        Arrays.asList("1", "2"),
                        Arrays.asList(1, 2));

        final Tuple2<List<String>, List<Integer>> output = Functional.unzip(input);

        assertThat(output).satisfies(__ -> {
            assertThat(output._1()).containsExactlyElementsOf(expected._1());
            assertThat(output._2()).containsExactlyElementsOf(expected._2());
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> output._1().add("string"));
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> output._2().add(0));
        });
    }

    @Test
    void unzip3ReturnsThreeLists() {
        final Collection<Tuple3<String, Integer, String>> input =
                new ArrayList<>();
        input.add(new Tuple3<>("1", 1, "L"));
        input.add(new Tuple3<>("2", 2, "M"));
        input.add(new Tuple3<>("3", 3, "K"));

        final Tuple3<Collection<String>, Collection<Integer>, Collection<String>> expected =
                new Tuple3<>(
                        Arrays.asList("1", "2", "3"),
                        Arrays.asList(1, 2, 3),
                        Arrays.asList("L", "M", "K"));

        final Tuple3<List<String>, List<Integer>, List<String>> output = Functional.unzip3(input);

        assertThat(output).satisfies(__ -> {
            assertThat(output._1()).containsExactlyElementsOf(expected._1());
            assertThat(output._2()).containsExactlyElementsOf(expected._2());
            assertThat(output._2()).containsExactlyElementsOf(expected._2());
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> output._1().add("string"));
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> output._2().add(0));
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> output._3().add("0"));
        });
    }
}
