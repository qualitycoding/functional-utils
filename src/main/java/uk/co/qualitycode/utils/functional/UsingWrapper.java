package uk.co.qualitycode.utils.functional;

import io.vavr.Function0;

import static java.util.Objects.requireNonNull;

/**
 * Used for functional composition. This is the simple reversal function. y(x) is equivalent to using(x).in(y)
 * See <a href="http://en.wikipedia.org/wiki/Function_composition_(computer_science)">Function Composition</a>
 * @param <T>
 */
public final class UsingWrapper<T> {
    private UsingWrapper() {
    }

    public static <T> Using<T> using(final T value) {
        return new Using<>(() -> value);
    }

    public static <T> Using<T> using(final Function0<T> supplier) {
        requireNonNull(supplier, "supplier must not be null");
        return new Using<>(supplier);
    }

    public static <X, Y> Using2<X, Y> using(final X x, final Y y) {
        return new Using2<>(() -> x, () -> y);
    }

    public static <X, Y, Z> Using3<X, Y, Z> using(final X x, final Y y, final Z z) {
        return new Using3<>(() -> x, () -> y, () -> z);
    }

    public static <W, X, Y, Z> Using4<W, X, Y, Z> using(final W w, final X x, final Y y, final Z z) {
        return new Using4<>(() -> w, () -> x, () -> y, () -> z);
    }
}
