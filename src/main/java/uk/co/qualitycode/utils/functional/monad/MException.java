package uk.co.qualitycode.utils.functional.monad;

import io.vavr.Lazy;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import uk.co.qualitycode.utils.functional.function.BinaryFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class MException<U> implements AnyM<MException.Witness, U> {
    /**
     * Identifies MException as an {@link AnyM}.
     */
    public static final class Witness {
        private Witness() {
        }
    }

    /**
     * Evaluated at most once, on first use, and safely published between threads (vavr's Lazy is thread-safe).
     * A null result is a legitimate value and is remembered like any other.
     */
    private final Lazy<Either<Tuple2<RuntimeException, StackTraceElement[]>, U>> outcome;

    private MException(final Lazy<Either<Tuple2<RuntimeException, StackTraceElement[]>, U>> outcome) {
        this.outcome = outcome;
    }

    // this is 'return'
    public static <B> MException<B> toMException(final Supplier<B> f) {
        requireNonNull(f, "f");
        return new MException<>(Lazy.of(() -> evaluate(f)));
    }

    /**
     * An MException holding a successfully computed value: the monad's {@code pure}, usable as {@code MException::of}
     * wherever an {@link AnyM.Pure} is needed.
     *
     * @param value the value
     * @param <B>   the type of the value
     * @return a successful MException
     */
    public static <B> MException<B> of(final B value) {
        return toMException(() -> value);
    }

    public <B> MException<B> bind(final Function<U, MException<B>> f) {
        return flatMap(f);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The value is held as an already-successful computation.
     */
    @Override
    public <B> MException<B> unit(final B value) {
        return of(value);
    }

    /**
     * {@inheritDoc}
     * <p>
     * A failure short-circuits; an exception thrown by {@code f} becomes a failure of the result.
     */
    @Override
    public <B> MException<B> flatMap(final Function<? super U, ? extends AnyM<Witness, B>> f) {
        requireNonNull(f, "f");
        return outcome.get().<MException<B>>fold(
                MException::failed,
                value -> {
                    try {
                        return narrow(f.apply(value));
                    } catch (final RuntimeException ex) {
                        return failed(new Tuple2<>(ex, new Throwable().getStackTrace()));
                    }
                });
    }

    @Override
    public <B> MException<B> map(final Function<? super U, ? extends B> f) {
        return narrow(AnyM.super.map(f));
    }

    @Override
    public <B> MException<B> ap(final AnyM<Witness, ? extends Function<? super U, ? extends B>> mf) {
        return narrow(AnyM.super.ap(mf));
    }

    @Override
    public <B, R> MException<R> zipWith(final AnyM<Witness, B> other, final BiFunction<? super U, ? super B, ? extends R> f) {
        return narrow(AnyM.super.zipWith(other, f));
    }

    /**
     * Recover the concrete type of an MException viewed as an {@link AnyM}. Safe because only MException uses
     * {@link Witness}.
     *
     * @param m   an MException viewed as an AnyM
     * @param <T> the type of the value
     * @return the same MException
     */
    public static <T> MException<T> narrow(final AnyM<Witness, T> m) {
        requireNonNull(m, "m");
        return (MException<T>) m;
    }

    public static <A, B, C> MException<C> lift(final BiFunction<A, B, C> f, final MException<A> a, final MException<B> b) {
        requireNonNull(f, "f");
        requireNonNull(a, "a");
        requireNonNull(b, "b");
        return a.bind(x -> b.bind(y -> toMException(BinaryFunction.delay(f, x, y))));
    }

    public boolean hasException() {
        return outcome.get().isLeft();
    }

    /**
     * @return the exception thrown while computing the value, or none if the computation succeeded
     */
    public Option<RuntimeException> getException() {
        return outcome.get().<Option<RuntimeException>>fold(failure -> Option.of(failure._1()), value -> Option.none());
    }

    /**
     * @return the exception and the stack at the point it was caught, or none if the computation succeeded.
     * The stack trace array is a copy.
     */
    public Option<Tuple2<RuntimeException, StackTraceElement[]>> getExceptionWithStackTrace() {
        return outcome.get().<Option<Tuple2<RuntimeException, StackTraceElement[]>>>fold(
                failure -> Option.of(new Tuple2<>(failure._1(), failure._2().clone())),
                value -> Option.none());
    }

    /**
     * @return the computed value
     * @throws RuntimeException the exception thrown while computing the value, if there was one
     */
    public U read() {
        return outcome.get().fold(failure -> {
            throw failure._1();
        }, Function.identity());
    }

    private static <B> MException<B> failed(final Tuple2<RuntimeException, StackTraceElement[]> failure) {
        return new MException<>(Lazy.of(() -> Either.left(failure)));
    }

    private static <B> Either<Tuple2<RuntimeException, StackTraceElement[]>, B> evaluate(final Supplier<B> f) {
        try {
            return Either.right(f.get());
        } catch (final RuntimeException ex) {
            return Either.left(new Tuple2<>(ex, new Throwable().getStackTrace()));
        }
    }
}
