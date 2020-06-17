package me.shaftesbury.utils.functional;

import io.vavr.control.Either;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * Herein are contained some standard algorithms from functional programming.
 * See <a href="http://en.wikipedia.org/wiki/Functional_programming">Functional Programming</a>
 * for more information
 */
public final class Functional {
    private Functional() {
    }

    /**
     * A simple predicate which checks the contents of the string parameter.
     *
     * @param s the input string
     * @return true if s is either null or s is the empty string; false otherwise.
     */
    @Deprecated // Use StringUtils instead of this please
    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Concatenate all of the input elements into a single string where each element is separated from the next by the supplied delimiter
     *
     * @param delimiter used to separate consecutive elements in the output
     * @param strs      input sequence, each element of which must be convertible to a string
     * @param <T>       the type of the element in the input sequence
     * @return a string containing the string representation of each input element separated by the supplied delimiter
     */
    public static <T> String join(final String delimiter, final Iterable<T> strs) {
        if (strs == null) return "";
        final Iterator<T> it = strs.iterator();
        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        while (it.hasNext()) {
            if (!isFirst) sb.append(delimiter);
            sb.append(it.next());
            isFirst = false;
        }
        return sb.toString();
    }

    /**
     * A string function: generate a string that contains the 'unitOfIndentation' repeated 'howMany' times prepended to 'indentThis'
     *
     * @param howMany           times should the unitOfIndentation be prefixed to the supplied 'indentThis' string
     * @param unitOfIndentation the indentation
     * @param indentThis        the input string that should be indented
     * @return a string indenting the input string by the indicated number of units
     */
    public static String indentBy(final int howMany, final String unitOfIndentation, final String indentThis) {
        final Collection<String> indentation = init(integer -> unitOfIndentation, howMany);
        return fold((state, str) -> str + state, indentThis, indentation);
    }

    /**
     * foldAndChoose: <tt>fold</tt> except that instead of folding every element in the input sequence, <tt>fold</tt>
     * only those for which the fold function 'f' returns a Some value (see <tt>Option</tt>)
     *
     * @param <A>          the type of the initialValue / seed
     * @param <B>          the type of the element in the input sequence
     * @param f            is the fold function modified such that the return value contains an Option in addition to the state
     * @param initialValue the seed for the fold function
     * @param input        the input sequence
     * @return the folded value paired with those transformed elements which are Some
     * @throws OptionNoValueAccessException
     */
    public static <A, B> Pair<A, List<B>> foldAndChoose(
            final BiFunction<A, B, Pair<A, Option<B>>> f,
            final A initialValue, final Iterable<B> input) throws OptionNoValueAccessException {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        A state = initialValue;
        final List<B> results = new ArrayList<>();
        for (final B b : input) {
            final Pair<A, Option<B>> intermediate = f.apply(state, b);
            state = intermediate.getLeft();
            if (!intermediate.getRight().isNone())
                results.add(intermediate.getRight().Some());
        }
        return Pair.of(state, Collections.unmodifiableList(results));
    }

    public static <T> List<T> toList(final Enumeration<T> input) {
        final List<T> output = new ArrayList<>();
        while (input.hasMoreElements())
            output.add(input.nextElement());
        return Collections.unmodifiableList(output);
    }

    /**
     * Analogue of string.Join for List<T> with the addition of a user-defined map function
     *
     * @param <T>       the type of the element in the input sequence
     * @param separator inserted between each transformed element
     * @param l         the input sequence
     * @param fn        map function (see <tt>map</tt>) which is used to transform the input sequence
     * @return a string containing the transformed string value of each input element separated by the supplied separator
     */
    public static <T> String join(final String separator, final Iterable<T> l, final Function<? super T, String> fn) {
        if (l == null) throw new IllegalArgumentException("l");
        if (fn == null) throw new IllegalArgumentException("fn");

        return join(separator, map(fn, l));
    }

    /**
     * @param lowerBound
     * @param upperBound
     * @param val
     * @param <T>        the type of the input element
     * @return lowerBound < val < upperBound
     */
    public static <T extends Comparable<T>> boolean between(final T lowerBound, final T upperBound, final T val) {
        if (val == null) throw new IllegalArgumentException("val");

        return val.compareTo(lowerBound) == 1 && val.compareTo(upperBound) == -1;
    }

    /**
     * Find the first element from the input sequence for which the supplied predicate returns true
     * find: (A -> bool) -> A list -> A
     *
     * @param f     predicate
     * @param input sequence
     * @param <A>   the type of the element in the input sequence
     * @return the first element from the input sequence for which the supplied predicate returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     */
    public static <A> A find(final Function<? super A, Boolean> f, final Iterable<A> input) {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        for (final A a : input)
            if (f.apply((a)))
                return a;
        throw new NoSuchElementException();
    }

    /**
     * Curried find.
     * Find the first element from the input sequence for which the supplied predicate returns true
     * find: (A -> bool) -> A list -> A
     *
     * @param f   predicate
     * @param <A> the type of the element in the input sequence
     * @return a curried function that expects an input sequence which it feeds to the predicate f
     * which returns the first element from the input sequence for which the supplied predicate returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Function<Iterable<A>, A> find(final Function<? super A, Boolean> f) {
        return input -> Functional.find(f, input);
    }

    /**
     * As <tt>find</tt> except that here we return the zero-based position in the input sequence of the found element
     * findIndex: (A -> bool) -> A list -> int
     *
     * @param f     predicate
     * @param input sequence
     * @param <A>   the type of the element in the input sequence
     * @return the position in the input sequence of the first element from the input sequence for which the supplied predicate
     * returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     */
    public static <A> int findIndex(final Function<A, Boolean> f, final Iterable<? extends A> input) {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        int pos = 0;
        for (final A a : input)
            if (f.apply(a))
                return pos;
            else pos++;
        throw new IllegalArgumentException();
    }

    /**
     * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
     * findLast: (A -> bool) -> A seq -> A
     *
     * @param f     predicate
     * @param input sequence
     * @param <A>   the type of the element in the input sequence
     * @return the last element in the input sequence for which the supplied predicate returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     */
    public static <A> A findLast(final Function<? super A, Boolean> f, final Iterable<A> input) {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        final Pair<List<A>, Iterable<A>> p = takeNAndYield(input, 1);
        final Pair<A, Boolean> seed = Pair.of(p.getLeft().get(0), f.apply(p.getLeft().get(0)));
        final Pair<A, Boolean> result = fold((state, item) -> f.apply(item) ? Pair.of(item, true) : state, seed, p.getRight());

        if (result.getRight()) return result.getLeft();
        throw new NoSuchElementException();
    }

    /**
     * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
     * findLast: (A -> bool) -> A list -> A
     *
     * @param f     predicate
     * @param input sequence
     * @param <A>   the type of the element in the input sequence
     * @return the last element in the input sequence for which the supplied predicate returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     */
    public static <A> A findLast(final Function<? super A, Boolean> f, final List<A> input) {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        for (final A a : Iterators.reverse(input))
            if (f.apply(a))
                return a;
        throw new NoSuchElementException();
    }

    /**
     * A curried version of findLast.
     * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
     * findLast: (A -> bool) -> A list -> A
     *
     * @param f   predicate
     * @param <A> the type of the element in the input sequence
     * @return the last element in the input sequence for which the supplied predicate returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Function<List<A>, A> findLast(final Function<A, Boolean> f) {
        return input -> Functional.findLast(f, input);
    }

    /**
     * 'pick' is an analogue of <tt>find</tt>. Instead of a predicate, 'pick' is passed a map function which returns an <tt>Option</tt>.
     * Each element of the input sequence is supplied in turn to the map function 'f' and the first non-None Option to be returned from
     * the map function is returned by 'pick' to the calling code.
     * pick: (A -> B option) -> A seq -> B
     *
     * @param f     the map function.
     * @param input the input sequence
     * @param <A>   the type of the element in the input sequence
     * @param <B>   the type of the output element
     * @return the first non-None transformed element of the input sequence
     */
    public static <A, B> B pick(final Function<A, Option<B>> f, final Iterable<? extends A> input) {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        for (final A a : input) {
            final Option<B> intermediate = f.apply(a); // which is, effectively, if(f(a)) return f(a), but without evaluating f twice
            if (!intermediate.isNone())
                return intermediate.Some();
        }
        throw new NoSuchElementException();
    }

    /**
     * 'pick' is an analogue of <tt>find</tt>. Instead of a predicate, 'pick' is passed a map function which returns an <tt>Option</tt>.
     * Each element of the input sequence is supplied in turn to the map function 'f' and the first non-None Option to be returned from
     * the map function is returned by 'pick' to the calling code.
     * <p>
     * This is a curried implementation of 'pick'
     * <p>
     * pick: (A -> B option) -> A seq -> B
     *
     * @param f   the map function.
     * @param <A> the type of the element in the input sequence
     * @param <B> the type of the output element
     * @return the first non-None transformed element of the input sequence
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> Function<Iterable<A>, B> pick(final Function<? super A, Option<B>> f) {
        return input -> Functional.pick(f, input);
    }

    /**
     * In, used for functional composition. This is the simple reversal function. y(x) is equivalent to x.In(y)
     * See <a href="http://en.wikipedia.org/wiki/Function_composition_(computer_science)">Function Composition</a>
     *
     * @param input the object which we wish to pass to the function parameter
     * @param f     the function we wish to evaluate
     * @param <A>   the base type of the input element. That is <tt>AA extends A</tt>
     * @param <B>   the type of the output of the function <tt>f</tt>
     * @param <AA>  the type of the input
     * @return f(input)
     */
    public static <A, B, AA extends A> B in(final AA input, final Function<A, B> f) {
        return f.apply(input);
    }

    /**
     * Then, the functional composition operator. Execute the first function then execute the second, passing the results
     * of the first as the input to the second.
     * See <a href="http://en.wikipedia.org/wiki/Function_composition_(computer_science)">Function Composition</a>
     *
     * @param f   the first function to execute.
     * @param g   the second function to execute. The input to this function will be the result of the first function, f
     * @param <A> the type of the input to <tt>f</tt>
     * @param <B> the type of the input to <tt>g</tt> and a base class of the output of <tt>f</tt>
     * @param <C> the type of the output of <tt>g</tt>
     * @return a function equivalent to g(f(x))
     */
    public static <A, B, C> Function<A, C> then(final Function<A, ? extends B> f, final Function<B, C> g) {
        return x -> g.apply(f.apply(x));
    }

    /**
     * Convolution of functions. That is, apply two transformation functions 'simultaneously' and return a list of pairs,
     * each of which contains one part of the results.
     *
     * @param f     the transformation function that generates the first value in the resultant pair
     * @param g     the transformation function that generates the second value in the resultant pair
     * @param input the input list to be transformed
     * @param <A>   a type that all the elements in the input list extend and that both of the transformation functions accept as input
     * @param <B>   the resulting type of the first transformation
     * @param <C>   the resulting type of the second transformation
     * @return a list of pairs containing the two transformed sequences
     */
    public static <A, B, C> List<Pair<B, C>> zip(final Function<? super A, B> f, final Function<? super A, C> g, final Collection<? extends A> input) {
        final List<Pair<B, C>> output = new ArrayList<>(input.size());

        for (final A element : input)
            output.add(Pair.of(f.apply(element), g.apply(element)));

        return output;
    }

    /**
     * The identity transformation function: that is, the datum supplied as input is returned as output
     *
     * @param <T> the type of the input element
     * @return a function which is the identity transformation
     */
    public static <T> Function<T, T> identity() {
        return t -> t;
    }

