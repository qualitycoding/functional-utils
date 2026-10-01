package uk.co.qualitycode.utils.functional;

import io.vavr.Function0;
import io.vavr.Function4;

import static java.util.Objects.requireNonNull;

/**
 * Four values to be passed together to a four-argument function.
 * See {@link UsingWrapper#using(Object, Object, Object, Object)}.
 */
public final class Using4<W, X, Y, Z> {
    private final Function0<W> w;
    private final Function0<X> x;
    private final Function0<Y> y;
    private final Function0<Z> z;

    Using4(final Function0<W> w, final Function0<X> x, final Function0<Y> y, final Function0<Z> z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public <R> R in(final Function4<W, X, Y, Z, R> f) {
        requireNonNull(f, "f must not be null");
        return f.apply(w.get(), x.get(), y.get(), z.get());
    }
}
