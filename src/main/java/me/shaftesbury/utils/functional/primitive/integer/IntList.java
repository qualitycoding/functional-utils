package me.shaftesbury.utils.functional.primitive.integer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class IntList implements IntIterable
{
    private final int[] backingStore;
    public IntList(){backingStore=new int[0];}
    public IntList(final int size) {backingStore=new int[size];}
    public IntList(final int[]array) {backingStore = Arrays.copyOf(array, array.length);}
    public IntList(final int[]array, final int size) {backingStore = Arrays.copyOf(array, size);}

    IntList(final int[] array1, final int[] array2)
    {
        backingStore = new int[array1.length + array2.length];
        System.arraycopy(array1,0,backingStore,0,array1.length);
        System.arraycopy(array2,0,backingStore,array1.length,array2.length);
    }

    int[] extractBackingStoreWithoutCopy() { return backingStore; }

    public int size() {
        return backingStore.length;
    }

    public boolean isEmpty() {
        return backingStore.length==0;
    }

    public boolean contains(final int i) {
        return Arrays.binarySearch(backingStore,i)>=0;
    }


    public IntIterator iterator() {
        return new IntIteratorImpl(backingStore);
    }

    public int[] toArray() {
        return Arrays.copyOf(backingStore, backingStore.length);
    }

    @SuppressWarnings("unchecked")
    public <T>T[] toArray(final T[] a) {
        if (a.length < backingStore.length) {
            // Make a new array of a's runtime type, but my contents:
            final Integer[] temp = new Integer[backingStore.length];
            for(int i=0;i<backingStore.length;++i) temp[i]=backingStore[i];
            return (T[]) Arrays.copyOf(temp, backingStore.length, a.getClass());
        }
        System.arraycopy(backingStore, 0, a, 0, backingStore.length);
        if (a.length > backingStore.length)
            a[backingStore.length] = null;
        return a;
    }

    public boolean add(final int integer) {
        return false;
    }

//    public boolean remove(Object o) {
//        return false;
//    }

    public boolean containsAll(final Collection<?> c) {
        return false;
    }

    public boolean addAll(final Collection<? extends Integer> c) {
        return false;
    }

    public boolean addAll(final int index, final Collection<? extends Integer> c) {
        return false;
    }

    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(final Collection<?> c) {
        return false;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public int get(final int index) {
        return backingStore[index];
    }

    public int set(final int index, final int element) {
        throw new UnsupportedOperationException();
    }

    public void add(final int index, final Integer element) {

    }

    public Integer remove(final int index) {
        throw new UnsupportedOperationException();
    }

    public int indexOf(final Object o) {
        return 0;
    }

    public int lastIndexOf(final Object o) {
        return 0;
    }

    public ListIterator<Integer> listIterator() {
        return null;
    }

    public ListIterator<Integer> listIterator(final int index) {
        return null;
    }

    public List<Integer> subList(final int fromIndex, final int toIndex) {
        return null;
    }
}
