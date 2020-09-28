package uk.co.qualitycode.utils.functional;

import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThatNoException;

abstract class InfiniteIterableTest<T1, T2, R> extends SequenceTest<T1, T2, R> {
    protected void consumeSequenceWithoutCallingHasNext() {
        final Collection<T2> l = initialValues();
        final Iterable<R> output = testFunction(l);

        final Iterator<R> iterator = output.iterator();
        assertThatNoException()
                .as("read all the elements in the sequence")
                .isThrownBy(() -> Functional.init(i -> iterator.next(), noOfElementsInOutput()));
    }
}
