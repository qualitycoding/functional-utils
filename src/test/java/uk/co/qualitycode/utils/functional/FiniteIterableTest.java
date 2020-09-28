package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

abstract class FiniteIterableTest<T1, T2, R> extends SequenceTest<T1, T2, R> {
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

    protected void consumeSequenceWithoutCallingHasNext() {
        final Collection<T2> l = initialValues();
        final Iterable<R> output = testFunction(l);

        final Iterator<R> iterator = output.iterator();
        assertThatNoException()
                .as("read all the elements in the sequence")
                .isThrownBy(() -> Functional.init(i -> iterator.next(), noOfElementsInOutput()));
        assertThatExceptionOfType(NoSuchElementException.class)
                .as("attempt (and fail) to read beyond the end of the sequence")
                .isThrownBy(iterator::next);
    }
}
