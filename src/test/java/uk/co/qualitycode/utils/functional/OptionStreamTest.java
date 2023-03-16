package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.qualitycode.utils.functional.OptionStream.$;

class OptionStreamTest {

    @Test
    void factoryMethodIsInAPI() {
        final Stream<Integer> os = $(Stream.of(Optional.of(1), Optional.empty()));
        assertThat(os).isNotNull().containsExactly(1);
    }

    @Test
    void demo() {
        final List<Optional<String>> input = Arrays.asList(Optional.of("this"), Optional.empty(), Optional.of("is"), Optional.empty(), Optional.of("it"));
        {
            final String result = input.stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(String::toUpperCase)
                    .collect(Collectors.joining(" "));

            assertThat(result).isEqualTo("THIS IS IT");
        }
        {
            final String result = $(input.stream())
                    .map(String::toUpperCase)
                    .collect(Collectors.joining(" "));

            assertThat(result).isEqualTo("THIS IS IT");
        }
    }
}