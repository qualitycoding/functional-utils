package uk.co.qualitycode.utils.functional.monad;

import io.vavr.Lazy;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import uk.co.qualitycode.utils.functional.function.BinaryFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class MException<U> {
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
        if (f == null) throw new IllegalArgumentException("f");
        return new MException<>(Lazy.of(() -> evaluate(f)));
    }

    public <B> MException<B> bind(final Function<U, MException<B>> f) {
        if (f == null) throw new IllegalArgumentException("f");
        return outcome.get().<MException<B>>fold(
                MException::failed,
                value -> {
                    try {
                        return f.apply(value);
                    } catch (final RuntimeException ex) {
                        return failed(new Tuple2<>(ex, new Throwable().getStackTrace()));
                    }
                });
    }

    public static <A, B, C> MException<C> lift(final BiFunction<A, B, C> f, final MException<A> a, final MException<B> b) {
        if (f == null) throw new IllegalArgumentException("f");
        if (a == null) throw new IllegalArgumentException("a");
        if (b == null) throw new IllegalArgumentException("b");
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
