package uk.co.qualitycode.utils.functional.primitive.integer;

/**
 * The Func interface is designed to represent lambda / anonymous functions which are defined and used in situ. This models
 * a function that takes one argument and returns a primitive int.
 * See <a href="http://en.wikipedia.org/wiki/Anonymous_function">Lambda function</a>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Closure_(computer_programming)">Closure</a>
 */
@FunctionalInterface
public interface Predicate_int {
    /**
     * Call <tt>test</tt> to evaluate the function object
     *
     * @param a the input value
     * @return an integer (int)
     */
    boolean test(int a);
}
