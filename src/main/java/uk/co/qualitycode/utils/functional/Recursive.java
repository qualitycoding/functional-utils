package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import uk.co.qualitycode.utils.functional.monad.Option;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static uk.co.qualitycode.utils.functional.Checks.notNull;
import static uk.co.qualitycode.utils.functional.Functional.*;

/**
 * Recursive formulations of the core sequence operations.
 * <p>
 * Also reachable as {@code Functional.Rec}, which is kept as an alias for existing code. The constructor is
 * package-private only so that alias can extend this class.
 *
 * See <a href="http://en.wikipedia.org/wiki/Recursion_(computer_science)">Recursion</a>
 * Recursive implementations of (some of) the algorithms contained herein
 */
public class Recursive {

    Recursive() {
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
