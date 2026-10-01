package uk.co.qualitycode.utils.functional;

import io.vavr.Function0;
import io.vavr.Function3;

import static java.util.Objects.requireNonNull;

/**
 * Three values to be passed together to a three-argument function. See {@link UsingWrapper#using(Object, Object, Object)}.
 */
public final class Using3<X, Y, Z> {
    private final Function0<X> x;
    private final Function0<Y> y;
    private final Function0<Z> z;

    Using3(final Function0<X> x, final Function0<Y> y, final Function0<Z> z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public <R> R in(final Function3<X, Y, Z, R> f) {
        requireNonNull(f, "f must not be null");
        return f.apply(x.get(), y.get(), z.get());
    }
}
