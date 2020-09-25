package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.qualitycode.utils.functional.OptionStream.$;

class OptionStreamTest {

    private OptionStream<Integer> stream;

    @BeforeEach
    public void setup() {
        stream = $(Stream.of(Optional.of(1), Optional.empty(), Optional.of(10)));
    }

    @Test
    void factoryMethodIsInAPI() {
        final OptionStream<Integer> os = $(Stream.of(Optional.of(1), Optional.empty()));
        assertThat(os).isNotNull();
    }

    @Test
    void filterIsInAPI() {
        final Stream<Integer> os = stream.filter(x -> x > 1);
        assertThat(os).isNotNull();
        assertThat(os).containsExactly(10);
    }

    @Test
    void mapIsInAPI() {
        final Stream<String> os = stream.map(x -> Integer.toString(x));
        assertThat(os).isNotNull();
        assertThat(os).containsExactly("1", "10");
    }

    @Test
    void flatMapIsInAPI() {
        final Stream<Integer> os = stream.flatMap(Stream::of);
        assertThat(os).isNotNull();
        assertThat(os).containsExactly(1, 10);
    }

    @Test
    void collectIsInAPI() {
        final List<Integer> os = stream.collect(Collectors.toList());
        assertThat(os).isNotNull().containsExactly(1, 10);
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