package uk.co.qualitycode.utils.functional.primitive.integer;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * /**
 * Lazily evaluated int sequences: the int counterparts of the generic {@code Functional.Lazy} operations. Nothing is
 * computed until a sequence is iterated, and then only as far as it is consumed, so infinite sequences work. Like
 * the generic ones, each sequence may be iterated only once.
 * <p>
 * Also reachable as {@code Functional.Lazy}, kept as an alias for existing code. The constructor is package-private
 * only so that alias can extend this class.
 */
public class LazySequences {
    LazySequences() {
    }

    private static final IntIterator NO_INTS = new IntList().iterator();

    public static IntIterable append(final int value, final IntIterable input) {
        final String op = "Lazy.append(int,IntIterable)";
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> concatenation(input.iterator(), new IntList(new int[]{value}).iterator()));
    }

    public static Function<IntIterable, IntIterable> append(final int value) {
        return input -> append(value, input);
    }

    public static IntIterable map(final Func_int_int f, final IntIterable input) {
        final String op = "Lazy.map(Func_int_int,IntIterable)";
        requireArgument(f, op, "f");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> {
            final IntIterator values = input.iterator();
            return sink -> {
                if (!values.hasNext()) return false;
                sink.accept(f.apply(values.next()));
                return true;
            };
        });
    }

    public static Function<IntIterable, IntIterable> map(final Func_int_int f) {
        return input -> map(f, input);
    }

    /**
     * @param f given the zero-based index and the element, the mapped element
     */
    public static IntIterable mapi(final Func2_int_int_int f, final IntIterable input) {
        final String op = "Lazy.mapi(Func2_int_int_int,IntIterable)";
        requireArgument(f, op, "f");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> new IntSequenceSupport.IntSource() {
            private final IntIterator values = input.iterator();
            private int index;

            @Override
            public boolean tryAdvance(final java.util.function.IntConsumer sink) {
                if (!values.hasNext()) return false;
                sink.accept(f.apply(index++, values.next()));
                return true;
            }
        });
    }

    public static Function<IntIterable, IntIterable> mapi(final Func2_int_int_int f) {
        return input -> mapi(f, input);
    }

    public static IntIterable concat(final IntIterable input1, final IntIterable input2) {
        final String op = "Lazy.concat(IntIterable,IntIterable)";
        requireArgument(input1, op, "input1");
        requireArgument(input2, op, "input2");
        return IntSequenceSupport.of(op, () -> concatenation(input1.iterator(), input2.iterator()));
    }

    public static IntIterable filter(final Predicate_int predicate, final IntIterable input) {
        final String op = "Lazy.filter(Predicate_int,IntIterable)";
        requireArgument(predicate, op, "predicate");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> {
            final IntIterator values = input.iterator();
            return sink -> {
                while (values.hasNext()) {
                    final int value = values.next();
                    if (predicate.test(value)) {
                        sink.accept(value);
                        return true;
                    }
                }
                return false;
            };
        });
    }

    public static Function<IntIterable, IntIterable> filter(final Predicate_int predicate) {
        return input -> filter(predicate, input);
    }

    public static IntIterable choose(final Func_int_Option_int chooser, final IntIterable input) {
        final String op = "Lazy.choose(Func_int_Option_int,IntIterable)";
        requireArgument(chooser, op, "chooser");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> {
            final IntIterator values = input.iterator();
            return sink -> {
                while (values.hasNext()) {
                    final Option_int chosen = chooser.apply(values.next());
                    if (chosen.isSome()) {
                        sink.accept(chosen.get());
                        return true;
                    }
                }
                return false;
            };
        });
    }

    public static Function<IntIterable, IntIterable> choose(final Func_int_Option_int chooser) {
        return input -> choose(chooser, input);
    }

    /**
     * @param f       given the one-based position, the element
     * @param howMany the number of elements
     */
    public static IntIterable init(final Func_int_int f, final int howMany) {
        final String op = "Lazy.init(Func_int_int,int)";
        requireArgument(f, op, "f");
        if (howMany < 0) throw new IllegalArgumentException(op + ": howMany must not be negative");
        return IntSequenceSupport.of(op, () -> unfolding(f, i -> i + 1, i -> i > howMany, 1));
    }

    /**
     * An infinite sequence.
     *
     * @param f given the one-based position, the element
     */
    public static IntIterable init(final Func_int_int f) {
        final String op = "Lazy.init(Func_int_int)";
        requireArgument(f, op, "f");
        return IntSequenceSupport.of(op, () -> unfolding(f, i -> i + 1, i -> false, 1));
    }

    public static IntIterable flatMap(final Func_int_T<? extends IntIterable> f, final IntIterable input) {
        final String op = "Lazy.flatMap(Func_int_T<IntIterable>,IntIterable)";
        requireArgument(f, op, "f");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> new IntSequenceSupport.IntSource() {
            private final IntIterator outer = input.iterator();
            private IntIterator inner = NO_INTS;

            @Override
            public boolean tryAdvance(final java.util.function.IntConsumer sink) {
                while (!inner.hasNext()) {
                    if (!outer.hasNext()) return false;
                    inner = f.apply(outer.next()).iterator();
                }
                sink.accept(inner.next());
                return true;
            }
        });
    }

    public static Function<IntIterable, IntIterable> flatMap(final Func_int_T<? extends IntIterable> f) {
        return input -> flatMap(f, input);
    }

    public static IntIterable skip(final int howMany, final IntIterable input) {
        final String op = "Lazy.skip(int,IntIterable)";
        if (howMany < 0) throw new IllegalArgumentException(op + ": howMany must not be negative");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> new IntSequenceSupport.IntSource() {
            private final IntIterator values = input.iterator();
            private int toSkip = howMany;

            @Override
            public boolean tryAdvance(final java.util.function.IntConsumer sink) {
                for (; toSkip > 0 && values.hasNext(); toSkip--) values.next();
                if (!values.hasNext()) return false;
                sink.accept(values.next());
                return true;
            }
        });
    }

    public static Function<IntIterable, IntIterable> skip(final int howMany) {
        return input -> skip(howMany, input);
    }

    public static IntIterable skipWhile(final Predicate_int predicate, final IntIterable input) {
        final String op = "Lazy.skipWhile(Predicate_int,IntIterable)";
        requireArgument(predicate, op, "predicate");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> new IntSequenceSupport.IntSource() {
            private final IntIterator values = input.iterator();
            private boolean skipping = true;

            @Override
            public boolean tryAdvance(final java.util.function.IntConsumer sink) {
                while (values.hasNext()) {
                    final int value = values.next();
                    if (!skipping || !predicate.test(value)) {
                        skipping = false;
                        sink.accept(value);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static Function<IntIterable, IntIterable> skipWhile(final Predicate_int predicate) {
        return input -> skipWhile(predicate, input);
    }

    public static IntIterable take(final int howMany, final IntIterable input) {
        final String op = "Lazy.take(int,IntIterable)";
        if (howMany < 0) throw new IllegalArgumentException(op + ": howMany must not be negative");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> new IntSequenceSupport.IntSource() {
            private final IntIterator values = input.iterator();
            private int remaining = howMany;

            @Override
            public boolean tryAdvance(final java.util.function.IntConsumer sink) {
                if (remaining == 0 || !values.hasNext()) return false;
                remaining--;
                sink.accept(values.next());
                return true;
            }
        });
    }

    public static Function<IntIterable, IntIterable> take(final int howMany) {
        return input -> take(howMany, input);
    }

    public static IntIterable takeWhile(final Predicate_int predicate, final IntIterable input) {
        final String op = "Lazy.takeWhile(Predicate_int,IntIterable)";
        requireArgument(predicate, op, "predicate");
        requireArgument(input, op, "input");
        return IntSequenceSupport.of(op, () -> {
            final IntIterator values = input.iterator();
            return sink -> {
                if (!values.hasNext()) return false;
                final int value = values.next();
                if (!predicate.test(value)) return false;
                sink.accept(value);
                return true;
            };
        });
    }

    public static Function<IntIterable, IntIterable> takeWhile(final Predicate_int predicate) {
        return input -> takeWhile(predicate, input);
    }

    /**
     * Emit {@code value(state)}, then move to {@code next(state)}, until {@code finished(state)} holds. Unbounded
     * if it never does.
     */
    public static IntIterable unfold(final Func_int_int value, final Func_int_int next, final Predicate_int finished, final int seed) {
        final String op = "Lazy.unfold(Func_int_int,Func_int_int,Predicate_int,int)";
        requireArgument(value, op, "value");
        requireArgument(next, op, "next");
        requireArgument(finished, op, "finished");
        return IntSequenceSupport.of(op, () -> unfolding(value, next, finished, seed));
    }

    /**
     * @throws IllegalArgumentException during iteration, if the sequences turn out to differ in length
     */
    public static Iterable<Tuple2<Integer, Integer>> zip(final IntIterable input1, final IntIterable input2) {
        final String op = "Lazy.zip(IntIterable,IntIterable)";
        requireArgument(input1, op, "input1");
        requireArgument(input2, op, "input2");
        return IntSequenceSupport.ofReferences(op, () -> {
            final IntIterator as = input1.iterator();
            final IntIterator bs = input2.iterator();
            return sink -> {
                final boolean more = as.hasNext();
                if (more != bs.hasNext())
                    throw new IllegalArgumentException(op + ": cannot zip two sequences of different lengths");
                if (!more) return false;
                sink.accept(new Tuple2<>(as.next(), bs.next()));
                return true;
            };
        });
    }

    /**
     * @throws IllegalArgumentException during iteration, if the sequences turn out to differ in length
     */
    public static Iterable<Tuple3<Integer, Integer, Integer>> zip3(final IntIterable input1, final IntIterable input2, final IntIterable input3) {
        final String op = "Lazy.zip3(IntIterable,IntIterable,IntIterable)";
        requireArgument(input1, op, "input1");
        requireArgument(input2, op, "input2");
        requireArgument(input3, op, "input3");
        return IntSequenceSupport.ofReferences(op, () -> {
            final IntIterator as = input1.iterator();
            final IntIterator bs = input2.iterator();
            final IntIterator cs = input3.iterator();
            return sink -> {
                final boolean more = as.hasNext();
                if (more != bs.hasNext() || more != cs.hasNext())
                    throw new IllegalArgumentException(op + ": cannot zip three sequences of different lengths");
                if (!more) return false;
                sink.accept(new Tuple3<>(as.next(), bs.next(), cs.next()));
                return true;
            };
        });
    }

    /**
     * Lazily split a sequence into the elements that satisfy the predicate and those that do not, preserving order.
     * The int counterpart of the generic {@code Lazy.partition(Predicate<T>, Iterable<T>)}: both halves draw on a
     * single traversal of {@code input}, the predicate is evaluated exactly once per element, and elements for
     * the other half wait, unboxed, until that half asks for them.
     *
     * @param predicate decides which half each element belongs to
     * @param input     the sequence to split
     * @return the elements satisfying the predicate, and the remaining elements
     */
    public static Tuple2<IntIterable, IntIterable> partition(final Predicate_int predicate, final IntIterable input) {
        final String op = "Lazy.partition(Predicate_int,IntIterable)";
        requireArgument(predicate, op, "predicate");
        requireArgument(input, op, "input");
        final IntPartitioner partitioner = new IntPartitioner(predicate, input);
        return new Tuple2<>(partitioner.half(true, op), partitioner.half(false, op));
    }

    public static Function<IntIterable, Tuple2<IntIterable, IntIterable>> partition(final Predicate_int predicate) {
        requireArgument(predicate, "Lazy.partition(Predicate_int)", "predicate");
        return input -> partition(predicate, input);
    }

    /**
     * The shared state behind a lazy int partition: one traversal of the input and a queue for each half.
     */
    private static final class IntPartitioner {
        private final Predicate_int predicate;
        private final IntIterable input;
        private final IntSequenceSupport.IntQueue matching = new IntSequenceSupport.IntQueue();
        private final IntSequenceSupport.IntQueue rest = new IntSequenceSupport.IntQueue();
        private final io.vavr.Lazy<IntIterator> source;

        IntPartitioner(final Predicate_int predicate, final IntIterable input) {
            this.predicate = predicate;
            this.input = input;
            this.source = io.vavr.Lazy.of(input::iterator);
        }

        private boolean fill(final IntSequenceSupport.IntQueue wanted) {
            while (wanted.isEmpty() && source.get().hasNext()) {
                final int element = source.get().next();
                (predicate.test(element) ? matching : rest).add(element);
            }
            return !wanted.isEmpty();
        }

        IntIterable half(final boolean matchingHalf, final String op) {
            final IntSequenceSupport.IntQueue queue = matchingHalf ? matching : rest;
            return IntSequenceSupport.of(op, () -> sink -> {
                if (!fill(queue)) return false;
                sink.accept(queue.remove());
                return true;
            });
        }
    }

    private static IntSequenceSupport.IntSource concatenation(final IntIterator first, final IntIterator second) {
        return sink -> {
            final IntIterator current = first.hasNext() ? first : second;
            if (!current.hasNext()) return false;
            sink.accept(current.next());
            return true;
        };
    }

    private static IntSequenceSupport.IntSource unfolding(final Func_int_int value, final Func_int_int next, final Predicate_int finished, final int seed) {
        return new IntSequenceSupport.IntSource() {
            private int state = seed;

            @Override
            public boolean tryAdvance(final java.util.function.IntConsumer sink) {
                if (finished.test(state)) return false;
                sink.accept(value.apply(state));
                state = next.apply(state);
                return true;
            }
        };
    }

    private static <T> T requireArgument(final T t, final String operation, final String parameterName) {
        return requireNonNull(t, operation + ": " + parameterName + " must not be null");
    }
}
