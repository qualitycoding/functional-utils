package me.shaftesbury.utils.functional.primitive.integer;

public interface IntIterator
{
    boolean hasNext();
    int next();
    void remove();
}

class IntIteratorImpl implements IntIterator
{
    private final int[] backingStore;
    private int position;

    private IntIteratorImpl(){backingStore=new int[0];position=0;}
    public IntIteratorImpl(final int[]array){backingStore=array;position=0;}

    public boolean hasNext() {
        return position<backingStore.length;
    }

    public int next() {
        return backingStore[position++];
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
