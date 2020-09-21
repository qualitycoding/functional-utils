package uk.co.qualitycode.utils.functional;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class OptionStream<T> {
    private final Stream<Optional<T>> stream;

    public OptionStream(final Stream<Optional<T>> streamOfOptionals) {
        this.stream = streamOfOptionals;
    }

    public Stream<T> filter(final Predicate<T> predicate) {
        return removeOptional().filter(predicate);
    }

    public <U> Stream<U> map(final Function<T, U> fn) {
        return removeOptional().map(fn);
    }

    public <U> Stream<U> flatMap(final Function<T, Stream<U>> o) {
        return removeOptional().flatMap(o);
    }

    public <R, A> R collect(final Collector<? super T, A, R> collector) {
        return removeOptional().collect(collector);
    }

    private Stream<T> removeOptional() {
        return stream.filter(Optional::isPresent).map(Optional::get);
    }
}
