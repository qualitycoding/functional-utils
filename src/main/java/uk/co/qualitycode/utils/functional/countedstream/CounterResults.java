package uk.co.qualitycode.utils.functional.countedstream;

import com.google.common.collect.ImmutableList;
import io.vavr.Tuple2;

public interface CounterResults<T, U> {
    Tuple2<ImmutableList<T>, ImmutableList<U>> get();

    ImmutableList<T> getCollection();

    ImmutableList<U> getResults();
}
