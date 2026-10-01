package uk.co.qualitycode.utils.functional;

import uk.co.qualitycode.utils.functional.monad.Option;
import java.util.Optional;
import java.util.function.Function;

import static uk.co.qualitycode.utils.functional.Checks.notNull;

/**
 * Adapters between flatMap functions over vavr's Option, java.util.Optional and this library's Option.
 * <p>
 * Each is also reachable as {@code Functional.Convert...}, kept as an alias for existing code; constructors are
 * package-private only so those aliases can extend them.
 */
public final class FlatMapConversions {
    private FlatMapConversions() {
    }

    public static class ConvertFlatMapVavrOptionToFlatMapOptional {
        ConvertFlatMapVavrOptionToFlatMapOptional() {
        }

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
        ConvertFlatMapOptionalToFlatMapVavrOption() {
        }

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
        ConvertFlatMapOptionalToFlatMapOption() {
        }

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
        ConvertFlatMapVavrOptionToFlatMapOption() {
        }

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
}
