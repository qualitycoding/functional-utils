package uk.co.qualitycode.utils.functional;

import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.function.Function;

/**
 * Created by Bob on 16/05/14.
 * <p>
 * Common use patterns written here so that we don't have to wrestle continuously with the clunkiness and waffle that is the Java syntax.
 */
public final class FunctionalHelper {
    /**
     * Given an input sequence of {@link Option} types return a new lazily-evaluated sequence of those which are {@link Option#isSome()} ()}
     *
     * @param input the input sequence
     * @param <T>   the type of the element underlying the {@link Option}
     * @return a sequence of {@link Option} which are {@link Option#isSome()} ()}
     */
    public static <T> Iterable2<Option<T>> areSome(final Iterable<Option<T>> input) {
        return Iterable2.of(input).filter(Option::isSome);
    }

    /**
     * Given an input sequence of {@link Option} types return a new lazily-evaluated sequence of those which are {@link Option#none()}
     *
     * @param input the input sequence
     * @param <T>   the type of the element underlying the {@link Option}
     * @return a sequence of {@link Option} which are {@link Option#none()}
     */
    public static <T> Iterable2<Option<T>> areNone(final Iterable<Option<T>> input) {
        return Iterable2.of(input).filter(Option::isNone);
    }

    /**
     * Given an input sequence of {@link Option} types return true if all the elements are {@link Option#isSome()} ()}
     *
     * @param input the input sequence
     * @param <T>   the type of the element underlying the {@link Option}
     * @return true if all the elements are {@link Option#isSome()} ()}, false otherwise
     */
    public static <T> boolean allSome(final Iterable<Option<T>> input) {
        return !Iterable2.of(input).exists(Option::isNone);
    }

    /**
     * Given an input sequence of {@link Option} types return true if all the elements are {@link Option#none()}
     *
     * @param input the input sequence
     * @param <T>   the type of the element underlying the {@link Option}
     * @return true if all the elements are {@link Option#none()}}, false otherwise
     */
    public static <T> boolean allNone(final Iterable<Option<T>> input) {
        return !Iterable2.of(input).exists(Option::isSome);
    }

    /**
     * Given an input sequence of {@link Option} return a lazily-evaluated sequence containing
     * the underlying data elements. Note that the proper way to perform this action is to use
     * {@link Option#bind(Function)}
     *
     * @param input the input sequence
     * @param <T>   the type of the underlying data
     * @return a lazily-evaluated sequence containing the underlying data
     * @throws java.util.NoSuchElementException if {@link Option#isNone()} is true for any element in <tt>input</tt>
     */
    public static <T> Iterable2<T> some(final Iterable<Option<T>> input) {
        return Iterable2.of(input).map(Option::get);
    }

    private FunctionalHelper() {
    }
}