    /**
     * <tt>isEven</tt> a function that accepts an integer and returns a boolean that indicates whether the passed integer
     * is or is not an even integer
     */
    public static Function<Integer, Boolean> isEven = i -> i % 2 == 0;
    /**
     * <tt>isOdd</tt> a function that accepts an integer and returns a boolean that indicates whether the passed integer
     * is or is not an odd integer
     */
    public static Function<Integer, Boolean> isOdd = i -> i % 2 != 0;
    /**
     * <tt>count</tt> a function that accepts a counter and another integer and returns 1 + counter
     */
    public static BiFunction<Integer, Integer, Integer> count = (state, b) -> state + 1;
    /**
     * <tt>sum</tt> a function that accepts two integers and returns the sum of them
     */
    public static BiFunction<Integer, Integer, Integer> sum = Integer::sum;

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is greater than
     * 'that' or false otherwise
     */
    public static <T extends Comparable<T>> Function<T, Boolean> greaterThan(final T that) {
        return ths -> ths.compareTo(that) > 0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is greater than
     * or equal to 'that' or false otherwise
     */
    public static <T extends Comparable<T>> Function<T, Boolean> greaterThanOrEqual(final T that) {
        return ths -> ths.compareTo(that) >= 0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is less than
     * 'that' or false otherwise
     */
    public static <T extends Comparable<T>> Function<T, Boolean> lessThan(final T that) {
        return ths -> ths.compareTo(that) < 0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is less than
     * or equal to 'that' or false otherwise
     */
    public static <T extends Comparable<T>> Function<T, Boolean> lessThanOrEqual(final T that) {
        return ths -> ths.compareTo(that) <= 0;
    }

    /**
     * The init function, not dissimilar to list comprehensions, which is used to return a new finite list whose contents are
     * determined by successive calls to the function f.
     * init: (int -> A) -> int -> A list
     *
     * @param <T>     the type of the element in the output sequence
     * @param f       generator function used to produce the individual elements of the output list. This function is called by init
     *                with the unity-based position of the current element in the output list being produced. Therefore, the first time
     *                f is called it will receive a literal '1' as its argument; the second time '2'; etc.
     * @param howMany the number of elements in the output list
     * @return a list of 'howMany' elements of type 'T' which were generated by the function 'f'
     */
    public static <T> List<T> init(final Function<Integer, T> f, final int howMany) {
        if (f == null) throw new IllegalArgumentException("f");
        if (howMany < 1) throw new IllegalArgumentException("howMany");

        final List<T> output = new ArrayList<>(howMany);
        for (int i = 1; i <= howMany; ++i)
            output.add(f.apply(i));
        return Collections.unmodifiableList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * map: (A -> B) -> A list -> B list
     *
     * @param f     a transformation function which takes a object of type A and returns an object, presumably related, of type B
     * @param input a sequence to be fed into f
     * @param <A>   the type of the element in the input sequence
     * @param <B>   the type of the element in the output sequence
     * @return a list of type B containing the transformed values.
     */
    public static <A, B> List<B> map(final Function<A, ? extends B> f, final Iterable<? extends A> input) {
        final List<B> output = input instanceof Collection<?> ? new ArrayList<>(((Collection) input).size()) : new ArrayList<>();
        for (final A a : input)
            output.add(f.apply(a));
        return Collections.unmodifiableList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * map: (A -> B) -> A list -> B list
     *
     * @param f   a transformation function which takes a object of type A and returns an object, presumably related, of type B
     * @param <A> the type of the element in the input sequence
     * @param <B> the type of the element in the output sequence
     * @return a curried function that expects an input sequence which it feeds to the transformation f which returns a list of type B
     * containing the transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> Function<Iterable<A>, List<B>> map(final Function<? super A, ? extends B> f) {
        return input -> Functional.map(f, input);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * mapi: (int -> A -> B) -> A list -> B list
     *
     * @param f     a transformation function which is passed each input object of type A along with its position in the input sequence
     *              (starting from zero) and returns an object, presumably related, of type B
     * @param input a sequence to be fed into f
     * @param <A>   the type of the element in the input sequence
     * @param <B>   the type of the element in the output sequence
     * @return a list of type B containing the transformed values.
     */
    public static <A, B> List<B> mapi(final BiFunction<Integer, A, ? extends B> f, final Iterable<? extends A> input) {
        final List<B> output = input instanceof Collection<?> ? new ArrayList<>(((Collection) input).size()) : new ArrayList<>();
        int pos = 0;
        for (final A a : input)
            output.add(f.apply(pos++, a));
        return Collections.unmodifiableList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * mapi: (int -> A -> B) -> A list -> B list
     *
     * @param f   a transformation function which is passed each input object of type A along with its position in the input sequence
     *            (starting from zero) and returns an object, presumably related, of type B
     * @param <A> the type of the element in the input sequence
     * @param <B> the type of the element in the output sequence
     * @return a curried function that expects an input sequence which it feeds to the transformation f which returns a list of type B
     * containing the transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> Function<Iterable<A>, List<B>> mapi(final BiFunction<Integer, ? super A, ? extends B> f) {
        return input -> Functional.mapi(f, input);
    }

    /// <summary> sortWith: (A -> A -> int) -> A list -> A list</summary>

    /**
     * sortWith: a wrapper for <tt>Collections.sort</tt> which preserves the input sequence.
     *
     * @param f     the <tt>Comparator</tt> to use for the sort
     * @param input the input
     * @param <A>   the type of the <tt>Comparator</tt>
     * @param <AA>  the type of the element in the input sequence
     * @return a sorted list containing all the elements of 'input' sorted using <tt>Collections.sort</tt> and 'f'
     */
    public static <A, AA extends A> List<AA> sortWith(final Comparator<A> f, final Collection<AA> input) {
        final List<AA> output = new ArrayList<>(input);
        Collections.sort(output, f);
        return Collections.unmodifiableList(output);
    }

    /**
     * A simple function which wraps left.compareTo(right) so that this can be used as a sort function.
     *
     * @param left  input element
     * @param right input element
     * @param <A>   the type of the elements to be compared
     * @return left.compareTo(right)
     */
    public static <A extends Comparable<A>> int Sorter(final A left, final A right) {
        return left.compareTo(right);
    }

    /**
     * A Comparator that encapsulates <tt>Sorter</tt> above
     */
    public static Comparator<Integer> dSorter = Functional::Sorter;

    /**
     * A wrapper around <tt>toString()</tt>
     *
     * @param a   the element to be turned into a string using T.toString()
     * @param <T> the type of element 'a'
     * @return a.toString()
     */
    public static <T> String Stringify(final T a) {
        return a.toString();
    }

    /**
     * A transformation function that wraps <tt>Stringify</tt>
     *
     * @param <T> the type of the element which we will render as a String
     * @return a function that calls <tt>Stringify</tt>
     */
    public static <T> Function<T, String> dStringify() {
        return Functional::Stringify;
    }

    /**
     * forAll2: the predicate 'f' is applied to all elements in the input sequences input1 and input2 as pairs. If the predicate returns
     * true for all pairs and there is the same number of elements in both input sequences then forAll2 returns true. If the predicate
     * returns false at any point then the traversal of the input sequences halts and forAll2 returns false.
     * forAll2: (A -> B -> bool) -> A list -> B list -> bool
     *
     * @param <A>    the base type of the element in the first input sequence
     * @param <B>    the base type of the element in the second input sequence
     * @param <AA>   the type of the element in the first input sequence
     * @param <BB>   the type of the element in the second input sequence
     * @param f      predicate to which each successive pair (input1_i, input2_i) is applied
     * @param input1 input sequence
     * @param input2 input sequence
     * @return true if the predicate 'f' evaluates true for all pairs, false otherwise
     * @throws java.lang.IllegalArgumentException if the predicate returns true for all pairs and the sequences contain differing numbers
     *                                            of elements
     */
    public static <A, B, AA extends A, BB extends B> boolean forAll2(final BiFunction<A, B, Boolean> f, final Iterable<AA> input1, final Iterable<BB> input2) {
        final Iterator<AA> enum1 = input1.iterator();
        final Iterator<BB> enum2 = input2.iterator();
        boolean enum1Moved = false, enum2Moved = false;
        do {
            enum1Moved = enum1.hasNext();
            enum2Moved = enum2.hasNext();
            if (enum1Moved && enum2Moved && !f.apply(enum1.next(), enum2.next()))
                return false;
        } while (enum1Moved && enum2Moved);
        if (enum1Moved != enum2Moved)
            throw new IllegalArgumentException();
        return true;
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
     *
     * @param <A>   the type of the element in the input sequence
     * @param pred  a filter function. This is passed each input element in turn and returns either true or false. If true then
     *              the input element is passed through to the output otherwise it is ignored.
     * @param input a sequence of objects
     * @return a list which contains zero or more of the elements of the input sequence. Each element is included only if the filter
     * function returns true for the element.
     */
    public static <A> List<A> filter(final Function<? super A, Boolean> pred, final Iterable<A> input) {
        final List<A> output = input instanceof Collection<?> ? new ArrayList<>(((Collection) input).size()) : new ArrayList<>();
        for (final A element : input)
            if (pred.apply(element))
                output.add(element);

        return Collections.unmodifiableList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
     *
     * @param <T> the type of the element in the input sequence
     * @param f   a filter function. This is passed each input element in turn and returns either true or false. If true then
     *            the input element is passed through to the output otherwise it is ignored.
     * @return a curried function that expects an input sequence which it feeds to the filter predicate which then returns
     * a list which contains zero or more of the elements of the input sequence. Each element is included only if the filter
     * function returns true for the element.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T> Function<Iterable<T>, List<T>> filter(final Function<? super T, Boolean> f) {
        return input -> Functional.filter(f, input);
    }

    /**
     * The converse operation to <tt>forAll</tt>. If the predicate returns true then 'exists' returns true and halts the traveral of the
     * input sequence. Otherwise return false.
     * exists: (A -> bool) -> A list -> bool
     *
     * @param <A>   the type of the element in the input sequence
     * @param f     predicate
     * @param input input sequence
     * @return true if the predicate returns true for any element in the input sequence, false otherwise
     */
    public static <A> boolean exists(final Function<? super A, Boolean> f, final Iterable<A> input) {
        for (final A a : input)
            if (f.apply(a))
                return true;
        return false;
    }

    /**
     * The converse operation to <tt>forAll</tt>. If the predicate returns true then 'exists' returns true and halts the traveral of the
     * input sequence. Otherwise return false.
     * exists: (A -> bool) -> A list -> bool
     * This is the curried implementation.
     *
     * @param <A> the type of the element in the input sequence
     * @param f   predicate
     * @return true if the predicate returns true for any element in the input sequence, false otherwise
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Function<Iterable<A>, Boolean> exists(final Function<? super A, Boolean> f) {
        return input -> Functional.exists(f, input);
    }

    /**
     * not reverses the result of the applied predicate
     * not: (A -> bool) -> (A -> bool)
     *
     * @param <A> the type of the input to the function <tt>f</tt>
     * @param f   the applied predicate
     * @return true if f returns false, false if f returns true
     */
    public static <A> Function<A, Boolean> not(final Function<A, Boolean> f) {
        return a -> !f.apply(a);
    }

    /**
     * The converse operation to <tt>exists</tt>. If the predicate returns true for all elements in the input sequence then 'forAll'
     * returns true otherwise return false.
     * forAll: (A -> bool) -> A list -> bool
     *
     * @param <A>   the type of the element in the input sequence
     * @param f     predicate
     * @param input input sequence
     * @return true if the predicate returns true for all elements in the input sequence, false otherwise
     */
    public static <A> boolean forAll(final Function<A, Boolean> f, final Iterable<? extends A> input) {
        return !exists(not(f), input);
    }

    /**
     * The converse operation to <tt>exists</tt>. If the predicate returns true for all elements in the input sequence then 'forAll'
     * returns true otherwise return false.
     * forAll: (A -> bool) -> A list -> bool
     * This is a curried implementation of 'forAll
     *
     * @param <A> the type of the element in the input sequence
     * @param f   predicate
     * @return true if the predicate returns true for all elements in the input sequence, false otherwise
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Function<Iterable<A>, Boolean> forAll(final Function<? super A, Boolean> f) {
        return input -> Functional.forAll(f, input);
    }

    /**
     * not2 reverses the result of the applied predicate
     * not2: (A -> B -> bool) -> (A -> B -> bool)
     *
     * @param <A> the type of the first input to the function <tt>f</tt>
     * @param <B> the type of the second input to the function <tt>f</tt>
     * @param f   the applied predicate
     * @return true if f returns false, false if f returns true
     */
    public static <A, B> BiFunction<A, B, Boolean> not2(final BiFunction<A, B, Boolean> f) {
        return (a, b) -> !f.apply(a, b);
    }

    /// <summary> </summary>
    /// <returns> (list * list). The first list contains all items for which f(a) is true. The second list contains the remainder.</returns>

    /**
     * partition is a group function. Given a predicate and an input sequence, 'partition' returns a pair of lists, the first list
     * containing those elements from the input sequence for which the predicate returned true, the second list containing those
     * elements from the input sequence for which the predicate returned false.
     * partition: (A -> bool) -> A list -> A list * A list
     *
     * @param <A>   the type of the element in the input sequence
     * @param f     predicate used to split the input sequence into two groups
     * @param input the input sequence
     * @return a pair of lists, the first being the 'true' and the second being the 'false'
     */
    public static <A> Pair<List<A>, List<A>> partition(final Function<? super A, Boolean> f, final Iterable<A> input) {
        final List<A> left;
        final List<A> right;
        if (input instanceof Collection<?>) {
            left = new ArrayList<>(((Collection) input).size());
            right = new ArrayList<>(((Collection) input).size());
        } else {
            left = new ArrayList<>();
            right = new ArrayList<>();
        }
        for (final A a : input)
            if (f.apply(a))
                left.add(a);
            else
                right.add(a);
        return Pair.of(Collections.unmodifiableList(left), Collections.unmodifiableList(right));
    }

    /**
     * partition is a group function. Given a predicate and an input sequence, 'partition' returns a pair of lists, the first list
     * containing those elements from the input sequence for which the predicate returned true, the second list containing those
     * elements from the input sequence for which the predicate returned false.
     * partition: (A -> bool) -> A list -> A list * A list
     * This is a curried implementation of 'forAll
     *
     * @param <A> the type of the element in the input sequence
     * @param f   predicate used to split the input sequence into two groups
     * @return a pair of lists, the first being the 'true' and the second being the 'false'
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Function<Iterable<A>, Pair<List<A>, List<A>>> partition(final Function<? super A, Boolean> f) {
        return input -> Functional.partition(f, input);
    }

    /**
     * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     *
     * @param <A>   the type of the element in the input sequence
     * @param <B>   the type of the element in the output sequence
     * @param f     map function. This transforms the input element into an Option
     * @param input input sequence
     * @return a list of transformed elements, numbering less than or equal to the number of input elements
     */
    public static <A, B> List<B> choose(final Function<? super A, Option<B>> f, final Iterable<A> input) {
        final List<B> results = input instanceof Collection<?> ? new ArrayList<>(((Collection) input).size()) : new ArrayList<>();
        for (final A a : input) {
            final Option<B> intermediate = f.apply(a);
            if (!intermediate.isNone())
                results.add(intermediate.Some());
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * choose: this is a curried implementation of choose.
     * choose is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     *
     * @param <A> the type of the element in the input sequence
     * @param <B> the type of the element in the output sequence
     * @param f   map function. This transforms the input element into an Option
     * @return a list of transformed elements, numbering less than or equal to the number of input elements
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> Function<Iterable<A>, List<B>> choose(final Function<? super A, Option<B>> f) {
        return input -> Functional.choose(f, input);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
     * fold: aggregate the elements of the input sequence given a seed and an aggregation function.
     * fold: (A -> B -> A) -> A -> B list -> A
     *
     * @param <A>          the type of the initialValue / seed
     * @param <B>          the type of the element in the input sequence
     * @param f            aggregation function
     * @param initialValue seed for the algorithm
     * @param input        input sequence
     * @return aggregated value
     */
    public static <A, B> A fold(final BiFunction<? super A, ? super B, ? extends A> f, final A initialValue, final Iterable<B> input) {
        A state = initialValue;
        for (final B b : input)
            state = f.apply(state, b);
        return state;
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
     * fold: aggregate the elements of the input sequence given a seed and an aggregation function.
     * This is the curried implementation
     * fold: (A -> B -> A) -> A -> B list -> A
     *
     * @param <A>          the type of the initialValue / seed
     * @param <B>          the type of the element in the output sequence
     * @param f            aggregation function
     * @param initialValue seed for the algorithm
     * @return aggregated value
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> Function<Iterable<B>, A> fold(final BiFunction<? super A, ? super B, ? extends A> f, final A initialValue) {
        return input -> Functional.fold(f, initialValue, input);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
     * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> List<A> unfold(final Function<? super B, Pair<A, B>> unspool, final Function<? super B, Boolean> finished, final B seed) {
        if (unspool == null) throw new IllegalArgumentException("unspool");
        if (finished == null) throw new IllegalArgumentException("finished");

        B next = seed;
        final List<A> results = new ArrayList<>();
        while (!finished.apply(next)) {
            final Pair<A, B> t = unspool.apply(next);
            results.add(t.getLeft());
            next = t.getRight();
        }
        return results;
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
     * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> List<A> unfold(final Function<? super B, Option<Pair<A, B>>> unspool, final B seed) {
        if (unspool == null) throw new IllegalArgumentException("unspool");

        B next = seed;
        final List<A> results = new ArrayList<>();
        while (true) {
            final Option<Pair<A, B>> t = unspool.apply(next);
            if (t.isNone()) break;
            results.add(t.Some().getLeft());
            next = t.Some().getRight();
        }
        return results;
    }

    /**
     * toDictionary: given each element from the input sequence apply the keyFn and valueFn to generate a (key,value) pair.
     * The resulting dictionary (java.util.Map) contains all these pairs.
     *
     * @param <T>     the type of the element in the input sequence
     * @param <K>     the type of the key elements
     * @param <V>     the type of the value elements
     * @param keyFn   function used to generate the key
     * @param valueFn function used to generate the value
     * @param input   input sequence
     * @return a java.util.Map containing the transformed input sequence
     * @throws IllegalArgumentException if some property of the specified key
     *                                  or value prevents it from being stored in this map
     */
    public static <T, K, V> Map<K, V> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn, final Iterable<T> input) {
        if (keyFn == null) throw new IllegalArgumentException("keyFn");
        if (valueFn == null) throw new IllegalArgumentException("valueFn");

        final Map<K, V> output = new HashMap<>();
        for (final T element : input) output.put(keyFn.apply(element), valueFn.apply(element));
        return Collections.unmodifiableMap(output);
    }

    /**
     * toArray: create an array containing all the objects in the input sequence
     *
     * @param input input sequence
     * @param <T>   the type of the element in the input sequence
     * @return an array containing all the elements of the input sequence
     */
    public static <T> Object[] toArray(final Iterable<T> input)
    //public static <T>T[] toArray(final Iterable<T> input)
    {
        if (input == null) throw new IllegalArgumentException("Functional.toArray(Iterable<T>): input is null");

        if (input instanceof Collection<?>)
            return ((Collection<T>) input).toArray();

        final List<T> output = new ArrayList<>();
        for (final T element : input) output.add(element);

        return output.toArray(); // this needs to be output.toArray(new T[0]) but that doesn't appear to be allowable Java :-(
    }

    public static <T> List<T> toMutableList(final Iterable<T> input) {
        if (input == null) throw new IllegalArgumentException("Functional.toMutableList(Iterable<T>): input is null");

        if (input instanceof Collection<?>) {
            final Collection<T> input_ = (Collection<T>) input;
            final List<T> output = new ArrayList<>(input_.size());
            output.addAll(input_);
            return output;
        }

        final List<T> output = new ArrayList<>();
        for (final T element : input) output.add(element);

        return output;
    }

    public static <K, V> Map<K, V> toMutableDictionary(final Map<K, V> input) {
        if (input == null)
            throw new IllegalArgumentException("Functional.toMutableDictionary(Map<K,V>): input is null");

        final Map<K, V> output = new HashMap<>(input.size());
        output.putAll(input);
        return output;
    }

    public static <T> Set<T> toMutableSet(final Iterable<T> input) {
        if (input == null) throw new IllegalArgumentException("Functional.toMutableSet(Iterable<T>): input is null");

        if (input instanceof Collection<?>) {
            final Collection<T> input_ = (Collection<T>) input;
            final Set<T> output = new HashSet<>(input_.size());
            output.addAll(input_);
            return output;
        }

        final Set<T> output = new HashSet<>();
        for (final T element : input) output.add(element);

        return output;
    }

    /**
     * Create a java.util.List which contains all of the elements in the input sequence
     *
     * @param input input sequence
     * @param <T>   the type of the element in the input sequence
     * @return a list containing the elements of the input sequence
     */
    public static <T> List<T> toList(final Iterable<T> input) {
        if (input == null) throw new IllegalArgumentException("Functional.toList(Iterable<T>): input is null");
        return Collections.unmodifiableList(toMutableList(input));
    }

    /**
     * Create a java.util.Set which contains all of the elements in the input sequence
     *
     * @param input input sequence
     * @param <T>   the type of the element in the input sequence
     * @return a set containing the elements of the input sequence
     */
    public static <T> Set<T> toSet(final Iterable<T> input) {
        //Sets.newSetFromMap();
        if (input == null) throw new IllegalArgumentException("Functional.toSet(Iterable<T>): input is null");
        return Collections.unmodifiableSet(toMutableSet(input));
    }

    /**
     * Return the final element from the input sequence
     *
     * @param input input sequence
     * @param <T>   the type of the element in the input sequence
     * @return the last element from the input sequence
     * @throws java.lang.IllegalArgumentException if the input sequence is null or empty
     */
    public static <T> T last(final Iterable<T> input) {
        if (input == null) throw new IllegalArgumentException("Functional.last(Iterable<T>): input is null");

        T state = null;
        for (final T element : input) state = element;

        if (state == null) throw new IllegalArgumentException("Functional.last(Iterable): input is empty");
        return state;
    }

    /**
     * Return the final element from the input array
     *
     * @param input input array
     * @param <T>   the type of the element in the input sequence
     * @return the last element from the input array
     */
    public static <T> T last(final T[] input) {
        if (input == null || input.length == 0)
            throw new IllegalArgumentException("Functional.last(T[]): input is null or empty");

        return input[input.length - 1];
    }

    /**
     * Concatenate two sequences and return a new list containing the concatenation.
     *
     * @param list1 first input sequence
     * @param list2 second input sequence
     * @param <T>   the type of the element in the input sequences
     * @return a list containing the elements of the first sequence followed by the elements of the second sequence
     */
    public static <T> List<T> concat(final Iterable<? extends T> list1, final Iterable<? extends T> list2) {
        if (list1 == null) throw new IllegalArgumentException("Functional.concat(List<T>,List<T>): list1 is null");
        if (list2 == null) throw new IllegalArgumentException("Functional.concat(List<T>,List<T>): list2 is null");

        final List<T> newList = new ArrayList<>(toList(list1));
        final boolean didItChange = newList.addAll(toList(list2));
        return Collections.unmodifiableList(newList);
    }

    /**
     * take: given a list return another list containing the first 'howMany' elements
     *
     * @param howMany a positive number of elements to be returned from the input sequence
     * @param list    the input sequence
     * @param <T>     the type of the element in the input sequence
     * @return a list containing the first 'howMany' elements of 'list'
     * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
     */
    public static <T> List<T> take(final int howMany, final Iterable<? extends T> list) {
        if (howMany < 0) throw new IllegalArgumentException("Functional.take(int,Iterable<T>): howMany is negative");
        if (list == null) throw new IllegalArgumentException("Functional.take(int,Iterable<T>): list is null");

        if (howMany == 0) return new ArrayList<>(0);

        final List<T> output = new ArrayList<>(howMany);
        final Iterator<? extends T> iterator = list.iterator();
        for (int i = 0; i < howMany; ++i) {
            if (iterator.hasNext())
                output.add(iterator.next());
            else
                throw new java.util.NoSuchElementException("Cannot take " + howMany + " elements from input list with fewer elements");
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * take: given a list return another list containing the first 'howMany' elements
     * This is the curried implementation
     *
     * @param <T>     the type of the element in the input sequence
     * @param howMany a positive number of elements to be returned from the input sequence
     * @return a list containing the first 'howMany' elements of 'list'
     * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T> Function<Iterable<? extends T>, List<T>> take(final int howMany) {
        return input -> Functional.take(howMany, input);
    }

    /**
     * takeWhile: given a list return another list containing the first elements up and not including the first element for which
     * the predicate returns false
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate the predicate to use
     * @param list      the input sequence
     * @return a list
     */
    public static <T> List<T> takeWhile(final Function<? super T, Boolean> predicate, final List<T> list) {
        if (predicate == null)
            throw new IllegalArgumentException("Functional.take(Func,Iterable<T>): predicate is null");
        if (list == null) throw new IllegalArgumentException("Functional.take(Func,Iterable<T>): list is null");

        if (list.size() == 0) return new ArrayList<>();

        for (int i = 0; i < list.size(); ++i) {
            final T element = list.get(i);
            if (!predicate.apply(element)) {
                if (i == 0) return new ArrayList<>();
                return Collections.unmodifiableList(list.subList(0, i));
            }
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * takeWhile: given a list return another list containing the first elements up and not including the first element for which
     * the predicate returns false
     * This is the curried implementation
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate the predicate to use
     * @return a list
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T> Function<List<T>, List<T>> takeWhile(final Function<? super T, Boolean> predicate) {
        return input -> Functional.takeWhile(predicate, input);
    }

    /**
     * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
     * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
     *
     * @param howMany a non-negative number of elements to be discarded from the input sequence
     * @param list    the input sequence
     * @param <T>     the type of the element in the input sequence
     * @return a list containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
     * are skipped than are present in the 'list'
     */
    public static <T> List<T> skip(final int howMany, final List<? extends T> list) {
        if (howMany < 0) throw new IllegalArgumentException("Functional.skip(int,List<T>): howMany is negative");
        if (list == null) throw new IllegalArgumentException("Functional.skip(int,List<T>): list is null");

        if (howMany == 0) return Collections.unmodifiableList(list);
        final int outputListSize = list.size() - howMany;
        if (outputListSize <= 0) return new ArrayList<>();

        return Collections.unmodifiableList(list.subList(howMany, list.size()));
    }

    /**
     * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
     * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
     * This is the curried implementation
     *
     * @param <T>     the type of the element in the input sequence
     * @param howMany a non-negative number of elements to be discarded from the input sequence
     * @return a list containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
     * are skipped than are present in the 'list'
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T> Function<List<? extends T>, List<T>> skip(final int howMany) {
        return input -> Functional.skip(howMany, input);
    }

    /**
     * skipWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate ignore elements in the input while the predicate is true.
     * @param list      the input sequence
     * @return a list containing the remaining elements after and including the first element for which the predicate returns false
     */
    public static <T> List<T> skipWhile(final Function<? super T, Boolean> predicate, final List<T> list) {
        if (predicate == null)
            throw new IllegalArgumentException("Functional.skipWhile(Func,List<T>): predicate is null");
        if (list == null) throw new IllegalArgumentException("Functional.skipWhile(Func,List<T>): list is null");

        for (int counter = 0; counter < list.size(); ++counter)
            if (!predicate.apply(list.get(counter)))
                return Collections.unmodifiableList(list.subList(counter, list.size()));

        return Collections.unmodifiableList(new ArrayList<>(0));
    }

    /**
     * skipWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate ignore elements in the input while the predicate is true.
     * @return a list containing the remaining elements after and including the first element for which the predicate returns false
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T> Function<List<T>, List<T>> skipWhile(final Function<? super T, Boolean> predicate) {
        return input -> Functional.skipWhile(predicate, input);
    }

    /**
     * constant: a function that returns a map function f(n) that returns the supplied 'constant'. Typically this would be
     * used in <tt>init</tt>
     *
     * @param <T>      the type of the constant
     * @param constant the desired constant value to be returned
     * @return a function that returns a function that returns the supplied constant
     */
    public static <T> Function<Integer, T> constant(final T constant) {
        return integer -> constant;
    }

    /**
     * range: a function that returns a map function f(n) that returns an integer from the open-ended range [startFrom+n, infinity).
     * Typically this would be used in <tt>init</tt>
     *
     * @param startFrom the lower bound of the range
     * @return a function that returns a function that returns an integer from the range [startFrom+n, infinity)
     */
    public static Function<Integer, Integer> range(final Integer startFrom) {
        return new Function<Integer, Integer>() {
            private final Integer start = startFrom;

            public Integer apply(final Integer input) {
                return (start - 1) + input; // because init starts counting from 1
            }
        };
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param l1  input sequence
     * @param l2  input sequence
     * @param <A> the type of the element in the first input sequence
     * @param <B> the type of the element in the second input sequence
     * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
     * in order. If the sequences do not have the same number of elements then an exception is thrown.
     * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
     */
    public static <A, B> List<Pair<A, B>> zip(final Iterable<? extends A> l1, final Iterable<? extends B> l2) {
        if (l1 == null) throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l1 is null");
        if (l2 == null) throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l2 is null");

        final List<Pair<A, B>> output;
        if (l1 instanceof Collection<?> && l2 instanceof Collection<?>) {
            if (((Collection) l1).size() != ((Collection) l2).size())
                throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l1 and l2 have differing numbers of elements");

            output = new ArrayList<>(((Collection) l1).size());
        } else output = new ArrayList<>();
        final Iterator<? extends A> l1_it = l1.iterator();
        final Iterator<? extends B> l2_it = l2.iterator();

        while (l1_it.hasNext() && l2_it.hasNext()) output.add(Pair.of(l1_it.next(), l2_it.next()));
        if (l1_it.hasNext() || l2_it.hasNext())
            throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l1 and l2 have differing numbers of elements");

        return Collections.unmodifiableList(output);
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param l1  input sequence
     * @param l2  input sequence
     * @param l3  input sequence
     * @param <A> the type of the element in the first input sequence
     * @param <B> the type of the element in the second input sequence
     * @param <C> the type of the element in the third input sequence
     * @return list of triplets; the first element from each of the input sequences is the first triplet in the output sequence and so on,
     * in order. If the sequences do not have the same number of elements then an exception is thrown.
     * @throws java.lang.IllegalArgumentException if any input sequence is null or if the sequences have differing lengths.
     */
    public static <A, B, C> List<Triple<A, B, C>> zip3(final Iterable<? extends A> l1, final Iterable<? extends B> l2, final Iterable<? extends C> l3) {
        if (l1 == null)
            throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l1 is null");
        if (l2 == null)
            throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l2 is null");
        if (l3 == null)
            throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l3 is null");

        final List<Triple<A, B, C>> output;
        if (l1 instanceof Collection<?> && l2 instanceof Collection<?> && l3 instanceof Collection<?>) {
            if (((Collection) l1).size() != ((Collection) l2).size())
                throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l1, l2 and l3 have differing numbers of elements");

            output = new ArrayList<>(((Collection) l1).size());
        } else output = new ArrayList<>();
        final Iterator<? extends A> l1_it = l1.iterator();
        final Iterator<? extends B> l2_it = l2.iterator();
        final Iterator<? extends C> l3_it = l3.iterator();

        while (l1_it.hasNext() && l2_it.hasNext() && l3_it.hasNext())
            output.add(Triple.of(l1_it.next(), l2_it.next(), l3_it.next()));
        if (l1_it.hasNext() || l2_it.hasNext() || l3_it.hasNext())
            throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l1, l2 and l3 have differing numbers of elements");

        return Collections.unmodifiableList(output);
    }

    /**
     * The converse of the Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param input sequence of pairs
     * @param <A>   the type of the first element in the pair
     * @param <B>   the type of the second element in the pair
     * @return pair of lists; the first element from each of the two output sequences is the first pair in the input sequence and so on,
     * in order.
     * @throws java.lang.IllegalArgumentException if the input sequence is null
     */
    public static <A, B> Pair<List<A>, List<B>> unzip(final Iterable<Pair<A, B>> input) {
        if (input == null) throw new IllegalArgumentException("Functional.unzip(Iterable<Pair<A,B>>): input is null");

        final List<A> l1;
        final List<B> l2;
        if (input instanceof Collection<?>) {
            final int size = ((Collection) input).size();
            l1 = new ArrayList<>(size);
            l2 = new ArrayList<>(size);
        } else {
            l1 = new ArrayList<>();
            l2 = new ArrayList<>();
        }
        for (final Pair<A, B> pair : input) {
            l1.add(pair.getLeft());
            l2.add(pair.getRight());
        }

        return Pair.of(Collections.unmodifiableList(l1), Collections.unmodifiableList(l2));
    }

    /**
     * The converse of the Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param input sequence of triplets
     * @param <A>   the type of the first element in the triplet
     * @param <B>   the type of the second element in the triplet
     * @param <C>   the type of the third element in the triplet
     * @return triplet of lists; the first element from each of the output sequences is the first triplet in the input sequence and so on,
     * in order.
     * @throws java.lang.IllegalArgumentException if the input sequence is null
     */
    public static <A, B, C> Triple<List<A>, List<B>, List<C>> unzip3(final Iterable<Triple<A, B, C>> input) {
        if (input == null) throw new IllegalArgumentException("Functional.unzip(Iterable<Pair<A,B>>): input is null");

        final List<A> l1;
        final List<B> l2;
        final List<C> l3;
        if (input instanceof Collection<?>) {
            final int size = ((Collection) input).size();
            l1 = new ArrayList<>(size);
            l2 = new ArrayList<>(size);
            l3 = new ArrayList<>(size);
        } else {
            l1 = new ArrayList<>();
            l2 = new ArrayList<>();
            l3 = new ArrayList<>();
        }

        for (final Triple<A, B, C> triplet : input) {
            l1.add(triplet.getLeft());
            l2.add(triplet.getMiddle());
            l3.add(triplet.getRight());
        }

        return Triple.of(Collections.unmodifiableList(l1), Collections.unmodifiableList(l2), Collections.unmodifiableList(l3));
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
     * These sequences are concatenated into one final output sequence at the end of the transformation.
     * map: (T -> U list) -> T list -> U list
     *
     * @param <T>   the type of the element in the input sequence
     * @param <U>   the type of the element in the output sequence
     * @param f     a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
     * @param input a sequence to be fed into f
     * @return a list of type U containing the concatenated sequences of transformed values.
     */
    public static <T, U> List<U> collect(final Function<? super T, ? extends Iterable<U>> f, final Iterable<T> input) {
        List<U> output = input instanceof Collection<?> ? new ArrayList<>(((Collection) input).size()) : new ArrayList<>();
        for (final T element : input)
            output = Functional.concat(output, Functional.toList(f.apply(element)));
        return Collections.unmodifiableList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
     * These sequences are concatenated into one final output sequence at the end of the transformation.
     * map: (T -> U list) -> T list -> U list
     * This is a curried implementation of 'collect'
     *
     * @param <T> the type of the element in the input sequence
     * @param <U> the type of the element in the output sequence
     * @param f   a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
     * @return a list of type U containing the concatenated sequences of transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T, U> Function<Iterable<T>, List<U>> collect(final Function<? super T, ? extends Iterable<U>> f) {
        return input -> Functional.collect(f, input);
    }

    /**
     * takeNAndYield: given an input sequence and an integer, return two sequences. The first output sequence returned is a list
     * containing the first 'howMany' elements of the 'input' sequence and the second output sequence contains all the remaining
     * elements of the 'input' sequence. The 'input' sequence is traversed only as far as is required to produce the first list
     * and so the remainder of the 'input' sequence remains unevaluated. If 'howMany' is greater than the number of elements in
     * 'input' then the output list will contain all the elements of the input and the output sequence will be empty.
     * This is like <tt>take</tt> but leaves the user with the ability to continue the traversal of the input sequence from the point
     * at which the 'take' stopped.
     *
     * @param input   the input sequence
     * @param howMany the number of elements to be included in the first output list
     * @param <A>     the type of the element in the input sequence
     * @return a pair: (list, seq) - the list contains 'howMany' elements of 'input' and the sequence contains the remainder
     */
    public static <A> Pair<List<A>, Iterable<A>> takeNAndYield(final Iterable<A> input, final int howMany) {
        if (input == null) throw new IllegalArgumentException("Functional.takeNAndYield: input is null");
        if (howMany < 0) throw new IllegalArgumentException("Functional.takeNAndYield: howMany is negative");

        int counter = 0;
        final List<A> output = new ArrayList<>(howMany);
        if (howMany > 0) {
            final Iterator<A> position = input.iterator();
            if (howMany > 0 && position.hasNext()) {
                while (counter < howMany) {
                    output.add(position.next());
                    counter++;
                    if (counter < howMany && !position.hasNext()) break;
                }
                return Pair.of(output, () -> position);
            }
        }
        return Pair.of(output, input);
    }

    /**
     * append: given the input sequence and an item, return a new, lazily-evaluated sequence containing the input with the item
     * as the final element.
     *
     * @param t     the item to be appended
     * @param input the input sequence
     * @param <T>   the type of the element in the input sequence
     * @return a sequence containing all the elements of 'input' followed by 't'
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> append(final T t, final Iterable<T> input) {
        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private int counter = 0;
                        private Iterator<? extends T> iterator = input.iterator();

                        public boolean hasNext() {
                            return counter == 0 || iterator.hasNext();
                        }


                        public T next() {
                            return counter++ == 0 ? t : iterator.next();
                        }


                        public void remove() {
                            throw new UnsupportedOperationException("Functional.append(T,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * groupBy: similar to {@link #partition(Function, Iterable)} in that the input is grouped according to a function. This is more general than
     * <tt>partition</tt> though as the output can be an arbitrary number of groups, up to and including one group per item in the
     * input data set. The 'keyFn' is the grouping operator and it is used to determine the key at which any given element from
     * the input data set should be added to the output dictionary / map.
     *
     * @param <T>   the type of the element in the input sequence
     * @param <U>   the type of the element in the key
     * @param keyFn the grouping function. Given an element return the key to be used when storing this element in the dictionary
     * @param input the input sequence
     * @return a java.util.Map containing a list of elements for each key
     */
    public static <T, U> Map<U, List<T>> groupBy(final Function<? super T, ? extends U> keyFn, final Iterable<T> input) {
        if (keyFn == null) throw new IllegalArgumentException("Functional.groupBy(Func,Iterable): keyFn is null");
        if (input == null) throw new IllegalArgumentException("Functional.groupBy(Func,Iterable): input is null");

        final Map<U, List<T>> intermediateResults = new HashMap<>();
        for (final T element : input) {
            final U key = keyFn.apply(element);
            if (intermediateResults.containsKey(key))
                intermediateResults.get(key).add(element);
            else {
                final List<T> list = new ArrayList<>();
                list.add(element);
                intermediateResults.put(key, list);
            }
        }
        final Map<U, List<T>> output = new HashMap<>(intermediateResults.size());
        for (final Map.Entry<U, List<T>> entry : intermediateResults.entrySet())
            output.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        return Collections.unmodifiableMap(output);
    }

    /**
     * The Range class holds an inclusive lower bound and an exclusive upper bound. That is lower <= pos < upper
     */
    public static class Range<T> {
        private final T lowerBound;
        private final T upperExBound;

        /**
         * Create a new Range object
         *
         * @param lower   the inclusive lower bound of the Range
         * @param upperEx the exclusive upper bound of the Range
         */
        public Range(final T lower, final T upperEx) {
            this.lowerBound = lower;
            this.upperExBound = upperEx;
        }

        /**
         * Return the inclusive lower bound
         *
         * @return the inclusive lower bound
         */
        public T from() {
            return lowerBound;
        }

        /**
         * return the exclusive upper bound
         *
         * @return the exclusive upper bound
         */
        public T to() {
            return upperExBound;
        }

        public boolean equals(final Object other) {
            if (!(other instanceof Range<?>)) return false;
            final Range<?> otherRange = (Range<?>) other;
            return from().equals(otherRange.from()) && to().equals(otherRange.to());
        }

        public int hashCode() {
            return 13 * from().hashCode() + 7 * to().hashCode();
        }
    }

    /**
     * This list generator returns a list of Range objects which split the interval [1-'howManyElements') into 'howManyPartitions' Range objects.
     * If the interval cannot be divided exactly then the remainder is allocated evenly across the first
     * 'howManyElements' % 'howManyPartitions' Range objects.
     *
     * @param howManyElements   defines the exclusive upper bound of the interval to be split
     * @param howManyPartitions defines the number of Range objects to generate to cover the interval
     * @return a list of Range objects
     */
    public static List<Range<Integer>> partition(final int howManyElements, final int howManyPartitions) {
        return partition(Functional.range(0), howManyElements, howManyPartitions);
    }

    /**
     * This sequence generator returns a sequence of Range objects which split the interval [1-'howManyElements') into 'howManyPartitions'
     * Range objects. If the interval cannot be divided exactly then the remainder is allocated evenly across the first
     * 'howManyElements' % 'howManyPartitions' Range objects.
     *
     * @param generator         a function which generates members of the input sequence
     * @param howManyElements   defines the exclusive upper bound of the interval to be split
     * @param howManyPartitions defines the number of Range objects to generate to cover the interval
     * @return a list of Range objects
     */
    public static <T> List<Range<T>> partition(final Function<Integer, T> generator, final int howManyElements, final int howManyPartitions) {
        if (howManyElements <= 0)
            throw new IllegalArgumentException("Functional.partition() howManyElements cannot be non-positive");
        if (howManyPartitions <= 0)
            throw new IllegalArgumentException("Functional.partition() howManyPartitions cannot be non-positive");

        final int size = howManyElements / howManyPartitions;
        final int remainder = howManyElements % howManyPartitions;

        assert size * howManyPartitions + remainder == howManyElements;

        final Integer seed = 0;
        final Function<Integer, Pair<T, Integer>> boundsCalculator = integer -> Pair.of(
                generator.apply(1 + (integer * size + (integer <= remainder ? integer : remainder))),
                integer + 1);
        final Function<Integer, Boolean> finished = integer -> integer > howManyPartitions;

        final Iterable<T> output = Functional.seq.unfold(boundsCalculator, finished, seed);

        final Iterator<T> iterator = output.iterator();
        if (iterator == null || !iterator.hasNext())
            throw new IllegalStateException("Somehow we have no entries in our sequence of bounds");
        T last = iterator.next();
        final List<Range<T>> retval = new ArrayList<>(howManyPartitions);
        for (int i = 0; i < howManyPartitions; ++i) {
            if (!iterator.hasNext())
                throw new IllegalStateException(String.format("Somehow we have fewer entries (%d) in our sequence of bounds than expected (%d)", i, howManyPartitions));
            final T next = iterator.next();
            retval.add(new Range(last, next));
            last = next;
        }
        return retval;

//        return Functional.seq.init(new Function<Integer, Range<T>>() {
//
//            public Range<T> apply(final Integer integer) {
//// inefficient - the upper bound is computed twice (once at the end of an iteration and once at the beginning of the next iteration)
//                return new Range<T>( // 1 + the value because the init function expects the control range to start from one.
//                        generator.apply(1 + ((integer - 1) * size + (integer <= remainder + 1 ? integer - 1 : remainder))),
//                        generator.apply(1 + (integer * size + (integer <= remainder ? integer : remainder))));
//            }
//        }, howManyPartitions);
    }

    /**
     * Lazily-evaluated implementations of various of the algorithms
     *
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     * Note that these functions do not generally expose a restartable sequence. If you want to restart the iteration
     * then you should make the convert the sequence (the Iterable) to a concrete collection before accessing the
     * iterator.
     */
    public static class seq {
        private seq() {
        }

        /**
         * See <A href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</A>
         * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
         * map: (T -> U) -> T seq -> U seq
         *
         * @param <T>   the type of the element in the input sequence
         * @param <U>   the type of the element in the output sequence
         * @param f     a transformation function which takes a object of type A and returns an object, presumably related, of type B
         * @param input a sequence to be fed into f
         * @return a lazily-evaluated sequence of type B containing the transformed values.
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Iterable<U> map(final Function<? super T, ? extends U> f, final Iterable<T> input) {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            return new Iterable<U>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public final Iterator<U> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<U>() {
                            private final Iterator<T> _input = input.iterator();
                            private final Function<? super T, ? extends U> _f = f;

                            public final boolean hasNext() {
                                return _input.hasNext();
                            }


                            public final U next() {
                                return _f.apply(_input.next());
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.map(Function<T,U>,Iterable<T>): Removing elements is strictly prohibited");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
         * map: (T -> U) -> T seq -> U seq
         *
         * @param <T> the type of the element in the input sequence
         * @param <U> the type of the element in the output sequence
         * @param f   a transformation function which takes a object of type A and returns an object, presumably related, of type B
         * @return a curried function that expects an input sequence which it feeds to the transformation f which returns a lazily-evaluated
         * sequence of type U containing the transformed values.
         * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Function<Iterable<T>, Iterable<U>> map(final Function<? super T, ? extends U> f) {
            return input -> seq.map(f, input);
        }

        /**
         * See <A href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</A>
         * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
         * mapi: (Integer -> T -> U) -> T seq -> U seq
         *
         * @param <T>   the type of the element in the input sequence
         * @param <U>   the type of the element in the output sequence
         * @param f     a transformation function which takes a object of type A and returns an object, presumably related, of type B
         * @param input a sequence to be fed into f
         * @return a lazily-evaluated sequence of type B containing the transformed values.
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Iterable<U> mapi(final BiFunction<Integer, ? super T, ? extends U> f, final Iterable<T> input) {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            return new Iterable<U>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public final Iterator<U> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<U>() {
                            private final Iterator<T> _input = input.iterator();
                            private final BiFunction<Integer, ? super T, ? extends U> _f = f;
                            private int counter = 0;

                            public final boolean hasNext() {
                                return _input.hasNext();
                            }


                            public final U next() {
                                return _f.apply(counter++, _input.next());
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.map(Function<T,U>,Iterable<T>): Removing elements is strictly prohibited");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
         * mapi: (int -> T -> U) -> T seq -> U seq
         *
         * @param <T> the type of the element in the input sequence
         * @param <U> the type of the element in the output sequence
         * @param f   a transformation function which takes a object of type A and returns an object, presumably related, of type B
         * @return a curried function that expects an input sequence which it feeds to the transformation f which returns a lazily-evaluated
         * sequence of type U containing the transformed values.
         * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Function<Iterable<T>, Iterable<U>> mapi(final BiFunction<Integer, ? super T, ? extends U> f) {
            return input -> seq.mapi(f, input);
        }

        /**
         * Concatenate two sequences and return a new sequence containing the concatenation.
         *
         * @param list1 first input sequence
         * @param list2 second input sequence
         * @param <T>   the type of the element in the input sequence
         * @return a lazily-evaluated sequence containing the elements of the first sequence followed by the elements of the second sequence
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> concat(final Iterable<? extends T> list1, final Iterable<? extends T> list2) {
            if (list1 == null)
                throw new IllegalArgumentException("Functional.seq.concat(Iterable<T>,Iterable<T>): list1 is null");
            if (list2 == null)
                throw new IllegalArgumentException("Functional.seq.concat(Iterable<T>,Iterable<T>): list2 is null");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<? extends T> _s1 = list1.iterator();
                            private final Iterator<? extends T> _s2 = list2.iterator();

                            public boolean hasNext() {
                                return _s1.hasNext() || _s2.hasNext();
                            }


                            public T next() {
                                return _s1.hasNext() ? _s1.next() : _s2.next();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.concat(Iterable<T>,Iterable<T>): remove is not supported");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
         *
         * @param <T>   the type of the element in the input sequence
         * @param f     a filter function. This is passed each input element in turn and returns either true or false. If true then
         *              the input element is passed through to the output otherwise it is ignored.
         * @param input a sequence of objects
         * @return a lazily-evaluated sequence which contains zero or more of the elements of the input sequence. Each element is included only if
         * the filter function returns true for the element.
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> filter(final Function<? super T, Boolean> f, final Iterable<T> input) //throws NoSuchElementException, IllegalArgumentException, UnsupportedOperationException
        {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public final Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<T> _input = input.iterator();
                            private final Function<? super T, Boolean> _f = f;
                            private T _next = null;

                            public final boolean hasNext() {
                                while (_next == null && // ie we haven't already read the next element
                                        _input.hasNext()) {
                                    final T next = _input.next();
                                    if (_f.apply(next)) {
                                        _next = next;
                                        return true;
                                    }
                                }
                                return _next != null;
                            }


                            public final T next() {
                                if (hasNext()) {
                                    final T next = _next;
                                    _next = null;
                                    return next;
                                }
                                throw new java.util.NoSuchElementException();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.filter(Function<T,Boolean>,Iterable<T>): Removing elements is strictly prohibited");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
         *
         * @param <T> the type of the element in the input sequence
         * @param f   a filter function. This is passed each input element in turn and returns either true or false. If true then
         *            the input element is passed through to the output otherwise it is ignored.
         * @return a lazily-evaluated sequence which contains zero or more of the elements of the input sequence. Each element is included only if
         * the filter function returns true for the element.
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Function<Iterable<T>, Iterable<T>> filter(final Function<? super T, Boolean> f) {
            return input -> seq.filter(f, input);
        }

        /**
         * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
         * be between zero and the number of elements in the input sequence.
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * choose: (A -> B option) -> A list -> B list
         *
         * @param <T>   the type of the element in the input sequence
         * @param <U>   the type of the element in the output sequence
         * @param f     map function. This transforms the input element into an Option
         * @param input input sequence
         * @return a lazily-evaluated sequence of transformed elements, numbering less than or equal to the number of input elements
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Iterable<U> choose(final Function<? super T, Option<U>> f, final Iterable<T> input) {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            return new Iterable<U>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public final Iterator<U> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<U>() {
                            private final Iterator<T> _input = input.iterator();
                            private final Function<? super T, Option<U>> _f = f;
                            private Option<U> _next = Option.None();

                            public final boolean hasNext() {
                                while (_next.isNone() && // ie we haven't already read the next element
                                        _input.hasNext()) {
                                    final Option<U> next = _f.apply(_input.next());
                                    if (next.isSome()) {
                                        _next = next;
                                        return true;
                                    }
                                }
                                return _next.isSome();
                            }


                            public final U next() {
                                if (hasNext()) {
                                    final Option<U> next = _next;
                                    _next = Option.None();
                                    // this exception is only possible (on the grounds that we have already called hasNext())
                                    // if next() is called on two separate threads for the same iterator, which shouldn't be possible.
                                    try {
                                        return next.Some();
                                    } catch (final OptionNoValueAccessException e) {
                                        throw new java.util.NoSuchElementException();
                                    }
                                }
                                throw new java.util.NoSuchElementException();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.choose(Function<T,U>,Iterable<T>): Removing elements is strictly prohibited");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
         * be between zero and the number of elements in the input sequence.
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * choose: (A -> B option) -> A list -> B list
         *
         * @param <T> the type of the element in the input sequence
         * @param <U> the type of the element in the output sequence
         * @param f   map function. This transforms the input element into an Option
         * @return a lazily-evaluated sequence of transformed elements, numbering less than or equal to the number of input elements
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Function<Iterable<T>, Iterable<U>> choose(final Function<? super T, Option<U>> f) {
            return input -> seq.choose(f, input);
        }

        /**
         * The init function, not dissimilar to list comprehensions, which is used to return a new finite sequence whose contents are
         * determined by successive calls to the function f.
         * init: (int -> T) -> int -> T seq
         *
         * @param <T>     the type of the element in the output sequence
         * @param f       generator function used to produce the individual elements of the output sequence.
         *                This function is called by init with the unity-based position of the current element in the output sequence being
         *                produced. Therefore, the first time f is called it will receive a literal '1' as its argument; the second time
         *                '2'; etc.
         * @param howMany the number of elements in the output sequence
         * @return a lazily-evaluated sequence which will contain no more than 'howMany' elements of type 'T' which were generated by the function 'f'
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> init(final Function<Integer, ? extends T> f, final int howMany) {
            if (f == null) throw new IllegalArgumentException("f");
            if (howMany < 1) throw new IllegalArgumentException("howMany");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private int _counter = 1;
                            private final Function<Integer, ? extends T> _f = f;

                            public boolean hasNext() {
                                return _counter <= howMany;
                            }


                            public T next() {
                                if (!hasNext())
                                    throw new NoSuchElementException();
                                return _f.apply(_counter++);
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.init(Function<T,U>,Iterable<T>): Removing elements is strictly prohibited");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * The init function, not dissimilar to list comprehensions, which is used to return a new infinite sequence whose contents are
         * determined by successive calls to the function f.
         * init: (int -> T) -> T seq
         *
         * @param <T> the type of the element in the output sequence
         * @param f   generator function used to produce the individual elements of the output sequence.
         *            This function is called by init with the unity-based position of the current element in the output sequence being
         *            produced. Therefore, the first time f is called it will receive a literal '1' as its argument; the second time
         *            '2'; etc.
         * @return a potentially infinite sequence containing elements of type 'T' which were generated by the function 'f'
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> init(final Function<Integer, ? extends T> f) {
            if (f == null) throw new IllegalArgumentException("f");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private int _counter = 1;
                            private final Function<Integer, ? extends T> _f = f;

                            public boolean hasNext() {
                                return true;
                            }


                            public T next() {
                                return _f.apply(_counter++);
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.init(Function<T,U>,Iterable<T>): Removing elements is strictly prohibited");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
         * These sequences are concatenated into one final output sequence at the end of the transformation.
         * map: (T -> U list) -> T list -> U list
         *
         * @param <T>   the type of the element in the input sequence
         * @param <U>   the type of the element in the output sequence
         * @param f     a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
         * @param input a sequence to be fed into f
         * @return a lazily-evaluated sequence of type U containing the concatenated sequences of transformed values.
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Iterable<U> collect(final Function<? super T, ? extends Iterable<U>> f, final Iterable<T> input) {
            if (f == null) throw new IllegalArgumentException("Functional.seq.collect: f is null");
            if (input == null) throw new IllegalArgumentException("Functional.seq.collect: input is null");

            return new Iterable<U>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<U> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<U>() {
                            private final Iterator<T> it = input.iterator();
                            private List<U> cache = new ArrayList<>();
                            private Iterator<U> cacheIterator = cache.iterator();

                            public boolean hasNext() {
                                return it.hasNext() || cacheIterator.hasNext();
                            }


                            public U next() {
                                if (cacheIterator.hasNext()) return cacheIterator.next();
                                cache = toList(f.apply(it.next()));
                                cacheIterator = cache.iterator();
                                return cacheIterator.next();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.collect: remove is not supported");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
         * These sequences are concatenated into one final output sequence at the end of the transformation.
         * map: (T -> U list) -> T list -> U list
         *
         * @param <T> the type of the element in the input sequence
         * @param <U> the type of the element in the output sequence
         * @param f   a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
         * @return a function returning a lazily-evaluated sequence of type U containing the concatenated sequences of transformed values.
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Function<Iterable<T>, Iterable<U>> collect(final Function<? super T, ? extends Iterable<U>> f) {
            return input -> seq.collect(f, input);
        }

        /**
         * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
         * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
         *
         * @param howMany a non-negative number of elements to be discarded from the input sequence
         * @param input   the input sequence
         * @param <T>     the type of the element in the input sequence
         * @return a lazily-evaluated sequence containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
         * are skipped than are present in the 'list'
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> skip(final int howMany, final Iterable<T> input) {
            if (howMany < 0)
                throw new IllegalArgumentException("Functional.skip(int,Iterable<T>): howMany is negative");
            if (input == null) throw new IllegalArgumentException("Functional.skip(int,Iterable<T>): input is null");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<T> it = input.iterator();
                            private boolean haveWeSkipped = false;


                            public boolean hasNext() {
                                if (haveWeSkipped && it.hasNext()) return true;
                                if (haveWeSkipped) return false;
                                for (int i = 0; i < howMany; ++i)
                                    if (it.hasNext()) it.next();
                                    else return false;
                                haveWeSkipped = true;
                                return it.hasNext();
                            }


                            public T next() {
                                if (!hasNext()) throw new NoSuchElementException();
                                return it.next();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.skip: remove is not supported");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
         * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
         *
         * @param <T>     the type of the element in the input sequence
         * @param howMany a non-negative number of elements to be discarded from the input sequence
         * @return a lazily-evaluated sequence containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
         * are skipped than are present in the 'list'
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Function<Iterable<T>, Iterable<T>> skip(final int howMany) {
            return input -> seq.skip(howMany, input);
        }

        /**
         * skipWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
         * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
         *
         * @param <T>       the type of the element in the input sequence
         * @param predicate ignore elements in the input while the predicate is true.
         * @param input     the input sequence
         * @return a lazily-evaluated sequence containing the remaining elements after and including the first element for which
         * the predicate returns false
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> skipWhile(final Function<? super T, Boolean> predicate, final Iterable<T> input) {
            if (predicate == null)
                throw new IllegalArgumentException("Functional.skipWhile(Func,Iterable<T>): predicate is null");
            if (input == null)
                throw new IllegalArgumentException("Functional.skipWhile(Func,Iterable<T>): input is null");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<T> it = input.iterator();
                            private boolean haveWeSkipped = false;
                            private boolean haveWeReadFirstValue = false;
                            private T firstValue = null;


                            public boolean hasNext() {
                                if (haveWeSkipped && it.hasNext()) return true;
                                if (haveWeSkipped) return false;
                                while (true) {
                                    if (it.hasNext()) {
                                        final T next = it.next();
                                        if (!predicate.apply(next)) {
                                            haveWeSkipped = true;
                                            firstValue = next;
                                            return true;
                                        }
                                    } else {
                                        haveWeSkipped = true;
                                        return false;
                                    }
                                }
                            }


                            public T next() {
                                if (haveWeSkipped && !haveWeReadFirstValue && firstValue != null) {
                                    haveWeReadFirstValue = true;
                                    return firstValue;
                                }
                                if (haveWeSkipped && !haveWeReadFirstValue) throw new NoSuchElementException();
                                if (haveWeSkipped) return it.next();
                                final boolean another = hasNext();
                                return next();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.skipWhile(Func,Iterable): it is not possible to remove elements from this sequence");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * skipWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
         * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
         *
         * @param <T>       the type of the element in the input sequence
         * @param predicate ignore elements in the input while the predicate is true.
         * @return a lazily-evaluated sequence containing the remaining elements after and including the first element for which
         * the predicate returns false
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Function<Iterable<T>, Iterable<T>> skipWhile(final Function<? super T, Boolean> predicate) {
            return input -> seq.skipWhile(predicate, input);
        }

        /**
         * take: given a sequence return another sequence containing the first 'howMany' elements
         *
         * @param howMany a positive number of elements to be returned from the input sequence
         * @param list    the input sequence
         * @param <T>     the type of the element in the input sequence
         * @return a sequence containing the first 'howMany' elements of 'list'
         * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
         */
        public static <T> Iterable<T> take(final int howMany, final Iterable<? extends T> list) {
            if (howMany < 0)
                throw new IllegalArgumentException("Functional.seq.take(int,Iterable<T>): howMany is negative");
            if (list == null) throw new IllegalArgumentException("Functional.seq.take(int,Iterable<T>): list is null");

            if (howMany == 0) return new ArrayList<>(0);

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<? extends T> it = list.iterator();
                            private int howManyHaveWeRetrievedAlready = 0;

                            public boolean hasNext() {
                                return howManyHaveWeRetrievedAlready < howMany && it.hasNext();
                            }


                            public T next() {
                                if (howManyHaveWeRetrievedAlready >= howMany)
                                    throw new java.util.NoSuchElementException("Cannot request additional elements from input");
                                final T next = it.next();
                                howManyHaveWeRetrievedAlready++;
                                return next;
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.take: remove is not supported");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * take: given a sequence return another sequence containing the first 'howMany' elements
         *
         * @param <T>     the type of the element in the input sequence
         * @param howMany a positive number of elements to be returned from the input sequence
         * @return a sequence containing the first 'howMany' elements of 'list'
         * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
         */
        public static <T> Function<Iterable<T>, Iterable<T>> take(final int howMany) {
            return input -> seq.take(howMany, input);
        }

        /**
         * takeWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
         * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
         *
         * @param <T>       the type of the element in the input sequence
         * @param predicate ignore elements in the input while the predicate is true.
         * @param input     the input sequence
         * @return a lazily-evaluated sequence containing the remaining elements after and including the first element for which
         * the predicate returns false
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> takeWhile(final Function<? super T, Boolean> predicate, final Iterable<T> input) {
            if (predicate == null)
                throw new IllegalArgumentException("Functional.takeWhile(Func,Iterable<T>): predicate is null");
            if (input == null)
                throw new IllegalArgumentException("Functional.takeWhile(Func,Iterable<T>): input is null");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<T> it = input.iterator();
                            private boolean haveWeFinished = false;
                            private T next = null;
                            private boolean haveWeCheckedTheCurrentElement = false;


                            public boolean hasNext() {
                                if (!haveWeFinished) {
                                    if (!haveWeCheckedTheCurrentElement) {
                                        if (it.hasNext()) {
                                            next = it.next();
                                            if (predicate.apply(next)) {
                                                haveWeCheckedTheCurrentElement = true;
                                                return true;
                                            } else {
                                                haveWeCheckedTheCurrentElement = true;
                                                haveWeFinished = true;
                                                return false;
                                            }
                                        } else {
                                            haveWeFinished = true;
                                            return false;
                                        }
                                    } else {
                                        return true;
                                    }
                                } else {
                                    return false;
                                }
                            }


                            public T next() {
                                if (!haveWeFinished) {
                                    if (hasNext()) {
                                        haveWeCheckedTheCurrentElement = false;
                                        return next;
                                    } else throw new NoSuchElementException();
                                }
                                throw new NoSuchElementException();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.takeWhile(Func,Iterable): it is not possible to remove elements from this sequence");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * takeWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
         * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
         *
         * @param <T>       the type of the element in the input sequence
         * @param predicate ignore elements in the input while the predicate is true.
         * @return a lazily-evaluated sequence containing the remaining elements after and including the first element for which
         * the predicate returns false
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Function<Iterable<T>, Iterable<T>> takeWhile(final Function<? super T, Boolean> predicate) {
            return input -> seq.takeWhile(predicate, input);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
         * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * This is the converse of <tt>fold</tt>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         */
        public static <A, B> Iterable<A> unfold(final Function<? super B, Pair<A, B>> unspool, final Function<? super B, Boolean> finished, final B seed) {
            if (unspool == null) throw new IllegalArgumentException("unspool");
            if (finished == null) throw new IllegalArgumentException("finished");

            return new Iterable<A>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<A> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<A>() {
                            B next = seed;

                            public boolean hasNext() {
                                return !finished.apply(next);
                            }


                            public A next() {
                                if (!hasNext()) throw new NoSuchElementException();
                                final Pair<A, B> t = unspool.apply(next);
                                next = t.getRight();
                                return t.getLeft();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.unfold(Func,Func,B): it is not possible to remove elements from this sequence");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
         * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * This is the converse of <tt>fold</tt>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         */
        public static <A, B> Iterable<A> unfold(final Function<? super B, Option<Pair<A, B>>> unspool, final B seed) {
            if (unspool == null) throw new IllegalArgumentException("unspool");

            return new Iterable<A>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<A> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<A>() {
                            B next = seed;

                            public boolean hasNext() {
                                return unspool.apply(next).isSome();
                            }


                            public A next() {
                                final Option<Pair<A, B>> temp = unspool.apply(next);
                                if (temp.isNone()) throw new NoSuchElementException();
                                next = temp.Some().getRight();
                                return temp.Some().getLeft();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.unfold(Func,B): it is not possible to remove elements from this sequence");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * This sequence generator returns a list of Range objects which split the interval [1-'howManyElements') into 'howManyPartitions' Range objects.
         * If the interval cannot be divided exactly then the remainder is allocated evenly across the first
         * 'howManyElements' % 'howManyPartitions' Range objects.
         *
         * @param howManyElements   defines the exclusive upper bound of the interval to be split
         * @param howManyPartitions defines the number of Range objects to generate to cover the interval
         * @return a list of Range objects
         */
        public static Iterable<Range<Integer>> partition(final int howManyElements, final int howManyPartitions) {
            return partition(Functional.range(0), howManyElements, howManyPartitions);
        }

        /**
         * This sequence generator returns a sequence of Range objects which split the interval [1-'howManyElements') into 'howManyPartitions'
         * Range objects. If the interval cannot be divided exactly then the remainder is allocated evenly across the first
         * 'howManyElements' % 'howManyPartitions' Range objects.
         *
         * @param generator         a function which generates members of the input sequence
         * @param howManyElements   defines the exclusive upper bound of the interval to be split
         * @param howManyPartitions defines the number of Range objects to generate to cover the interval
         * @return a list of Range objects
         */
        public static <T> Iterable<Range<T>> partition(final Function<Integer, T> generator, final int howManyElements, final int howManyPartitions) {
            if (howManyElements <= 0)
                throw new IllegalArgumentException("Functional.partition() howManyElements cannot be non-positive");
            if (howManyPartitions <= 0)
                throw new IllegalArgumentException("Functional.partition() howManyPartitions cannot be non-positive");

            final int size = howManyElements / howManyPartitions;
            final int remainder = howManyElements % howManyPartitions;

            assert size * howManyPartitions + remainder == howManyElements;

            final Integer seed = 0;
            final Function<Integer, Pair<T, Integer>> boundsCalculator = integer -> Pair.of(
                    generator.apply(1 + (integer * size + (integer <= remainder ? integer : remainder))),
                    integer + 1);
            final Function<Integer, Boolean> finished = integer -> integer > howManyPartitions;

            final Iterable<T> output = Functional.seq.unfold(boundsCalculator, finished, seed);

            return new Iterable<Range<T>>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<Range<T>> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<Range<T>>() {
                            final Iterator<T> iterator = output.iterator();
                            T last = iterator.next();

                            public boolean hasNext() {
                                return iterator.hasNext();
                            }


                            public Range<T> next() {
                                final T next = iterator.next();
                                final Range<T> retval = new Range(last, next);
                                last = next;
                                return retval;
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.partition(Func,int,int): it is not possible to remove elements from this sequence");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * The Convolution operator
         * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
         *
         * @param l1  input sequence
         * @param l2  input sequence
         * @param <A> the type of the element in the first input sequence
         * @param <B> the type of the element in the second input sequence
         * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
         * in order. If the sequences do not have the same number of elements then an exception is thrown.
         * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
         */
        public static <A, B> Iterable<Pair<A, B>> zip(final Iterable<? extends A> l1, final Iterable<? extends B> l2) {
            if (l1 == null)
                throw new IllegalArgumentException("Functional.seq.zip(Iterable<A>,Iterable<B>): l1 is null");
            if (l2 == null)
                throw new IllegalArgumentException("Functional.seq.zip(Iterable<A>,Iterable<B>): l2 is null");

            return new Iterable<Pair<A, B>>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<Pair<A, B>> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<Pair<A, B>>() {
                            private final Iterator<? extends A> l1_it = l1.iterator();
                            private final Iterator<? extends B> l2_it = l2.iterator();

                            public boolean hasNext() {
                                final boolean l1_it_hasNext = l1_it.hasNext();
                                final boolean l2_it_hasNext = l2_it.hasNext();
                                if (l1_it_hasNext != l2_it_hasNext)
                                    throw new IllegalArgumentException("Functional.seq.zip(Iterable<A>,Iterable<B>): l1 and l2 have differing numbers of elements");
                                return l1_it_hasNext && l2_it_hasNext;
                            }


                            public Pair<A, B> next() {
                                return Pair.of(l1_it.next(), l2_it.next());
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.zip(Iterable,Iterable): it is not possible to remove elements from this sequence");
                            }

                            //
                            //                        public void forEachRemaining(Consumer<? super Pair<A, B>> action) {
                            //
                            //                        }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        public static <A, B> Function<Iterable<B>, Iterable<Pair<A, B>>> zip(final Iterable<? extends A> l1) {
            return l2 -> seq.zip(l1, l2);
        }

        /**
         * The Convolution operator
         * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
         *
         * @param l1  input sequence
         * @param l2  input sequence
         * @param l3  input sequence
         * @param <A> the type of the element in the first input sequence
         * @param <B> the type of the element in the second input sequence
         * @param <C> the type of the element in the third input sequence
         * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
         * in order. If the sequences do not have the same number of elements then an exception is thrown.
         * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
         */
        public static <A, B, C> Iterable<Triple<A, B, C>> zip3(final Iterable<? extends A> l1, final Iterable<? extends B> l2, final Iterable<? extends C> l3) {
            if (l1 == null)
                throw new IllegalArgumentException("Functional.seq.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l1 is null");
            if (l2 == null)
                throw new IllegalArgumentException("Functional.seq.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l2 is null");
            if (l3 == null)
                throw new IllegalArgumentException("Functional.seq.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l3 is null");

            return new Iterable<Triple<A, B, C>>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<Triple<A, B, C>> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<Triple<A, B, C>>() {
                            private final Iterator<? extends A> l1_it = l1.iterator();
                            private final Iterator<? extends B> l2_it = l2.iterator();
                            private final Iterator<? extends C> l3_it = l3.iterator();

                            public boolean hasNext() {
                                final boolean l1_it_hasNext = l1_it.hasNext();
                                final boolean l2_it_hasNext = l2_it.hasNext();
                                final boolean l3_it_hasNext = l3_it.hasNext();
                                if (l1_it_hasNext != l2_it_hasNext || l1_it_hasNext != l3_it_hasNext)
                                    throw new IllegalArgumentException("Functional.seq.zip3(Iterable<A>,Iterable<B>,Iterable<C>): the input sequences have differing numbers of elements");
                                return l1_it_hasNext && l2_it_hasNext && l3_it_hasNext;
                            }


                            public Triple<A, B, C> next() {
                                return Triple.of(l1_it.next(), l2_it.next(), l3_it.next());
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.zip3(Iterable,Iterable,Iterable): it is not possible to remove elements from this sequence");
                            }

                            //
                            //                        public void forEachRemaining(Consumer<? super Pair<A, B>> action) {
                            //
                            //                        }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }

        public static <A, B, C> Function<Iterable<C>, Iterable<Triple<A, B, C>>> zip3(final Iterable<? extends A> l1, final Iterable<? extends B> l2) {
            return l3 -> seq.zip3(l1, l2, l3);
        }

        /**
         * Convolution of functions
         * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
         *
         * @param <A>   the type of the input sequence
         * @param <B>   the result type of the first transformation
         * @param <C>   the result type of the second transformation
         * @param f     the first transformation function
         * @param g     the second transformation function
         * @param input the input sequence
         * @return a sequence of pairs. The first value of the pair is the result of the first transformation and the seocnd value of the
         * pair of the result of the second transformation.
         */
        public static <A, B, C> Iterable<Pair<B, C>> zip(final Function<? super A, B> f, final Function<? super A, C> g, final Iterable<? extends A> input) {
            return new Iterable<Pair<B, C>>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<Pair<B, C>> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<Pair<B, C>>() {
                            private final Iterator<? extends A> iterator = input.iterator();

                            public boolean hasNext() {
                                return iterator.hasNext();
                            }


                            public Pair<B, C> next() {
                                final A next = iterator.next();
                                return Pair.of(f.apply(next), g.apply(next));
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Functional.seq.zip(Func,Func): it is not possible to remove elements from this sequence");
                            }
                        };
                    else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
                }
            };
        }
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
     * Recursive implementations of (some of) the algorithms contained herein
     */
    public static class rec {
        private rec() {
        }

        private static <A> Iterable<A> filter(final Function<? super A, Boolean> f, final Iterator<A> input, final Collection<A> accumulator) {
            if (input.hasNext()) {
                final A next = input.next();
                if (f.apply(next)) accumulator.add(next);
                return filter(f, input, accumulator);
            } else return accumulator;
        }

        /**
         * See <A href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</A>
         * This is a recursive implementation of filter.
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         *
         * @param <A> the type of the element in the input sequence
         * @param f   a filter function. This is passed each input element in turn and returns either true or false. If true then
         *            the input element is passed through to the output otherwise it is ignored.
         * @return a sequence which contains zero or more of the elements of the input sequence. Each element is included only if
         * the filter function returns true for the element.
         */
        public static <A> Iterable<A> filter(final Function<? super A, Boolean> f, final Iterable<A> input) {
            return filter(f, input.iterator(), input instanceof Collection<?> ? new ArrayList<>(((Collection) input).size()) : new ArrayList<>());
        }

        private static <A, B> Iterable<B> map(final Function<? super A, ? extends B> f, final Iterator<A> input, final Collection<B> accumulator) {
            if (input.hasNext()) {
                accumulator.add(f.apply(input.next()));
                return map(f, input, accumulator);
            } else return accumulator;
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * This is a recursive implementation of the map function. It is a 1-to-1 transformation.
         * Every element in the input sequence will be transformed into an element in the output sequence.
         * map: (A -> B) -> A seq -> B seq
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         *
         * @param <A>   the type of the element in the input sequence
         * @param <B>   the type of the element in the output sequence
         * @param f     a transformation function which takes a object of type A and returns an object, presumably related, of type B
         * @param input a sequence to be fed into f
         * @return a seq of type B containing the transformed values.
         */
        public static <A, B> Iterable<B> map(final Function<? super A, ? extends B> f, final Iterable<A> input) {
            return map(f, input.iterator(), input instanceof Collection<?> ? new ArrayList<>(((Collection) input).size()) : new ArrayList<>());
        }

        private static <A, B> A fold(final BiFunction<? super A, ? super B, ? extends A> f, final A initialValue, final Iterator<B> input) {
            if (input.hasNext()) {
                final B next = input.next();
                return fold(f, f.apply(initialValue, next), input);
            } else return initialValue;
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
         * fold: (A -> B -> A) -> A -> B list -> A
         * This is a recursive implementation of fold
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         *
         * @param <A>          the type of the initialValue / seed
         * @param <B>          the type of the element in the output sequence
         * @param f            the aggregation function
         * @param initialValue the seed for the aggregation
         * @return the aggregated value
         */
        public static <A, B> A fold(final BiFunction<? super A, ? super B, ? extends A> f, final A initialValue, final Iterable<B> input) {
            return fold(f, initialValue, input.iterator());
        }

        private static <A, B> List<A> unfold(final Function<? super B, Pair<A, B>> unspool, final Function<? super B, Boolean> finished, final B seed, final List<A> accumulator) {
            if (finished.apply(seed)) return accumulator;
            final Pair<A, B> p = unspool.apply(seed);
            accumulator.add(p.getLeft());
            return unfold(unspool, finished, p.getRight(), accumulator);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
         * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         * This is a recursive implementation of unfold
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         */
        public static <A, B> List<A> unfold(final Function<? super B, Pair<A, B>> unspool, final Function<? super B, Boolean> finished, final B seed) {
            return unfold(unspool, finished, seed, new ArrayList<>());
        }

        private static <A, B> List<A> unfold(final Function<? super B, Option<Pair<A, B>>> unspool, final B seed, final List<A> accumulator) {
            final Option<Pair<A, B>> p = unspool.apply(seed);
            if (p.isNone()) return accumulator;
            accumulator.add(p.Some().getLeft());
            return unfold(unspool, p.Some().getRight(), accumulator);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
         * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         * This is a recursive implementation of unfold
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         */
        public static <A, B> List<A> unfold(final Function<? super B, Option<Pair<A, B>>> unspool, final B seed) {
            return unfold(unspool, seed, new ArrayList<>());
        }
    }
        /*
        // Following are functions for non-list collections
        */

    public static <A, B, C> Map<B, C> map_dict(final Function<? super A, Map.Entry<B, C>> f, final Iterable<A> input) {
        final Map<B, C> results = new HashMap<>();
        for (final A a : input) {
            final Map.Entry<B, C> intermediate = f.apply(a);
            results.put(intermediate.getKey(), intermediate.getValue());
        }
        return results;
    }

    /**
     * Implementations of the algorithms contained herein which return sets
     * See <a href="http://en.wikipedia.org/wiki/Set_(computer_science)">Set</a>
     */
    public static class set {
        private set() {
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
         *
         * @param <A>   the type of the element in the input sequence
         * @param pred  a filter function. This is passed each input element in turn and returns either true or false. If true then
         *              the input element is passed through to the output otherwise it is ignored.
         * @param input a sequence of objects
         * @return a set which contains zero or more of the elements of the input sequence. Each element is included only if the filter
         * function returns true for the element.
         */
        public static <A> Set<A> filter(final Function<? super A, Boolean> pred, final Iterable<A> input) {
            final Set<A> output = input instanceof Collection<?> ? new HashSet<>(((Collection) input).size()) : new HashSet<>();
            for (final A element : input) {
                if (pred.apply(element))
                    output.add(element);
            }
            return Collections.unmodifiableSet(output);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
         * These sequences are concatenated into one final output sequence at the end of the transformation.
         * map: (T -> U list) -> T list -> U list
         *
         * @param <T>   the type of the element in the input sequence
         * @param <U>   the type of the element in the output sequence
         * @param f     a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
         * @param input a sequence to be fed into f
         * @return a set of type U containing the concatenated sequences of transformed values.
         */
        public static <T, U> Set<U> collect(final Function<? super T, ? extends Iterable<U>> f, final Iterable<T> input) {
            final Set<U> output = input instanceof Collection<?> ? new HashSet<>(((Collection) input).size()) : new HashSet<>();
            for (final T element : input)
                output.addAll(Functional.toSet(f.apply(element)));
            return Collections.unmodifiableSet(output);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * This implementation of the map function returns a set instead of an ordered sequence. It is a 1-to-1 transformation.
         * Every element in the input sequence will be transformed into an element in the output sequence.
         * map: (A -> B) -> A seq -> B set
         *
         * @param <A>   the type of the element in the input sequence
         * @param <B>   the type of the element in the output sequence
         * @param f     a transformation function which takes a object of type A and returns an object, presumably related, of type B
         * @param input a sequence to be fed into f
         * @return a set of type B containing the transformed values.
         * @throws UnsupportedOperationException if the <tt>add</tt> operation
         *                                       is not supported by this set
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this set
         * @throws NullPointerException          if the specified element is null and this
         *                                       set does not permit null elements
         * @throws IllegalArgumentException      if some property of the specified element
         *                                       prevents it from being added to this set
         */
        public static <A, B> Set<B> map(final Function<? super A, ? extends B> f, final Iterable<A> input) {
            final Set<B> output = input instanceof Collection<?> ? new HashSet<>(((Collection) input).size()) : new HashSet<>();
            for (final A a : input)
                output.add(f.apply(a));
            return Collections.unmodifiableSet(output);
        }

        /**
         * Concatenate two sequences and return a new list containing the concatenation.
         *
         * @param list1 first input sequence
         * @param list2 second input sequence
         * @param <T>   the type of the element in the input sequence
         * @return a set containing the elements of the first sequence and the elements of the second sequence
         */
        public static <T> Set<T> concat(final Set<? extends T> list1, final Set<? extends T> list2) {
            if (list1 == null) throw new IllegalArgumentException("Functional.concat(Set<T>,List<T>): list1 is null");
            if (list2 == null) throw new IllegalArgumentException("Functional.concat(Set<T>,List<T>): list2 is null");

            if (list1.size() == 0) return Collections.unmodifiableSet(list2);
            if (list2.size() == 0) return Collections.unmodifiableSet(list1);

            final Set<T> newList = new HashSet<>(list1);
            final boolean didItChange = newList.addAll(list2);
            return Collections.unmodifiableSet(newList);
        }

        /*
         * Non-destructive wrappers for set intersection and set difference
         */

        /**
         * Non-destructive wrapper for set intersection
         *
         * @param e1  input set
         * @param e2  input set
         * @param <E>
         * @return a set containing those elements which are contained within both sets 'e1' and 'e2'
         */
        public static <E> Set<E> intersection(final Set<? extends E> e1, final Set<? extends E> e2) {
            final Set<E> i = new HashSet<>(e1);
            i.retainAll(e2);
            return Collections.unmodifiableSet(i);
        }

        /**
         * Non-destructive wrapper for set difference
         *
         * @param inSet    input set
         * @param notInSet input set
         * @param <E>
         * @return a set of those elements which are in 'inSet' and not in 'notInSet'
         */
        public static <E> Set<E> asymmetricDifference(final Set<? extends E> inSet, final Set<? extends E> notInSet) {
            final Set<E> i = new HashSet<>(inSet);
            i.removeAll(notInSet);
            return Collections.unmodifiableSet(i);
        }
    }

    /**
     * Implementations of the algorithms contained herein in terms of 'fold'
     * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
     */
    public static class inTermsOfFold {
        private inTermsOfFold() {
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output
         * sequence.
         * map: (A -> B) -> A seq -> B list
         *
         * @param f   a transformation function which takes a object of type A and returns an object, presumably related, of type B
         * @param l   a sequence to be fed into f
         * @param <T> the type of the element in the input sequence
         * @param <U> the type of the element in the output sequence
         * @return a list of type B containing the transformed values.
         * @see <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
         */
        public static <T, U> List<T> map(final Function<? super U, ? extends T> f, final Iterable<U> l) {
            final List<T> l2 = Functional.fold((BiFunction<List<T>, U, List<T>>) (state, o2) -> {
                state.add(f.apply(o2));
                return state;
            }, l instanceof Collection<?> ? new ArrayList<>(((Collection) l).size()) : new ArrayList<>(), l);
            return l2;
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
         *
         * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
         *                  the input element is passed through to the output otherwise it is ignored.
         * @param l         a sequence of objects
         * @param <T>       the type of the element in the input sequence
         * @return a list which contains zero or more of the elements of the input sequence. Each element is included only if the filter
         * function returns true for the element.
         * @see <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
         */
        public static <T> List<T> filter(final Function<? super T, Boolean> predicate, final Iterable<T> l) {
            final List<T> l2 = Functional.fold((BiFunction<List<T>, T, List<T>>) (ts, o) -> {
                if (predicate.apply(o)) ts.add(o);
                return ts;
            }, l instanceof Collection<?> ? new ArrayList<>(((Collection) l).size()) : new ArrayList<>(), l);
            return l2;
        }

        /**
         * The init function, not dissimilar to list comprehensions, which is used to return a new finite list whose contents are
         * determined by successive calls to the function f.
         * init: (int -> A) -> int -> A list
         *
         * @param f       generator function used to produce the individual elements of the output list. This function is called by init
         *                with the unity-based position of the current element in the output list being produced. Therefore, the first time
         *                f is called it will receive a literal '1' as its argument; the second time '2'; etc.
         * @param howMany the number of elements in the output list
         * @param <A>     the type of the element in the output sequence
         * @return a list of 'howMany' elements of type 'T' which were generated by the function 'f'
         * @see <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
         */
        public static <A> List<A> init(final Function<Integer, ? extends A> f, final int howMany) {
            return Functional.unfold(a -> a <= howMany ? Option.toOption(Pair.of(f.apply(a), a + 1)) : Option.None(), 1);
        }
    }

    /**
     * This class provides alternative implementations of those standard functions which would ordinarily throw an exception
     * in the event of an unexpected failure. The functions in this class will indicate the failure in a different manner, typically
     * using the Option type.
     */
    public static class noException {
        private noException() {
        }

        /**
         * Find the first element from the input sequence for which the supplied predicate returns true
         * find: (A -> bool) -> A list -> A option
         *
         * @param f     predicate
         * @param input sequence
         * @param <A>   the type of the element in the input sequence
         * @return the first element from the input sequence for which the supplied predicate returns true or None
         * if no element is found that satisfies the predicate
         * @throws java.lang.IllegalArgumentException if f or input are null
         */
        public static <A> Option<A> find(final Function<? super A, Boolean> f, final Iterable<A> input) {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            for (final A a : input)
                if (f.apply((a)))
                    return Option.toOption(a);
            return Option.None();
        }

        /**
         * As <tt>find</tt> except that here we return the zero-based position in the input sequence of the found element
         * findIndex: (A -> bool) -> A list -> int option
         *
         * @param f     predicate
         * @param input sequence
         * @param <A>   the type of the element in the input sequence
         * @return the position in the input sequence of the first element from the input sequence for which the supplied predicate
         * returns true or None if no element is found that satisfies the predicate
         * @throws java.lang.IllegalArgumentException if f or input are null
         */
        public static <A> Option<Integer> findIndex(final Function<A, Boolean> f, final Iterable<? extends A> input) {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            int pos = 0;
            for (final A a : input)
                if (f.apply(a))
                    return Option.toOption(pos);
                else pos++;
            return Option.None();
        }

        /**
         * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
         * findLast: (A -> bool) -> A seq -> A option
         *
         * @param f     predicate
         * @param input sequence
         * @param <A>   the type of the element in the input sequence
         * @return the last element in the input sequence for which the supplied predicate returns true or None
         * if no element is found that satisfies the predicate
         * @throws java.lang.IllegalArgumentException if f or input are null
         */
        public static <A> Option<A> findLast(final Function<? super A, Boolean> f, final Iterable<A> input) {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            final Pair<List<A>, Iterable<A>> p = takeNAndYield(input, 1);
            if (p.getLeft().isEmpty()) return Option.None();
            final Pair<A, Boolean> seed = Pair.of(p.getLeft().get(0), f.apply(p.getLeft().get(0)));
            final Pair<A, Boolean> result = fold((state, item) -> f.apply(item) ? Pair.of(item, true) : state, seed, p.getRight());

            if (result.getRight()) return Option.toOption(result.getLeft());
            return Option.None();
        }

        /**
         * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
         * findLast: (A -> bool) -> A list -> A option
         *
         * @param f     predicate
         * @param input sequence
         * @param <A>   the type of the element in the input sequence
         * @return the last element in the input sequence for which the supplied predicate returns true or None
         * if no element is found that satisfies the predicate
         * @throws java.lang.IllegalArgumentException if f or input are null
         */
        public static <A> Option<A> findLast(final Function<? super A, Boolean> f, final List<A> input) {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            if (input.isEmpty()) return Option.None();
            for (final A a : Iterators.reverse(input))
                if (f.apply(a))
                    return Option.toOption(a);
            return Option.None();
        }

        /**
         * Return the final element from the input sequence
         *
         * @param input input sequence
         * @param <T>   the type of the element in the input sequence
         * @return the last element from the input sequence
         * @throws java.lang.IllegalArgumentException if the input sequence is null or empty
         */
        public static <T> Option<T> last(final Iterable<T> input) {
            if (input == null) throw new IllegalArgumentException("Functional.last(Iterable<T>): input is null");

            T state = null;
            for (final T element : input) state = element;

            return state == null ? Option.None() : Option.toOption(state);
        }

        /**
         * 'pick' is an analogue of <tt>find</tt>. Instead of a predicate, 'pick' is passed a map function which returns an <tt>Option</tt>.
         * Each element of the input sequence is supplied in turn to the map function 'f' and the first non-None Option to be returned from
         * the map function is returned by 'pick' to the calling code.
         * pick: (A -> B option) -> A seq -> B option
         *
         * @param f     the map function.
         * @param input the input sequence
         * @param <A>   the type of the element in the input sequence
         * @param <B>   the type of the output element
         * @return the first non-None transformed element of the input sequence or None if no such element exists
         */
        public static <A, B> Option<B> pick(final Function<A, Option<B>> f, final Iterable<? extends A> input) {
            if (f == null) throw new IllegalArgumentException("f");
            if (input == null) throw new IllegalArgumentException("input");

            for (final A a : input) {
                final Option<B> intermediate = f.apply(a); // which is, effectively, if(f(a)) return f(a), but without evaluating f twice
                if (!intermediate.isNone())
                    return intermediate;
            }
            return Option.None();
        }

        /**
         * forAll2: the predicate 'f' is applied to all elements in the input sequences input1 and input2 as pairs. If the predicate returns
         * true for all pairs and there is the same number of elements in both input sequences then forAll2 returns true. If the predicate
         * returns false at any point then the traversal of the input sequences halts and forAll2 returns false.
         * forAll2: (A -> B -> bool) -> A list -> B list -> bool option
         *
         * @param f      predicate to which each successive pair (input1_i, input2_i) is applied
         * @param input1 input sequence
         * @param input2 input sequence
         * @param <A>    the base type of the element in the first input sequence
         * @param <B>    the base type of the element in the second input sequence
         * @param <AA>   the type of the element in the first input sequence
         * @param <BB>   the type of the element in the second input sequence
         * @return true if the predicate 'f' evaluates true for all pairs, false otherwise or None
         * if the predicate returns true for all pairs and the sequences contain differing numbers
         * of elements
         */
        public static <A, B, AA extends A, BB extends B> Option<Boolean> forAll2(final BiFunction<A, B, Boolean> f, final Iterable<AA> input1, final Iterable<BB> input2) {
            final Iterator<AA> enum1 = input1.iterator();
            final Iterator<BB> enum2 = input2.iterator();
            boolean enum1Moved = false, enum2Moved = false;
            do {
                enum1Moved = enum1.hasNext();
                enum2Moved = enum2.hasNext();
                if (enum1Moved && enum2Moved && !f.apply(enum1.next(), enum2.next()))
                    return Option.toOption(false);
            } while (enum1Moved && enum2Moved);
            if (enum1Moved != enum2Moved)
                return Option.None();
            return Option.toOption(true);
        }

        /**
         * take: given a list return another list containing the first 'howMany' elements or fewer if there are not enough elements
         * in the input sequence
         *
         * @param howMany a positive upper bound for the number of elements to be returned from the input sequence
         * @param list    the input sequence
         * @param <T>     the type of the element in the input sequence
         * @return a list containing the first 'howMany' elements of 'list'
         */
        public static <T> List<T> take(final int howMany, final Iterable<? extends T> list) {
            if (howMany < 0)
                throw new IllegalArgumentException("Functional.take(int,Iterable<T>): howMany is negative");
            if (list == null) throw new IllegalArgumentException("Functional.take(int,Iterable<T>): list is null");

            if (howMany == 0) return new ArrayList<>(0);

            final List<T> output = new ArrayList<>(howMany);
            final Iterator<? extends T> iterator = list.iterator();
            for (int i = 0; i < howMany; ++i) {
                if (iterator.hasNext())
                    output.add(iterator.next());
                else
                    break;
            }
            return Collections.unmodifiableList(output);
        }

        /**
         * The Convolution operator
         * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
         *
         * @param l1  input sequence
         * @param l2  input sequence
         * @param <A> the type of the element in the first input sequence
         * @param <B> the type of the element in the second input sequence
         * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
         * in order. If the sequences do not have the same number of elements the results thus far are returned
         * @throws java.lang.IllegalArgumentException
         */
        public static <A, B> List<Pair<A, B>> zip(final Iterable<? extends A> l1, final Iterable<? extends B> l2) {
            if (l1 == null) throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l1 is null");
            if (l2 == null) throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l2 is null");

            final List<Pair<A, B>> output = (l1 instanceof Collection<?> && l2 instanceof Collection<?>)
                    ? new ArrayList<>(((Collection) l1).size())
                    : new ArrayList<>();
            final Iterator<? extends A> l1_it = l1.iterator();
            final Iterator<? extends B> l2_it = l2.iterator();

            while (l1_it.hasNext() && l2_it.hasNext()) output.add(Pair.of(l1_it.next(), l2_it.next()));

            return Collections.unmodifiableList(output);
        }

        /**
         * The Convolution operator
         * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
         *
         * @param l1  input sequence
         * @param l2  input sequence
         * @param l3  input sequence
         * @param <A> the type of the element in the first input sequence
         * @param <B> the type of the element in the second input sequence
         * @param <C> the type of the element in the third input sequence
         * @return list of triplets; the first element from each of the input sequences is the first triplet in the output sequence and so on,
         * in order. If the sequences do not have the same number of elements then the results thus far are returned
         * @throws java.lang.IllegalArgumentException if any input sequence is null or if the sequences have differing lengths.
         */
        public static <A, B, C> List<Triple<A, B, C>> zip3(final Iterable<? extends A> l1, final Iterable<? extends B> l2, final Iterable<? extends C> l3) {
            if (l1 == null)
                throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l1 is null");
            if (l2 == null)
                throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l2 is null");
            if (l3 == null)
                throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l3 is null");

            final List<Triple<A, B, C>> output = (l1 instanceof Collection<?> && l2 instanceof Collection<?> && l3 instanceof Collection<?>)
                    ? new ArrayList<>(((Collection) l1).size())
                    : new ArrayList<>();
            final Iterator<? extends A> l1_it = l1.iterator();
            final Iterator<? extends B> l2_it = l2.iterator();
            final Iterator<? extends C> l3_it = l3.iterator();

            while (l1_it.hasNext() && l2_it.hasNext() && l3_it.hasNext())
                output.add(Triple.of(l1_it.next(), l2_it.next(), l3_it.next()));

            return Collections.unmodifiableList(output);
        }
    }

    /*
    // Following are control structures, eg if, switch
     */

    /**
     * A functional 'if' statement. Given 'a', evaluate the predicate. If the predicate evaluates to true then return the value of
     * evaluating the 'thenClause' with 'a' otherwise return the value of evaluating the 'elseClause' with 'a'
     *
     * @param a          value to be tested with the predicate and thence passed to one of the 'thenClause' and 'elseClause'
     * @param predicate  function
     * @param thenClause function
     * @param elseClause function
     * @param <A>        the type of the element which we are passing to the predicate
     * @param <B>        the type of the result of the <tt>thenClause</tt> and <tt>elseClause</tt>
     * @return the results of evaluating the 'thenClause' or the 'elseClause', depending on whether the 'predicate' evaluates to true
     * or false respectively
     */
    public static <A, B> B If(final A a, final Function<? super A, Boolean> predicate, final Function<? super A, ? extends B> thenClause, final Function<? super A, ? extends B> elseClause) {
        if (a == null) throw new IllegalArgumentException("a");
        if (predicate == null) throw new IllegalArgumentException("predicate");
        if (thenClause == null) throw new IllegalArgumentException("thenClause");
        if (elseClause == null) throw new IllegalArgumentException("elseClause");

        return predicate.apply(a) ? thenClause.apply(a) : elseClause.apply(a);
    }

    /**
     * toCase: a Case builder function.
     *
     * @param pred   predicate
     * @param result the result function to be applied if the predicate evaluates to true
     * @param <A>    the type of the element being passed to the predicate
     * @param <B>    the type of the result of the transformation function
     * @return a new Case object
     */
    public static <A, B> Case<A, B> toCase(final Function<A, Boolean> pred, final Function<A, B> result) {
        if (pred == null) throw new IllegalArgumentException("pred");
        if (result == null) throw new IllegalArgumentException("res");

        return new Case<>(pred, result);
    }

    /**
     * Functional switch statement. Provide a sequence of Cases and a function which will be evaluated if none of the Cases are true.
     *
     * @param input       the value to be tested
     * @param cases       sequence of Case objects
     * @param defaultCase function to be evaluated if none of the Cases are true
     * @param <A>         the type of the element being passed to the predicates in the {@link me.shaftesbury.utils.functional.Case}
     * @param <B>         the type of the result
     * @return the result of the appropriate Case or the result of the 'defaultCase' function
     */
    public static <A, B> B Switch(final A input, final Iterable<Case<A, B>> cases, final Function<A, B> defaultCase) {
        return Switch(input, IterableHelper.create(cases), defaultCase);
    }

    /**
     * Functional switch statement. Provide a sequence of Cases and a function which will be evaluated if none of the Cases are true.
     *
     * @param input       the value to be tested
     * @param cases       sequence of Case objects
     * @param defaultCase function to be evaluated if none of the Cases are true
     * @param <A>         the type of the element passed to the predicate in the {@link me.shaftesbury.utils.functional.Case}
     * @param <B>         the type of the result
     * @return the result of the appropriate Case or the result of the 'defaultCase' function
     */
    public static <A, B> B Switch(final A input, final Iterable2<Case<A, B>> cases, final Function<A, B> defaultCase) {
        if (input == null) throw new IllegalArgumentException("input");
        if (cases == null) throw new IllegalArgumentException("cases");
        if (defaultCase == null) throw new IllegalArgumentException("defaultCase");

        //return Try<InvalidOperationException>.ToTry(input, a => cases.First(chk => chk.check(a)).results(a), defaultCase);
        try {
            return cases.find(abCase -> abCase.predicate(input)).results(input);
        } catch (final NoSuchElementException k) {
            return defaultCase.apply(input);
        }
    }

    /**
     * Helper function to return the first element in a Pair
     *
     * @param <A> the type of the first element in the pair
     * @param <B> the type of the second element in the pair
     * @return a function that returns the first element in a Pair
     */
    public static <A, B> Function<Pair<A, B>, A> first() {
        return Pair::getLeft;
    }

    /**
     * Helper function to return the second element in a Pair
     *
     * @param <A> the type of the first element in the pair
     * @param <B> the type of the second element in the pair
     * @return a function that returns the second element in a Pair
     */
    public static <A, B> Function<Pair<A, B>, B> second() {
        return Pair::getRight;
    }


    /**
     * Wrap the application of the function so that if an exception is thrown then Either.left() is returned otherwise Either.right() is returned.
     *
     * @param function this function declares that it throws EX
     * @param <A>      the type of the input data
     * @param <R>      the return type of the function
     * @param <E>      the checked exception that could be thrown by the function
     * @return a function that will return Either.left() if the supplied function throws an exception, Either.right() otherwise
     * @throws NullPointerException if the function is null.
     */
    public static <A, R, E extends Exception> Function<A, Either<Exception, R>> $(final FunctionWithExceptionDeclaration<A, R, E> function) {
        requireNonNull(function, "function must not be null");

        return a -> {
            try {
                return Either.right(function.apply(a));
            } catch (final Exception e) {
                return Either.left(e);
            }
        };
    }

    /**
     * As with <code>Stream.forEach()</code>, apply the function <code>consumer</code> to every element in the <code>input</code> stream. However, if an
     * exception occurs then it will be wrapped in an <code>EarlyExitException</code>
     *
     * @param input    the stream over which the forEach should operate
     * @param consumer the function to be applied (which has a checked exception declaration)
     * @param <A>      base class of the type of the elements in the stream
     * @param <EX>     the type of the checked exception thrown by the consumer function
     * @throws EarlyExitException   when the consumer throws the expected exception on an element in the stream
     * @throws NullPointerException if either of the parameters are null
     */
    public static <A, EX extends Exception> void forEach(final Stream<? extends A> input, final ConsumerWithExceptionDeclaration<? super A, EX> consumer) {
        requireNonNull(consumer, "consumer must not be null");
        final Spliterator<? extends A> spliterator = requireNonNull(input, "input must not be null").spliterator();
        final Iterator<? extends A> iterator = Spliterators.iterator(spliterator);

        while (iterator.hasNext()) {
            try {
                consumer.accept(iterator.next());
            } catch (final Exception e) {
                throw new EarlyExitException("There was an error that caused the consumer to fail.", e);
            }
        }
    }

    public static <A, B, C> Stream<C> zip(final Stream<? extends A> a, final Stream<? extends B> b, final BiFunction<? super A, ? super B, ? extends C> zipper) {
        requireNonNull(zipper, "zipper must not be null");
        final Spliterator<? extends A> aSpliterator = requireNonNull(a, "a must not be null").spliterator();
        final Spliterator<? extends B> bSpliterator = requireNonNull(b, "b must not be null").spliterator();

        // Zipping looses DISTINCT and SORTED characteristics
        final int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics() & ~(Spliterator.DISTINCT | Spliterator.SORTED);

        final long zipSize = ((characteristics & Spliterator.SIZED) != 0)
                ? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1;

        final Iterator<? extends A> aIterator = Spliterators.iterator(aSpliterator);
        final Iterator<? extends B> bIterator = Spliterators.iterator(bSpliterator);
        final Iterator<C> cIterator = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return aIterator.hasNext() && bIterator.hasNext();
            }

            @Override
            public C next() {
                return zipper.apply(aIterator.next(), bIterator.next());
            }
        };

        final Spliterator<C> split = Spliterators.spliterator(cIterator, zipSize, characteristics);
        return StreamSupport.stream(split, (a.isParallel() || b.isParallel()));
    }
}