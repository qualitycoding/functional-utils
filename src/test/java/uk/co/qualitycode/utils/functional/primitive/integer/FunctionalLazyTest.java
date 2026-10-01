package uk.co.qualitycode.utils.functional.primitive.integer;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static uk.co.qualitycode.utils.functional.primitive.integer.IntSequenceContract.drain;

/**
 * The lazy int sequences: each operation against the sequence contract, then laziness, infinite sequences and errors.
 */
class FunctionalLazyTest {
    private static IntList ints(final int... values) {
        return new IntList(values);
    }

    private static int[] drained(final IntIterable sequence) {
        return drain(sequence.iterator());
    }

    @Nested
    class Append extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.append(9, ints(1, 2, 3));
        }

        @Override
        protected int[] expected() {
            return new int[]{1, 2, 3, 9};
        }

        @Override
        protected String operation() {
            return "Lazy.append(int,IntIterable)";
        }
    }

    @Nested
    class Map extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.map(x -> x * x, ints(1, 2, 3));
        }

        @Override
        protected int[] expected() {
            return new int[]{1, 4, 9};
        }

        @Override
        protected String operation() {
            return "Lazy.map(Func_int_int,IntIterable)";
        }
    }

    @Nested
    class Mapi extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.mapi((i, x) -> i * 100 + x, ints(5, 6, 7));
        }

        @Override
        protected int[] expected() {
            return new int[]{5, 106, 207};
        }

        @Override
        protected String operation() {
            return "Lazy.mapi(Func2_int_int_int,IntIterable)";
        }
    }

    @Nested
    class Concat extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.concat(ints(1, 2), ints(3));
        }

        @Override
        protected int[] expected() {
            return new int[]{1, 2, 3};
        }

        @Override
        protected String operation() {
            return "Lazy.concat(IntIterable,IntIterable)";
        }
    }

    @Nested
    class Filter extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.filter(x -> x % 2 == 0, ints(1, 2, 3, 4));
        }

        @Override
        protected int[] expected() {
            return new int[]{2, 4};
        }

        @Override
        protected String operation() {
            return "Lazy.filter(Predicate_int,IntIterable)";
        }
    }

    @Nested
    class Choose extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.choose(x -> x > 2 ? Option_int.of(x * 10) : Option_int.none(), ints(1, 2, 3, 4));
        }

        @Override
        protected int[] expected() {
            return new int[]{30, 40};
        }

        @Override
        protected String operation() {
            return "Lazy.choose(Func_int_Option_int,IntIterable)";
        }
    }

    @Nested
    class Init extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.init(i -> i * 10, 3);
        }

        @Override
        protected int[] expected() {
            return new int[]{10, 20, 30};
        }

        @Override
        protected String operation() {
            return "Lazy.init(Func_int_int,int)";
        }
    }

    @Nested
    class FlatMap extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.flatMap(x -> ints(x, -x), ints(1, 2));
        }

        @Override
        protected int[] expected() {
            return new int[]{1, -1, 2, -2};
        }

        @Override
        protected String operation() {
            return "Lazy.flatMap(Func_int_T<IntIterable>,IntIterable)";
        }
    }

    @Nested
    class Skip extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.skip(2, ints(1, 2, 3, 4));
        }

        @Override
        protected int[] expected() {
            return new int[]{3, 4};
        }

        @Override
        protected String operation() {
            return "Lazy.skip(int,IntIterable)";
        }
    }

    @Nested
    class SkipWhile extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.skipWhile(x -> x < 3, ints(1, 2, 3, 1));
        }

        @Override
        protected int[] expected() {
            return new int[]{3, 1};
        }

        @Override
        protected String operation() {
            return "Lazy.skipWhile(Predicate_int,IntIterable)";
        }
    }

    @Nested
    class Take extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.take(2, ints(1, 2, 3));
        }

        @Override
        protected int[] expected() {
            return new int[]{1, 2};
        }

        @Override
        protected String operation() {
            return "Lazy.take(int,IntIterable)";
        }
    }

    @Nested
    class TakeWhile extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.takeWhile(x -> x < 3, ints(1, 2, 3, 1));
        }

        @Override
        protected int[] expected() {
            return new int[]{1, 2};
        }

        @Override
        protected String operation() {
            return "Lazy.takeWhile(Predicate_int,IntIterable)";
        }
    }

    @Nested
    class Unfold extends IntSequenceContract {
        @Override
        protected IntIterable sequence() {
            return Functional.Lazy.unfold(s -> s * s, s -> s + 1, s -> s * s >= 30, 1);
        }

        @Override
        protected int[] expected() {
            return new int[]{1, 4, 9, 16, 25};
        }

        @Override
        protected String operation() {
            return "Lazy.unfold(Func_int_int,Func_int_int,Predicate_int,int)";
        }
    }

    @Nested
    class Laziness {
        @Test
        void computesNothingUntilIterated() {
            final AtomicInteger evaluations = new AtomicInteger();

            Functional.Lazy.map(x -> evaluations.incrementAndGet(), Functional.Lazy.init(i -> i));

            assertThat(evaluations).hasValue(0);
        }

        @Test
        void computesOnlyAsFarAsConsumed() {
            final AtomicInteger evaluations = new AtomicInteger();
            final IntIterator iterator = Functional.Lazy.map(x -> evaluations.incrementAndGet(), Functional.Lazy.init(i -> i)).iterator();

            iterator.next();
            iterator.next();

            assertThat(evaluations).hasValue(2);
        }

        @Test
        void takeWhileStopsEvaluatingAtTheFirstFailure() {
            final AtomicInteger evaluations = new AtomicInteger();

            drained(Functional.Lazy.takeWhile(x -> evaluations.incrementAndGet() > 0 && x < 3, Functional.Lazy.init(i -> i)));

            assertThat(evaluations).hasValue(3);
        }
    }

    @Nested
    class InfiniteSequences {
        @Test
        void initWithoutABoundIsInfinite() {
            assertThat(drained(Functional.Lazy.take(4, Functional.Lazy.init(i -> i * i)))).containsExactly(1, 4, 9, 16);
        }

        @Test
        void unfoldThatNeverFinishesIsInfinite() {
            assertThat(drained(Functional.Lazy.takeWhile(x -> x < 50, Functional.Lazy.unfold(s -> s, s -> s * 2, s -> false, 1))))
                    .containsExactly(1, 2, 4, 8, 16, 32);
        }

        @Test
        void curriedOperationsComposeIntoAPipeline() {
            assertThat(drained(Functional.Lazy.take(3).apply(Functional.Lazy.filter(x -> x % 3 == 0).apply(Functional.Lazy.init(i -> i)))))
                    .containsExactly(3, 6, 9);
        }
    }

    @Nested
    class EdgeCases {
        @Test
        void acceptsASinglePassInput() {
            assertThat(drained(Functional.Lazy.map(x -> -x, Iterators.reverse(ints(1, 2, 3))))).containsExactly(-3, -2, -1);
        }

        @Test
        void takeOfMoreThanThereAreStopsAtTheEnd() {
            assertThat(drained(Functional.Lazy.take(5, ints(1, 2)))).containsExactly(1, 2);
        }

        @Test
        void skipOfMoreThanThereAreIsEmpty() {
            assertThat(drained(Functional.Lazy.skip(5, ints(1, 2)))).isEmpty();
        }

        @Test
        void initOfNothingIsEmpty() {
            assertThat(drained(Functional.Lazy.init(i -> i, 0))).isEmpty();
        }

        @Test
        void rejectsNegativeCounts() {
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.take(-1, ints(1)))
                    .withMessage("Lazy.take(int,IntIterable): howMany must not be negative");
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.skip(-1, ints(1)))
                    .withMessage("Lazy.skip(int,IntIterable): howMany must not be negative");
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.init(i -> i, -1))
                    .withMessage("Lazy.init(Func_int_int,int): howMany must not be negative");
        }

        @Test
        void rejectsNullArguments() {
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.filter(x -> true, null))
                    .withMessage("Lazy.filter(Predicate_int,IntIterable): input must not be null");
            assertThatIllegalArgumentException().isThrownBy(() -> Functional.Lazy.map(null, ints(1)))
                    .withMessage("Lazy.map(Func_int_int,IntIterable): f must not be null");
        }
    }

    @Nested
    class Zipping {
        @Test
        void zipPairsElements() {
            assertThat(io.vavr.collection.List.ofAll(Functional.Lazy.zip(ints(1, 2), ints(3, 4))))
                    .containsExactly(new Tuple2<>(1, 3), new Tuple2<>(2, 4));
        }

        @Test
        void zip3TriplesElements() {
            assertThat(io.vavr.collection.List.ofAll(Functional.Lazy.zip3(ints(1, 2), ints(3, 4), ints(5, 6))))
                    .containsExactly(new Tuple3<>(1, 3, 5), new Tuple3<>(2, 4, 6));
        }

        @Test
        void zipReportsDifferentLengthsWhenIterated() {
            final Iterable<Tuple2<Integer, Integer>> zipped = Functional.Lazy.zip(ints(1, 2, 3), ints(1, 2));

            assertThatIllegalArgumentException().isThrownBy(() -> io.vavr.collection.List.ofAll(zipped))
                    .withMessage("Lazy.zip(IntIterable,IntIterable): cannot zip two sequences of different lengths");
        }

        @Test
        void zip3ReportsDifferentLengthsWhenIterated() {
            final Iterable<Tuple3<Integer, Integer, Integer>> zipped = Functional.Lazy.zip3(ints(1), ints(1), ints());

            assertThatIllegalArgumentException().isThrownBy(() -> io.vavr.collection.List.ofAll(zipped))
                    .withMessage("Lazy.zip3(IntIterable,IntIterable,IntIterable): cannot zip three sequences of different lengths");
        }
    }
}
