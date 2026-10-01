package uk.co.qualitycode.utils.functional.monad;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * The base type for monads in this library.
 * <p>
 * Java has no higher-kinded types, so a monad cannot say "flatMap returns <em>the same kind of container</em> as me".
 * {@code AnyM<W, T>} works around that with a <em>witness</em>: {@code W} is a marker type unique to each monad and
 * stands in for the type constructor. {@code AnyM<Option.Witness, T>} means "an Option of T". Because {@link #flatMap}
 * only accepts functions that return the same witness, binding an Option to an Either is a compile-time error rather
 * than a runtime surprise. Moving between monads needs an explicit conversion, such as a natural transformation
 * ({@code option.toEither(...)}) or a monad transformer.
 * <p>
 * To make a type a monad:
 * <ol>
 *     <li>Declare a witness: {@code public static final class Witness { private Witness() {} }}</li>
 *     <li>Implement {@code AnyM<YourType.Witness, T>}; for a type with two parameters such as
 *     {@code Either<L, R>}, put the fixed parameter on the witness: {@code AnyM<Either.Witness<L>, R>}.</li>
 *     <li>Implement {@link #unit} and {@link #flatMap}. Everything else is derived from them.</li>
 *     <li>Optionally, override the derived methods with covariant return types so callers keep the concrete type,
 *     e.g. {@code public <U> Option<U> map(...) { return narrow(AnyM.super.map(f)); }}</li>
 * </ol>
 * Implementations must obey the monad laws, for every value {@code a}, monad {@code m} and functions {@code f}, {@code g}:
 * <ul>
 *     <li>left identity: {@code m.unit(a).flatMap(f)} is equivalent to {@code f.apply(a)}</li>
 *     <li>right identity: {@code m.flatMap(m::unit)} is equivalent to {@code m}</li>
 *     <li>associativity: {@code m.flatMap(f).flatMap(g)} is equivalent to {@code m.flatMap(x -> f.apply(x).flatMap(g))}</li>
 * </ul>
 * The test suite provides a reusable contract that checks these laws for any implementation.
 *
 * @param <W> the witness identifying the monad
 * @param <T> the type of the value(s) in the monad
 */
public interface AnyM<W, T> {
    /**
     * Wrap a value in this monad: {@code return} / {@code pure}. It is an instance method because Java cannot
     * declare an abstract static method; the receiver only identifies which monad to build.
     *
     * @param value the value to wrap
     * @param <U>   the type of the value
     * @return the minimal monad of this kind containing {@code value}
     */
    <U> AnyM<W, U> unit(U value);

    /**
     * Apply a function that returns a monad of the same kind, and flatten the result: {@code bind} / {@code >>=}.
     *
     * @param f   the function to apply to the contained value(s)
     * @param <U> the type contained in the result
     * @return the flattened result
     */
    <U> AnyM<W, U> flatMap(Function<? super T, ? extends AnyM<W, U>> f);

    /**
     * Transform the contained value(s).
     *
     * @param f   the transformation
     * @param <U> the type of the result
     * @return a monad of the same kind containing the transformed value(s)
     */
    default <U> AnyM<W, U> map(final Function<? super T, ? extends U> f) {
        requireNonNull(f, "f must not be null");
        return flatMap(t -> this.<U>unit(f.apply(t)));
    }

    /**
     * Apply a function that is itself inside a monad of the same kind (the applicative {@code <*>}).
     *
     * @param mf  the monad containing the function
     * @param <U> the type of the result
     * @return a monad containing the result of applying the function to the value(s)
     */
    default <U> AnyM<W, U> ap(final AnyM<W, ? extends Function<? super T, ? extends U>> mf) {
        requireNonNull(mf, "mf must not be null");
        return mf.flatMap(this::map);
    }

    /**
     * Combine this monad with another of the same kind using a binary function, lifting the function into the
     * monad. {@code Option.lift} and {@code MException.lift} are special cases of this.
     *
     * @param other the other monad
     * @param f     the combining function
     * @param <U>   the type in the other monad
     * @param <R>   the type of the result
     * @return a monad containing {@code f} applied to both values, or the first failure/emptiness encountered
     */
    default <U, R> AnyM<W, R> zipWith(final AnyM<W, U> other, final BiFunction<? super T, ? super U, ? extends R> f) {
        requireNonNull(other, "other must not be null");
        requireNonNull(f, "f must not be null");
        return flatMap(t -> other.map(u -> f.apply(t, u)));
    }

    /**
     * Remove one level of nesting: {@code join}.
     *
     * @param nested a monad containing a monad of the same kind
     * @param <W>    the witness
     * @param <T>    the inner type
     * @return the flattened monad
     */
    static <W, T> AnyM<W, T> flatten(final AnyM<W, ? extends AnyM<W, T>> nested) {
        requireNonNull(nested, "nested must not be null");
        return nested.flatMap(Function.identity());
    }

    /**
     * Turn a sequence of monads into a monad of a sequence, e.g. a list of Options into an Option of a list that is
     * empty if any element was empty. {@code pure} is needed because an empty input has no monad to call
     * {@link #unit} on; a method reference such as {@code Option::of} will do.
     *
     * @param pure the unit of the monad
     * @param ms   the monads to combine, in order
     * @param <W>  the witness
     * @param <T>  the type in each monad
     * @return a monad containing the values in order
     */
    static <W, T> AnyM<W, io.vavr.collection.List<T>> sequence(final Pure<W> pure, final Iterable<? extends AnyM<W, T>> ms) {
        return traverse(pure, ms, m -> m);
    }

    /**
     * Map each element to a monad and combine the results, as {@link #sequence} does.
     *
     * @param pure  the unit of the monad
     * @param input the values to map
     * @param f     the function producing a monad for each value
     * @param <W>   the witness
     * @param <A>   the type of the input values
     * @param <T>   the type in each resulting monad
     * @return a monad containing the mapped values in order
     */
    static <W, A, T> AnyM<W, io.vavr.collection.List<T>> traverse(final Pure<W> pure,
                                                                  final Iterable<? extends A> input,
                                                                  final Function<? super A, ? extends AnyM<W, T>> f) {
        requireNonNull(pure, "pure must not be null");
        requireNonNull(input, "input must not be null");
        requireNonNull(f, "f must not be null");
        // Left to right, so f is applied in input order and the first failure wins; prepend is O(1), so the list
        // is built reversed and turned round once at the end.
        return io.vavr.collection.List.<A>ofAll(input)
                .foldLeft(pure.of(io.vavr.collection.List.<T>empty()),
                        (acc, a) -> acc.zipWith(f.apply(a), (values, t) -> values.prepend(t)))
                .map(io.vavr.collection.List::reverse);
    }

    /**
     * The unit of a monad, for code that has no instance to call {@link #unit} on. Being a generic functional
     * interface it cannot be a lambda, but a method reference such as {@code Option::of} works.
     *
     * @param <W> the witness
     */
    @FunctionalInterface
    interface Pure<W> {
        <T> AnyM<W, T> of(T value);
    }
}
