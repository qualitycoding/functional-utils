package uk.co.qualitycode.utils.functional.primitive.integer;

import java.util.Arrays;

public final class IntList implements IntIterable {
    private final int[] backingStore;

    public IntList() {
        backingStore = new int[0];
    }

    public IntList(final int size) {
        backingStore = new int[size];
    }

    public IntList(final int[] array) {
        backingStore = Arrays.copyOf(array, array.length);
    }

    public IntList(final int[] array, final int size) {
        backingStore = Arrays.copyOf(array, size);
    }

    IntList(final int[] array1, final int[] array2) {
        backingStore = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, backingStore, 0, array1.length);
        System.arraycopy(array2, 0, backingStore, array1.length, array2.length);
    }

    int[] extractBackingStoreWithoutCopy() {
        return backingStore;
    }

    public int size() {
        return backingStore.length;
    }

    public boolean isEmpty() {
        return backingStore.length == 0;
    }

    public boolean contains(final int i) {
        return Arrays.stream(backingStore).anyMatch(element -> element == i);
    }

    public IntIterator iterator() {
        return new IntIteratorImpl(backingStore);
    }

    public int[] toArray() {
        return Arrays.copyOf(backingStore, backingStore.length);
    }

    /**
     * Follows the contract of {@link java.util.Collection#toArray(Object[])}: the elements are boxed into
     * {@code a} if it is large enough, otherwise into a new array of the same runtime type.
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] a) {
        final Integer[] boxed = Arrays.stream(backingStore).boxed().toArray(Integer[]::new);
        if (a.length < boxed.length)
            return (T[]) Arrays.copyOf(boxed, boxed.length, a.getClass());
        System.arraycopy(boxed, 0, a, 0, boxed.length);
        if (a.length > boxed.length)
            a[boxed.length] = null;
        return a;
    }

    public int get(final int index) {
        return backingStore[index];
    }
}
