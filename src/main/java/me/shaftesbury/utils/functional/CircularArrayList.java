package me.shaftesbury.utils.functional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CircularArrayList<T> implements List<T>
{
    private final int bufferSize;
    private final ArrayList<T> buffer;
    public CircularArrayList(final Iterable<T> input, final int howMany)
    {
        this.bufferSize = howMany;
        this.buffer = new ArrayList<>(howMany);
        int counter=0;
        final Iterator<T> iterator = input.iterator();
        while(counter<howMany && iterator.hasNext())
        {
            buffer.add(iterator.next());
            ++counter;
        }
        while(counter<howMany)
        {
            final Iterator<T> iterator1 = buffer.iterator();
            while(counter<howMany && iterator1.hasNext())
            {
                buffer.add(iterator1.next());
                ++counter;
            }
        }
    }


    public int size() {
        return bufferSize;
    }


    public boolean isEmpty() {
        return false;
    }


    public boolean contains(final Object o) {
        return buffer.contains(o);
    }


    public Iterator<T> iterator() {
        return buffer.iterator();
    }


    public Object[] toArray() {
        return buffer.toArray();
    }

    @SuppressWarnings("unchecked")

    public <T1>T1[] toArray(final T1[] a) {
        if (a.length < bufferSize)
            // Make a new array of a's runtime type, but my contents:
            return (T1[]) Arrays.copyOf(buffer.toArray(), bufferSize, a.getClass());
        System.arraycopy(buffer.toArray(), 0, a, 0, bufferSize);
        if (a.length > bufferSize)
            a[bufferSize] = null;
        return a;
    }


    public boolean add(final T t) {
        return false;
    }


    public boolean remove(final Object o) {
        return false;
    }


    public boolean containsAll(final Collection<?> c) {
        return buffer.containsAll(c);
    }


    public boolean addAll(final Collection<? extends T> c) {
        return false;
    }


    public boolean addAll(final int index, final Collection<? extends T> c) {
        return false;
    }


    public boolean removeAll(final Collection<?> c) {
        return false;
    }


    public boolean retainAll(final Collection<?> c) {
        return false;
    }


    public void clear() {
        throw new UnsupportedOperationException("This is a read-only container. It is not possible to clear it");
    }


    public T get(final int index) {
        return buffer.get(index);
    }


    public T set(final int index, final T element) {
        return null;
    }


    public void add(final int index, final T element) {

    }


    public T remove(final int index) {
        return null;
    }


    public int indexOf(final Object o) {
        return buffer.indexOf(o);
    }


    public int lastIndexOf(final Object o) {
        return 0; // TODO implement this remembering that this is a circular list
    }


    public ListIterator<T> listIterator() {
        return null;
    }


    public ListIterator<T> listIterator(final int index) {
        return null;
    }


    public List<T> subList(final int fromIndex, final int toIndex) {
        return null;
    }
}
