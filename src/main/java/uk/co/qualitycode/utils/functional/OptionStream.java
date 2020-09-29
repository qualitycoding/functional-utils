package uk.co.qualitycode.utils.functional;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class OptionStream<T> {
    private final Stream<Optional<T>> stream;

    private OptionStream(final Stream<Optional<T>> streamOfOptionals) {
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

    public Optional<T> findFirst() {
        return removeOptional().findFirst();
    }

    public Optional<T> max(final Comparator<T> comparator) {
        return removeOptional().max(comparator);
    }

    public static <T> OptionStream<T> $(final Stream<Optional<T>> streamOfOptionals) {
        return new OptionStream<>(streamOfOptionals);
    }

    private Stream<T> removeOptional() {
        return stream.filter(Optional::isPresent).map(Optional::get);
    }
}
