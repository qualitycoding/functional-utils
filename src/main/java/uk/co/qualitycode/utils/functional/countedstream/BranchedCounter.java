package uk.co.qualitycode.utils.functional.countedstream;

import java.util.function.Function;

public interface BranchedCounter<T, U> {
    CounterResults<T, U> orElse(Function<T, U> doIt);
}
