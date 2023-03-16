package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;

abstract class SequenceTest<T1, T2, R> {
    protected abstract Iterable<T2> initialValues();

    protected abstract Iterable<R> testFunction(final Iterable<T2> l);

    protected abstract String methodNameInExceptionMessage();

    protected abstract int noOfElementsInOutput();

    protected abstract void consumeSequenceWithoutCallingHasNext();

    @Test
    void canConsumeSequenceWithoutCallingHasNext() {
        consumeSequenceWithoutCallingHasNext();
    }

    @Test
    void tryToRemoveFromAnIterator() {
        final Iterable<T2> l = initialValues();
        final Iterable<R> output = testFunction(l);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.iterator().remove())
                .withMessage(methodNameInExceptionMessage() + ": it is not possible to remove elements from this sequence");
    }

    @Test
    void iterableCanOnlyHaveOneIterator() {
        final Iterable<T2> l = initialValues();
        final Iterable<R> output = testFunction(l);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Should not reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(output::iterator)
                .withMessage(methodNameInExceptionMessage() + ": this Iterable does not allow multiple Iterators");
    }

    @Test
    void canCallHasNextMultipleTimesWithoutAdvancingThePosition() {
        if (noOfElementsInOutput() <= 0) return;
        final Iterable<T2> l = initialValues();
        final Iterable<R> output = testFunction(l);
        final Iterator<R> iterator = output.iterator();
        IntStream.range(0, noOfElementsInOutput())
                .forEach(i -> assertThat(iterator.hasNext()).isTrue());
        iterator.next();
        IntStream.range(1, noOfElementsInOutput())
                .forEach(i -> assertThat(iterator.hasNext()).isTrue());
    }
}
