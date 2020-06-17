package me.shaftesbury.utils.functional.primitive.integer;

import me.shaftesbury.utils.functional.Option;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static me.shaftesbury.utils.functional.primitive.integer.Iterators.reverse;

/**
 * Herein are contained some standard algorithms from functional programming tailored for use with Java
 * primitive types.
 * See <a href="http://en.wikipedia.org/wiki/Functional_programming">Functional Programming</a>
 * for more information
 */
public final class Functional
{
    private Functional() {}

    /**
     * Concatenate all of the input elements into a single string where each element is separated from the next by the supplied delimiter
     * @param delimiter used to separate consecutive elements in the output
     * @param ints input sequence, each element of which will be converted to a string
     * @return a string containing the string representation of each input element separated by the supplied delimiter
     */
    public static String join(final String delimiter, final IntList ints)
    {
        if(ints==null) return "";
        final IntIterator it = ints.iterator();
        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        while(it.hasNext())
        {
            if(!isFirst) sb.append(delimiter);
            sb.append(it.next());
            isFirst=false;
        }
        return sb.toString();
    }

    /**
     * Analogue of string.Join for List<T> with the addition of a user-defined map function
     *
     * @param separator inserted between each transformed element
     * @param l the input sequence
     * @param fn map function (see <tt>map</tt>) which is used to transform the input sequence
     * @return a string containing the transformed string value of each input element separated by the supplied separator
     */
    public static String join(final String separator, final IntIterable l, final Func_int_T<String> fn)
    {
        if (l == null) throw new IllegalArgumentException("l");
        if (fn == null) throw new IllegalArgumentException("fn");

        return me.shaftesbury.utils.functional.Functional.join(separator, map(fn, l));
    }

    /**
     * Find the first element from the input sequence for which the supplied predicate returns true
     * find: (A -> bool) -> A list -> A
     * @param f predicate
     * @param input sequence
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException if no element is found that satisfies the predicate
     * @return the first element from the input sequence for which the supplied predicate returns true
     */
    public static int find(final Func_int_T<Boolean> f, final IntList input)
    {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        final IntIterator iterator = input.iterator();
        while(iterator.hasNext()) {
            final int a = iterator.next();
            if (f.apply((a)))
                return a;
        }
        throw new NoSuchElementException();
    }

    /**
     * As <tt>find</tt> except that here we return the zero-based position in the input sequence of the found element
     * findIndex: (A -> bool) -> A list -> int
     * @param f predicate
     * @param input sequence
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException if no element is found that satisfies the predicate
     * @return the position in the input sequence of the first element from the input sequence for which the supplied predicate
     * returns true
     */
    public static int findIndex(final Func_int_T<Boolean> f, final IntList input)
    {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        int pos = 0;
        final IntIterator iterator = input.iterator();
        while(iterator.hasNext()) {
            final int a = iterator.next();
            if (f.apply(a))
                return pos;
            else pos++;
        }
        throw new IllegalArgumentException();
    }

    /**
     * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
     * findLast: (A -> bool) -> A list -> A
     * @param f predicate
     * @param input sequence
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException if no element is found that satisfies the predicate
     * @return the last element in the input sequence for which the supplied predicate returns true
     */
    public static int findLast(final Func_int_T<Boolean> f, final IntList input)
    {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        final IntIterable reversed = reverse(input);
        final IntIterator iterator = reversed.iterator();
        while (iterator.hasNext()) {
            final int a = iterator.next();
            if (f.apply(a))
                return a;
        }
        throw new NoSuchElementException();
    }

//    /**
//     * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
//     * findLast: (A -> bool) -> A seq -> A
//     * @param f predicate
//     * @param input sequence
//     * @throws java.lang.IllegalArgumentException if f or input are null
//     * @throws java.util.NoSuchElementException if no element is found that satisfies the predicate
//     * @return the last element in the input sequence for which the supplied predicate returns true
//     */
//    public static int findLast(final Func_int_T<Boolean> f, final IntIterable input)
//    {
//        if (f == null) throw new IllegalArgumentException("f");
//        if (input == null) throw new IllegalArgumentException("input");
//
//        final Pair<IntList,IntIterable> p = takeNAndYield(input,1);
//        final Pair<A,Boolean> seed = Pair.of(p.getLeft().get(0),f.apply(p.getLeft().get(0)));
//        final Pair<A,Boolean> result = fold(new BiFunction<Pair<A,Boolean>,A,Pair<A,Boolean>>(){
//             public Pair<A,Boolean> apply(final Pair<A,Boolean> state, final A item){return f.apply(item)?Pair.of(item,true):state;}
//        },seed,p.getValue1());
//
//        if(result.getValue1()) return result.getLeft();
//        throw new NoSuchElementException();
//    }

    /**
     * 'pick' is an analogue of <tt>find</tt>. Instead of a predicate, 'pick' is passed a map function which returns an <tt>Option</tt>.
     * Each element of the input sequence is supplied in turn to the map function 'f' and the first non-None Option to be returned from
     * the map function is returned by 'pick' to the calling code.
     * pick: (A -> B option) -> A seq -> B
     *
     * @param f the map function.
     * @param input the input sequence
     * @param <B> the type of the output element
     * @return the first non-None transformed element of the input sequence
     */
    public static <B>B pick(final Func_int_T<Option<B>> f, final IntIterable input)
    {
        if (f == null) throw new IllegalArgumentException("f");
        if (input == null) throw new IllegalArgumentException("input");

        final IntIterator iterator = input.iterator();
        while(iterator.hasNext())
        {
            final int a = iterator.next();
            final Option<B> intermediate = f.apply(a); // which is, effectively, if(f(a)) return f(a), but without evaluating f twice
            if (!intermediate.isNone())
                return intermediate.Some();
        }
        throw new NoSuchElementException();
    }

    /**
     * The identity transformation function: that is, the datum supplied as input is returned as output
     * @return a function which is the identity transformation
     */
    public static Func_int_T<Integer> identity()
    {
        return t -> t;
    }

