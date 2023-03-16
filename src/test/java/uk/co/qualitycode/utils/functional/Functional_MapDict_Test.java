package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_MapDict_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.map_dict(null, mock(Iterable.class)))
                .withMessage("map_dict(Function<A,Map.Entry<B,C>>,Iterable<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.map_dict(mock(Function.class), null))
                .withMessage("map_dict(Function<A,Map.Entry<B,C>>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.map_dict_t2(null, mock(Iterable.class)))
                .withMessage("map_dict_t2(Function<A,Tuple2<B,C>>,Iterable<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.map_dict_t2(mock(Function.class), null))
                .withMessage("map_dict_t2(Function<A,Tuple2<B,C>>,Iterable<A>): input must not be null");
    }

    @Test
    void mapDictUsingMapEntry() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Map<Integer, String> expected = input.stream().collect(Collectors.toMap(i -> i, i -> Integer.toString(i)));
        final Map<Integer, String> output = Functional.map_dict(i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i)), input);
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void mapDictUsingTuple2() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Map<Integer, String> expected = input.stream().collect(Collectors.toMap(i -> i, i -> Integer.toString(i)));
        final Map<Integer, String> output = Functional.map_dict_t2(i -> new Tuple2<>(i, Integer.toString(i)), input);
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
