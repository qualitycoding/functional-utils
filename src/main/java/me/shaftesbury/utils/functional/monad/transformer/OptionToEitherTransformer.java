package me.shaftesbury.utils.functional.monad.transformer;

import io.vavr.control.Either;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Sometimes your chain of functions has to be interrupted because you want to change the containing type.
 * Suppose you have code like:
 * () -&gt; Option&lt;U&gt; -&gt; Option&lt;V&gt; -&gt; List&lt;W&gt;
 * and you want to generate a message if the resultant Option is empty. Normally, you would have to do
 * this manually, like
 * final Option&lt;U&gt; u = Option.some(...);
 * final Option&lt;V&gt; v = u.map(...);
 * if(v.isDefined()) {
 * final W w = v.map(...);
 * ...
 * } else {
 * return "Oh dear, it's empty";
 * }
 * <p>
 * Using a Monad Transformer, the 'if' statement can be replaced by a combination of one of map() etc from Either. For example,
 * final Either&lt;Exception,U&gt; u = of(Option.some(...));
 * final Either&lt;Exception,V&gt; v = u.map(...);
 * final Either&lt;String,V&gt; w1 = v.getOrElse("Oh dear, it's empty");
 * final Either&lt;String,W&gt; w2 = w1.map(...);
 * </p>
 *
 * @param <T>
 */
public class OptionToEitherTransformer<T> {
    public static <R> Either<? extends Exception, R> of(final io.vavr.control.Option<R> option) {
        requireNonNull(option, "option must not be null");
        return option.map(Either::<Exception, R>right).getOrElse(Either.left(new RuntimeException("No value was contained in the option prior to transformation")));
    }

    public static <R> Either<? extends Exception, R> of(final Optional<R> option) {
        requireNonNull(option, "option must not be null");
        return option.map(Either::<Exception, R>right).orElse(Either.left(new RuntimeException("No value was contained in the option prior to transformation")));
    }
}
