package uk.co.qualitycoding.utils.functional;

import uk.co.qualitycoding.utils.functional.monad.Option;
import uk.co.qualitycoding.utils.functional.monad.OptionNoValueAccessException;

import java.util.function.Function;

/**
 * Created by Bob on 16/05/14.
 * <p>
 * Common use patterns written here so that we don't have to wrestle continuously with the clunkiness and waffle that is the Java syntax.
 */
public final class FunctionalHelper {
    /**
     * Given an input sequence of {@link Option} types return a new lazily-evaluated sequence of those which are {@link Option#Some()}
     *
     * @param input the input sequence
     * @param <T>   the type of the element underlying the {@link Option}
     * @return a sequence of {@link Option} which are {@link Option#Some()}
     */
    public static <T> Iterable2<Option<T>> areSome(final Iterable<Option<T>> input) {
        return IterableHelper.create(input).filter(Option::isSome);
    }

    /**
     * Given an input sequence of {@link Option} types return a new lazily-evaluated sequence of those which are {@link Option#None()}
     *
     * @param input the input sequence
     * @param <T>   the type of the element underlying the {@link Option}
     * @return a sequence of {@link Option} which are {@link Option#None()}
     */
    public static <T> Iterable2<Option<T>> areNone(final Iterable<Option<T>> input) {
        return IterableHelper.create(input).filter(Option::isNone);
    }

    /**
     * Given an input sequence of {@link Option} types return true if all the elements are {@link Option#Some()}
     *
     * @param input the input sequence
     * @param <T>   the type of the element underlying the {@link Option}
     * @return true if all the elements are {@link Option#Some()}, false otherwise
     */
    public static <T> boolean allSome(final Iterable<Option<T>> input) {
        return !IterableHelper.create(input).exists(Option::isNone);
    }

    /**
     * Given an input sequence of {@link Option} types return true if all the elements are {@link Option#None()}
     *
     * @param input the input sequence
     * @param <T>   the type of the element underlying the {@link Option}
     * @return true if all the elements are {@link Option#None()}}, false otherwise
     */
    public static <T> boolean allNone(final Iterable<Option<T>> input) {
        return !IterableHelper.create(input).exists(Option::isSome);
    }

    /**
     * Given an input sequence of {@link Option} return a lazily-evaluated sequence containing
     * the underlying data elements. Note that the proper way to perform this action is to use
     * {@link Option#bind(Function)}
     *
     * @param input the input sequence
     * @param <T>   the type of the underlying data
     * @return a lazily-evaluated sequence containing the underlying data
     * @throws OptionNoValueAccessException if {@link Option#isNone()} is true for any element in <tt>input</tt>
     */
    public static <T> Iterable2<T> some(final Iterable<Option<T>> input) {
        return IterableHelper.create(input).map(tOption -> tOption.Some());
    }

    private FunctionalHelper() {
    }
}
