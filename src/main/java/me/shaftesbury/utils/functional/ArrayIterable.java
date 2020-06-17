package me.shaftesbury.utils.functional;

import java.util.Iterator;

/**
 * ArrayIterable exists because the native Java array does not implement {@link java.lang.Iterable}. This, therefore, is
 * a helpful wrapper so that arrays can be used almost transparently within this functional library.
 * @param <T> type of the underlying data element
 */
public final class ArrayIterable<T> implements  Iterable<T>
{
    private final T[] _a;
    private ArrayIterable(final T[] array) { _a = array; }

    /**
     * Expose a new {@link java.util.Iterator} to the underlying data array
     * @return a {@link java.util.Iterator}
     */
    public final Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            private final T[] _array = _a;
            private int _posn = 0;

            public final boolean hasNext() { return _posn < _array.length; }
            public final T next() { return _array[_posn++]; }
            public void remove() { throw new UnsupportedOperationException("remove is not permitted in ArrayIterable"); }
        };
    }

    /**
     * Factory method to create a new <tt>ArrayIterable</tt> given an array of <tt>T</tt>.
     * @param array the input array
     * @param <T> the type of the elements in the input array
     * @return an ArrayIterable object which wraps the input array
     */
    public final static <T>ArrayIterable<T> create(final T[] array) { return new ArrayIterable<>(array); }
}
