package uk.co.qualitycode.utils.functional.monad;

import io.vavr.Tuple2;
import uk.co.qualitycode.utils.functional.function.BinaryFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MException<U> {
    private Supplier<U> fn;
    private U state;
    private RuntimeException exception;
    private StackTraceElement[] stacktrace;

    // this is 'return'
    public static <B> MException<B> toMException(final Supplier<B> f) {
        return new MException<>(f);
    }

    public <B> MException<B> bind(final Function<U, MException<B>> f) {
        if (f == null) throw new IllegalArgumentException("f");
        if (!hasException())
            try {
                return f.apply(state);
            } catch (final RuntimeException ex) {
                return new MException<>(ex, new Throwable().getStackTrace());
            }
        else
            return new MException<>(exception, stacktrace);
    }

    private static <C> MException<C> toMException(final Tuple2<RuntimeException, StackTraceElement[]> exc) {
        return new MException(exc._1(), exc._2());
    }

    public static <A, B, C> MException<C> lift(final BiFunction<A, B, C> f, final MException<A> a, final MException<B> b) {
        if (f == null) throw new IllegalArgumentException("f");
        if (a == null) throw new IllegalArgumentException("a");
        if (b == null) throw new IllegalArgumentException("b");

        if (a.hasException()) return toMException(a.getExceptionWithStackTrace());
        if (b.hasException()) return toMException(b.getExceptionWithStackTrace());
        return new MException(BinaryFunction.delay(f, a.read(), b.read()));
    }

    private MException(final Supplier<U> f) {
        if (f == null) throw new IllegalArgumentException("f");
        fn = f;
    }

    private MException(final RuntimeException ex, final StackTraceElement[] stack) {
        exception = ex;
        stacktrace = stack;
    }

    public boolean hasException() {
        if (exception != null) return true;
        if (state == null) {
            try {
                state = fn.get();
            } catch (final RuntimeException ex) {
                exception = ex;
                stacktrace = new Throwable().getStackTrace();
            }
        }
        return exception != null;
    }

    public RuntimeException getException() {
        return exception; // what happens if exception==null?
    }

    public Tuple2<RuntimeException, StackTraceElement[]> getExceptionWithStackTrace() {
        return new Tuple2<>(exception, stacktrace); // what happens if exception==null?
    }

    public U read() {
        if (!hasException()) return state;
        throw exception;
    }
}
