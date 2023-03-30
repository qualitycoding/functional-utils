package uk.co.qualitycode.utils.countedstream;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static uk.co.qualitycode.utils.comparing.integer.HasSize.hasSize;
import static uk.co.qualitycode.utils.countedstream.SeqCounter.toCountedSeq;

public final class ApplyOrTakeAction {
    private ApplyOrTakeAction() {
    }

    public static <U, V> ApplyOr<U, V> apply(final Function<U, V> f) {
        return new ApplyOr<>(f);
    }

    public static class ApplyOr<U, V> {
        private final Function<U, V> f;

        private ApplyOr(final Function<U, V> f) {
            this.f = f;
        }

        public Action<U, V> ifSingleElement(final Collection<U> us) {
            return new Action<>(f, us);
        }

        public static class Action<U, V> {
            private final Function<U, V> f;
            private final Collection<U> xs;

            private Action(final Function<U, V> f, final Collection<U> xs) {
                this.f = f;
                this.xs = xs;
            }

            public V orElse(final Supplier<V> action) {
                return doIt(action).get(0);
            }

            public Optional<V> orElseOptional(final Supplier<V> action) {
                return doIt(action).stream().findFirst();
            }

            public V orThrow(final Supplier<? extends RuntimeException> ex) {
                return doIt(() -> {
                    throw ex.get();
                }).get(0);
            }

            private ImmutableList<V> doIt(final Supplier<V> action) {
                // We need special behaviour for empty collections because they will never trigger the map in the CounterResults constructor during orElse()
                return xs.isEmpty()
                        ? ImmutableList.of(action.get())
                        : xs.stream()
                        .collect(toCountedSeq())
                        .sizeSatisfies(hasSize(1), f)
                        .orElse(__ -> action.get())
                        .getResults();
            }
        }

    }
}
