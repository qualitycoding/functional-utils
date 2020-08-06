package uk.co.qualitycode.utils.functional.function;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An implementation of {@link uk.co.qualitycode.utils.functional.Func} that is intended for use by the {@link me.shaftesbury.utils.functional.MException}
 *
 * @param <A> the type of the argument of the function
 * @param <B> the type of the return value of the function
 */
public abstract class UnaryFunction<A, B> implements Function<A, B> {
    /**
     * Functional composition. Return a new function which, when evaluated, returns the composition of the current {@link UnaryFunction}
     * and the supplied {@link java.util.function.Function} parameter.
     *
     * @param <C> the type of the return value of f
     * @param f   the single-argument function to be composed with this
     * @return a {@link UnaryFunction} that accepts a parameter of type A and returns a datum of type C
     * @see <a href="http://en.wikipedia.org/wiki/Function_composition_(computer_science)">Function Composition</a>
     */
    public <C> UnaryFunction<A, C> then(final Function<B, C> f) {
        return compose(this, f);
    }

    private static <A, B, C> UnaryFunction<A, C> compose(final Function<A, B> f, final Function<B, C> g) {
        return new UnaryFunction<A, C>() {
            public C apply(final A a) {
                return g.apply(f.apply(a));
            }
        };
    }

    /**
     * Given a {@link java.util.function.Function} and its argument, produce a function of no arguments that
     * can be evaluated at a later point.
     *
     * @param <A> the type of the parameter of f
     * @param <B> the type of the return value of the delayed function
     * @param f   the one-parameter function
     * @param a   the parameter of f
     * @return a function that takes no arguments and returns a value of type B
     */
    public static <A, B> Supplier<B> delay(final Function<A, B> f, final A a) {
        return () -> f.apply(a);
    }
}
