package me.shaftesbury.utils.functional;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An implementation of {@link java.util.function.BiFunction} that is intended for use by the {@link me.shaftesbury.utils.functional.MException}
 * @param <A> the type of the first argument of the function
 * @param <B> the type of the second argument of the function
 * @param <C> the type of the return value of the function
 */
public abstract class BinaryFunction<A,B,C> implements BiFunction<A,B,C>
{
    /**
     * Create a curried unary function from this binary function
     * @return a function which takes an argument of type B and returns a function that accepts an argument of type A and returns
     * an object of type C
     */
    public Function<B, Function<A, C>> toFunc()
    {
        return toUnaryFunc(this);
    }

    /**
     * Helper function. The same as {@link #toFunc(Object)} above with the addition that the curried function is partially applied
     * by passing the argument <tt>b</tt>
     * @param b the argument to be passed to the curried function
     * @return a function that accepts an argument of type A and returns an object of type C
     */
    public Function<A, C> toFunc(final B b)
    {
        return toUnaryFunc(this).apply(b);
    }

    private static <A,B,C>Function<B, Function<A, C>> toUnaryFunc(final BinaryFunction<A,B,C> f)
    {
        return b -> (Function<A, C>) a -> f.apply(a,b);
    }

    /**
     * Given a {@link me.shaftesbury.utils.functional.Func2} and its two arguments, produce a function of no arguments that
     * can be evaluated at a later point.
     * @param <A> the type of the first parameter of f
     * @param <B> the type of the second parameter of f
     * @param <C> the type of the return value of the delayed function
     * @param f the two-parameter function
     * @param a the first parameter of f
     * @param b the second parameter of f
     * @return a function that takes no arguments and returns a value of type C
     */
    public static <A,B,C>Supplier<C> delay(final BiFunction<A, B, C> f, final A a, final B b)
    {
        return () -> f.apply(a,b);
    }
}
