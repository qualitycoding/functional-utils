package uk.co.qualitycode.utils.functional.monad;

import java.util.function.Predicate;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public abstract class ConformingObjectBuilder<T, MONAD> {
    private io.vavr.control.Option<T> value = none();
    private Predicate<T> rule = t -> true;

    public ConformingObjectBuilder<T, MONAD> withValue(final T value) {
        this.value = some(value);
        return this;
    }

    public ConformingObjectBuilder<T, MONAD> withRule(final Predicate<T> rule) {
        this.rule = rule;
        return this;
    }

    public abstract MONAD build();

    public io.vavr.control.Option<T> getValue() {
        return value;
    }

    public Predicate<T> getRule() {
        return rule;
    }
}
