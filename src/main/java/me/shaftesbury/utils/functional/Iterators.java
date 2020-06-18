package me.shaftesbury.utils.functional;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Iterators {
    private Iterators() {
    }

    public static <T> Iterable<T> reverse(final List<T> list) {
        if (list == null) throw new IllegalArgumentException("list");

        if (list.isEmpty())
            throw new IllegalArgumentException("Collection is empty");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);
            private final List<T> _list = list;

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private int _posn = list.size() - 1;

                        public boolean hasNext() {
                            return _posn >= 0;
                        }


                        public T next() {
                            return _list.get(_posn--);
                        }


                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
            }
        };
    }

    // Return the first item of the sequence and then every nth item thereafter
    public static <T> Iterable<T> everyNth(final int step, final Iterable<T> it) {
        if (it == null) throw new IllegalArgumentException("enumerable");

        if (step < 1)
            throw new IllegalArgumentException("Invalid step value, must be greater than zero.");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);
            final private Iterable<T> cache = it;


            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private boolean isFirst = true;
                        private boolean isNextReady = true;
                        final private Iterator<T> posn = cache.iterator();


                        public boolean hasNext() {
                            if (isFirst || isNextReady) ;
                            else {
                                for (int i = 0; i < step - 1; ++i) if (posn.hasNext()) posn.next();
                                isNextReady = true;
                            }
                            return posn.hasNext();
                        }


                        public T next() {
                            if (isFirst || isNextReady) ;
                            else for (int i = 0; i < step - 1; ++i) posn.next();
                            isFirst = false;
                            isNextReady = false;
                            return posn.next();
                        }


                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                else throw new UnsupportedOperationException("This Iterable does not allow multiple Iterators");
            }
        };
    }
}
