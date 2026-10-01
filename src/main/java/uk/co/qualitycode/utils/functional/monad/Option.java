package uk.co.qualitycode.utils.functional.monad;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Option is an facade over the Vavr Option that supplies {@link #bind(Function)} and {@link #lift(BiFunction, Option, Option)}.
 * See http://en.wikipedia.org/wiki/Option_type
 * {@see http://en.wikipedia.org/wiki/Monad_(functional_programming)}
 */
public final class Option<T> implements AnyM<Option.Witness, T> {
    /**
     * Identifies Option as an {@link AnyM}.
     */
    public static final class Witness {
        private Witness() {
        }
    }

    private final io.vavr.control.Option<T> t;

    private Option(final io.vavr.control.Option<T> t) {
        this.t = t;
    }

    public static <T> Option<T> of(final T t) {
        return new Option<>(io.vavr.control.Option.of(t));
    }

    public static <T> Option<T> of(final Optional<T> t) {
        return t==null?none():new Option<>(io.vavr.control.Option.ofOptional(t));
    }

    public static <T> Option<T> of(final io.vavr.control.Option<T> t) {
        return t==null?none():new Option<>(t);
    }

    public static <T> Option<T> none() {
        return new Option<>(io.vavr.control.Option.none());
    }

    public boolean isNone() { return t.isEmpty(); }
    public boolean isSome() { return t.isDefined(); }

    public T get() { return t.get(); }

    public T getOrElse(final T t) { return this.t.getOrElse(t); }

    public T getOrElse(final Supplier<T> supplier) { return t.getOrElse(supplier); }

    public <X extends Throwable> T getOrElseThrow(final Supplier<X> supplier) throws X {
        return t.getOrElseThrow(supplier);
    }

    /**
     * Convert this Option to a java.util.Optional
     *
     * @return the Optional containing <tt>t</tt>
     */
    public Optional<T> toJavaOptional() {
        return t.toJavaOptional();
    }

    /**
     * Convert this Option to a Vavr Option
     *
     * @return the Option containing <tt>t</tt>
     */
    public io.vavr.control.Option<T> toVavrOption() {
        return t;
    }

    /**
     * Apply a function to the underlying object and return the result
     * {@see http://en.wikipedia.org/wiki/Monad_(functional_programming)}
     *
     * @param <U> the type of the resulting Option type
     * @param f   the function to be bound
     * @return an Option containing the result of the function <tt>f</tt> or empty
     */
    public <U> Option<U> bind(final Function<T, Option<U>> f) {
        return flatMap(f);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Like {@link #of(Object)}, a null value gives none, so the monad laws hold for non-null values.
     */
    @Override
    public <U> Option<U> unit(final U value) {
        return of(value);
    }

    @Override
    public <U> Option<U> flatMap(final Function<? super T, ? extends AnyM<Witness, U>> f) {
        requireNonNull(f, "f must not be null");
        return t.<Option<U>>map(value -> narrow(f.apply(value))).getOrElse(Option::none);
    }

    @Override
    public <U> Option<U> map(final Function<? super T, ? extends U> f) {
        return narrow(AnyM.super.map(f));
    }

    @Override
    public <U> Option<U> ap(final AnyM<Witness, ? extends Function<? super T, ? extends U>> mf) {
        return narrow(AnyM.super.ap(mf));
    }

    @Override
    public <U, R> Option<R> zipWith(final AnyM<Witness, U> other, final BiFunction<? super T, ? super U, ? extends R> f) {
        return narrow(AnyM.super.zipWith(other, f));
    }

    /**
     * Recover the concrete type of an Option viewed as an {@link AnyM}. Safe because only Option uses
     * {@link Witness}.
     *
     * @param m   an Option viewed as an AnyM
     * @param <T> the contained type
     * @return the same Option
     */
    public static <T> Option<T> narrow(final AnyM<Witness, T> m) {
        return (Option<T>) requireNonNull(m, "m must not be null");
    }

    /**
     * Given two monadic Options apply the supplied binary function to them if they are both defined and return
     * a wrapped Option containing the result or empty.
     *
     * @param f   the binary function to be lifted
     * @param o1  the first Option to be passed to the lift function <tt>f</tt>
     * @param o2  the second Option to be passed to the lift function <tt>f</tt>
     * @param <A> the type of the first Option
     * @param <B> the type of the second Option
     * @param <C> the type of the resulting Option
     * @return an Option containing the result of the lifted function as applied to <tt>o1</tt> and <tt>o2</tt> or empty
     */
    public static <A, B, C> Option<C> lift(final BiFunction<A, B, C> f, final Option<A> o1, final Option<B> o2) {
        return o1.zipWith(o2, f);
    }

    /**
     * Two Options are equal when both are empty, or both contain equal values.
     */
    @Override
    public boolean equals(final Object o) {
        return this == o || o instanceof Option && t.equals(((Option<?>) o).t);
    }

    @Override
    public int hashCode() {
        return t.hashCode();
    }

    @Override
    public java.lang.String toString() {
        return t.map(value -> "Some(" + value + ")").getOrElse("None");
    }
}
