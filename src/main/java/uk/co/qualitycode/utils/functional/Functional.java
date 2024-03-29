package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Either;
import uk.co.qualitycode.utils.functional.function.ConsumerWithExceptionDeclaration;
import uk.co.qualitycode.utils.functional.function.FunctionWithExceptionDeclaration;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Herein are contained some standard algorithms from functional programming.
 * See <a href="http://en.wikipedia.org/wiki/Functional_programming">Functional Programming</a>
 * for more information
 */
public final class Functional {
    private Functional() {
    }

    public static class ConvertFlatMapVavrOptionToFlatMapOptional {
        /**
         * So you have a flatmap function that returns an Option but you want to stream through java.util? Never fear,
         * functional-utils are here.
         *
         * @param tfm the function that returns an io.vavr.control.Option you want to use
         * @param <T> the input type of the conversion function
         * @param <R> the underlying type of the resultant type of the conversion function
         * @return a function that takes T and returns Optional R
         */
        public static <T, R> Function<T, Optional<R>> convert(final Function<T, io.vavr.control.Option<R>> tfm) {
            notNull(tfm, "convert(Function<T,Option<R>>)", "tfm");
            return t -> Option.of(tfm.apply(t)).toJavaOptional();
        }
    }

    public static class ConvertFlatMapOptionalToFlatMapVavrOption {
        /**
         * So you have a flatmap function that returns an Optional but you want to stream through vavr? Never fear,
         * functional-utils are here.
         *
         * @param tfm the function that returns an Optional you want to use
         * @param <T> the input type of the conversion function
         * @param <R> the underlying type of the resultant type of the conversion function
         * @return a function that takes T and returns io.vavr.control.Option R
         */
        public static <T, R> Function<T, io.vavr.control.Option<R>> convert(final Function<T, Optional<R>> tfm) {
            notNull(tfm, "convert(Function<T,Optional<R>>)", "tfm");
            return t -> Option.of(tfm.apply(t)).toVavrOption();
        }
    }

    public static class ConvertFlatMapOptionalToFlatMapOption {
        /**
         * So you have a flatmap function that returns an Optional but you want to stream through the functions here? Never fear,
         * functional-utils are here.
         *
         * @param tfm the function that returns an Optional you want to use
         * @param <T> the input type of the conversion function
         * @param <R> the underlying type of the resultant type of the conversion function
         * @return a function that takes T and returns Option R
         */
        public static <T, R> Function<T, Option<R>> convert(final Function<T, Optional<R>> tfm) {
            notNull(tfm, "convert(Function<T,Optional<R>>)", "tfm");
            return t -> Option.of(tfm.apply(t));
        }
    }

    public static class ConvertFlatMapVavrOptionToFlatMapOption {
        /**
         * So you have a flatmap function that returns a Vavr Option but you want to stream through the functions here? Never fear,
         * functional-utils are here.
         *
         * @param tfm the function that returns an io.vavr.control.Option you want to use
         * @param <T> the input type of the conversion function
         * @param <R> the underlying type of the resultant type of the conversion function
         * @return a function that takes T and returns Option R
         */
        public static <T, R> Function<T, Option<R>> convert(final Function<T, io.vavr.control.Option<R>> tfm) {
            notNull(tfm, "convert(Function<T,Option<R>>)", "tfm");
            return t -> Option.of(tfm.apply(t));
        }
    }

    /**
     * Concatenate all of the input elements into a single string where each element is separated from the next by the supplied delimiter
     *
     * @param delimiter used to separate consecutive elements in the output, if null it will be replaced with empty string ""
     * @param <T>       the type of the element in the input sequence
     * @return a string containing the string representation of each input element separated by the supplied delimiter
     */
    public static <T> Function<Iterable<T>, String> join(final String delimiter) {
        return strs ->
                isNull(strs)
                        ? ""
                        : asStream(strs).map(T::toString).collect(Collectors.joining(isNull(delimiter) ? "" : delimiter));
    }

    /**
     * Concatenate all of the input elements into a single string where each element is separated from the next by the supplied delimiter
     *
     * @param delimiter used to separate consecutive elements in the output, if null it will be replaced with empty string ""
     * @param strs      input sequence, each element of which must be convertible to a string
     * @param <T>       the type of the element in the input sequence
     * @return a string containing the string representation of each input element separated by the supplied delimiter
     */
    public static <T> String join(final String delimiter, final Iterable<T> strs) {
        return isNull(strs) ? "" : Functional.<T>join(delimiter).apply(strs);
    }

