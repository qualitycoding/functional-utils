package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.fail;

abstract class IterableResultTest<T1, T2, R> {
    protected abstract Collection<T2> initialValues();

    protected abstract Iterable<R> testFunction(final Iterable<T2> l);

    protected abstract String methodNameInExceptionMessage();

    protected abstract int noOfElementsInOutput();

    @Test
    void nextThrowsIfThereAreNoMoreElements() {
        final Collection<T2> l = initialValues();
        final Iterable<R> output = testFunction(l);

        final Iterator<R> iterator = output.iterator();
        Functional.init(i -> iterator.next(), noOfElementsInOutput());
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(iterator::next)
                .withMessage(methodNameInExceptionMessage() + ": cannot seek beyond the end of the sequence");
    }

    @Test
    void canConsumeSequenceWithoutCallingHasNext() {
        final Collection<T2> l = initialValues();
        final Iterable<R> output = testFunction(l);

        final Iterator<R> iterator = output.iterator();
        assertThatNoException()
                .isThrownBy(() -> Functional.init(i -> iterator.next(), noOfElementsInOutput()));
    }

    @Test
    void tryToRemoveFromAnIterator() {
        final Collection<T2> l = initialValues();
        final Iterable<R> output = testFunction(l);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.iterator().remove())
                .withMessage(methodNameInExceptionMessage() + ": it is not possible to remove elements from this sequence");
    }

    @Test
    void iterableCanOnlyHaveOneIterator() {
        final Collection<T2> l = initialValues();
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
}
