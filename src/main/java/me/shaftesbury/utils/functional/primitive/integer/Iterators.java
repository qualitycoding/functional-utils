package me.shaftesbury.utils.functional.primitive.integer;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Bob
 * Date: 23/10/13
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public final class Iterators
{
    private Iterators(){}

    public static IntIterable reverse(final IntList list)
    {
        if (list == null) throw new IllegalArgumentException("list");

        if (list.isEmpty())
            throw new IllegalArgumentException("Collection is empty");

        return new IntIterable() {
            private final IntList _list=list;

            public IntIterator iterator() {
                return new IntIterator() {
                    private int _posn=list.size()-1;

                    public boolean hasNext() {
                        return _posn>=0;
                    }


                    public int next() {
                        return _list.get(_posn--);
                    }


                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    // Return the first item of the sequence and then every nth item thereafter
    public static <T>Iterable<T> everyNth(final int step, final Iterable<T> it)
    {
        if (it == null) throw new IllegalArgumentException("enumerable");

        if (step < 1)
            throw new IllegalArgumentException("Invalid step value, must be greater than zero.");

        return new Iterable<T>(){
            final private Iterable<T> cache = it;


            public Iterator<T> iterator() {
                return new Iterator<T>(){
                    private boolean isFirst = true;
                    private boolean isNextReady = true;
                    final private Iterator<T> posn = cache.iterator();


                    public boolean hasNext() {
                        if(isFirst||isNextReady) ;
                        else {
                            for(int i=0;i<step-1;++i) if(posn.hasNext()) posn.next();
                            isNextReady = true;
                        }
                        return posn.hasNext();
                    }


                    public T next() {
                        if(isFirst||isNextReady) ;
                        else for(int i=0;i<step-1;++i) posn.next();
                        isFirst=false;
                        isNextReady = false;
                        return posn.next();
                    }


                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
