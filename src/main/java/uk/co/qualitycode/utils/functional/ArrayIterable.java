package uk.co.qualitycode.utils.functional;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

/**
 * ArrayIterable exists because the native Java array does not implement {@link java.lang.Iterable}. This, therefore, is
 * a helpful wrapper so that arrays can be used almost transparently within this functional library.
 *
 * @param <T> type of the underlying data element
 */
public final class ArrayIterable<T> implements Iterable<T> {
    private final T[] elements;

    private ArrayIterable(final T[] array) {
        // Copy, so that later changes to the caller's array do not show through this Iterable.
        elements = array.clone();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int position;

            @Override
            public boolean hasNext() {
                return position < elements.length;
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException("ArrayIterable: cannot seek beyond the end of the array");
                return elements[position++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove is not permitted in ArrayIterable");
            }
        };
    }

    public static <T> ArrayIterable<T> create(final T[] array) {
        return new ArrayIterable<>(requireNonNull(array, "create(T[]): array must not be null"));
    }
}
