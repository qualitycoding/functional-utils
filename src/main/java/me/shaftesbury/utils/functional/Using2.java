package me.shaftesbury.utils.functional;

import io.vavr.Function0;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Function4;

import static java.util.Objects.requireNonNull;

public class Using2<X, Y> {
    private final Using<X> x;
    private final Using<Y> y;

    Using2(final Function0<X> x, final Using<Y> y) {
        this.x = new Using<>(x);
        this.y = y;
    }

    public <R> R in(final Function2<X, Y, R> f) {
        requireNonNull(f, "f must not be null");
        return f.apply(x.getValue().get()).apply(y.getValue().get());
    }

    public <Y2, Z, R> R in(final Function3<X, Y2, Z, R> f) {
        requireNonNull(f, "f must not be null");
        try {
            return ((Using2<Y2, Z>) this.y.getValue().get()).in(f.apply(x.getValue().get()));
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("the number of parameters supplied does not match the number of parameters expected");
        }
    }

    public <Y2, Z2,A,Z, R> R in(final Function4<X, Y2, Z2, A, R> f) {
        requireNonNull(f, "f must not be null");
        try {
            return ((Using2<Y2, Z>) this.y.getValue().get()).in(f.apply(x.getValue().get()));
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("the number of parameters supplied does not match the number of parameters expected");
        }
    }
}
