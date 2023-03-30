package uk.co.qualitycode.utils.functional.countedstream;

import com.google.common.collect.ImmutableList;
import uk.co.qualitycode.utils.functional.comparing.Comparator;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Counted<T> {
    ImmutableList<T> toList();

    Stream<T> toStream();

    <U> Counted<T> applyToCounter(Consumer<Integer> applyToSize);

    <U> BranchedCounter<T, U> sizeSatisfies(Comparator<Integer> predicate, Function<T, U> then);
}
