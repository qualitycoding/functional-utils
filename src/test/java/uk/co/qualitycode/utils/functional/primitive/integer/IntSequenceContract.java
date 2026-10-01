package uk.co.qualitycode.utils.functional.primitive.integer;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * The contract of a lazy int sequence, matching the generic {@code SequenceTest}: the expected elements, one iterator
 * per sequence, {@code hasNext()} without side effects, and the documented failures.
 */
abstract class IntSequenceContract {
    /**
     * @return a fresh sequence on each call
     */
    protected abstract IntIterable sequence();

    protected abstract int[] expected();

    /**
     * @return the operation as named in messages, e.g. {@code "Lazy.map(Func_int_int,IntIterable)"}
     */
    protected abstract String operation();

    static int[] drain(final IntIterator iterator) {
        final PrimitiveIterator.OfInt adapted = new PrimitiveIterator.OfInt() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public int nextInt() {
                return iterator.next();
            }
        };
        return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(adapted, Spliterator.ORDERED), false).toArray();
    }

    @Test
    void yieldsTheExpectedElements() {
        assertThat(drain(sequence().iterator())).containsExactly(expected());
    }

    @Test
    void allowsOnlyOneIterator() {
        final IntIterable sequence = sequence();
        sequence.iterator();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(sequence::iterator)
                .withMessage(operation() + ": this Iterable does not allow multiple Iterators");
    }

    @Test
    void hasNextDoesNotAdvance() {
        final IntIterator iterator = sequence().iterator();
        iterator.hasNext();
        iterator.hasNext();

        assertThat(drain(iterator)).containsExactly(expected());
    }

    @Test
    void nextPastTheEndThrows() {
        final IntIterator iterator = sequence().iterator();
        drain(iterator);

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(iterator::next)
                .withMessage(operation() + ": cannot seek beyond the end of the sequence");
    }

    @Test
    void removeIsUnsupported() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> sequence().iterator().remove())
                .withMessage(operation() + ": it is not possible to remove elements from this sequence");
    }
}
