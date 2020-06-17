package me.shaftesbury.utils.functional;

import io.vavr.Function0;

import static java.util.Objects.requireNonNull;

public class UsingWrapper<T> {
    public static <T> Using<T> using(final T value) {
        return new Using<>(() -> value);
    }

    public static <T> Using<T> using(final Function0<T> supplier) {
        requireNonNull(supplier, "supplier must not be null");
        return new Using<>(supplier);
    }

    public static <X, Y> Using2<X, Y> using(final X x, final Y y) {
        return new Using2<>(() -> x, using(() -> y));
    }

    public static <X, Y, Z> Using2<X, Using2<Y, Z>> using(final X x, final Y y, final Z z) {
        return using(x, using(y, z));
    }

    public static <X, Y, Z, Z1> Using2<X, Using2<Y, Using2<Z, Z1>>> using(final X x, final Y y, final Z z, final Z1 z1) {
        return using(x, using(y, using(z, z1)));
    }
}
