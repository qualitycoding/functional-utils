package uk.co.qualitycode.utils.functional.monad;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Option is an facade over the Vavr Option that supplies {@link #bind(Function)} and {@link #lift(BiFunction, Option, Option)}.
 * See http://en.wikipedia.org/wiki/Option_type
 * {@see http://en.wikipedia.org/wiki/Monad_(functional_programming)}
 */
public final class Option<T> {
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
        return t.map(f).getOrElse(Option::none);
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
        return o1.toVavrOption().isDefined() && o2.toVavrOption().isDefined()
                ? of(f.apply(o1.toVavrOption().get(), o2.toVavrOption().get()))
                : new Option<C>(io.vavr.control.Option.none());
    }
}
