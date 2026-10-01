package uk.co.qualitycode.utils.functional;

import uk.co.qualitycode.utils.functional.monad.Option;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static uk.co.qualitycode.utils.functional.Checks.notNull;
import static uk.co.qualitycode.utils.functional.Functional.*;

/**
 * Pattern-matching style selection: the first case whose predicate holds supplies the result.
 * <p>
 * Also reachable as {@code Functional.Matcher}, which is kept as an alias for existing code. The constructor is
 * package-private only so that alias can extend this class.
 */
public class Matching {
    Matching() {
    }

    /**
     * Functional switch statement. Provide a sequence of Cases and a function which will be evaluated if none of the Cases are true.
     *
     * @param input       the value to be tested
     * @param cases       sequence of Match objects
     * @param defaultCase function to be evaluated if none of the Cases are true
     * @param <A>         the type of the element passed to the predicate in the {@link Match}
     * @return the result of the appropriate Match or the result of the 'defaultCase' function
     */
    public static <A> WithMatches<A> findMatch(final A input) {
        return new WithMatches<A>() {
            @Override
            public <B> WithDefaultCase<A, B> from(final Matches<A, B> cases) {
                return defaultCase -> switchBetween(input, cases, defaultCase);
            }
        };
    }

    /**
     * The result type B is fixed by the cases, so a typed set of {@link Matches} can be reused without a type witness.
     */
    public interface WithMatches<A> {
        <B> WithDefaultCase<A, B> from(Matches<A, B> cases);
    }

    public interface WithDefaultCase<A, B> {
        B orElse(Function<A, B> defaultCase);
    }

    /**
     * An ordered set of cases for {@link #findMatch(Object)}. The first case whose predicate holds wins.
     */
    public static final class Matches<A, B> {
        private final io.vavr.collection.List<Match<A, B>> cases;

        private Matches(final io.vavr.collection.List<Match<A, B>> cases) {
            this.cases = cases;
        }

        public static <A, B> Matches<A, B> of(final java.lang.Iterable<Match<A, B>> it) {
            notNull(it, "Matches.of(Iterable<Match<A,B>>)", "it");
            return new Matches<>(io.vavr.collection.List.ofAll(it));
        }

        Option<Match<A, B>> find(final Predicate<? super Match<A, B>> f) {
            return Option.of(cases.find(f));
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
