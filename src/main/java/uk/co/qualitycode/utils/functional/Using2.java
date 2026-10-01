package uk.co.qualitycode.utils.functional;

import io.vavr.Function0;
import io.vavr.Function2;

import static java.util.Objects.requireNonNull;

/**
 * Two values to be passed together to a two-argument function. See {@link UsingWrapper#using(Object, Object)}.
 */
public final class Using2<X, Y> {
    private final Function0<X> x;
    private final Function0<Y> y;

    Using2(final Function0<X> x, final Function0<Y> y) {
        this.x = x;
        this.y = y;
    }

    public <R> R in(final Function2<X, Y, R> f) {
        requireNonNull(f, "f must not be null");
        return f.apply(x.get(), y.get());
    }
}
