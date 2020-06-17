package me.shaftesbury.utils.functional.primitive.integer;

import java.util.function.IntSupplier;

/**
 * The Func0 interface is designed to represent lambda / anonymous functions which are defined and used in situ. This is similar to the
 * {@link java.util.function.Supplier} interface except this interface models a function that takes no arguments.
 * See <a href="http://en.wikipedia.org/wiki/Anonymous_function">Lambda function</a>
 * @see <a href="http://en.wikipedia.org/wiki/Closure_(computer_programming)">Closure</a>
 */
public interface Func0_int extends IntSupplier
{
    /**
     * Call <tt>apply</tt> to evaluate the function object
     * @return an element of type R
     */
    int apply();

    default int getAsInt() { return apply(); }
}