    /**
     * Analogue of string.Join for List<T> with the addition of a user-defined map function
     *
     * @param <T>       the type of the element in the input sequence
     * @param separator inserted between each transformed element
     * @param l         the input sequence
     * @param tfm       map function (see <tt>map</tt>) which is used to transform the input sequence
     * @return a string containing the transformed string value of each input element separated by the supplied separator
     */
    public static <T> String join(final String separator, final Iterable<T> l, final Function<? super T, String> tfm) {
        notNull(tfm, "join(String,Iterable<T>,Function<T,String>)", "tfm");

        return isNull(l) ? "" : join(separator, map(tfm, l));

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
        if (howMany < 0)
            throw new IllegalArgumentException("indentBy(int,String,String): Negative numbers must not be supplied as 'howMany'");
        notNull(unitOfIndentation, "indentBy(int,String,String)", "unitOfIndentation");
        notNull(indentThis, "indentBy(int,String,String)", "indentThis");

        return fold((state, str) -> str + state, indentThis, init(integer -> unitOfIndentation, howMany));
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
     */
    public static <A, B> Tuple2<A, List<B>> foldAndChoose(final BiFunction<A, B, Tuple2<A, Option<B>>> f, final A initialValue, final Iterable<B> input) {
        notNull(f, "foldAndChoose(BiFunction<A,B,Tuple2<A,Option<B>>,A,Iterable<B>)", "f");
        notNull(input, "foldAndChoose(BiFunction<A,B,Tuple2<A,Option<B>>,A,Iterable<B>)", "input");

        final Tuple2<A, List<B>> initial = new Tuple2<>(initialValue, new ArrayList<>());
        return fold((state, b) -> {
                    final Tuple2<A, Option<B>> intermediate = f.apply(state._1, b);
                    if (intermediate._2.isSome()) {
                        state._2.add(intermediate._2.get());
                    }
                    return new Tuple2<>(intermediate._1, state._2);
                },
                initial,
                input);
    }

    /**
     * @param lowerBound the lower bound of the range being checked
     * @param upperBound the upper bound of the range being checked
     * @param val        the value being checked
     * @param <T>        the type of the input element
     * @return lowerBound < val < upperBound
     */
    public static <T extends Comparable<T>> boolean between(final T lowerBound, final T upperBound, final T val) {
        notNull(lowerBound, "between(T,T,T)", "lowerBound");
        notNull(upperBound, "between(T,T,T)", "upperBound");
        if (lowerBound.compareTo(upperBound) > -1)
            throw new IllegalArgumentException("between(T,T,T): lower bound must be less than upper bound");
        notNull(val, "between(T,T,T)", "val");

        return val.compareTo(lowerBound) > 0 && val.compareTo(upperBound) < 0;
    }

    /**
     * @param bounds is a Tuple2 of (lowerBound, upperBound)
     * @param value  the value being tested
     * @param <T>    the type of the input element
     * @return lowerBound < value < upperBound
     */
    public static <T extends Comparable<T>> boolean between(final Tuple2<T, T> bounds, final T value) {
        notNull(bounds, "between(Tuple2<T,T>,T)", "bounds");
        notNull(value, "between(Tuple2<T,T>,T)", "value");
        return between(bounds._1, bounds._2, value);
    }

    /**
     * @param bounds is a Tuple2 of (lowerBound, upperBound)
     * @param <T>    the type of the input element
     * @return lowerBound < val < upperBound
     */
    public static <T extends Comparable<T>> Predicate<T> between(final Tuple2<T, T> bounds) {
        notNull(bounds, "between(Tuple2<T,T>)", "bounds");
        return val -> between(bounds, val);
    }

    /**
     * Find the first element from the input sequence for which the supplied predicate returns true
     * find: (A -> bool) -> A list -> A option
     *
     * @param f     predicate
     * @param input sequence
     * @param <A>   the type of the element in the input sequence
     * @return Option.of(the first element) from the input sequence for which the supplied predicate returns true
     * or Option.none() if no match is found
     * @throws java.lang.IllegalArgumentException if f or input are null
     */
    public static <A> Option<A> find(final Predicate<? super A> f, final Iterable<A> input) {
        notNull(f, "find(Predicate<A>,Iterable<A>)", "f");
        notNull(input, "find(Predicate<A>,Iterable<A>)", "input");

        for (final A a : input) {
            if (f.test((a)))
                return Option.of(a);
        }
        return Option.none();
    }

    /**
     * Curried find.
     * Find the first element from the input sequence for which the supplied predicate returns true
     * find: (A -> bool) -> A list -> A option
     *
     * @param f   predicate
     * @param <A> the type of the element in the input sequence
     * @return Option.of(the first element) from the input sequence for which the supplied predicate returns true
     * or Option.none() if no match is found
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Function<Iterable<A>, Option<A>> find(final Predicate<? super A> f) {
        notNull(f, "find(Predicate<A>)", "f");
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
    public static <A> int findIndex(final Predicate<A> f, final Iterable<? extends A> input) {
        notNull(f, "findIndex(Predicate<A>,Iterable<A>)", "f");
        notNull(input, "findIndex(Predicate<A>,Iterable<A>)", "input");

        int pos = 0;
        for (final A a : input)
            if (f.test(a))
                return pos;
            else pos++;
        throw new IllegalArgumentException();
    }

    /**
     * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
     * findLast: (A -> bool) -> A seq -> A option
     *
     * @param f     predicate
     * @param input sequence
     * @param <A>   the type of the element in the input sequence
     * @return the last element in the input sequence for which the supplied predicate returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     */
    public static <A> Option<A> findLast(final Predicate<A> f, final Iterable<? extends A> input) {
        notNull(f, "findLast(Predicate<A>,Iterable<A>)", "f");
        notNull(input, "findLast(Predicate<A>,Iterable<A>)", "input");

        final Tuple2<? extends List<? extends A>, ? extends Iterable<? extends A>> p = takeNAndYield(1, input);
        final Tuple2<A, Boolean> seed = new Tuple2<>(p._1().get(0), f.test(p._1().get(0)));
        final Tuple2<A, Boolean> result = fold((state, item) -> f.test(item) ? new Tuple2<>(item, true) : state, seed, p._2());

        return result._2() ? Option.of(result._1()) : Option.none();
    }

    /**
     * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
     * findLast: (A -> bool) -> A list -> A option
     *
     * @param f     predicate
     * @param input sequence
     * @param <A>   the type of the element in the input sequence
     * @return the last element in the input sequence for which the supplied predicate returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     */
    public static <A> Option<A> findLast(final Predicate<A> f, final List<? extends A> input) {
        notNull(f, "findLast(Predicate<A>,List<A>)", "f");
        notNull(input, "findLast(Predicate<A>,List<A>)", "input");

        for (final A a : Iterators.reverse(input)) {
            if (f.test(a))
                return Option.of(a);
        }
        return Option.none();
    }

    /**
     * A curried version of findLast.
     * As <tt>find</tt> except that here we return the last element in the input sequence that satisfies the predicate 'f'
     * findLast: (A -> bool) -> A list -> A option
     *
     * @param f   predicate
     * @param <A> the type of the element in the input sequence
     * @return the last element in the input sequence for which the supplied predicate returns true
     * @throws java.lang.IllegalArgumentException if f or input are null
     * @throws java.util.NoSuchElementException   if no element is found that satisfies the predicate
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Function<Iterable<A>, Option<A>> findLast(final Predicate<A> f) {
        notNull(f, "findLast(Predicate<A>)", "f");
        return input -> input instanceof List ? Functional.findLast(f, (List<A>) input) : Functional.findLast(f, input);
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
     * @return the first non-None transformed element of the input sequence
     */
    public static <A, B> Option<B> pick(final Function<? super A, Option<B>> f, final Iterable<A> input) {
        notNull(f, "pick(Function<A,Option<B>>, Iterable<A>)", "f");
        notNull(input, "pick(Function<A,Option<B>>, Iterable<A>)", "input");

        for (final A a : input) {
            final Option<B> intermediate = f.apply(a); // which is, effectively, if(f(a)) return f(a), but without evaluating f twice
            if (intermediate.isSome())
                return intermediate;
        }
        return Option.none();
    }

    /**
     * 'pick' is an analogue of <tt>find</tt>. Instead of a predicate, 'pick' is passed a map function which returns an <tt>Option</tt>.
     * Each element of the input sequence is supplied in turn to the map function 'f' and the first non-None Option to be returned from
     * the map function is returned by 'pick' to the calling code.
     * <p>
     * This is a curried implementation of 'pick'
     * <p>
     * pick: (A -> B option) -> A seq -> B option
     *
     * @param f   the map function.
     * @param <A> the type of the element in the input sequence
     * @param <B> the type of the output element
     * @return the first non-None transformed element of the input sequence
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> Function<Iterable<A>, Option<B>> pick(final Function<? super A, Option<B>> f) {
        notNull(f, "pick(Function<A,Option<B>)", "f");
        return input -> Functional.pick(f, input);
    }

    /**
     * Convolution of functions. That is, apply two transformation functions 'simultaneously' and return a list of pairs,
     * each of which contains one part of the results.
     *
     * @param zipFunc1 the transformation function that generates the first value in the resultant pair
     * @param zipFunc2 the transformation function that generates the second value in the resultant pair
     * @param input    the input list to be transformed
     * @param <A>      a type that all the elements in the input list extend and that both of the transformation functions accept as input
     * @param <B>      the resulting type of the first transformation
     * @param <C>      the resulting type of the second transformation
     * @return a list of pairs containing the two transformed sequences
     */
    public static <A, B, C> List<Tuple2<B, C>> zip(final Function<? super A, B> zipFunc1, final Function<? super A, C> zipFunc2, final Iterable<? extends A> input) {
        notNull(zipFunc1, "zip(Function<A,B>,Function<A,B>,Iterable<A>)", "zipFunc1");
        notNull(zipFunc2, "zip(Function<A,B>,Function<A,B>,Iterable<A>)", "zipFunc2");
        notNull(input, "zip(Function<A,B>,Function<A,B>,Iterable<A>)", "input");
        return Collections.unmodifiableList(StreamSupport.stream(input.spliterator(), false)
                .map(element -> new Tuple2<>(zipFunc1.apply(element), zipFunc2.apply(element)))
                .collect(Collectors.toList()));
    }

    /**
     * Convolution of functions. That is, apply two transformation functions 'simultaneously' and return a list of pairs,
     * each of which contains one part of the results.
     *
     * @param zipFunc1 the transformation function that generates the first value in the resultant pair
     * @param zipFunc2 the transformation function that generates the second value in the resultant pair
     * @param <A>      a type that all the elements in the input list extend and that both of the transformation functions accept as input
     * @param <B>      the resulting type of the first transformation
     * @param <C>      the resulting type of the second transformation
     * @return a list of pairs containing the two transformed sequences
     */
    public static <A, B, C> Function<Iterable<? extends A>, List<Tuple2<B, C>>> zip(final Function<? super A, B> zipFunc1, final Function<? super A, C> zipFunc2) {
        notNull(zipFunc1, "zip(Function<A,B>,Function<A,B>)", "zipFunc1");
        notNull(zipFunc2, "zip(Function<A,B>,Function<A,B>)", "zipFunc2");
        return input -> zip(zipFunc1, zipFunc2, input);
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param input1 input sequence
     * @param input2 input sequence
     * @param <A>    the type of the element in the first input sequence
     * @param <B>    the type of the element in the second input sequence
     * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
     * in order. If the sequences do not have the same number of elements then an exception is thrown.
     * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
     */
    public static <A, B> List<Tuple2<A, B>> zip(final Iterable<? extends A> input1, final Iterable<? extends B> input2) {
        notNull(input1, "zip(Iterable<A>,Iterable<B>)", "input1");
        notNull(input2, "zip(Iterable<A>,Iterable<B>)", "input2");

        final List<Tuple2<A, B>> output = new ArrayList<>();
        final Iterator<? extends A> l1_it = input1.iterator();
        final Iterator<? extends B> l2_it = input2.iterator();

        while (l1_it.hasNext() && l2_it.hasNext()) output.add(new Tuple2<>(l1_it.next(), l2_it.next()));
        if (l1_it.hasNext() || l2_it.hasNext())
            throw new IllegalArgumentException("zip(Iterable<A>,Iterable<B>): Cannot zip two iterables with different lengths");

        return Collections.unmodifiableList(output);
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param input1 input sequence
     * @param input2 input sequence
     * @param <A>    the type of the element in the first input sequence
     * @param <B>    the type of the element in the second input sequence
     * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
     * in order. If the sequences do not have the same number of elements then an exception is thrown.
     * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
     */
    public static <A, B> List<Tuple2<A, B>> zip(final Collection<? extends A> input1, final Collection<? extends B> input2) {
        notNull(input1, "zip(Collection<A>,Collection<B>)", "input1");
        notNull(input2, "zip(Collection<A>,Collection<B>)", "input2");
        if (input1.size() != input2.size()) {
            throw new IllegalArgumentException("zip(Collection<A>,Collection<B>): The input sequences must have the same number of elements");
        }

        final Iterator<? extends A> l1_it = input1.iterator();
        final Iterator<? extends B> l2_it = input2.iterator();

        final List<Tuple2<A, B>> output = new ArrayList<>(input1.size());
        while (l1_it.hasNext() && l2_it.hasNext()) output.add(new Tuple2<>(l1_it.next(), l2_it.next()));
        if (l1_it.hasNext() || l2_it.hasNext())
            throw new IllegalArgumentException("zip(Collection<A>,Collection<B>): The input sequences must have the same number of elements");

        return Collections.unmodifiableList(output);
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param input1 input sequence
     * @param input2 input sequence
     * @param input3 input sequence
     * @param <A>    the type of the element in the first input sequence
     * @param <B>    the type of the element in the second input sequence
     * @param <C>    the type of the element in the third input sequence
     * @return list of triplets; the first element from each of the input sequences is the first triplet in the output sequence and so on,
     * in order. If the sequences do not have the same number of elements then an exception is thrown.
     * @throws java.lang.IllegalArgumentException if any input sequence is null or if the sequences have differing lengths.
     */
    public static <A, B, C> List<Tuple3<A, B, C>> zip3(final Iterable<? extends A> input1, final Iterable<? extends B> input2, final Iterable<? extends C> input3) {
        notNull(input1, "zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input1");
        notNull(input2, "zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input2");
        notNull(input3, "zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input3");

        final List<Tuple3<A, B, C>> output;
        if (input1 instanceof Collection<?> && input2 instanceof Collection<?> && input3 instanceof Collection<?>) {
            if (((Collection<?>) input1).size() != ((Collection<?>) input2).size())
                throw new IllegalArgumentException("zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths");

            output = new ArrayList<>(((Collection<?>) input1).size());
        } else output = new ArrayList<>();
        final Iterator<? extends A> l1_it = input1.iterator();
        final Iterator<? extends B> l2_it = input2.iterator();
        final Iterator<? extends C> l3_it = input3.iterator();

        while (l1_it.hasNext() && l2_it.hasNext() && l3_it.hasNext())
            output.add(new Tuple3<>(l1_it.next(), l2_it.next(), l3_it.next()));
        if (l1_it.hasNext() || l2_it.hasNext() || l3_it.hasNext())
            throw new IllegalArgumentException("zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths");

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
    public static <A, B> Tuple2<List<A>, List<B>> unzip(final Iterable<Tuple2<A, B>> input) {
        notNull(input, "unzip(Iterable<Tuple2<A,B>>)", "input");

        final List<A> l1;
        final List<B> l2;
        if (input instanceof Collection<?>) {
            final int size = ((Collection<?>) input).size();
            l1 = new ArrayList<>(size);
            l2 = new ArrayList<>(size);
        } else {
            l1 = new ArrayList<>();
            l2 = new ArrayList<>();
        }
        for (final Tuple2<A, B> pair : input) {
            l1.add(pair._1());
            l2.add(pair._2());
        }

        return new Tuple2<>(Collections.unmodifiableList(l1), Collections.unmodifiableList(l2));
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
    public static <A, B, C> Tuple3<List<A>, List<B>, List<C>> unzip3(final Iterable<Tuple3<A, B, C>> input) {
        notNull(input, "unzip3(Iterable<Tuple3<A,B,C>>)", "input");

        final List<A> l1;
        final List<B> l2;
        final List<C> l3;
        if (input instanceof Collection<?>) {
            final int size = ((Collection<?>) input).size();
            l1 = new ArrayList<>(size);
            l2 = new ArrayList<>(size);
            l3 = new ArrayList<>(size);
        } else {
            l1 = new ArrayList<>();
            l2 = new ArrayList<>();
            l3 = new ArrayList<>();
        }

        for (final Tuple3<A, B, C> triplet : input) {
            l1.add(triplet._1());
            l2.add(triplet._2());
            l3.add(triplet._3());
        }

        return new Tuple3<>(Collections.unmodifiableList(l1), Collections.unmodifiableList(l2), Collections.unmodifiableList(l3));
    }

    /**
     * <tt>isEven</tt> a function that accepts an integer and returns a boolean that indicates whether the passed integer
     * is or is not an even integer
     */
    public static boolean isEven(final int i) {
        return i % 2 == 0;
    }

    /**
     * <tt>isOdd</tt> a function that accepts an integer and returns a boolean that indicates whether the passed integer
     * is or is not an odd integer
     */
    public static boolean isOdd(final int i) {
        return i % 2 != 0;
    }

    /**
     * <tt>count</tt> a function that accepts a counter and another integer and returns 1 + counter
     */
    @SuppressWarnings("unused")
    public static int count(final int state, final int b) {
        return state + 1;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is greater than
     * 'that' or false otherwise
     */
    public static <T extends Comparable<T>> Predicate<T> greaterThan(final T that) {
        return ths -> ths.compareTo(that) > 0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is greater than
     * or equal to 'that' or false otherwise
     */
    public static <T extends Comparable<T>> Predicate<T> greaterThanOrEqual(final T that) {
        return ths -> ths.compareTo(that) >= 0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is less than
     * 'that' or false otherwise
     */
    public static <T extends Comparable<T>> Predicate<T> lessThan(final T that) {
        return ths -> ths.compareTo(that) < 0;
    }

    /**
     * @param <T> the type of <tt>that</tt>, the input argument
     * @return a function that compares its supplied argument with the 'that' argument and returns true if 'this' is less than
     * or equal to 'that' or false otherwise
     */
    public static <T extends Comparable<T>> Predicate<T> lessThanOrEqual(final T that) {
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
        notNull(f, "init(Function<Integer,T>,int)", "f");
        if (howMany < 0)
            throw new IllegalArgumentException("init(Function<Integer,T>,int): howMany must be non-negative");

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
        notNull(f, "map(Function<A,B>,Iterable<A>)", "f");
        notNull(input, "map(Function<A,B>,Iterable<A>)", "input");
        return Collections.unmodifiableList(StreamSupport.stream(input.spliterator(), false).map(f).collect(Collectors.toList()));
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
    public static <A, B> List<B> map(final Function<A, ? extends B> f, final Collection<? extends A> input) {
        notNull(f, "map(Function<A,B>,Collection<A>)", "f");
        notNull(input, "map(Function<A,B>,Collection<A>)", "input");
        return Collections.unmodifiableList(input.stream().map(f).collect(Collectors.toList()));
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
    public static <A, B> Function<Iterable<A>, Iterable<B>> map(final Function<? super A, ? extends B> f) {
        notNull(f, "map(Function<A,B>)", "f");
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
        notNull(f, "mapi(BiFunction<Integer,A,B>,Iterable<A>)", "f");
        notNull(input, "mapi(BiFunction<Integer,A,B>,Iterable<A>)", "input");
        final List<B> output = new ArrayList<>();
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
     * @param f     a transformation function which is passed each input object of type A along with its position in the input sequence
     *              (starting from zero) and returns an object, presumably related, of type B
     * @param input a sequence to be fed into f
     * @param <A>   the type of the element in the input sequence
     * @param <B>   the type of the element in the output sequence
     * @return a list of type B containing the transformed values.
     */
    public static <A, B> List<B> mapi(final BiFunction<Integer, A, ? extends B> f, final Collection<? extends A> input) {
        notNull(f, "mapi(BiFunction<Integer,A,B>,Collection<A>)", "f");
        notNull(input, "mapi(BiFunction<Integer,A,B>,Collection<A>)", "input");
        final List<B> output = new ArrayList<>(input.size());
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
        notNull(f, "mapi(BiFunction<Integer,A,B>)", "f");
        return input -> Functional.mapi(f, input);
    }

    /// <summary> sortWith: (A -> A -> int) -> A list -> A list</summary>

    /**
     * sortWith: a wrapper for <tt>Collections.sort</tt> which preserves the input sequence.
     *
     * @param comparator the <tt>Comparator</tt> to use for the sort
     * @param input      the input
     * @param <A>        the type of the <tt>Comparator</tt>
     * @param <AA>       the type of the element in the input sequence
     * @return a sorted list containing all the elements of 'input' sorted using <tt>Collections.sort</tt> and 'comparator'
     */
    public static <A, AA extends A> List<AA> sortWith(final Comparator<A> comparator, final Collection<AA> input) {
        notNull(comparator, "sortWith(Comparator<A>,Collection<B>)", "comparator");
        notNull(input, "sortWith(Comparator<A>,Collection<B>)", "input");
        return Collections.unmodifiableList(input.stream().sorted(comparator).collect(Collectors.toList()));
    }

    /**
     * sortWith: a wrapper for <tt>Collections.sort</tt> which preserves the input sequence.
     *
     * @param comparator the <tt>Comparator</tt> to use for the sort
     * @param input      the input
     * @param <A>        the type of the <tt>Comparator</tt>
     * @param <AA>       the type of the element in the input sequence
     * @return a sorted list containing all the elements of 'input' sorted using <tt>Collections.sort</tt> and 'comparator'
     */
    public static <A, AA extends A> List<AA> sortWith(final Comparator<A> comparator, final Iterable<AA> input) {
        notNull(comparator, "sortWith(Comparator<A>,Iterable<B>)", "comparator");
        notNull(input, "sortWith(Comparator<A>,Iterable<B>)", "input");
        return Collections.unmodifiableList(StreamSupport.stream(input.spliterator(), false).sorted(comparator).collect(Collectors.toList()));
    }

    /**
     * A simple function which wraps left.compareTo(right) so that this can be used as a sort function.
     *
     * @param left  input element
     * @param right input element
     * @param <A>   the type of the elements to be compared
     * @return left.compareTo(right)
     */
    public static <A extends Comparable<A>> int sorter(final A left, final A right) {
        return left.compareTo(right);
    }

    /**
     * A Comparator that encapsulates {link=sorter} above
     */
    public static Comparator<Integer> sorter = Functional::sorter;

    /**
     * A wrapper around <tt>toString()</tt>
     *
     * @param a   the element to be turned into a string using T.toString()
     * @param <T> the type of element 'a'
     * @return a.toString()
     */
    public static <T> String stringify(final T a) {
        return a.toString();
    }

    /**
     * A transformation function that wraps <tt>Stringify</tt>
     *
     * @param <T> the type of the element which we will render as a String
     * @return a function that calls <tt>Stringify</tt>
     */
    public static <T> Function<T, String> stringify() {
        return Functional::stringify;
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
     *                  the input element is passed through to the output otherwise it is ignored.
     * @param input     a sequence of objects
     * @return a list which contains zero or more of the elements of the input sequence. Each element is included only if the filter
     * function returns true for the element.
     */
    public static <A> List<A> filter(final Predicate<? super A> predicate, final Iterable<A> input) {
        notNull(predicate, "filter(Predicate<A>,Iterable<A>)", "predicate");
        notNull(input, "filter(Predicate<A>,Iterable<A>)", "input");
        return Collections.unmodifiableList(StreamSupport.stream(input.spliterator(), false).filter(predicate).collect(Collectors.toList()));
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
     *                  the input element is passed through to the output otherwise it is ignored.
     * @param input     a sequence of objects
     * @return a list which contains zero or more of the elements of the input sequence. Each element is included only if the filter
     * function returns true for the element.
     */
    public static <A> List<A> filter(final Predicate<? super A> predicate, final Collection<A> input) {
        notNull(predicate, "filter(Predicate<A>,Collection<A>)", "predicate");
        notNull(input, "filter(Predicate<A>,Collection<A>)", "input");
        return Collections.unmodifiableList(input.stream().filter(predicate).collect(Collectors.toList()));
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
     *                  the input element is passed through to the output otherwise it is ignored.
     * @return a curried function that expects an input sequence which it feeds to the filter predicate which then returns
     * a list which contains zero or more of the elements of the input sequence. Each element is included only if the filter
     * function returns true for the element.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T> Function<Iterable<T>, List<T>> filter(final Predicate<? super T> predicate) {
        notNull(predicate, "filter(Predicate<A>)", "predicate");
        return input -> input instanceof Collection<?> ? Functional.filter(predicate, (Collection<T>) input) : Functional.filter(predicate, input);
    }

    /**
     * The converse operation to <tt>forAll</tt>. If the predicate returns true then 'exists' returns true and halts the traversal of the
     * input sequence. Otherwise return false.
     * exists: (A -> bool) -> A list -> bool
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate predicate
     * @param input     input sequence
     * @return true if the predicate returns true for any element in the input sequence, false otherwise
     */
    public static <A> boolean exists(final Predicate<? super A> predicate, final Iterable<A> input) {
        notNull(predicate, "exists(Predicate<T>,Iterable<T>)", "predicate");
        notNull(input, "exists(Predicate<T>,Iterable<T>)", "input");
        return StreamSupport.stream(input.spliterator(), false).anyMatch(predicate);
    }

    /**
     * The converse operation to <tt>forAll</tt>. If the predicate returns true then 'exists' returns true and halts the traversal of the
     * input sequence. Otherwise return false.
     * exists: (A -> bool) -> A list -> bool
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate predicate
     * @param input     input sequence
     * @return true if the predicate returns true for any element in the input sequence, false otherwise
     */
    public static <A> boolean exists(final Predicate<? super A> predicate, final Collection<A> input) {
        notNull(predicate, "exists(Predicate<T>,Collection<T>)", "predicate");
        notNull(input, "exists(Predicate<T>,Collection<T>)", "input");
        return input.stream().anyMatch(predicate);
    }

    /**
     * The converse operation to <tt>forAll</tt>. If the predicate returns true then 'exists' returns true and halts the traversal of the
     * input sequence. Otherwise return false.
     * exists: (A -> bool) -> A list -> bool
     * This is the curried implementation.
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate predicate
     * @return true if the predicate returns true for any element in the input sequence, false otherwise
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Predicate<Iterable<A>> exists(final Predicate<? super A> predicate) {
        notNull(predicate, "exists(Predicate<T>)", "predicate");
        return input -> Functional.exists(predicate, input);
    }

    /**
     * not reverses the result of the applied predicate
     * not: (A -> bool) -> (A -> bool)
     *
     * @param <A>       the type of the input to the function <tt>predicate</tt>
     * @param predicate the applied predicate
     * @return true if predicate returns false, false if predicate returns true
     */
    public static <A> Predicate<A> not(final Predicate<A> predicate) {
        notNull(predicate, "not(Predicate<A>)", "predicate");
        return a -> !predicate.test(a);
    }

    /**
     * not2 reverses the result of the applied predicate
     * not2: (A -> B -> bool) -> (A -> B -> bool)
     *
     * @param <A>       the type of the first input to the function <tt>predicate</tt>
     * @param <B>       the type of the second input to the function <tt>predicate</tt>
     * @param predicate the applied predicate
     * @return true if predicate returns false, false if predicate returns true
     */
    public static <A, B> BiPredicate<A, B> not2(final BiPredicate<A, B> predicate) {
        notNull(predicate, "not2(BiPredicate<A,B>)", "predicate");
        return (a, b) -> !predicate.test(a, b);
    }

    /**
     * The converse operation to <tt>exists</tt>. If the predicate returns true for all elements in the input sequence then 'forAll'
     * returns true otherwise return false.
     * forAll: (A -> bool) -> A list -> bool
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate predicate
     * @param input     input sequence
     * @return true if the predicate returns true for all elements in the input sequence, false otherwise
     */
    public static <A> boolean forAll(final Predicate<A> predicate, final Iterable<? extends A> input) {
        notNull(predicate, "forAll(Predicate<A>,Iterable<A>)", "predicate");
        notNull(input, "forAll(Predicate<A>,Iterable<A>)", "input");
        return !exists(not(predicate), input);
    }

    /**
     * The converse operation to <tt>exists</tt>. If the predicate returns true for all elements in the input sequence then 'forAll'
     * returns true otherwise return false.
     * forAll: (A -> bool) -> A list -> bool
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate predicate
     * @param input     input sequence
     * @return true if the predicate returns true for all elements in the input sequence, false otherwise
     */
    public static <A> boolean forAll(final Predicate<A> predicate, final Collection<? extends A> input) {
        notNull(predicate, "forAll(Predicate<A>,Collection<A>)", "predicate");
        notNull(input, "forAll(Predicate<A>,Collection<A>)", "input");
        return !exists(not(predicate), input);
    }

    /**
     * The converse operation to <tt>exists</tt>. If the predicate returns true for all elements in the input sequence then 'forAll'
     * returns true otherwise return false.
     * forAll: (A -> bool) -> A list -> bool
     * This is a curried implementation of 'forAll
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate predicate
     * @return true if the predicate returns true for all elements in the input sequence, false otherwise
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Predicate<Iterable<A>> forAll(final Predicate<? super A> predicate) {
        notNull(predicate, "forAll(Predicate<A>)", "predicate");
        return input -> Functional.forAll(predicate, input);
    }

    /**
     * forAll2: the predicate 'predicate' is applied to all elements in the input sequences input1 and input2 as pairs. If the predicate returns
     * true for all pairs and there is the same number of elements in both input sequences then forAll2 returns true. If the predicate
     * returns false at any point then the traversal of the input sequences halts and forAll2 returns false.
     * forAll2: (A -> B -> bool) -> A list -> B list -> bool
     *
     * @param <A>       the base type of the element in the first input sequence
     * @param <B>       the base type of the element in the second input sequence
     * @param <AA>      the type of the element in the first input sequence
     * @param <BB>      the type of the element in the second input sequence
     * @param predicate predicate to which each successive pair (input1_i, input2_i) is applied
     * @param input1    input sequence
     * @param input2    input sequence
     * @return true if the predicate 'predicate' evaluates true for all pairs, false otherwise
     * @throws java.lang.IllegalArgumentException if the predicate returns true for all pairs and the sequences contain differing numbers
     *                                            of elements
     */
    public static <A, B, AA extends A, BB extends B> boolean forAll2(final BiPredicate<A, B> predicate, final Iterable<AA> input1, final Iterable<BB> input2) {
        notNull(predicate, "forAll2(BiPredicate<A,B>,Iterable<A>,Iterable<B>)", "predicate");
        notNull(input1, "forAll2(BiPredicate<A,B>,Iterable<A>,Iterable<B>)", "input1");
        notNull(input2, "forAll2(BiPredicate<A,B>,Iterable<A>,Iterable<B>)", "input2");

        try {
            return StreamSupport.stream(Lazy.zip(input1, input2).spliterator(), false).allMatch(t -> predicate.test(t._1, t._2));
        } catch (final IllegalArgumentException e) {
            if (e.getMessage().contains("Lazy.zip(Iterable<A>,Iterable<B>): cannot zip two iterables with different lengths"))
                throw new IllegalArgumentException("forAll2(BiPredicate<A,B>,Iterable<A>,Iterable<B>): Cannot compare two sequences with different numbers of elements");
            throw e;
        }
    }

    /// <summary> </summary>
    /// <returns> (list * list). The first list contains all items for which f(a) is true. The second list contains the remainder.</returns>

    /**
     * partition is a group function. Given a predicate and an input sequence, 'partition' returns a pair of lists, the first list
     * containing those elements from the input sequence for which the predicate returned true, the second list containing those
     * elements from the input sequence for which the predicate returned false.
     * partition: (A -> bool) -> A list -> A list * A list
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate predicate used to split the input sequence into two groups
     * @param input     the input sequence
     * @return a pair of lists, the first being the 'true' and the second being the 'false'
     */
    public static <A> Tuple2<List<A>, List<A>> partition(final Predicate<? super A> predicate, final Iterable<A> input) {
        notNull(predicate, "partition(Predicate<A>,Iterable<A>)", "predicate");
        notNull(input, "partition(Predicate<A>,Iterable<A>)", "input");
        final List<A> left;
        final List<A> right;
        if (input instanceof Collection<?>) {
            left = new ArrayList<>(((Collection<?>) input).size());
            right = new ArrayList<>(((Collection<?>) input).size());
        } else {
            left = new ArrayList<>();
            right = new ArrayList<>();
        }
        for (final A a : input)
            if (predicate.test(a))
                left.add(a);
            else
                right.add(a);
        return new Tuple2<>(Collections.unmodifiableList(left), Collections.unmodifiableList(right));
    }

    /**
     * partition is a group function. Given a predicate and an input sequence, 'partition' returns a pair of lists, the first list
     * containing those elements from the input sequence for which the predicate returned true, the second list containing those
     * elements from the input sequence for which the predicate returned false.
     * partition: (A -> bool) -> A list -> A list * A list
     * This is a curried implementation of 'forAll
     *
     * @param <A>       the type of the element in the input sequence
     * @param predicate predicate used to split the input sequence into two groups
     * @return a pair of lists, the first being the 'true' and the second being the 'false'
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A> Function<Iterable<A>, Tuple2<List<A>, List<A>>> partition(final Predicate<? super A> predicate) {
        notNull(predicate, "partition(Predicate<A>)", "predicate");
        return input -> Functional.partition(predicate, input);
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
        if (howManyElements <= 0)
            throw new IllegalArgumentException("partition(int,int): howManyElements must be positive");
        if (howManyPartitions <= 0)
            throw new IllegalArgumentException("partition(int,int): howManyPartitions must be positive");
        return partition(Functional.range(1), howManyElements, howManyPartitions);
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
        notNull(generator, "partition(Function<Integer,A>,int,int)", "generator");
        if (howManyElements <= 0)
            throw new IllegalArgumentException("partition(Function<Integer,A>,int,int): howManyElements must be positive");
        if (howManyPartitions <= 0)
            throw new IllegalArgumentException("partition(Function<Integer,A>,int,int): howManyPartitions must be positive");

        final int size = howManyElements / howManyPartitions;
        final int remainder = howManyElements % howManyPartitions;

        assert size * howManyPartitions + remainder == howManyElements;

        final Integer seed = 0;
        final Function<Integer, Tuple2<T, Integer>> boundsCalculator = integer -> new Tuple2<>(
                generator.apply(1 + (integer * size + (integer <= remainder ? integer : remainder))),
                integer + 1);
        final Predicate<Integer> finished = integer -> integer > howManyPartitions;

        final Iterable<T> output = Lazy.unfold(boundsCalculator, finished, seed);

        final Iterator<T> iterator = output.iterator();
        if (!iterator.hasNext())
            throw new IllegalStateException("Somehow we have no entries in our sequence of bounds");
        T last = iterator.next();
        final List<Range<T>> retval = new ArrayList<>(howManyPartitions);
        for (int i = 0; i < howManyPartitions; ++i) {
            if (!iterator.hasNext())
                throw new IllegalStateException(String.format("Somehow we have fewer entries (%d) in our sequence of bounds than expected (%d)", i, howManyPartitions));
            final T next = iterator.next();
            retval.add(new Range<>(last, next));
            last = next;
        }
        return retval;

//        return Functional.Lazy.init(new Function<Integer, Range<T>>() {
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

        @Override
        public String toString() {
            return "Range{" +
                    "lowerBound=" + lowerBound +
                    ", upperExBound=" + upperExBound +
                    '}';
        }
    }

    private static <A, B> List<B> chooseReducer(final Function<? super A, Option<B>> f, final Stream<A> input) {
        return input.reduce(new ArrayList<>(), (state, a) -> {
            final Option<B> intermediate = f.apply(a);
            if (intermediate.isSome())
                state.add(intermediate.get());
            return state;
        }, (t1, t2) -> {
            t1.addAll(t2);
            return t1;
        });
    }

    /**
     * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     *
     * @param <A>     the type of the element in the input sequence
     * @param <B>     the type of the element in the output sequence
     * @param chooser map function. This transforms the input element into an Option
     * @param input   input sequence
     * @return a list of transformed elements, numbering less than or equal to the number of input elements
     */
    public static <A, B> List<B> choose(final Function<? super A, Option<B>> chooser, final Iterable<A> input) {
        notNull(chooser, "choose(Function<A,Option<B>>,Iterable<A>)", "chooser");
        notNull(input, "choose(Function<A,Option<B>>,Iterable<A>)", "input");
        return Collections.unmodifiableList(chooseReducer(chooser, StreamSupport.stream(input.spliterator(), false)));
    }

    /**
     * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     *
     * @param <A>     the type of the element in the input sequence
     * @param <B>     the type of the element in the output sequence
     * @param chooser map function. This transforms the input element into an Option
     * @param input   input sequence
     * @return a list of transformed elements, numbering less than or equal to the number of input elements
     */
    public static <A, B> List<B> choose(final Function<? super A, Option<B>> chooser, final Collection<A> input) {
        notNull(chooser, "choose(Function<A,Option<B>>,Collection<A>)", "chooser");
        notNull(input, "choose(Function<A,Option<B>>,Collection<A>)", "input");

        return Collections.unmodifiableList(chooseReducer(chooser, input.stream()));
    }

    /**
     * choose: this is a curried implementation of choose.
     * choose is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     *
     * @param <A>     the type of the element in the input sequence
     * @param <B>     the type of the element in the output sequence
     * @param chooser map function. This transforms the input element into an Option
     * @return a list of transformed elements, numbering less than or equal to the number of input elements
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> Function<Iterable<A>, List<B>> choose(final Function<? super A, Option<B>> chooser) {
        notNull(chooser, "choose(Function<A,Option<B>>)", "chooser");
        return input -> Functional.choose(chooser, input);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
     * fold: aggregate the elements of the input sequence given a seed and an aggregation function.
     * fold: (A -> B -> A) -> A -> B list -> A
     *
     * @param <A>          the type of the initialValue / seed
     * @param <B>          the type of the element in the input sequence
     * @param folder       aggregation function
     * @param initialValue seed for the algorithm
     * @param input        input sequence
     * @return aggregated value
     */
    public static <A, B> A fold(final BiFunction<? super A, ? super B, ? extends A> folder, final A initialValue, final Iterable<B> input) {
        notNull(folder, "fold(BiFunction<A,B,A>,A,Iterable<B>)", "folder");
        notNull(input, "fold(BiFunction<A,B,A>,A,Iterable<B>)", "input");
        A state = initialValue;
        for (final B b : input)
            state = folder.apply(state, b);
        return state;
    }

    // The JDK reduce() doesn't seem to be quite as broadly applicable because it accepts "A" and not "? super A"
//    public static <A, B> A fold(final BiFunction<A, ? super B, A> f, final A initialValue, final Collection<B> input) {
//        return input.stream().reduce(initialValue, f, (a,b)->a);
//    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
     * fold: aggregate the elements of the input sequence given a seed and an aggregation function.
     * This is the curried implementation
     * fold: (A -> B -> A) -> A -> B list -> A
     *
     * @param <A>          the type of the initialValue / seed
     * @param <B>          the type of the element in the output sequence
     * @param folder       aggregation function
     * @param initialValue seed for the algorithm
     * @return aggregated value
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> Function<Iterable<B>, A> fold(final BiFunction<? super A, ? super B, ? extends A> folder, final A initialValue) {
        notNull(folder, "fold(BiFunction<A,B,A>,A)", "folder");
        return input -> Functional.fold(folder, initialValue, input);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
     * fold: aggregate the elements of the input sequence given a seed and an aggregation function.
     * This is the curried implementation
     * fold: (A -> B -> A) -> A -> B list -> A
     *
     * @param <A>    the type of the initialValue / seed
     * @param <B>    the type of the element in the output sequence
     * @param folder aggregation function
     * @return aggregated value
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <A, B> WithInitialValue<A, Function<Iterable<B>, A>> fold(final BiFunction<? super A, ? super B, ? extends A> folder) {
        notNull(folder, "fold(BiFunction<A,B,A>)", "folder");
        return initialValue -> input -> Functional.fold(folder, initialValue, input);
    }

    public interface WithInitialValue<A, F extends Function<?, A>> {
        F withInitialValue(final A initialValue);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
     * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> List<A> unfold(final Function<? super B, Tuple2<A, B>> unspooler, final Predicate<? super B> finished, final B seed) {
        notNull(unspooler, "unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)", "unspooler");
        notNull(finished, "unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)", "finished");

        B next = seed;
        final List<A> results = new ArrayList<>();
        while (!finished.test(next)) {
            final Tuple2<A, B> t = unspooler.apply(next);
            results.add(t._1());
            next = t._2();
        }
        return results;
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
     * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> WithSeed<A, B> unfold(final Function<? super B, Tuple2<A, B>> unspooler, final Predicate<? super B> finished) {
        notNull(unspooler, "unfold(Function<B,Tuple2<A,B>>,Predicate<B>)", "unspooler");
        notNull(finished, "unfold(Function<B,Tuple2<A,B>>,Predicate<B>)", "finished");

        return seed -> Functional.unfold(unspooler, finished, seed);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
     * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> WithFinished<A, B> unfold(final Function<? super B, Tuple2<A, B>> unspooler) {
        notNull(unspooler, "unfold(Function<B,Tuple2<A,B>>)", "unspooler");
        return finished -> seed -> Functional.unfold(unspooler, finished, seed);
    }

    public interface WithSeed<A, B> {
        List<A> withSeed(B seed);
    }

    public interface WithFinished<A, B> {
        WithSeed<A, B> withFinished(Predicate<? super B> finished);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
     * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> List<A> unfold(final Function<? super B, Option<Tuple2<A, B>>> unspooler, final B seed) {
        notNull(unspooler, "unfold(Function<B,Option<Tuple2<A,B>>>,B)", "unspooler");

        B next = seed;
        final List<A> results = new ArrayList<>();
        while (true) {
            final Option<Tuple2<A, B>> t = unspooler.apply(next);
            if (t.isNone()) break;
            results.add(t.get()._1());
            next = t.get()._2();
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
        notNull(keyFn, "toDictionary(Function<T,K>,Function<T,V>,Iterable<T>)", "keyFn");
        notNull(valueFn, "toDictionary(Function<T,K>,Function<T,V>,Iterable<T>)", "valueFn");
        notNull(input, "toDictionary(Function<T,K>,Function<T,V>,Iterable<T>)", "input");

        return Collections.unmodifiableMap(StreamSupport.stream(input.spliterator(), false).collect(Collectors.toMap(keyFn, valueFn)));
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
    public static <T, K, V> Map<K, V> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn, final Collection<T> input) {
        notNull(keyFn, "toDictionary(Function<T,K>,Function<T,V>,Collection<T>)", "keyFn");
        notNull(valueFn, "toDictionary(Function<T,K>,Function<T,V>,Collection<T>)", "valueFn");
        notNull(input, "toDictionary(Function<T,K>,Function<T,V>,Collection<T>)", "input");

        return Collections.unmodifiableMap(input.stream().collect(Collectors.toMap(keyFn, valueFn)));
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
     * @return a java.util.Map containing the transformed input sequence
     * @throws IllegalArgumentException if some property of the specified key
     *                                  or value prevents it from being stored in this map
     */
    public static <T, K, V> Function<Iterable<T>, Map<K, V>> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn) {
        notNull(keyFn, "toDictionary(Function<T,K>,Function<T,V>)", "keyFn");
        notNull(valueFn, "toDictionary(Function<T,K>,Function<T,V>)", "valueFn");

        return input -> toDictionary(keyFn, valueFn, input);
    }

    /**
     * toDictionary: given each element from the input sequence apply the keyFn and valueFn to generate a (key,value) pair.
     * The resulting dictionary (java.util.Map) contains all these pairs.
     *
     * @param <T> the type of the element in the input sequence
     * @param <K> the type of the key elements
     * @param <V> the type of the value elements
     * @return a java.util.Map containing the transformed input sequence
     * @throws IllegalArgumentException if some property of the specified key
     *                                  or value prevents it from being stored in this map
     */
    public static <T, K, V> WithKeyFn<T, K, V> toDictionary(final Iterable<T> input) {
        notNull(input, "toDictionary(Iterable<T>)", "input");
        return keyFn -> valueFn -> toDictionary(keyFn, valueFn, input);
    }


    /**
     * toDictionary: given each element from the input sequence apply the keyFn and valueFn to generate a (key,value) pair.
     * The resulting dictionary (java.util.Map) contains all these pairs.
     *
     * @param <T> the type of the element in the input sequence
     * @param <K> the type of the key elements
     * @param <V> the type of the value elements
     * @return a java.util.Map containing the transformed input sequence
     * @throws IllegalArgumentException if some property of the specified key
     *                                  or value prevents it from being stored in this map
     */
    public static <T, K, V> WithKeyFn<T, K, V> toDictionary(final Collection<T> input) {
        notNull(input, "toDictionary(Collection<T>)", "input");
        return keyFn -> valueFn -> toDictionary(keyFn, valueFn, input);
    }

    public interface WithKeyFn<T, K, V> {
        WithValueFn<T, K, V> withKeyFn(Function<? super T, ? extends K> keyFn);
    }

    public interface WithValueFn<T, K, V> {
        Map<K, V> withValueFn(Function<? super T, ? extends V> valueFn);
    }

    public static <K, V> Map<K, V> toMutableDictionary(final Map<K, V> input) {
        notNull(input, "toMutableDictionary(Map<K,V>)", "input");

        return io.vavr.collection.HashMap.ofAll(input).toJavaMap();
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
        notNull(input, "last(Iterable<T>)", "input");

        T state = null;
        for (final T element : input) state = element;

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
        notNull(input, "last(T[])", "input");
        if (input.length == 0)
            throw new IllegalArgumentException("last(T[]): input must not be empty");

        return input[input.length - 1];
    }

    /**
     * Concatenate two sequences and return a new list containing the concatenation.
     *
     * @param input1 first input sequence
     * @param input2 second input sequence
     * @param <T>    the type of the element in the input sequences
     * @return a list containing the elements of the first sequence followed by the elements of the second sequence
     */
    public static <T> List<T> concat(final Iterable<T> input1, final Iterable<T> input2) {
        notNull(input1, "concat(Iterable<T>,Iterable<T>)", "input1");
        notNull(input2, "concat(Iterable<T>,Iterable<T>)", "input2");

        return io.vavr.collection.List.ofAll(input1).appendAll(input2).toJavaList();
    }

    /**
     * take: given a list return another list containing the first 'howMany' elements
     *
     * @param howMany a positive number of elements to be returned from the input sequence
     * @param input   the input sequence
     * @param <T>     the type of the element in the input sequence
     * @return a list containing the first 'howMany' elements of 'input'
     * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
     */
    public static <T> List<T> take(final int howMany, final Iterable<? extends T> input) {
        if (howMany < 0) throw new IllegalArgumentException("take(int,Iterable<T>): howMany must not be negative");
        notNull(input, "take(int,Iterable<T>)", "input");

        if (howMany == 0) return new ArrayList<>(0);

        return StreamSupport.stream(input.spliterator(), false).limit(howMany).collect(Collectors.toList());
    }

    /**
     * take: given a list return another list containing the first 'howMany' elements
     *
     * @param howMany a positive number of elements to be returned from the input sequence
     * @param input   the input sequence
     * @param <T>     the type of the element in the input sequence
     * @return a list containing the first 'howMany' elements of 'input'
     * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
     */
    public static <T> List<T> take(final int howMany, final List<? extends T> input) {
        if (howMany < 0) throw new IllegalArgumentException("take(int,List<T>): howMany must not be negative");
        notNull(input, "take(int,List<T>)", "input");

        if (howMany == 0) return new ArrayList<>(0);

        return input.stream().limit(howMany).collect(Collectors.toList());
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
        if (howMany < 0) throw new IllegalArgumentException("take(int): howMany must not be negative");
        return input -> Functional.take(howMany, input);
    }

    /**
     * takeWhile: given a list return another list containing the first elements up and not including the first element for which
     * the predicate returns false
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate the predicate to use
     * @param input     the input sequence
     * @return a list
     */
    public static <T> List<T> takeWhile(final Predicate<? super T> predicate, final Iterable<T> input) {
        notNull(predicate, "takeWhile(Predicate<T>,Iterable<T>)", "predicate");
        notNull(input, "takeWhile(Predicate<T>,Iterable<T>)", "input");

        final List<T> result = new ArrayList<>();
        final Iterator<T> iterator = input.iterator();
        T next;
        while (iterator.hasNext() && predicate.test(next = iterator.next())) {
            result.add(next);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * takeWhile: given a list return another list containing the first elements up and not including the first element for which
     * the predicate returns false
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate the predicate to use
     * @param input     the input sequence
     * @return a list
     */
    public static <T> List<T> takeWhile(final Predicate<? super T> predicate, final List<T> input) {
        notNull(predicate, "takeWhile(Predicate<T>,List<T>)", "predicate");
        notNull(input, "takeWhile(Predicate<T>,List<T>)", "input");

        if (input.size() == 0) return new ArrayList<>();

        for (int i = 0; i < input.size(); ++i) {
            final T element = input.get(i);
            if (!predicate.test(element)) {
                if (i == 0) return new ArrayList<>();
                return Collections.unmodifiableList(input.subList(0, i));
            }
        }
        return Collections.unmodifiableList(input);
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
    public static <T> Function<List<T>, List<T>> takeWhile(final Predicate<? super T> predicate) {
        notNull(predicate, "takeWhile(Predicate<T>)", "predicate");
        return input -> Functional.takeWhile(predicate, input);
    }

    /**
     * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
     * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
     *
     * @param howMany a non-negative number of elements to be discarded from the input sequence
     * @param input   the input sequence
     * @param <T>     the type of the element in the input sequence
     * @return a list containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
     * are skipped than are present in the 'input'
     */
    public static <T> List<T> skip(final int howMany, final Iterable<? extends T> input) {
        if (howMany < 0) throw new IllegalArgumentException("skip(int,Iterable<T>): howMany must not be negative");
        notNull(input, "skip(int,Iterable<T>)", "input");

        return skip(howMany, asStream(input).collect(Collectors.toList()));
    }

    /**
     * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
     * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
     *
     * @param howMany a non-negative number of elements to be discarded from the input sequence
     * @param input   the input sequence
     * @param <T>     the type of the element in the input sequence
     * @return a list containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
     * are skipped than are present in the 'input'
     */
    public static <T> List<T> skip(final int howMany, final List<? extends T> input) {
        if (howMany < 0) throw new IllegalArgumentException("skip(int,List<T>): howMany must not be negative");
        notNull(input, "skip(int,List<T>)", "input");

        if (howMany == 0) return Collections.unmodifiableList(input);
        final int outputListSize = input.size() - howMany;
        if (outputListSize <= 0) return new ArrayList<>();

        return Collections.unmodifiableList(input.subList(howMany, input.size()));
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
        if (howMany < 0) throw new IllegalArgumentException("skip(int): howMany must not be negative");
        return input -> Functional.skip(howMany, input);
    }

    /**
     * skipWhile: the converse of <tt>takeWhile</tt>. Given a input return another input containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate ignore elements in the input while the predicate is true.
     * @param input     the input sequence
     * @return a input containing the remaining elements after and including the first element for which the predicate returns false
     */
    public static <T> List<T> skipWhile(final Predicate<? super T> predicate, final Iterable<T> input) {
        notNull(predicate, "skipWhile(Predicate<T>,Iterable<T>)", "predicate");
        notNull(input, "skipWhile(Predicate<T>,Iterable<T>)", "input");

        return skipWhile(predicate, asStream(input).collect(Collectors.toList()));
    }

    /**
     * skipWhile: the converse of <tt>takeWhile</tt>. Given a input return another input containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate ignore elements in the input while the predicate is true.
     * @param input     the input sequence
     * @return a input containing the remaining elements after and including the first element for which the predicate returns false
     */
    public static <T> List<T> skipWhile(final Predicate<? super T> predicate, final List<T> input) {
        notNull(predicate, "skipWhile(Predicate<T>,List<T>)", "predicate");
        notNull(input, "skipWhile(Predicate<T>,List<T>)", "input");

        for (int counter = 0; counter < input.size(); ++counter)
            if (!predicate.test(input.get(counter)))
                return Collections.unmodifiableList(input.subList(counter, input.size()));

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
    public static <T> Function<List<T>, List<T>> skipWhile(final Predicate<? super T> predicate) {
        notNull(predicate, "skipWhile(Predicate<T>)", "predicate");
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
        return __ -> constant;
    }

    /**
     * range: a function that returns a map function f(n) that returns an integer from the open-ended range [startFrom+n, infinity).
     * Typically this would be used in <tt>init</tt>
     *
     * @param startFrom the lower bound of the range
     * @return a function that returns a function that returns an integer from the range [startFrom+n, infinity)
     */
    public static Function<Integer, Integer> range(final int startFrom) {
        return new Function<Integer, Integer>() {
            private final int start = startFrom;

            public Integer apply(final Integer input) {
                return (start - 1) + input; // because init starts counting from 1
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
     * @return a list of type U containing the concatenated sequences of transformed values.
     */
    public static <T, U> List<U> flatMap(final Function<? super T, ? extends Iterable<U>> f, final Iterable<T> input) {
        notNull(f, "flatMap(Function<A,B>,Iterable<A>)", "f");
        notNull(input, "flatMap(Function<A,B>,Iterable<A>)", "input");
        List<U> output = new ArrayList<>();
        for (final T element : input)
            output = Functional.concat(output, f.apply(element));
        return Collections.unmodifiableList(output);
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
    public static <T, U> List<U> flatMap(final Function<? super T, ? extends Iterable<U>> f, final Collection<T> input) {
        notNull(f, "flatMap(Function<A,B>,Collection<A>)", "f");
        notNull(input, "flatMap(Function<A,B>,Collection<A>)", "input");
        List<U> output = new ArrayList<>(input.size());
        for (final T element : input)
            output = Functional.concat(output, f.apply(element));
        return Collections.unmodifiableList(output);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
     * These sequences are concatenated into one final output sequence at the end of the transformation.
     * map: (T -> U list) -> T list -> U list
     * This is a curried implementation of 'flatMap'
     *
     * @param <T> the type of the element in the input sequence
     * @param <U> the type of the element in the output sequence
     * @param f   a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
     * @return a list of type U containing the concatenated sequences of transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     */
    public static <T, U> Function<Iterable<T>, List<U>> flatMap(final Function<? super T, ? extends Iterable<U>> f) {
        notNull(f, "flatMap(Function<A,Iterable<B>>)", "f");
        return input -> Functional.flatMap(f, input);
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
     * @param <A>     the type of the element in the input sequence
     * @param howMany the number of elements to be included in the first output list
     * @param input   the input sequence
     * @return a pair: (list, seq) - the list contains 'howMany' elements of 'input' and the sequence contains the remainder
     */
    public static <A> Tuple2<List<A>, Iterable<A>> takeNAndYield(final int howMany, final Iterable<A> input) {
        notNull(input, "takeNAndYield(int,Iterable<A>)", "input");
        if (howMany < 0)
            throw new IllegalArgumentException("takeNAndYield(int,Iterable<A>): howMany must not be negative");

        int counter = 0;
        final List<A> output = new ArrayList<>(howMany);
        if (howMany > 0) {
            final Iterator<A> position = input.iterator();
            if (position.hasNext()) {
                while (counter < howMany) {
                    output.add(position.next());
                    counter++;
                    if (counter < howMany && !position.hasNext()) break;
                }
                return new Tuple2<>(output, () -> position);
            }
        }
        return new Tuple2<>(output, input);
    }

    /**
     * groupBy: similar to {@link #partition(Predicate, Iterable)} in that the input is grouped according to a function. This is more general than
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
        notNull(keyFn, "groupBy(Function<T,U>,Iterable<T>)", "keyFn");
        notNull(input, "groupBy(Function<T,U>,Iterable<T>)", "input");

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
     * groupBy: similar to {@link #partition(Predicate, Iterable)} in that the input is grouped according to a function. This is more general than
     * <tt>partition</tt> though as the output can be an arbitrary number of groups, up to and including one group per item in the
     * input data set. The 'keyFn' is the grouping operator and it is used to determine the key at which any given element from
     * the input data set should be added to the output dictionary / map.
     *
     * @param <T>   the type of the element in the input sequence
     * @param <U>   the type of the element in the key
     * @param keyFn the grouping function. Given an element return the key to be used when storing this element in the dictionary
     * @return a java.util.Map containing a list of elements for each key
     */
    public static <T, U> Function<Iterable<T>, Map<U, List<T>>> groupBy(final Function<? super T, ? extends U> keyFn) {
        notNull(keyFn, "groupBy(Function<T,U>)", "keyFn");
        return input -> groupBy(keyFn, input);
    }

    /**
     * Lazily-evaluated implementations of various of the algorithms
     *
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     * Note that these functions do not generally expose a restartable sequence. If you want to restart the iteration
     * then you should make the convert the sequence (the Iterable) to a concrete collection before accessing the
     * iterator.
     */
    public static final class Lazy {
        private Lazy() {
        }

        /**
         * append: given the input sequence and an item, return a new, lazily-evaluated sequence containing the input with the item
         * as the final element.
         *
         * @param value the item to be appended
         * @param input the input sequence
         * @param <T>   the type of the element in the input sequence
         * @return a sequence containing all the elements of 'input' followed by 'value'
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> append(final T value, final Iterable<T> input) {
            notNull(value, "Lazy.append(T,Iterable<T>)", "value");
            notNull(input, "Lazy.append(T,Iterable<T>)", "input");
            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<? extends T> iterator = input.iterator();
                            private boolean hasMoreInput = iterator.hasNext();
                            private boolean hasNotConsumedAppendee = true;

                            public boolean hasNext() {
                                hasMoreInput = iterator.hasNext();
                                return hasMoreInput || hasNotConsumedAppendee;
                            }

                            public T next() {
                                final T next;
                                if (hasMoreInput) {
                                    next = iterator.next();
                                    hasMoreInput = iterator.hasNext();
                                } else if (hasNotConsumedAppendee) {
                                    next = value;
                                    hasNotConsumedAppendee = false;
                                } else
                                    throw new NoSuchElementException("Lazy.append(T,Iterable<T>): cannot seek beyond the end of the sequence");
                                return next;
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.append(T,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.append(T,Iterable<T>): this Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * append: given the input sequence and an item, return a new, lazily-evaluated sequence containing the input with the item
         * as the final element.
         *
         * @param value the item to be appended
         * @param <T>   the type of the element in the input sequence
         * @return a sequence containing all the elements of 'input' followed by 'value'
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Function<Iterable<T>, Iterable<T>> append(final T value) {
            notNull(value, "Lazy.append(T)", "value");
            return input -> append(value, input);
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
            notNull(f, "Lazy.map(Function<T,R>,Iterable<T>)", "f");
            notNull(input, "Lazy.map(Function<T,R>,Iterable<T>)", "input");

            return new Iterable<U>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<U> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<U>() {
                            private final Iterator<? extends T> iterator = input.iterator();
                            private boolean hasMoreInput = iterator.hasNext();

                            public boolean hasNext() {
                                hasMoreInput = iterator.hasNext();
                                return hasMoreInput;
                            }

                            public U next() {
                                final U next;
                                if (hasMoreInput) {
                                    next = f.apply(iterator.next());
                                    hasMoreInput = iterator.hasNext();
                                } else
                                    throw new NoSuchElementException("Lazy.map(Function<T,R>,Iterable<T>): cannot seek beyond the end of the sequence");
                                return next;
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.map(Function<T,R>,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.map(Function<T,R>,Iterable<T>): this Iterable does not allow multiple Iterators");
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
            notNull(f, "Lazy.map(Function<T,R>)", "f");
            return input -> Lazy.map(f, input);
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
            notNull(f, "Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>)", "f");
            notNull(input, "Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>)", "input");

            return new Iterable<U>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<U> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<U>() {
                            private final Iterator<? extends T> iterator = input.iterator();
                            private boolean hasMoreInput = iterator.hasNext();
                            private int counter;

                            public boolean hasNext() {
                                hasMoreInput = iterator.hasNext();
                                return hasMoreInput;
                            }

                            public U next() {
                                final U next;
                                if (hasMoreInput) {
                                    next = f.apply(counter++, iterator.next());
                                    hasMoreInput = iterator.hasNext();
                                } else
                                    throw new NoSuchElementException("Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>): cannot seek beyond the end of the sequence");
                                return next;

                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>): this Iterable does not allow multiple Iterators");
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
            notNull(f, "Lazy.mapi(BiFunction<Integer,U,V>)", "f");
            return input -> Lazy.mapi(f, input);
        }

        /**
         * Concatenate two sequences and return a new sequence containing the concatenation.
         *
         * @param input1 first input sequence
         * @param input2 second input sequence
         * @param <T>    the type of the element in the input sequence
         * @return a lazily-evaluated sequence containing the elements of the first sequence followed by the elements of the second sequence
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> concat(final Iterable<? extends T> input1, final Iterable<? extends T> input2) {
            notNull(input1, "Lazy.concat(Iterable<T>,Iterable<T>)", "input1");
            notNull(input2, "Lazy.concat(Iterable<T>,Iterable<T>)", "input2");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<? extends T> _s1 = input1.iterator();
                            private final Iterator<? extends T> _s2 = input2.iterator();

                            public boolean hasNext() {
                                return _s1.hasNext() || _s2.hasNext();
                            }

                            public T next() {
                                if (_s1.hasNext()) return _s1.next();
                                if (_s2.hasNext()) return _s2.next();
                                throw new NoSuchElementException("Lazy.concat(Iterable<T>,Iterable<T>): cannot seek beyond the end of the sequence");
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.concat(Iterable<T>,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.concat(Iterable<T>,Iterable<T>): this Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
         *
         * @param <T>       the type of the element in the input sequence
         * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
         *                  the input element is passed through to the output otherwise it is ignored.
         * @param input     a sequence of objects
         * @return a lazily-evaluated sequence which contains zero or more of the elements of the input sequence. Each element is included only if
         * the filter function returns true for the element.
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Iterable<T> filter(final Predicate<? super T> predicate, final Iterable<T> input) {
            notNull(predicate, "Lazy.filter(Predicate<T>,Iterable<T>)", "predicate");
            notNull(input, "Lazy.filter(Predicate<T>,Iterable<T>)", "input");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<T> _input = input.iterator();
                            private final Predicate<? super T> _f = predicate;
                            private T _next;

                            public boolean hasNext() {
                                while (isNull(_next) && // ie we haven't already read the next element
                                        _input.hasNext()) {
                                    final T next = _input.next();
                                    if (_f.test(next)) {
                                        _next = next;
                                        return true;
                                    }
                                }
                                return _next != null;
                            }

                            public T next() {
                                if (hasNext()) {
                                    final T next = _next;
                                    _next = null;
                                    return next;
                                }
                                throw new NoSuchElementException("Lazy.filter(Predicate<T>,Iterable<T>): cannot seek beyond the end of the sequence");
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.filter(Predicate<T>,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.filter(Predicate<T>,Iterable<T>): this Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
         *
         * @param <T>       the type of the element in the input sequence
         * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
         *                  the input element is passed through to the output otherwise it is ignored.
         * @return a lazily-evaluated sequence which contains zero or more of the elements of the input sequence. Each element is included only if
         * the filter function returns true for the element.
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T> Function<Iterable<T>, Iterable<T>> filter(final Predicate<? super T> predicate) {
            notNull(predicate, "Lazy.filter(Predicate<T>)", "predicate");
            return input -> Lazy.filter(predicate, input);
        }

        /**
         * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
         * be between zero and the number of elements in the input sequence.
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * choose: (A -> B option) -> A list -> B list
         *
         * @param <T>     the type of the element in the input sequence
         * @param <U>     the type of the element in the output sequence
         * @param chooser map function. This transforms the input element into an Option
         * @param input   input sequence
         * @return a lazily-evaluated sequence of transformed elements, numbering less than or equal to the number of input elements
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Iterable<U> choose(final Function<? super T, Option<U>> chooser, final Iterable<T> input) {
            notNull(chooser, "Lazy.choose(Function<A,Option<B>>,Iterable<A>)", "chooser");
            notNull(input, "Lazy.choose(Function<A,Option<B>>,Iterable<A>)", "input");

            return new Iterable<U>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<U> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<U>() {
                            private final Iterator<T> _input = input.iterator();
                            private final Function<? super T, Option<U>> _f = chooser;
                            private Option<U> _next = Option.none();

                            public boolean hasNext() {
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

                            public U next() {
                                if (hasNext()) {
                                    final Option<U> next = _next;
                                    _next = Option.none();
                                    return next.get();
                                }
                                throw new NoSuchElementException("Lazy.choose(Function<T,Option<U>>,Iterable<T>): cannot seek beyond the end of the sequence");
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.choose(Function<T,Option<U>>,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.choose(Function<T,Option<U>>,Iterable<T>): this Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
         * be between zero and the number of elements in the input sequence.
         * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
         * choose: (A -> B option) -> A list -> B list
         *
         * @param <T>     the type of the element in the input sequence
         * @param <U>     the type of the element in the output sequence
         * @param chooser map function. This transforms the input element into an Option
         * @return a lazily-evaluated sequence of transformed elements, numbering less than or equal to the number of input elements
         * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
         */
        public static <T, U> Function<Iterable<T>, Iterable<U>> choose(final Function<? super T, Option<U>> chooser) {
            notNull(chooser, "Lazy.choose(Function<A,Option<B>>)", "chooser");
            return input -> Lazy.choose(chooser, input);
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
            notNull(f, "Lazy.init(Function<Integer,T>,int)", "f");
            if (howMany < 1)
                throw new IllegalArgumentException("Lazy.init(Function<Integer,T>,int): howMany must be non-negative");

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
                                    throw new NoSuchElementException("Lazy.init(Function<Integer,T>,int): cannot seek beyond the end of the sequence");
                                return _f.apply(_counter++);
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.init(Function<Integer,T>,int): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.init(Function<Integer,T>,int): this Iterable does not allow multiple Iterators");
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
            notNull(f, "Lazy.init(Function<Integer,T>)", "f");

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
                                throw new UnsupportedOperationException("Lazy.init(Function<Integer,T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.init(Function<Integer,T>): this Iterable does not allow multiple Iterators");
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
        public static <T, U> Iterable<U> flatMap(final Function<? super T, ? extends Iterable<U>> f, final Iterable<T> input) {
            notNull(f, "Lazy.flatMap(Function<A,Iterable<B>>,Iterable<A>)", "f");
            notNull(input, "Lazy.flatMap(Function<A,Iterable<B>>,Iterable<A>)", "input");

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
                                if (!hasNext())
                                    throw new NoSuchElementException("Lazy.flatMap(Function<T,Iterable<U>>,Iterable<T>): cannot seek beyond the end of the sequence");
                                if (cacheIterator.hasNext()) return cacheIterator.next();
                                cache = StreamSupport.stream(f.apply(it.next()).spliterator(), false).collect(Collectors.toList());
                                cacheIterator = cache.iterator();
                                return cacheIterator.next();
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.flatMap(Function<T,Iterable<U>>,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.flatMap(Function<T,Iterable<U>>,Iterable<T>): this Iterable does not allow multiple Iterators");
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
        public static <T, U> Function<Iterable<T>, Iterable<U>> flatMap(final Function<? super T, ? extends Iterable<U>> f) {
            notNull(f, "Lazy.flatMap(Function<A,Iterable<B>>)", "f");
            return input -> Lazy.flatMap(f, input);
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
                throw new IllegalArgumentException("Lazy.skip(int,Iterable<T>): howMany must not be negative");
            notNull(input, "Lazy.skip(int,Iterable<T>)", "input");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<T> it = input.iterator();
                            private boolean haveWeSkipped;

                            public boolean hasNext() {
                                if (haveWeSkipped) return it.hasNext();
                                for (int i = 0; i < howMany; ++i)
                                    if (it.hasNext()) it.next();
                                    else return false;
                                haveWeSkipped = true;
                                return it.hasNext();
                            }

                            public T next() {
                                if (!hasNext())
                                    throw new NoSuchElementException("Lazy.skip(int,Iterable<T>): cannot seek beyond the end of the sequence");
                                return it.next();
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.skip(int,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.skip(int,Iterable<T>): this Iterable does not allow multiple Iterators");
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
            if (howMany < 0)
                throw new IllegalArgumentException("Lazy.skip(int): howMany must not be negative");
            return input -> Lazy.skip(howMany, input);
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
        public static <T> Iterable<T> skipWhile(final Predicate<? super T> predicate, final Iterable<T> input) {
            notNull(predicate, "Lazy.skipWhile(Predicate<T>,Iterable<T>)", "predicate");
            notNull(input, "Lazy.skipWhile(Predicate<T>,Iterable<T>)", "input");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<T> it = input.iterator();
                            private boolean haveWeSkipped = false;
                            private T next;
                            private boolean haveWeConsumedNext = false;
                            private boolean nextIsSet = false;

                            public boolean hasNext() {
                                if (haveWeSkipped) return it.hasNext();
                                if (it.hasNext()) {
                                    boolean hasNext;
                                    while ((hasNext = it.hasNext()) && predicate.test(next = it.next())) ;
                                    if (hasNext) {
                                        nextIsSet = true;
                                        haveWeSkipped = true;
                                        return true;
                                    } else return false;
                                }
                                haveWeSkipped = true;
                                return false;
                            }

                            public T next() {
                                if (!hasNext())
                                    throw new NoSuchElementException("Lazy.skipWhile(Predicate<T>,Iterable<T>): cannot seek beyond the end of the sequence");
                                if (haveWeConsumedNext && !nextIsSet) next = it.next();
                                haveWeConsumedNext = true;
                                nextIsSet = false;
                                return next;
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.skipWhile(Predicate<T>,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.skipWhile(Predicate<T>,Iterable<T>): this Iterable does not allow multiple Iterators");
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
        public static <T> Function<Iterable<T>, Iterable<T>> skipWhile(final Predicate<? super T> predicate) {
            notNull(predicate, "Lazy.skipWhile(Predicate<T>)", "predicate");
            return input -> Lazy.skipWhile(predicate, input);
        }

        /**
         * take: given a sequence return another sequence containing the first 'howMany' elements
         *
         * @param howMany a positive number of elements to be returned from the input sequence
         * @param input   the input sequence
         * @param <T>     the type of the element in the input sequence
         * @return a sequence containing the first 'howMany' elements of 'input'
         * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
         */
        public static <T> Iterable<T> take(final int howMany, final Iterable<? extends T> input) {
            if (howMany < 0)
                throw new IllegalArgumentException("Lazy.take(int,Iterable<T>): howMany must not be negative");
            notNull(input, "Lazy.take(int,Iterable<T>)", "input");

            if (howMany == 0) return new ArrayList<>(0);

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<? extends T> it = input.iterator();
                            private int howManyHaveWeRetrievedAlready;

                            public boolean hasNext() {
                                return howManyHaveWeRetrievedAlready < howMany && it.hasNext();
                            }

                            public T next() {
                                if (howManyHaveWeRetrievedAlready >= howMany)
                                    throw new java.util.NoSuchElementException("Lazy.take(int,Iterable<T>): cannot seek beyond the end of the sequence");
                                final T next = it.next();
                                howManyHaveWeRetrievedAlready++;
                                return next;
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.take(int,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.take(int,Iterable<T>): this Iterable does not allow multiple Iterators");
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
            if (howMany < 0)
                throw new IllegalArgumentException("Lazy.take(int): howMany must not be negative");
            return input -> Lazy.take(howMany, input);
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
        public static <T> Iterable<T> takeWhile(final Predicate<? super T> predicate, final Iterable<T> input) {
            notNull(predicate, "Lazy.takeWhile(Predicate<T>,Iterable<T>)", "predicate");
            notNull(input, "Lazy.takeWhile(Predicate<T>,Iterable<T>)", "input");

            return new Iterable<T>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<T> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<T>() {
                            private final Iterator<T> it = input.iterator();
                            private boolean haveWeFinished;
                            private T next;
                            private boolean haveWeCheckedTheCurrentElement;

                            public boolean hasNext() {
                                if (!haveWeFinished) {
                                    if (!haveWeCheckedTheCurrentElement) {
                                        if (it.hasNext()) {
                                            next = it.next();
                                            if (predicate.test(next)) {
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
                                    } else
                                        throw new NoSuchElementException("Lazy.takeWhile(Predicate<T>,Iterable<T>): cannot seek beyond the end of the sequence");
                                }
                                throw new NoSuchElementException();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.takeWhile(Predicate<T>,Iterable<T>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.takeWhile(Predicate<T>,Iterable<T>): this Iterable does not allow multiple Iterators");
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
        public static <T> Function<Iterable<T>, Iterable<T>> takeWhile(final Predicate<? super T> predicate) {
            notNull(predicate, "Lazy.takeWhile(Predicate<T>)", "predicate");
            return input -> Lazy.takeWhile(predicate, input);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
         * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * This is the converse of <tt>fold</tt>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         */
        public static <A, B> Iterable<A> unfold(final Function<? super B, Tuple2<A, B>> unspool, final Predicate<? super B> finished, final B seed) {
            notNull(unspool, "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)", "unspooler");
            notNull(finished, "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)", "finished");

            return new Iterable<A>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<A> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<A>() {
                            B next = seed;

                            public boolean hasNext() {
                                return !finished.test(next);
                            }

                            public A next() {
                                if (!hasNext()) throw new NoSuchElementException();
                                final Tuple2<A, B> t = unspool.apply(next);
                                next = t._2();
                                return t._1();
                            }


                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): this Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
         * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * This is the converse of <tt>fold</tt>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         */
        public static <A, B> Functional.Lazy.WithSeed<A, B> unfold(final Function<? super B, Tuple2<A, B>> unspooler, final Predicate<? super B> finished) {
            notNull(unspooler, "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>)", "unspooler");
            notNull(finished, "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>)", "finished");

            return seed -> Functional.Lazy.unfold(unspooler, finished, seed);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
         * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * This is the converse of <tt>fold</tt>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         */
        public static <A, B> Functional.Lazy.WithFinished<A, B> unfold(final Function<? super B, Tuple2<A, B>> unspooler) {
            notNull(unspooler, "Lazy.unfold(Function<B,Tuple2<A,B>>)", "unspooler");
            return finished -> seed -> Functional.Lazy.unfold(unspooler, finished, seed);
        }

        public interface WithSeed<A, B> {
            Iterable<A> withSeed(B seed);
        }

        public interface WithFinished<A, B> {
            Functional.Lazy.WithSeed<A, B> withFinished(Predicate<? super B> finished);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
         * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * This is the converse of <tt>fold</tt>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         */
        public static <A, B> Iterable<A> unfold(final Function<? super B, Option<Tuple2<A, B>>> unspooler, final B seed) {
            notNull(unspooler, "Lazy.unfold(Function<B,Option<Tuple2<A,B>>>,B)", "unspooler");

            return new Iterable<A>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<A> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<A>() {
                            B next = seed;

                            public boolean hasNext() {
                                return unspooler.apply(next).isSome();
                            }

                            public A next() {
                                final Option<Tuple2<A, B>> temp = unspooler.apply(next);
                                if (temp.isNone()) throw new NoSuchElementException();
                                next = temp.get()._2();
                                return temp.get()._1();
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.unfold(Func,B): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.unfold(Func,B): This Iterable does not allow multiple Iterators");
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
            if (howManyElements <= 0)
                throw new IllegalArgumentException("Lazy.partition(int,int): howManyElements must be positive");
            if (howManyPartitions <= 0)
                throw new IllegalArgumentException("Lazy.partition(int,int): howManyPartitions must be positive");
            return partition(Functional.range(1), howManyElements, howManyPartitions);
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
            notNull(generator, "Lazy.partition(Function<Integer,T>,int,int)", "generator");
            if (howManyElements <= 0)
                throw new IllegalArgumentException("Lazy.partition(Function<Integer,T>,int,int): howManyElements must be positive");
            if (howManyPartitions <= 0)
                throw new IllegalArgumentException("Lazy.partition(Function<Integer,T>,int,int): howManyPartitions must be positive");

            final int size = howManyElements / howManyPartitions;
            final int remainder = howManyElements % howManyPartitions;

            assert size * howManyPartitions + remainder == howManyElements;

            final Integer seed = 0;
            final Function<Integer, Tuple2<T, Integer>> boundsCalculator = integer -> new Tuple2<>(
                    generator.apply(1 + (integer * size + (integer <= remainder ? integer : remainder))),
                    integer + 1);
            final Predicate<Integer> finished = integer -> integer > howManyPartitions;

            final Iterable<T> output = Lazy.unfold(boundsCalculator, finished, seed);

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
                                if (!iterator.hasNext())
                                    throw new NoSuchElementException("Lazy.partition(Function<Integer,T>,int,int): cannot seek beyond the end of the sequence");
                                final T next = iterator.next();
                                final Range<T> retval = new Range<>(last, next);
                                last = next;
                                return retval;
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.partition(Function<Integer,T>,int,int): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.partition(Function<Integer,T>,int,int): this Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * Convolution of functions
         * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
         *
         * @param <A>      the type of the input sequence
         * @param <B>      the result type of the first transformation
         * @param <C>      the result type of the second transformation
         * @param zipFunc1 the first transformation function
         * @param zipFunc2 the second transformation function
         * @param input    the input sequence
         * @return a sequence of pairs. The first value of the pair is the result of the first transformation and the second value of the
         * pair of the result of the second transformation.
         */
        public static <A, B, C> Iterable<Tuple2<B, C>> zip(final Function<? super A, B> zipFunc1, final Function<? super A, C> zipFunc2, final Iterable<? extends A> input) {
            notNull(zipFunc1, "Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>)", "zipFunc1");
            notNull(zipFunc2, "Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>)", "zipFunc2");
            notNull(input, "Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>)", "input");

            return new Iterable<Tuple2<B, C>>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<Tuple2<B, C>> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<Tuple2<B, C>>() {
                            private final Iterator<? extends A> iterator = input.iterator();

                            public boolean hasNext() {
                                return iterator.hasNext();
                            }

                            public Tuple2<B, C> next() {
                                if (!iterator.hasNext())
                                    throw new NoSuchElementException("Lazy.zip(Function<A,B>,Function<A,C>,Iterable<A>): cannot seek beyond the end of the sequence");
                                final A next = iterator.next();
                                return new Tuple2<>(zipFunc1.apply(next), zipFunc2.apply(next));
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.zip(Function<A,B>,Function<A,C>,Iterable<A>): it is not possible to remove elements from this sequence");
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.zip(Function<A,B>,Function<A,C>,Iterable<A>): this Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * Convolution of functions. That is, apply two transformation functions 'simultaneously' and return a list of pairs,
         * each of which contains one part of the results.
         *
         * @param zipFunc1 the transformation function that generates the first value in the resultant pair
         * @param zipFunc2 the transformation function that generates the second value in the resultant pair
         * @param <A>      a type that all the elements in the input list extend and that both of the transformation functions accept as input
         * @param <B>      the resulting type of the first transformation
         * @param <C>      the resulting type of the second transformation
         * @return a list of pairs containing the two transformed sequences
         */
        public static <A, B, C> Function<Iterable<? extends A>, Iterable<Tuple2<B, C>>> zip(final Function<? super A, B> zipFunc1, final Function<? super A, C> zipFunc2) {
            notNull(zipFunc1, "Lazy.zip(Function<A,B>,Function<A,B>)", "zipFunc1");
            notNull(zipFunc2, "Lazy.zip(Function<A,B>,Function<A,B>)", "zipFunc2");
            return input -> Lazy.zip(zipFunc1, zipFunc2, input);
        }

        /**
         * The Convolution operator
         * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
         *
         * @param input1 input sequence
         * @param input2 input sequence
         * @param <A>    the type of the element in the first input sequence
         * @param <B>    the type of the element in the second input sequence
         * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
         * in order. If the sequences do not have the same number of elements then an exception is thrown.
         * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
         */
        public static <A, B> Iterable<Tuple2<A, B>> zip(final Iterable<? extends A> input1, final Iterable<? extends B> input2) {
            notNull(input1, "Lazy.zip(Iterable<A>,Iterable<B>)", "input1");
            notNull(input2, "Lazy.zip(Iterable<A>,Iterable<B>)", "input2");

            return new Iterable<Tuple2<A, B>>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<Tuple2<A, B>> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<Tuple2<A, B>>() {
                            private final Iterator<? extends A> l1_it = input1.iterator();
                            private final Iterator<? extends B> l2_it = input2.iterator();

                            public boolean hasNext() {
                                return bothHaveNext();
                            }

                            public Tuple2<A, B> next() {
                                if (!bothHaveNext())
                                    throw new NoSuchElementException();
                                return new Tuple2<>(l1_it.next(), l2_it.next());
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.zip(Iterable<A>,Iterable<B>): it is not possible to remove elements from this sequence");
                            }

                            private boolean bothHaveNext() {
                                final boolean l1_it_hasNext = l1_it.hasNext();
                                final boolean l2_it_hasNext = l2_it.hasNext();
                                if (l1_it_hasNext != l2_it_hasNext)
                                    throw new IllegalArgumentException("Lazy.zip(Iterable<A>,Iterable<B>): cannot zip two iterables with different lengths");
                                return l1_it_hasNext;
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.zip(Iterable<A>,Iterable<B>): this Iterable does not allow multiple Iterators");
                }
            };
        }

        /**
         * The Convolution operator
         * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
         *
         * @param input1 input sequence
         * @param input2 input sequence
         * @param input3 input sequence
         * @param <A>    the type of the element in the first input sequence
         * @param <B>    the type of the element in the second input sequence
         * @param <C>    the type of the element in the third input sequence
         * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
         * in order. If the sequences do not have the same number of elements then an exception is thrown.
         * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
         */
        public static <A, B, C> Iterable<Tuple3<A, B, C>> zip3(final Iterable<? extends A> input1, final Iterable<? extends B> input2, final Iterable<? extends C> input3) {
            notNull(input1, "Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input1");
            notNull(input2, "Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input2");
            notNull(input3, "Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input3");

            return new Iterable<Tuple3<A, B, C>>() {
                private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

                public Iterator<Tuple3<A, B, C>> iterator() {
                    if (haveCreatedIterator.compareAndSet(false, true))
                        return new Iterator<Tuple3<A, B, C>>() {
                            private final Iterator<? extends A> l1_it = input1.iterator();
                            private final Iterator<? extends B> l2_it = input2.iterator();
                            private final Iterator<? extends C> l3_it = input3.iterator();

                            public boolean hasNext() {
                                return allHaveNext();
                            }

                            public Tuple3<A, B, C> next() {
                                if (!allHaveNext())
                                    throw new NoSuchElementException("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot seek beyond the end of the sequence");
                                return new Tuple3<>(l1_it.next(), l2_it.next(), l3_it.next());
                            }

                            public void remove() {
                                throw new UnsupportedOperationException("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): it is not possible to remove elements from this sequence");
                            }

                            private boolean allHaveNext() {
                                final boolean l1_it_hasNext = l1_it.hasNext();
                                final boolean l2_it_hasNext = l2_it.hasNext();
                                final boolean l3_it_hasNext = l3_it.hasNext();
                                if (l1_it_hasNext != l2_it_hasNext || l1_it_hasNext != l3_it_hasNext)
                                    throw new IllegalArgumentException("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths");
                                return l1_it_hasNext;
                            }
                        };
                    else
                        throw new UnsupportedOperationException("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): this Iterable does not allow multiple Iterators");
                }
            };
        }
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
     * Recursive implementations of (some of) the algorithms contained herein
     */
    public static class Rec {
        private Rec() {
        }

        /**
         * See <A href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</A>
         * This is a recursive implementation of filter.
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         *
         * @param <A>       the type of the element in the input sequence
         * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
         *                  the input element is passed through to the output otherwise it is ignored.
         * @return a sequence which contains zero or more of the elements of the input sequence. Each element is included only if
         * the filter function returns true for the element.
         */
        public static <A> Iterable<A> filter(final Predicate<? super A> predicate, final Iterable<A> input) {
            notNull(predicate, "Rec.filter(Predicate<A>,Iterable<A>)", "predicate");
            notNull(input, "Rec.filter(Predicate<A>,Iterable<A>)", "input");
            return filter(predicate, input.iterator(), new ArrayList<>());
        }

        private static <A> Iterable<A> filter(final Predicate<? super A> f, final Iterator<A> input, final Collection<A> accumulator) {
            if (input.hasNext()) {
                final A next = input.next();
                if (f.test(next)) accumulator.add(next);
                return filter(f, input, accumulator);
            }
            return accumulator;
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
            notNull(f, "Rec.map(Function<A,B>,Iterable<A>)", "f");
            notNull(input, "Rec.map(Function<A,B>,Iterable<A>)", "input");
            return map(f, input.iterator(), new ArrayList<>());
        }

        private static <A, B> Iterable<B> map(final Function<? super A, ? extends B> f, final Iterator<A> input, final Collection<B> accumulator) {
            if (input.hasNext()) {
                accumulator.add(f.apply(input.next()));
                return map(f, input, accumulator);
            }
            return accumulator;
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Fold</a>
         * fold: (A -> B -> A) -> A -> B list -> A
         * This is a recursive implementation of fold
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         *
         * @param <A>          the type of the initialValue / seed
         * @param <B>          the type of the element in the output sequence
         * @param folder       the aggregation function
         * @param initialValue the seed for the aggregation
         * @return the aggregated value
         */
        public static <A, B> A fold(final BiFunction<? super A, ? super B, ? extends A> folder, final A initialValue, final Iterable<B> input) {
            notNull(folder, "Rec.fold(BiFunction<A,B,A>,A,Iterable<B>)", "folder");
            notNull(input, "Rec.fold(BiFunction<A,B,A>,A,Iterable<B>)", "input");
            return fold(folder, initialValue, input.iterator());
        }

        private static <A, B> A fold(final BiFunction<? super A, ? super B, ? extends A> f, final A initialValue, final Iterator<B> input) {
            if (input.hasNext()) {
                final B next = input.next();
                return fold(f, f.apply(initialValue, next), input);
            }
            return initialValue;
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
         * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         * This is a recursive implementation of unfold
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         */
        public static <A, B> List<A> unfold(final Function<? super B, Tuple2<A, B>> unspooler, final Predicate<? super B> finished, final B seed) {
            notNull(unspooler, "Rec.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)", "unspooler");
            notNull(finished, "Rec.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)", "finished");
            return unfold(unspooler, finished, seed, new ArrayList<>());
        }

        private static <A, B> List<A> unfold(final Function<? super B, Tuple2<A, B>> unspool, final Predicate<? super B> finished, final B seed, final List<A> accumulator) {
            if (finished.test(seed)) return accumulator;
            final Tuple2<A, B> p = unspool.apply(seed);
            accumulator.add(p._1());
            return unfold(unspool, finished, p._2(), accumulator);
        }

        /**
         * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
         * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
         * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
         * This is a recursive implementation of unfold
         * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
         */
        public static <A, B> List<A> unfold(final Function<? super B, Option<Tuple2<A, B>>> unspool, final B seed) {
            return unfold(unspool, seed, new ArrayList<>());
        }

        private static <A, B> List<A> unfold(final Function<? super B, Option<Tuple2<A, B>>> unspool, final B seed, final List<A> accumulator) {
            final Option<Tuple2<A, B>> p = unspool.apply(seed);
            if (p.isNone()) return accumulator;
            accumulator.add(p.get()._1());
            return unfold(unspool, p.get()._2(), accumulator);
        }
    }
        /*
        // Following are functions for non-list collections
        */

    public static <A, B, C> Map<B, C> map_dict(final Function<? super A, Map.Entry<B, C>> f, final Iterable<A> input) {
        notNull(f, "map_dict(Function<A,Map.Entry<B,C>>,Iterable<A>)", "f");
        notNull(input, "map_dict(Function<A,Map.Entry<B,C>>,Iterable<A>)", "input");
        return map_dict_(f, Map.Entry::getKey, Map.Entry::getValue, input);
    }

    public static <A, B, C> Map<B, C> map_dict_t2(final Function<? super A, Tuple2<B, C>> f, final Iterable<A> input) {
        notNull(f, "map_dict_t2(Function<A,Tuple2<B,C>>,Iterable<A>)", "f");
        notNull(input, "map_dict_t2(Function<A,Tuple2<B,C>>,Iterable<A>)", "input");
        return map_dict_(f, Tuple2::_1, Tuple2::_2, input);
    }

    private static <A, B, C, T> Map<B, C> map_dict_(final Function<? super A, T> f, final Function<T, B> keyFn, final Function<T, C> valueFn, final Iterable<A> input) {
        final Map<B, C> results = new HashMap<>();
        for (final A a : input) {
            final T intermediate = f.apply(a);
            results.put(keyFn.apply(intermediate), valueFn.apply(intermediate));
        }
        return results;
    }

    /**
     * Implementations of the algorithms contained herein which return sets
     * See <a href="http://en.wikipedia.org/wiki/Set_(computer_science)">Set</a>
     */
    public static class Set {
        private Set() {
        }

        /**
         * Non-destructive wrapper for set intersection
         *
         * @param e1  input set
         * @param e2  input set
         * @param <E> the underlying base type of the two input sets
         * @return a set containing those elements which are contained within both sets 'e1' and 'e2'
         */
        public static <E> java.util.Set<E> intersection(final java.util.Set<? extends E> e1, final java.util.Set<? extends E> e2) {
            final java.util.Set<E> i = new HashSet<>(e1);
            i.retainAll(e2);
            return Collections.unmodifiableSet(i);
        }

        /**
         * Non-destructive wrapper for set difference
         *
         * @param inSet    input set
         * @param notInSet input set
         * @param <E>      the underlying base type of the two input sets
         * @return a set of those elements which are in 'inSet' and not in 'notInSet'
         */
        public static <E> java.util.Set<E> asymmetricDifference(final java.util.Set<? extends E> inSet, final java.util.Set<? extends E> notInSet) {
            final java.util.Set<E> i = new HashSet<>(inSet);
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
            return Functional.fold((BiFunction<List<T>, U, List<T>>) (state, o2) -> {
                state.add(f.apply(o2));
                return state;
            }, l instanceof Collection<?> ? new ArrayList<>(((Collection<?>) l).size()) : new ArrayList<>(), l);
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
        public static <T> List<T> filter(final Predicate<? super T> predicate, final Iterable<T> l) {
            return Functional.fold((BiFunction<List<T>, T, List<T>>) (ts, o) -> {
                if (predicate.test(o)) ts.add(o);
                return ts;
            }, l instanceof Collection<?> ? new ArrayList<>(((Collection<?>) l).size()) : new ArrayList<>(), l);
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
            return Functional.unfold(a -> a <= howMany ? Option.of(new Tuple2<>(f.apply(a), a + 1)) : Option.none(), 1);
        }
    }

    /*
     * Following are control structures, eg if, switch
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
    public static <A, B> B if_(final A a, final Predicate<? super A> predicate, final Function<? super A, ? extends B> thenClause, final Function<? super A, ? extends B> elseClause) {
        notNull(predicate, "if_(A,Predicate<A>,Function<A,B>,Function<A,B>)", "predicate");
        notNull(thenClause, "if_(A,Predicate<A>,Function<A,B>,Function<A,B>)", "thenClause");
        notNull(elseClause, "if_(A,Predicate<A>,Function<A,B>,Function<A,B>)", "elseClause");

        return predicate.test(a) ? thenClause.apply(a) : elseClause.apply(a);
    }

    public static <A, B> WithPredicate<A, B> if_(final A a) {
        return predicate -> thenClause -> elseClause -> if_(a, predicate, thenClause, elseClause);
    }

    public interface WithPredicate<A, B> {
        Then<A, B> withPredicate(final Predicate<? super A> predicate);
    }

    public interface Then<A, B> {
        Else<A, B> then(final Function<? super A, ? extends B> thenClause);
    }

    public interface Else<A, B> {
        B orElse(final Function<? super A, ? extends B> elseClause);
    }

    public static final class Matcher {
        private Matcher() {
        }

        /**
         * Functional switch statement. Provide a sequence of Cases and a function which will be evaluated if none of the Cases are true.
         *
         * @param input       the value to be tested
         * @param cases       sequence of Match objects
         * @param defaultCase function to be evaluated if none of the Cases are true
         * @param <A>         the type of the element passed to the predicate in the {@link Match}
         * @param <B>         the type of the result
         * @return the result of the appropriate Match or the result of the 'defaultCase' function
         */
        public static <A, B> WithMatches<A, B> findMatch(final A input) {
            return cases -> defaultCase -> switchBetween(input, cases, defaultCase);
        }

        public interface WithMatches<A, B> {
            WithDefaultCase<A, B> from(Matches<A, B> cases);
        }

        public interface WithDefaultCase<A, B> {
            B orElse(Function<A, B> defaultCase);
        }

        public interface Matches<A, B> extends Iterable2<Match<A, B>> {
            static <A, B> Matches<A, B> of(final java.lang.Iterable<Match<A, B>> it) {
                if (it instanceof Matches) return (Matches<A, B>) it;
                return new Matches<A, B>() {
                    private final Iterable2<Match<A, B>> i = Iterable2.of(it);

                    @Override
                    public Iterator<Match<A, B>> iterator() {
                        return null;
                    }

                    @Override
                    public Iterable2<Match<A, B>> filter(final Predicate<? super Match<A, B>> f) {
                        return null;
                    }

                    @Override
                    public <U> Iterable2<U> map(final Function<? super Match<A, B>, ? extends U> f) {
                        return null;
                    }

                    @Override
                    public <U> Iterable2<U> mapi(final BiFunction<Integer, Match<A, B>, ? extends U> f) {
                        return null;
                    }

                    @Override
                    public <U> Iterable2<U> choose(final Function<? super Match<A, B>, Option<U>> f) {
                        return null;
                    }

                    @Override
                    public boolean exists(final Predicate<? super Match<A, B>> f) {
                        return false;
                    }

                    @Override
                    public boolean forAll(final Predicate<? super Match<A, B>> f) {
                        return false;
                    }

                    @Override
                    public <U> boolean forAll2(final BiPredicate<? super U, ? super Match<A, B>> f, final Iterable<U> input1) {
                        return false;
                    }

                    @Override
                    public <U> U fold(final BiFunction<? super U, ? super Match<A, B>, ? extends U> f, final U seed) {
                        return null;
                    }

                    @Override
                    public <K, V> Map<K, V> toDictionary(final Function<? super Match<A, B>, ? extends K> keyFn, final Function<? super Match<A, B>, ? extends V> valueFn) {
                        return null;
                    }

                    @Override
                    public Match<A, B> last() {
                        return null;
                    }

                    @Override
                    public Iterable2<Match<A, B>> sortWith(final Comparator<Match<A, B>> f) {
                        return null;
                    }

                    @Override
                    public Iterable2<Match<A, B>> concat(final Iterable2<Match<A, B>> list2) {
                        return null;
                    }

                    @Override
                    public Option<Match<A, B>> find(final Predicate<? super Match<A, B>> f) {
                        return i.find(f);
                    }

                    @Override
                    public int findIndex(final Predicate<? super Match<A, B>> f) {
                        return 0;
                    }

                    @Override
                    public <U> Option<U> pick(final Function<? super Match<A, B>, Option<U>> f) {
                        return null;
                    }

                    @Override
                    public <U> Iterable2<U> collect(final Function<? super Match<A, B>, ? extends Iterable<U>> f) {
                        return null;
                    }

                    @Override
                    public Iterable2<Match<A, B>> take(final int howMany) {
                        return null;
                    }

                    @Override
                    public Iterable2<Match<A, B>> takeWhile(final Predicate<? super Match<A, B>> f) {
                        return null;
                    }

                    @Override
                    public Iterable2<Match<A, B>> skip(final int howMany) {
                        return null;
                    }

                    @Override
                    public Iterable2<Match<A, B>> skipWhile(final Predicate<? super Match<A, B>> f) {
                        return null;
                    }

                    @Override
                    public String join(final String delimiter) {
                        return null;
                    }

                    @Override
                    public Option<Match<A, B>> findLast(final Predicate<Match<A, B>> f) {
                        return null;
                    }

                    @Override
                    public Tuple2<List<Match<A, B>>, List<Match<A, B>>> partition(final Predicate<? super Match<A, B>> f) {
                        return null;
                    }

                    @Override
                    public <U> Iterable2<Tuple2<Match<A, B>, U>> zip(final Iterable2<? extends U> l2) {
                        return null;
                    }

                    @Override
                    public <U, V> Iterable2<Tuple3<Match<A, B>, U, V>> zip3(final Iterable<? extends U> l2, final Iterable<? extends V> l3) {
                        return null;
                    }

                    @Override
                    public <U> U in(final Function<Iterable2<Match<A, B>>, U> f) {
                        return null;
                    }

                    @Override
                    public <U> Map<U, List<Match<A, B>>> groupBy(final Function<? super Match<A, B>, ? extends U> keyFn) {
                        return null;
                    }

                    @Override
                    public List<Match<A, B>> toList() {
                        return null;
                    }

                    @Override
                    public java.util.Set<Match<A, B>> toSet() {
                        return null;
                    }
                };
            }
        }

        /**
         * matcher: a Match builder function.
         *
         * @param predicate predicate
         * @param result    the result function to be applied if the predicate evaluates to true
         * @param <A>       the type of the element being passed to the predicate
         * @param <B>       the type of the result of the transformation function
         * @return a new Match object
         */
        public static <A, B> Match<A, B> matcher(final Predicate<A> predicate, final Function<A, B> result) {
            notNull(predicate, "matcher(Predicate<A>,Function<A,B>)", "predicate");
            notNull(result, "matcher(Predicate<A>,Function<A,B>)", "result");

            return Match.of(predicate, result);
        }

        public static <A, B> Matches<A, B> matchers(final Match<A, B> match1) {
            return Matches.of(Arrays.asList(match1));
        }

        public static <A, B> Matches<A, B> matchers(final Match<A, B> match1, final Match<A, B> match2) {
            return Matches.of(Arrays.asList(match1, match2));
        }

        public static <A, B> Matches<A, B> matchers(final Match<A, B> match1, final Match<A, B> match2, final Match<A, B> match3) {
            return Matches.of(Arrays.asList(match1, match2, match3));
        }

        public static <A, B> Matches<A, B> matchers(final Match<A, B> match1, final Match<A, B> match2, final Match<A, B> match3, final Match<A, B> match4) {
            return Matches.of(Arrays.asList(match1, match2, match3, match4));
        }

        public static <A, B> Matches<A, B> matchers(final Match<A, B> match1, final Match<A, B> match2, final Match<A, B> match3, final Match<A, B> match4, final Match<A, B> match5) {
            return Matches.of(Arrays.asList(match1, match2, match3, match4, match5));
        }

        private static final class Match<A, B> {
            private final Predicate<A> check;
            private final Function<A, B> result;

            private Match(final Predicate<A> chk, final Function<A, B> res) {
                this.check = chk;
                this.result = res;
            }

            public static <A, B> Match<A, B> of(final Predicate<A> chk, final Function<A, B> res) {
                return new Match<>(chk, res);
            }

            public boolean test(final A a) {
                return check.test(a);
            }

            public B getResultsFor(final A a) {
                return result.apply(a);
            }
        }

        private static <A, B> B switchBetween(final A input, final Matches<A, B> cases, final Function<A, B> defaultCase) {
            notNull(cases, "findMatch(A).from(Matches<A,B>).orElse(Function<A,B>)", "cases");
            notNull(defaultCase, "findMatch(A).from(Matches<A,B>).orElse(Function<A,B>)", "defaultCase");
            return cases
                    .find(abMatch -> abMatch.test(input)).toVavrOption()
                    .map(c -> c.getResultsFor(input))
                    .getOrElse(() -> defaultCase.apply(input));
        }
    }

    /**
     * Wrap the application of fn so that if an exception is thrown then Either.left() is returned otherwise Either.right() is returned.
     *
     * @param fn  this function declares that it throws EX
     * @param <A> the type of the input data
     * @param <R> the return type of the function
     * @param <E> the checked exception that could be thrown by the function
     * @return a function that will return Either.left() if the supplied fn throws an exception, Either.right() otherwise
     * @throws NullPointerException if fn is null.
     */
    public static <A, R, E extends Exception> Function<A, Either<Exception, R>> $(final FunctionWithExceptionDeclaration<A, R, E> fn) {
        notNull(fn, "$(FunctionWithExceptionDeclaration<A,R,E>)", "fn");

        return a -> {
            try {
                return Either.right(fn.apply(a));
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

    public static Function<Integer, List<Integer>> repeat(final int howMany) {
        return integer -> Functional.init(counter -> integer, howMany);
    }

    @VisibleForTesting
    static <B> Stream<B> asStream(final Iterable<B> input) {
        return StreamSupport.stream(input.spliterator(), false);
    }

    private static <T> T notNull(final T t, final String functionName, final String parameterName) {
        return notNull(t, functionName + ": " + parameterName + " must not be null");
    }

    private static <T> T notNull(final T t, final String fullMessage) {
        if (isNull(t)) throw new IllegalArgumentException(fullMessage);
        return t;
    }


}
