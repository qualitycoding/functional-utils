package me.shaftesbury.utils.functional.primitive.integer;

/**
 * The Func2 interface is designed to represent lambda / anonymous functions which are defined and used in situ. This models
 * a function that takes two arguments.
 * See <a href="http://en.wikipedia.org/wiki/Anonymous_function">Lambda function</a>
 * @param <C> the type of the return value
 * @see <a href="http://en.wikipedia.org/wiki/Closure_(computer_programming)">Closure</a>
 */
public interface Func2_int_int_T<C> {
    /**
     * Call <tt>apply</tt> to evaluate the function object
     * @param a the first input value
     * @param b the second input value
     * @return an element of type C
     */
    C apply(int a, int b);
}
