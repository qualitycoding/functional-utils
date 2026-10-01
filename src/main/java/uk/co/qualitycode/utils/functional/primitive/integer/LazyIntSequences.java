package uk.co.qualitycode.utils.functional.primitive.integer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * The machinery behind {@link Functional.Lazy}: each operation describes how to produce its next element, and this
 * class turns that into a sequence with the same contract as the generic {@code Functional.Lazy} sequences:
 * <ul>
 *     <li>a sequence may be iterated only once; a second {@code iterator()} call throws
 *     {@link UnsupportedOperationException}</li>
 *     <li>{@code hasNext()} may be called any number of times without advancing</li>
 *     <li>{@code next()} past the end throws {@link NoSuchElementException}</li>
 *     <li>{@code remove()} is not supported</li>
 *     <li>nothing is computed until the sequence is iterated, and then only as far as it is consumed</li>
 * </ul>
 * Every message names the operation, as the generic ones do.
 */
final class LazyIntSequences {
    private LazyIntSequences() {
    }

    /**
     * Produces the next element by passing it to {@code sink} and returning true, or returns false at the end.
     * Once it has returned false it is not called again.
     */
    @FunctionalInterface
    interface IntSource {
        boolean tryAdvance(IntConsumer sink);
    }

    /**
     * The reference-valued counterpart of {@link IntSource}, for operations such as zip that produce objects.
     */
    @FunctionalInterface
    interface Source<T> {
        boolean tryAdvance(Consumer<? super T> sink);
    }

    /**
     * @param operation names the operation in messages, e.g. {@code "Lazy.map(Func_int_int,IntIterable)"}
     * @param source    creates the element source when the sequence is first iterated
     */
    static IntIterable of(final String operation, final Supplier<IntSource> source) {
        final AtomicBoolean iterated = new AtomicBoolean();
        return () -> {
            if (!iterated.compareAndSet(false, true))
                throw new UnsupportedOperationException(operation + ": this Iterable does not allow multiple Iterators");
            return new LookaheadIntIterator(operation, source.get());
        };
    }

    static <T> Iterable<T> ofReferences(final String operation, final Supplier<Source<T>> source) {
        final AtomicBoolean iterated = new AtomicBoolean();
        return () -> {
            if (!iterated.compareAndSet(false, true))
                throw new UnsupportedOperationException(operation + ": this Iterable does not allow multiple Iterators");
            return new LookaheadIterator<>(operation, source.get());
        };
    }

    /**
     * A first-in, first-out queue of unboxed ints: a ring buffer that doubles when full.
     */
    static final class IntQueue {
        private int[] items = new int[8];
        private int head;
        private int size;

        boolean isEmpty() {
            return size == 0;
        }

        void add(final int value) {
            if (size == items.length) grow();
            items[(head + size) % items.length] = value;
            size++;
        }

        int remove() {
            if (size == 0) throw new NoSuchElementException("IntQueue is empty");
            final int value = items[head];
            head = (head + 1) % items.length;
            size--;
            return value;
        }

        private void grow() {
            final int[] larger = new int[items.length * 2];
            for (int i = 0; i < size; i++) larger[i] = items[(head + i) % items.length];
            items = larger;
            head = 0;
        }
    }

    /**
     * Buffers one element so that {@code hasNext()} can be answered without advancing.
     */
    private static final class LookaheadIntIterator implements IntIterator {
        private final String operation;
        private final IntSource source;
        private boolean buffered;
        private boolean exhausted;
        private int value;

        LookaheadIntIterator(final String operation, final IntSource source) {
            this.operation = operation;
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            if (!buffered && !exhausted) {
                buffered = source.tryAdvance(next -> value = next);
                exhausted = !buffered;
            }
            return buffered;
        }

        @Override
        public int next() {
            if (!hasNext())
                throw new NoSuchElementException(operation + ": cannot seek beyond the end of the sequence");
            buffered = false;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(operation + ": it is not possible to remove elements from this sequence");
        }
    }

    private static final class LookaheadIterator<T> implements Iterator<T> {
        private final String operation;
        private final Source<T> source;
        private boolean buffered;
        private boolean exhausted;
        private T value;

        LookaheadIterator(final String operation, final Source<T> source) {
            this.operation = operation;
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            if (!buffered && !exhausted) {
                buffered = source.tryAdvance(next -> value = next);
                exhausted = !buffered;
            }
            return buffered;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException(operation + ": cannot seek beyond the end of the sequence");
            buffered = false;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(operation + ": it is not possible to remove elements from this sequence");
        }
    }
}
