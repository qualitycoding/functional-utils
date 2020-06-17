package me.shaftesbury.utils.functional.primitive.integer;

import java.util.function.IntBinaryOperator;

/**
 * The Func2 interface is designed to represent lambda / anonymous functions which are defined and used in situ. This models
 * a function that takes two arguments.
 * See <a href="http://en.wikipedia.org/wiki/Anonymous_function">Lambda function</a>
 * @see <a href="http://en.wikipedia.org/wiki/Closure_(computer_programming)">Closure</a>
 */
public interface Func2_int_int_int extends IntBinaryOperator {
    /**
     * Call <tt>apply</tt> to evaluate the function object
     * @param a the first input value
     * @param b the second input value
     * @return an int
     */
    int apply(int a, int b);

    default int applyAsInt(final int a, final int b) { return applyAsInt(a, b);}
}
