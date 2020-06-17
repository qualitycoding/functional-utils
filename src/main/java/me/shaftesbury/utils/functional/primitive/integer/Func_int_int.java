package me.shaftesbury.utils.functional.primitive.integer;

import java.util.function.IntUnaryOperator;

/**
 * The Func interface is designed to represent lambda / anonymous functions which are defined and used in situ. This models
 * a function that takes one argument and returns a primitive int.
 * See <a href="http://en.wikipedia.org/wiki/Anonymous_function">Lambda function</a>
 * @see <a href="http://en.wikipedia.org/wiki/Closure_(computer_programming)">Closure</a>
 */
public interface Func_int_int extends IntUnaryOperator {
    /**
     * Call <tt>apply</tt> to evaluate the function object
     * @param a the input value
     * @return an integer (int)
     */
    int apply(int a);

    default int applyAsInt(final int a) { return apply(a); }
}
