package uk.co.qualitycode.utils.functional.countedstream;

import com.google.common.collect.ImmutableList;
import uk.co.qualitycode.utils.functional.comparing.Comparator;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

public final class SeqCounter<O> {
    private final Stream<O> str;

    private SeqCounter(final Stream<O> str) {
        this.str = str;
    }

    public static <T> SeqCounter<T> $(final Stream<T> str) {
        if (isNull(str)) throw new IllegalArgumentException("stream must not be null");
        return new SeqCounter<>(str);
    }

    public static <T> SeqCounter<T> of(final Stream<T> str) {
        if (isNull(str)) throw new IllegalArgumentException("stream must not be null");
        return new SeqCounter<>(str);
    }

    public static <O> MyCollector<O> toCountedSeq() {
        return new MyCollector<>();
    }

    // TODO how can we parameterise this so that the collection type is not fixed to be ImmutableList?
    public static final class MyCollector<O> implements java.util.stream.Collector<O, List<O>, Counted<O>> {
        private MyCollector() {
        }

        @Override
        public Supplier<List<O>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<O>, O> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<O>> combiner() {
            return (l1, l2) -> {
                l1.addAll(l2);
                return l1;
            };
        }

        @Override
        public Function<List<O>, Counted<O>> finisher() {
            return CountedImpl::new;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of();
        }
    }

    public Counted<O> count() {
        return CountedImpl.of(str.collect(ImmutableList.toImmutableList()));
    }

    private static final class CountedImpl<T> implements Counted<T> {
        private final ImmutableList<T> str;

        private CountedImpl(final List<T> str) {
            this.str = ImmutableList.copyOf(str);
        }

        private static <O> Counted<O> of(final List<O> str) {
            return new CountedImpl<>(str);
        }

        @Override
        public ImmutableList<T> toList() {
            return str;
        }

        @Override
        public Stream<T> toStream() {
            return str.stream();
        }

        @Override
        public <U> Counted<T> applyToCounter(final Consumer<Integer> applyToSize) {
            if (isNull(applyToSize)) throw new IllegalArgumentException("consumer must not be null");
            applyToSize.accept(str.size());
            return this;
        }

        @Override
        public <U> BranchedCounter<T, U> sizeSatisfies(final Comparator<Integer> comparator, final Function<T, U> thenClause) {
            if (isNull(comparator)) throw new IllegalArgumentException("comparator must not be null");
            if (isNull(thenClause)) throw new IllegalArgumentException("thenClause must not be null");
            return new BranchedCounterImpl<>(comparator, thenClause, str);
        }
    }

    private static class BranchedCounterImpl<T, U> implements BranchedCounter<T, U> {

        private final Comparator<Integer> comparator;
        private final Function<T, U> thenClause;
        private final ImmutableList<T> originalCollection;

        private BranchedCounterImpl(final Comparator<Integer> comparator, final Function<T, U> thenClause, final ImmutableList<T> originalCollection) {
            this.comparator = comparator;
            this.thenClause = thenClause;
            this.originalCollection = originalCollection;
        }

        @Override
        public CounterResults<T, U> orElse(final Function<T, U> elseClause) {
            if (isNull(elseClause)) throw new IllegalArgumentException("elseClause must not be null");

            return new CounterResults<>() {
                private final ImmutableList<U> results = originalCollection.stream()
                        .map(comparator.compareWith(originalCollection.size()) ? thenClause : elseClause)
                        .collect(ImmutableList.toImmutableList());

                @Override
                public Tuple2<ImmutableList<T>, ImmutableList<U>> get() {
                    return Tuple.of(originalCollection, results);
                }

                @Override
                public ImmutableList<T> getCollection() {
                    return originalCollection;
                }

                @Override
                public ImmutableList<U> getResults() {
                    return results;
                }
            };
        }
    }
}