    /**
     * <tt>isEven</tt> a function that accepts an integer and returns a boolean that indicates whether the passed integer
     * is or is not an even integer
     */
    public static Func_int_T<Boolean> isEven = i -> i % 2 == 0;
    /**
     * <tt>isOdd</tt> a function that accepts an integer and returns a boolean that indicates whether the passed integer
     * is or is not an odd integer
     */
    public static Func_int_T<Boolean> isOdd = i -> i % 2 != 0;
    /**
     * <tt>count</tt> a function that accepts a counter and another integer and returns 1 + counter
     */
    public static BiFunction<Integer,Integer,Integer> count = (state, b) -> state + 1;
    /**
     * <tt>sum</tt> a function that accepts two integers and returns the sum of them
     */
    public static BiFunction<Integer,Integer,Integer> sum = Integer::sum;

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is greater than
     * 'that' or false otherwise
     */
    public static <T extends Comparable<T>>Function<T,Boolean> greaterThan(final T that)
    {
        return ths -> ths.compareTo(that)>0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is greater than
     * or equal to 'that' or false otherwise
     */
    public static <T extends Comparable<T>>Function<T,Boolean> greaterThanOrEqual(final T that)
    {
        return ths -> ths.compareTo(that)>=0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is less than
     * 'that' or false otherwise
     */
    public static <T extends Comparable<T>>Function<T,Boolean> lessThan(final T that)
    {
        return ths -> ths.compareTo(that)<0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is less than
     * or equal to 'that' or false otherwise
     */
    public static <T extends Comparable<T>>Function<T,Boolean> lessThanOrEqual(final T that)
    {
        return ths -> ths.compareTo(that)<=0;
    }

    /**
     * The init function, not dissimilar to list comprehensions, which is used to return a new finite list whose contents are
     * determined by successive calls to the function f.
     * init: (int -> A) -> int -> A list
     * @param f generator function used to produce the individual elements of the output list. This function is called by init
     *          with the unity-based position of the current element in the output list being produced. Therefore, the first time
     *          f is called it will receive a literal '1' as its argument; the second time '2'; etc.
     * @param howMany the number of elements in the output list
     * @return a list of 'howMany' elements of type 'T' which were generated by the function 'f'
     */
    public static <T>List<T> init(final Func_int_T<T> f,final int howMany)
    {
        if (f == null) throw new IllegalArgumentException("f");
        if(howMany<1) throw new IllegalArgumentException("howMany");

        final ArrayList<T> output = new ArrayList<>(howMany);
        for(int i=1; i<=howMany; ++i)
            output.add(f.apply(i));
        return output;
    }

    /**
     * The init function, not dissimilar to list comprehensions, which is used to return a new finite list whose contents are
     * determined by successive calls to the function f.
     * init: (int -> A) -> int -> A list
     * @param f generator function used to produce the individual elements of the output list. This function is called by init
     *          with the unity-based position of the current element in the output list being produced. Therefore, the first time
     *          f is called it will receive a literal '1' as its argument; the second time '2'; etc.
     * @param howMany the number of elements in the output list
     * @return a list of 'howMany' elements of type 'T' which were generated by the function 'f'
     */
    public static IntList init(final Func_int_int f,final int howMany)
    {
        if (f == null) throw new IllegalArgumentException("f");
        if(howMany<1) throw new IllegalArgumentException("howMany");

        final int[] output = new int[howMany];
        for(int i=1; i<=howMany; ++i)
            output[i-1] = f.apply(i);
        return new IntList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * map: (A -> B) -> A list -> B list
     * @param f a transformation function which takes a object of type A and returns an object, presumably related, of type B
     * @param input a sequence to be fed into f
     * @param <B> the type of the element in the output sequence
     * @return a list of type B containing the transformed values.
     */
    public static <B> List<B> map(final Func_int_T<? extends B> f, final IntIterable input)
    {
        final List<B> output = input instanceof IntList ? new ArrayList<>(((IntList) input).size()) : new ArrayList<>();
        final IntIterator iterator = input.iterator();
        while(iterator.hasNext()) {
            final int a = iterator.next();
            output.add(f.apply(a));
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * map: (A -> B) -> A list -> B list
     * @param f a transformation function which takes a object of type A and returns an object, presumably related, of type B
     * @param input a sequence to be fed into f
     * @param <B> the type of the element in the output sequence
     * @return a list of type B containing the transformed values.
     */
    public static <B> IntList map(final Func_T_int<? super B> f, final Iterable<B> input)
    {
        final int[] output = input instanceof Collection<?> ? new int[((Collection<?>) input).size()] : new int[]{};
        int pos = 0;
        for(final B b : input)
            output[pos++] = f.apply(b);

        return new IntList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * mapi: (int -> A -> B) -> A list -> B list
     * @param f a transformation function which is passed each input object of type A along with its position in the input sequence
     *          (starting from zero) and returns an object, presumably related, of type B
     * @param input a sequence to be fed into f
     * @param <B> the type of the element in the output sequence
     * @return a list of type B containing the transformed values.
     */
    public static <B> List<B> mapi(final Func2_int_int_T<? extends B> f, final IntIterable input)
    {
        final List<B> output = input instanceof IntList ? new ArrayList<>(((IntList) input).size()) : new ArrayList<>();
        int pos = 0;
        final IntIterator iterator = input.iterator();
        while(iterator.hasNext()) {
            final int a = iterator.next();
            output.add(f.apply(pos++, a));
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * A transformation function that wraps <tt>Stringify</tt>
     * @return a function that calls <tt>Stringify</tt>
     */
    public static Func_int_T<String> dStringify()
    {
        return Integer::toString;
    }

    /**
     * forAll2: the predicate 'f' is applied to all elements in the input sequences input1 and input2 as pairs. If the predicate returns
     * true for all pairs and there is the same number of elements in both input sequences then forAll2 returns true. If the predicate
     * returns false at any point then the traversal of the input sequences halts and forAll2 returns false.
     * forAll2: (A -> B -> bool) -> A list -> B list -> bool
     * @param f predicate to which each successive pair (input1_i, input2_i) is applied
     * @param input1 input sequence
     * @param input2 input sequence
     * @param <A> the base type of the element in the first input sequence
     * @param <B> the base type of the element in the second input sequence
     * @param <AA> the type of the element in the first input sequence
     * @param <BB> the type of the element in the second input sequence
     * @return true if the predicate 'f' evaluates true for all pairs, false otherwise
     * @throws java.lang.IllegalArgumentException if the predicate returns true for all pairs and the sequences contain differing numbers
     * of elements
     */
    public static <A, B,AA extends A,BB extends B>boolean forAll2(final Func2_int_T_T<B,Boolean> f, final IntIterable input1, final Iterable<BB> input2)
    {
        final IntIterator enum1 = input1.iterator();
        final Iterator<BB> enum2 = input2.iterator();
        boolean enum1Moved = false, enum2Moved = false;
        do
        {
            enum1Moved = enum1.hasNext();
            enum2Moved = enum2.hasNext();
            if (enum1Moved && enum2Moved && !f.apply(enum1.next(), enum2.next()))
                return false;
        } while (enum1Moved && enum2Moved);
        if( enum1Moved != enum2Moved)
            throw new IllegalArgumentException();
        return true;
    }

    /**
     * forAll2: the predicate 'f' is applied to all elements in the input sequences input1 and input2 as pairs. If the predicate returns
     * true for all pairs and there is the same number of elements in both input sequences then forAll2 returns true. If the predicate
     * returns false at any point then the traversal of the input sequences halts and forAll2 returns false.
     * forAll2: (A -> B -> bool) -> A list -> B list -> bool
     * @param f predicate to which each successive pair (input1_i, input2_i) is applied
     * @param input1 input sequence
     * @param input2 input sequence
     * @param <A> the base type of the element in the first input sequence
     * @param <B> the base type of the element in the second input sequence
     * @param <AA> the type of the element in the first input sequence
     * @param <BB> the type of the element in the second input sequence
     * @return true if the predicate 'f' evaluates true for all pairs, false otherwise
     * @throws java.lang.IllegalArgumentException if the predicate returns true for all pairs and the sequences contain differing numbers
     * of elements
     */
    public static <A, B,AA extends A,BB extends B>boolean forAll2(final Func2_T_int_T<A,Boolean> f, final Iterable<AA> input1, final IntIterable input2)
    {
        final Iterator<AA> enum1 = input1.iterator();
        final IntIterator enum2 = input2.iterator();
        boolean enum1Moved = false, enum2Moved = false;
        do
        {
            enum1Moved = enum1.hasNext();
            enum2Moved = enum2.hasNext();
            if (enum1Moved && enum2Moved && !f.apply(enum1.next(), enum2.next()))
                return false;
        } while (enum1Moved && enum2Moved);
        if( enum1Moved != enum2Moved)
            throw new IllegalArgumentException();
        return true;
    }

    /**
     * forAll2: the predicate 'f' is applied to all elements in the input sequences input1 and input2 as pairs. If the predicate returns
     * true for all pairs and there is the same number of elements in both input sequences then forAll2 returns true. If the predicate
     * returns false at any point then the traversal of the input sequences halts and forAll2 returns false.
     * forAll2: (A -> B -> bool) -> A list -> B list -> bool
     * @param f predicate to which each successive pair (input1_i, input2_i) is applied
     * @param input1 input sequence
     * @param input2 input sequence
     * @param <A> the base type of the element in the first input sequence
     * @param <B> the base type of the element in the second input sequence
     * @param <AA> the type of the element in the first input sequence
     * @param <BB> the type of the element in the second input sequence
     * @return true if the predicate 'f' evaluates true for all pairs, false otherwise
     * @throws java.lang.IllegalArgumentException if the predicate returns true for all pairs and the sequences contain differing numbers
     * of elements
     */
    public static <A, B,AA extends A,BB extends B>boolean forAll2(final Func2_int_int_T<Boolean> f, final IntIterable input1, final IntIterable input2)
    {
        final IntIterator enum1 = input1.iterator();
        final IntIterator enum2 = input2.iterator();
        boolean enum1Moved = false, enum2Moved = false;
        do
        {
            enum1Moved = enum1.hasNext();
            enum2Moved = enum2.hasNext();
            if (enum1Moved && enum2Moved && !f.apply(enum1.next(), enum2.next()))
                return false;
        } while (enum1Moved && enum2Moved);
        if( enum1Moved != enum2Moved)
            throw new IllegalArgumentException();
        return true;
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
     * @param pred a filter function. This is passed each input element in turn and returns either true or false. If true then
     *             the input element is passed through to the output otherwise it is ignored.
     * @param input a sequence of objects
     * @return a list which contains zero or more of the elements of the input sequence. Each element is included only if the filter
     *          function returns true for the element.
     */
    public static IntList filter(final Func_int_T<Boolean> pred, final IntIterable input)
    {
        final int[] output = input instanceof IntList ? new int[((IntList) input).size()] : new int[]{};
        final IntIterator iterator = input.iterator();
        int pos=0;
        while(iterator.hasNext()) {
            final int element = iterator.next();
            if (pred.apply(element))
                output[pos++]=element;
        }
        return new IntList(output,pos);
    }

    /**
     * The converse operation to <tt>forAll</tt>. If the predicate returns true then 'exists' returns true and halts the traveral of the
     * input sequence. Otherwise return false.
     * exists: (A -> bool) -> A list -> bool
     * @param f predicate
     * @param input input sequence
     * @return true if the predicate returns true for any element in the input sequence, false otherwise
     */
    public static boolean exists(final Func_int_T<Boolean> f, final IntIterable input)
    {
        final IntIterator iterator = input.iterator();
        while(iterator.hasNext()) {
            final int a = iterator.next();
            if (f.apply(a))
                return true;
        }
        return false;
    }

    /**
     * not reverses the result of the applied predicate
     * not: (A -> bool) -> (A -> bool)
     * @param f the applied predicate
     * @return true if f returns false, false if f returns true
     */
    public static Func_int_T<Boolean> not(final Func_int_T<Boolean> f)
    {
        return a -> !f.apply(a);
    }

    /**
     * The converse operation to <tt>exists</tt>. If the predicate returns true for all elements in the input sequence then 'forAll'
     * returns true otherwise return false.
     * forAll: (A -> bool) -> A list -> bool
     * @param f predicate
     * @param input input sequence
     * @param <A> the type of the element in the input sequence
     * @return true if the predicate returns true for all elements in the input sequence, false otherwise
     */
    public static <A>boolean forAll(final Func_int_T<Boolean> f, final IntIterable input)
    {
        return !exists(not(f), input);
    }

    /**
     * not2 reverses the result of the applied predicate
     * not2: (A -> B -> bool) -> (A -> B -> bool)
     * @param f the applied predicate
     * @param <A> the type of the first input to the function <tt>f</tt>
     * @param <B> the type of the second input to the function <tt>f</tt>
     * @return true if f returns false, false if f returns true
     */
    public static <A,B> Func2_int_int_T<Boolean> not2(final Func2_int_int_T<Boolean> f)
    {
        return (a, b) -> !f.apply(a,b);
    }

    /// <summary> </summary>
    /// <returns> (list * list). The first list contains all items for which f(a) is true. The second list contains the remainder.</returns>

    /**
     * partition is a group function. Given a predicate and an input sequence, 'partition' returns a pair of lists, the first list
     * containing those elements from the input sequence for which the predicate returned true, the second list containing those
     * elements from the input sequence for which the predicate returned false.
     * partition: (A -> bool) -> A list -> A list * A list
     * @param f predicate used to split the input sequence into two groups
     * @param input the input sequence
     * @return a pair of lists, the first being the 'true' and the second being the 'false'
     */
    public static Pair<List<Integer>,List<Integer>> partition(final Func_int_T<Boolean> f, final IntIterable input)
    {
        final List<Integer> left;
        final List<Integer> right;
        if(input instanceof IntList)
        {
            left = new ArrayList<>(((IntList) input).size());
            right = new ArrayList<>(((IntList) input).size());
        }
        else
        {
            left = new ArrayList<>();
            right = new ArrayList<>();
        }
        final IntIterator iterator = input.iterator();
        while(iterator.hasNext()) {
            final int a = iterator.next();
            if (f.apply(a))
                left.add(a);
            else
                right.add(a);
        }
        return Pair.of(Collections.unmodifiableList(left), Collections.unmodifiableList(right));
    }

    /**
     * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     * @param f map function. This transforms the input element into an Option
     * @param input input sequence
     * @param <B> the type of the element in the output sequence
     * @return a list of transformed elements, numbering less than or equal to the number of input elements
     */
    public static <B>List<B> choose(final Func_int_T<Option<B>> f, final IntIterable input)
    {
        final List<B> results = input instanceof IntList ? new ArrayList<>(((IntList) input).size()) : new ArrayList<>();
        final IntIterator iterator = input.iterator();
        while(iterator.hasNext())
        {
            final int a = iterator.next();
            final Option<B> intermediate = f.apply(a);
            if (!intermediate.isNone())
                results.add(intermediate.Some());
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     * @param f map function. This transforms the input element into an Option
     * @param input input sequence
     * @return a list of transformed elements, numbering less than or equal to the number of input elements
     */
    public static IntList choose(final Func_int_Option_int f, final IntIterable input)
    {
        final int[] results = input instanceof IntList ? new int[(((IntList) input).size())] : new int[]{};
        final IntIterator iterator = input.iterator();
        int counter=0;
        while(iterator.hasNext())
        {
            final int a = iterator.next();
            final Option_int intermediate = f.apply(a);
            if (!intermediate.isNone())
                results[counter++]=intermediate.Some();
        }
        return new IntList(results,counter);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
     * fold: aggregate the elements of the input sequence given a seed and an aggregation function.
     * fold: (A -> B -> A) -> A -> B list -> A
     * @param f aggregation function
     * @param initialValue seed for the algorithm
     * @param input input sequence
     * @param <A> the type of the initialValue / seed
     * @return aggregated value
     */
    public static <A>A fold(final Func2_T_int_T<? super A, ? extends A> f, final A initialValue, final IntIterable input)
    {
        A state = initialValue;
        final IntIterator iterator = input.iterator();
        while(iterator.hasNext()) {
            final int b = iterator.next();
            state = f.apply(state, b);
        }
        return state;
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
     * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A,B>List<A> unfold(final Function<? super B,Pair<A,B>> unspool, final Function<? super B,Boolean> finished, final B seed)
    {
        if(unspool==null) throw new IllegalArgumentException("unspool");
        if(finished==null) throw new IllegalArgumentException("finished");

        B next = seed;
        final List<A> results = new ArrayList<>();
        while(!finished.apply(next)) {
            final Pair<A,B> t = unspool.apply(next);
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
    public static <A,B>List<A> unfold(final Function<? super B,Option<Pair<A,B>>> unspool, final B seed)
    {
        if(unspool==null) throw new IllegalArgumentException("unspool");

        B next = seed;
        final List<A> results = new ArrayList<>();
        while(true) {
            final Option<Pair<A,B>> t = unspool.apply(next);
            if(t.isNone()) break;
            results.add(t.Some().getLeft());
            next = t.Some().getRight();
        }
        return results;
    }

    /**
     * @param lowerBound
     * @param upperBound
     * @param val
     * @return lowerBound < val < upperBound
     */
    public static boolean between(final int lowerBound, final int upperBound, final int val)
    {
        return lowerBound < val && val < upperBound;
    }

    /**
     * toDictionary: given each element from the input sequence apply the keyFn and valueFn to generate a (key,value) pair.
     * The resulting dictionary (java.util.Map) contains all these pairs.
     * @param keyFn function used to generate the key
     * @param valueFn function used to generate the value
     * @param input input sequence
     * @param <T> the type of the element in the input sequence
     * @param <K> the type of the key elements
     * @param <V> the type of the value elements
     * @return a java.util.Map containing the transformed input sequence
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this map
     */
    public static <T,K,V>Map<K,V> toDictionary(final Func_int_T<? extends K> keyFn, final Func_int_T<? extends V> valueFn, final IntIterable input)
    {
        if(keyFn==null) throw new IllegalArgumentException("keyFn");
        if(valueFn==null) throw new IllegalArgumentException("valueFn");

        final Map<K,V> output = new HashMap<>();
        final IntIterator iterator = input.iterator();
        while(iterator.hasNext())
        {
            final int element = iterator.next();
            output.put(keyFn.apply(element), valueFn.apply(element));
        }
        return Collections.unmodifiableMap(output);
    }

    /**
     * toArray: create an array containing all the objects in the input sequence
     * @param input input sequence
     * @param <T> the type of the element in the input sequence
     * @return an array containing all the elements of the input sequence
     */
    public static <T>Object[] toArray(final Iterable<T> input)
    //public static <T>T[] toArray(final Iterable<T> input)
    {
        if(input==null) throw new IllegalArgumentException("Functional.toArray(Iterable<T>): input is null");

        if(input instanceof Collection<?>)
            return ((Collection<T>)input).toArray();

        final List<T> output = new ArrayList<>();
        for(final T element: input) output.add(element);

        return output.toArray(); // this needs to be output.toArray(new T[0]) but that doesn't appear to be allowable Java :-(
    }

    public static <T>List<T> toMutableList(final Iterable<T> input)
    {
        if(input==null) throw new IllegalArgumentException("Functional.toMutableList(Iterable<T>): input is null");

        if(input instanceof Collection<?>)
        {
            final Collection<T> input_ = (Collection<T>)input;
            final List<T> output = new ArrayList<>(input_.size());
            output.addAll(input_);
            return output;
        }

        final List<T> output = new ArrayList<>();
        for(final T element: input) output.add(element);

        return output;
    }

    public static <K,V>Map<K,V> toMutableDictionary(final Map<K,V> input)
    {
        if(input==null) throw new IllegalArgumentException("Functional.toMutableDictionary(Map<K,V>): input is null");

        final Map<K,V> output = new HashMap<>(input.size());
        output.putAll(input);
        return output;
    }

    public static <T>Set<T> toMutableSet(final Iterable<T> input)
    {
        if(input==null) throw new IllegalArgumentException("Functional.toMutableSet(Iterable<T>): input is null");

        if(input instanceof Collection<?>)
        {
            final Collection<T> input_ = (Collection<T>)input;
            final Set<T> output = new HashSet<>(input_.size());
            output.addAll(input_);
            return output;
        }

        final Set<T> output = new HashSet<>();
        for(final T element: input) output.add(element);

        return output;
    }

    /**
     * Create a java.util.List which contains all of the elements in the input sequence
     * @param input input sequence
     * @param <T> the type of the element in the input sequence
     * @return a list containing the elements of the input sequence
     */
    public static <T>List<T> toList(final Iterable<T> input)
    {
        if(input==null) throw new IllegalArgumentException("Functional.toList(Iterable<T>): input is null");
        return Collections.unmodifiableList(toMutableList(input));
    }

    /**
     * Create a java.util.Set which contains all of the elements in the input sequence
     * @param input input sequence
     * @param <T> the type of the element in the input sequence
     * @return a set containing the elements of the input sequence
     */
    public static <T>Set<T> toSet(final Iterable<T> input)
    {
        //Sets.newSetFromMap();
        if(input==null) throw new IllegalArgumentException("Functional.toSet(Iterable<T>): input is null");
        return Collections.unmodifiableSet(toMutableSet(input));
    }

    /**
     * Return the final element from the input sequence
     * @param input input sequence
     * @return the last element from the input sequence
     * @throws java.lang.IllegalArgumentException if the input sequence is null or empty
     */
    public static int last(final IntIterable input)
    {
        if(input==null) throw new IllegalArgumentException("Functional.last(Iterable<T>): input is null");

        boolean isSet=false;
        int state = 0;
        final IntIterator iterator = input.iterator();
        if(!iterator.hasNext()) throw new IllegalArgumentException("Functional.last(Iterable): input is empty");
        while(iterator.hasNext())
        {
            final int element = iterator.next();
            state = element;
            isSet=true;
        }

        return state;
    }

    /**
     * Return the final element from the input array
     * @param input input array
     * @param <T> the type of the element in the input sequence
     * @return the last element from the input array
     */
    public static <T>T last(final T[] input)
    {
        if(input==null||input.length==0) throw new IllegalArgumentException("Functional.last(T[]): input is null or empty");

        return input[input.length-1];
    }

    /**
     * Concatenate two sequences and return a new list containing the concatenation.
     * @param list1 first input sequence
     * @param list2 second input sequence
     * @return a list containing the elements of the first sequence followed by the elements of the second sequence
     */
    public static IntList concat(final IntList list1, final IntList list2)
    {
        if(list1==null) throw new IllegalArgumentException("Functional.concat(List<T>,List<T>): list1 is null");
        if(list2==null) throw new IllegalArgumentException("Functional.concat(List<T>,List<T>): list2 is null");

        final int[] first = list1.extractBackingStoreWithoutCopy();
        final int[] second = list2.extractBackingStoreWithoutCopy();
        return new IntList(first,second);
    }

    /**
     * take: given a list return another list containing the first 'howMany' elements
     * @param howMany a positive number of elements to be returned from the input sequence
     * @param list the input sequence
     * @param <T> the type of the element in the input sequence
     * @return a list containing the first 'howMany' elements of 'list'
     * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
     */
    public static<T>List<T> take(final int howMany, final Iterable<? extends T> list)
    {
        if(howMany<0) throw new IllegalArgumentException("Functional.take(int,Iterable<T>): howMany is negative");
        if(list==null) throw new IllegalArgumentException("Functional.take(int,Iterable<T>): list is null");

        if(howMany==0) return new ArrayList<>(0);

        final List<T> output = new ArrayList<>(howMany);
        final Iterator<? extends T> iterator = list.iterator();
        for(int i=0;i<howMany;++i)
        {
            if(iterator.hasNext())
                output.add(iterator.next());
            else
                throw new java.util.NoSuchElementException("Cannot take "+howMany+" elements from input list with fewer elements");
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * take: given a list return another list containing the first 'howMany' elements
     * This is the curried implementation
     * @param howMany a positive number of elements to be returned from the input sequence
     * @param <T> the type of the element in the input sequence
     * @return a list containing the first 'howMany' elements of 'list'
     * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static<T>Function<Iterable<? extends T>,List<T>> take(final int howMany)
    {
        return input -> Functional.take(howMany,input);
    }

    /**
     * takeWhile: given a list return another list containing the first elements up and not including the first element for which
     * the predicate returns false
     * @param predicate the predicate to use
     * @param list the input sequence
     * @param <T> the type of the element in the input sequence
     * @return a list
     */
    public static<T>List<T> takeWhile(final Function<? super T, Boolean> predicate, final List<T> list)
    {
        if(predicate==null) throw new IllegalArgumentException("Functional.take(Func,Iterable<T>): predicate is null");
        if(list==null) throw new IllegalArgumentException("Functional.take(Func,Iterable<T>): list is null");

        if(list.size()==0) return new ArrayList<>();

        for(int i=0;i<list.size();++i)
        {
            final T element = list.get(i);
            if(!predicate.apply(element))
            {
                if(i==0) return new ArrayList<>();
                return Collections.unmodifiableList(list.subList(0,i));
            }
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * takeWhile: given a list return another list containing the first elements up and not including the first element for which
     * the predicate returns false
     * This is the curried implementation
     * @param predicate the predicate to use
     * @param <T> the type of the element in the input sequence
     * @return a list
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static<T>Function<List<T>,List<T>> takeWhile(final Function<? super T, Boolean> predicate)
    {
        return input -> Functional.takeWhile(predicate, input);
    }

    /**
     * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
     * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
     * @param howMany a non-negative number of elements to be discarded from the input sequence
     * @param list the input sequence
     * @param <T> the type of the element in the input sequence
     * @return a list containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
     * are skipped than are present in the 'list'
     */
    public static <T>List<T> skip(final int howMany, final List<? extends T> list)
    {
        if(howMany<0) throw new IllegalArgumentException("Functional.skip(int,List<T>): howMany is negative");
        if(list==null) throw new IllegalArgumentException("Functional.skip(int,List<T>): list is null");

        if(howMany==0) return Collections.unmodifiableList(list);
        final int outputListSize = list.size()-howMany;
        if(outputListSize<=0) return new ArrayList<>();

        return Collections.unmodifiableList(list.subList(howMany,list.size()));
    }

    /**
     * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
     * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
     * This is the curried implementation
     * @param howMany a non-negative number of elements to be discarded from the input sequence
     * @param <T> the type of the element in the input sequence
     * @return a list containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
     * are skipped than are present in the 'list'
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static<T>Function<List<? extends T>,List<T>> skip(final int howMany)
    {
        return input -> Functional.skip(howMany, input);
    }

    /**
     * skipWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     * @param predicate ignore elements in the input while the predicate is true.
     * @param list the input sequence
     * @param <T> the type of the element in the input sequence
     * @return a list containing the remaining elements after and including the first element for which the predicate returns false
     */
    public static <T>List<T> skipWhile(final Function<? super T, Boolean> predicate, final List<T> list)
    {
        if(predicate==null) throw new IllegalArgumentException("Functional.skipWhile(Func,List<T>): predicate is null");
        if(list==null) throw new IllegalArgumentException("Functional.skipWhile(Func,List<T>): list is null");

        for(int counter=0; counter<list.size();++counter)
            if(!predicate.apply(list.get(counter)))
                return Collections.unmodifiableList(list.subList(counter,list.size()));

        return Collections.unmodifiableList(new ArrayList<>(0));
    }

    /**
     * skipWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     * @param predicate ignore elements in the input while the predicate is true.
     * @param <T> the type of the element in the input sequence
     * @return a list containing the remaining elements after and including the first element for which the predicate returns false
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static<T>Function<List<T>,List<T>> skipWhile(final Function<? super T, Boolean> predicate)
    {
        return input -> Functional.skipWhile(predicate, input);
    }

    /**
     * constant: a function that returns a map function f(n) that returns the supplied 'constant'. Typically this would be
     * used in <tt>init</tt>
     * @param constant the desired constant value to be returned
     * @param <T> the type of the constant
     * @return a function that returns a function that returns the supplied constant
     */
    public static <T>Function<Integer,T> constant(final T constant)
    {
        return integer -> constant;
    }

    /**
     * range: a function that returns a map function f(n) that returns an integer from the open-ended range [startFrom+n, infinity).
     * Typically this would be used in <tt>init</tt>
     * @param startFrom the lower bound of the range
     * @return a function that returns a function that returns an integer from the range [startFrom+n, infinity)
     */
    public static Func_int_int range(final int startFrom)
    {
        return new Func_int_int(){
            private final int start = startFrom;
            public int apply(final int input) {
                return (start-1)+input; // because init starts counting from 1
            }
        };
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     * @param l1 input sequence
     * @param l2 input sequence
     * @param <A> the type of the element in the first input sequence
     * @param <B> the type of the element in the second input sequence
     * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
     * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
     *          in order. If the sequences do not have the same number of elements then an exception is thrown.
     */
    public static <A,B>List<Pair<A,B>> zip(final Iterable<? extends A> l1, final Iterable<? extends B> l2)
    {
        if(l1==null) throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l1 is null");
        if(l2==null) throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l2 is null");

        final List<Pair<A,B>> output;
        if(l1 instanceof Collection<?> && l2 instanceof Collection<?>) {
            if (((Collection) l1).size() != ((Collection) l2).size())
                throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l1 and l2 have differing numbers of elements");

            output = new ArrayList<>(((Collection) l1).size());
        }
        else output = new ArrayList<>();
        final Iterator<? extends A> l1_it = l1.iterator();
        final Iterator<? extends B> l2_it = l2.iterator();

        while(l1_it.hasNext() && l2_it.hasNext()) output.add(Pair.of(l1_it.next(),l2_it.next()));
        if(l1_it.hasNext() || l2_it.hasNext()) throw new IllegalArgumentException("Functional.zip(Iterable<A>,Iterable<B>): l1 and l2 have differing numbers of elements");

        return Collections.unmodifiableList(output);
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     * @param l1 input sequence
     * @param l2 input sequence
     * @param l3 input sequence
     * @param <A> the type of the element in the first input sequence
     * @param <B> the type of the element in the second input sequence
     * @param <C> the type of the element in the third input sequence
     * @throws java.lang.IllegalArgumentException if any input sequence is null or if the sequences have differing lengths.
     * @return list of triplets; the first element from each of the input sequences is the first triplet in the output sequence and so on,
     *          in order. If the sequences do not have the same number of elements then an exception is thrown.
     */
    public static <A,B,C>List<Triple<A,B,C>> zip3(final Iterable<? extends A> l1, final Iterable<? extends B> l2, final Iterable<? extends C> l3)
    {
        if(l1==null) throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l1 is null");
        if(l2==null) throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l2 is null");
        if(l3==null) throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l3 is null");

        final List<Triple<A,B,C>> output;
        if(l1 instanceof Collection<?> && l2 instanceof Collection<?> && l3 instanceof Collection<?>) {
            if (((Collection) l1).size() != ((Collection) l2).size())
                throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l1, l2 and l3 have differing numbers of elements");

            output = new ArrayList<>(((Collection) l1).size());
        }
        else output = new ArrayList<>();
        final Iterator<? extends A> l1_it = l1.iterator();
        final Iterator<? extends B> l2_it = l2.iterator();
        final Iterator<? extends C> l3_it = l3.iterator();

        while(l1_it.hasNext() && l2_it.hasNext() && l3_it.hasNext()) output.add(Triple.of(l1_it.next(),l2_it.next(),l3_it.next()));
        if(l1_it.hasNext() || l2_it.hasNext() || l3_it.hasNext())
            throw new IllegalArgumentException("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): l1, l2 and l3 have differing numbers of elements");

        return Collections.unmodifiableList(output);
    }

    /**
     * The converse of the Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     * @param input sequence of pairs
     * @param <A> the type of the first element in the pair
     * @param <B> the type of the second element in the pair
     * @throws java.lang.IllegalArgumentException if the input sequence is null
     * @return pair of lists; the first element from each of the two output sequences is the first pair in the input sequence and so on,
     *          in order.
     */
    public static <A,B>Pair<List<A>,List<B>> unzip(final Iterable<Pair<A,B>> input)
    {
        if(input==null) throw new IllegalArgumentException("Functional.unzip(Iterable<Pair<A,B>>): input is null");

        final List<A> l1;
        final List<B> l2;
        if(input instanceof Collection<?>) {
            final int size = ((Collection) input).size();
            l1 = new ArrayList<>(size);
            l2 = new ArrayList<>(size);
        }
        else {
            l1 = new ArrayList<>();
            l2 = new ArrayList<>();
        }
        for(final Pair<A,B> pair:input)
        {
            l1.add(pair.getLeft());
            l2.add(pair.getRight());
        }

        return Pair.of(Collections.unmodifiableList(l1),Collections.unmodifiableList(l2));
    }

    /**
     * The converse of the Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     * @param input sequence of triplets
     * @param <A> the type of the first element in the triplet
     * @param <B> the type of the second element in the triplet
     * @param <C> the type of the third element in the triplet
     * @throws java.lang.IllegalArgumentException if the input sequence is null
     * @return triplet of lists; the first element from each of the output sequences is the first triplet in the input sequence and so on,
     *          in order.
     */
    public static <A,B,C>Triple<List<A>,List<B>,List<C>> unzip3(final Iterable<Triple<A,B,C>> input)
    {
        if(input==null) throw new IllegalArgumentException("Functional.unzip(Iterable<Pair<A,B>>): input is null");

        final List<A> l1;
        final List<B> l2;
        final List<C> l3;
        if(input instanceof Collection<?>) {
            final int size = ((Collection) input).size();
            l1 = new ArrayList<>(size);
            l2 = new ArrayList<>(size);
            l3 = new ArrayList<>(size);
        }
        else {
            l1 = new ArrayList<>();
            l2 = new ArrayList<>();
            l3 = new ArrayList<>();
        }

        for(final Triple<A,B,C> triplet:input)
        {
            l1.add(triplet.getLeft());
            l2.add(triplet.getMiddle());
            l3.add(triplet.getRight());
        }

        return Triple.of(Collections.unmodifiableList(l1),Collections.unmodifiableList(l2),Collections.unmodifiableList(l3));
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
     * These sequences are concatenated into one final output sequence at the end of the transformation.
     * map: (T -> U list) -> T list -> U list
     * @param f a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
     * @param input a sequence to be fed into f
     * @param <T> the type of the element in the input sequence
     * @param <U> the type of the element in the output sequence
     * @return a list of type U containing the concatenated sequences of transformed values.
     */
    public static <T,U>List<U> collect(final Function<? super T,? extends Iterable<U>> f, final Iterable<T> input)
    {
        final List<U> output = input instanceof Collection<?> ? new ArrayList<>(((Collection) input).size()) : new ArrayList<>();
//        for(final T element : input)
//            output = Functional.concat(output, Functional.toList(f.apply(element)));
        return Collections.unmodifiableList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
     * These sequences are concatenated into one final output sequence at the end of the transformation.
     * map: (T -> U list) -> T list -> U list
     * This is a curried implementation of 'collect'
     * @param f a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
     * @param <T> the type of the element in the input sequence
     * @param <U> the type of the element in the output sequence
     * @return a list of type U containing the concatenated sequences of transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T,U>Function<Iterable<T>,List<U>> collect(final Function<? super T,? extends Iterable<U>> f)
    {
        return input -> Functional.collect(f,input);
    }

    /**
     * takeNAndYield: given an input sequence and an integer, return two sequences. The first output sequence returned is a list
     * containing the first 'howMany' elements of the 'input' sequence and the second output sequence contains all the remaining
     * elements of the 'input' sequence. The 'input' sequence is traversed only as far as is required to produce the first list
     * and so the remainder of the 'input' sequence remains unevaluated. If 'howMany' is greater than the number of elements in
     * 'input' then the output list will contain all the elements of the input and the output sequence will be empty.
     * This is like <tt>take</tt> but leaves the user with the ability to continue the traversal of the input sequence from the point
     * at which the 'take' stopped.
     * @param input the input sequence
     * @param howMany the number of elements to be included in the first output list
     * @param <A> the type of the element in the input sequence
     * @return a pair: (list, seq) - the list contains 'howMany' elements of 'input' and the sequence contains the remainder
     */
    public static <A>Pair<List<A>,Iterable<A>> takeNAndYield(final Iterable<A> input, final int howMany)
    {
        if (input == null) throw new IllegalArgumentException("Functional.takeNAndYield: input is null");

        int counter = 0;
        final List<A> output = new ArrayList<>(howMany);
        final Iterator<A> position = input.iterator();
        if(howMany>0&&position.hasNext())
        {
            while(counter<howMany)
            {
                output.add(position.next());
                counter++;
                if (counter < howMany && !position.hasNext()) break;
            }
            return Pair.of(output, (Iterable<A>) () -> position);
        }
        return Pair.of(output, input);
    }

    /**
     * append: given the input sequence and an item, return a new, lazily-evaluated sequence containing the input with the item
     * as the final element.
     * @param t the item to be appended
     * @param input the input sequence
     * @param <T> the type of the element in the input sequence
     * @return a sequence containing all the elements of 'input' followed by 't'
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T>Iterable<T> append(final T t, final Iterable<T> input)
    {
        return () -> new Iterator<T>(){
            private int counter=0;
            private Iterator<? extends T> iterator=input.iterator();

            public boolean hasNext() {
                return counter==0||iterator.hasNext();
            }


            public T next() {
                return counter++==0 ? t : iterator.next();
            }


            public void remove() {
                throw new UnsupportedOperationException("Functional.append(T,Iterable<T>): it is not possible to remove elements from this sequence");
            }
        };
    }

    /**
     * groupBy: similar to {@link #partition(Func_int_T,IntIterable)} in that the input is grouped according to a function. This is more general than
     * <tt>partition</tt> though as the output can be an arbitrary number of groups, up to and including one group per item in the
     * input data set. The 'keyFn' is the grouping operator and it is used to determine the key at which any given element from
     * the input data set should be added to the output dictionary / map.
     * @param keyFn the grouping function. Given an element return the key to be used when storing this element in the dictionary
     * @param input the input sequence
     * @param <T> the type of the element in the input sequence
     * @param <U> the type of the element in the key
     * @return a java.util.Map containing a list of elements for each key
     */
    public static <T,U>Map<U,List<T>> groupBy(final Function<? super T, ? extends U> keyFn, final Iterable<T> input)
    {
        if (keyFn == null) throw new IllegalArgumentException("Functional.groupBy(Func,Iterable): keyFn is null");
        if (input == null) throw new IllegalArgumentException("Functional.groupBy(Func,Iterable): input is null");

        final Map<U,List<T>> intermediateResults = new HashMap<>();
        for(final T element : input)
        {
            final U key = keyFn.apply(element);
            if(intermediateResults.containsKey(key))
                intermediateResults.get(key).add(element);
            else
            {
                final List<T> list = new ArrayList<>();
                list.add(element);
                intermediateResults.put(key, list);
            }
        }
        final Map<U,List<T>> output = new HashMap<>(intermediateResults.size());
        for(final Map.Entry<U,List<T>> entry : intermediateResults.entrySet())
            output.put(entry.getKey(),Collections.unmodifiableList(entry.getValue()));
        return Collections.unmodifiableMap(output);
    }

    /**
     * The Range class holds an inclusive lower bound and an exclusive upper bound. That is lower <= pos < upper
     */
    public static class Range<T>
    {
        private final T lowerBound;
        private final T upperExBound;

        /**
         * Create a new Range object
         * @param lower the inclusive lower bound of the Range
         * @param upperEx the exclusive upper bound of the Range
         */
        public Range(final T lower, final T upperEx)
        {
            this.lowerBound=lower;
            this.upperExBound=upperEx;
        }

        /**
         * Return the inclusive lower bound
         * @return the inclusive lower bound
         */
        public T from(){return lowerBound;}

        /**
         * return the exclusive upper bound
         * @return the exclusive upper bound
         */
        public T to(){return upperExBound;}

        public boolean equals(final Object other)
        {
            if(! (other instanceof Range<?>)) return false;
            final Range<?> otherRange = (Range<?>)other;
            return from().equals(otherRange.from()) && to().equals(otherRange.to());
        }

        public int hashCode()
        {
            return 13 * from().hashCode() + 7 * to().hashCode();
        }
    }

//    /**
//     * This list generator returns a list of Range objects which split the interval [1-'howManyElements') into 'howManyPartitions' Range objects.
//     * If the interval cannot be divided exactly then the remainder is allocated evenly across the first
//     * 'howManyElements' % 'howManyPartitions' Range objects.
//     * @param howManyElements defines the exclusive upper bound of the interval to be split
//     * @param howManyPartitions defines the number of Range objects to generate to cover the interval
//     * @return a list of Range objects
//     */
//    public static List<Range<Integer>> partition(final int howManyElements, final int howManyPartitions)
//    {
//        return partition(Functional.range(0), howManyElements, howManyPartitions);
//    }
//
//    /**
//     * This sequence generator returns a sequence of Range objects which split the interval [1-'howManyElements') into 'howManyPartitions'
//     * Range objects. If the interval cannot be divided exactly then the remainder is allocated evenly across the first
//     * 'howManyElements' % 'howManyPartitions' Range objects.
//     * @param generator a function which generates members of the input sequence
//     * @param howManyElements defines the exclusive upper bound of the interval to be split
//     * @param howManyPartitions defines the number of Range objects to generate to cover the interval
//     * @return a list of Range objects
//     */
//    public static <T>List<Range<T>> partition(final Function<Integer,T> generator, final int howManyElements, final int howManyPartitions)
//    {
//        if(howManyElements<=0) throw new IllegalArgumentException("Functional.partition() howManyElements cannot be non-positive");
//        if(howManyPartitions<=0) throw new IllegalArgumentException("Functional.partition() howManyPartitions cannot be non-positive");
//
//        final int size = howManyElements/howManyPartitions;
//        final int remainder = howManyElements % howManyPartitions;
//
//        assert size*howManyPartitions + remainder == howManyElements;
//
//        final Integer seed = 0;
//        final Function<Integer,Pair<T,Integer>> boundsCalculator = new Function<Integer, Pair<T, Integer>>() {
//
//            public Pair<T, Integer> apply(final Integer integer) {
//                return Pair.of(
//                        generator.apply(1 + (integer * size + (integer <= remainder ? integer : remainder))),
//                        integer+1);
//            }
//        };
//        final Function<Integer,Boolean> finished = new Function<Integer, Boolean>() {
//
//            public Boolean apply(Integer integer) {
//                return integer>howManyPartitions;
//            }
//        };
//
//        final Iterable<T> output = Functional.seq.unfold(boundsCalculator,finished,seed);
//
//        final Iterator<T> iterator = output.iterator();
//        if(iterator==null || !iterator.hasNext()) throw new IllegalStateException("Somehow we have no entries in our sequence of bounds");
//        T last = iterator.next();
//        final List<Range<T>> retval = new ArrayList<Range<T>>(howManyPartitions);
//        for(int i=0;i<howManyPartitions;++i)
//        {
//            if(!iterator.hasNext()) throw new IllegalStateException(String.format("Somehow we have fewer entries (%d) in our sequence of bounds than expected (%d)",i,howManyPartitions));
//            final T next = iterator.next();
//            retval.add(new Range(last, next));
//            last = next;
//        }
//        return retval;
//
////        return Functional.seq.init(new Function<Integer, Range<T>>() {
////
////            public Range<T> apply(final Integer integer) {
////// inefficient - the upper bound is computed twice (once at the end of an iteration and once at the beginning of the next iteration)
////                return new Range<T>( // 1 + the value because the init function expects the control range to start from one.
////                        generator.apply(1 + ((integer - 1) * size + (integer <= remainder + 1 ? integer - 1 : remainder))),
////                        generator.apply(1 + (integer * size + (integer <= remainder ? integer : remainder))));
////            }
////        }, howManyPartitions);
//    }


    public static IntList init(final int constant,final int howMany)
    {
        if(howMany<1) throw new IllegalArgumentException("howMany");

        final int[] ints = new int[howMany];
        Arrays.fill(ints, constant);

        return new IntList(ints);
    }
}
