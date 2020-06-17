package me.shaftesbury.utils.functional;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IterableHelper
{
    private IterableHelper() {}
    /**
     * Note this is not intended to be a wrapper for a restartable sequence. If you want a restartable sequence turn
     * the underlying container into a concrete collection first.
     * @param it the singly-traversable input sequence
     * @param <T> the type of the underlying data in the input sequence
     * @return an Iterable2
     */
    public static <T>Iterable2<T> create(final java.lang.Iterable<T> it)
    {
        return new Iterable2<T>()
        {
            private final java.lang.Iterable<T> i = it;

            public Iterator<T> iterator() { return i.iterator(); }

            public final Iterable2<T> filter(final Function<? super T, Boolean> f) { return create(Functional.seq.filter(f,i)); }
            public final <U>Iterable2<U> map(final Function<? super T, ? extends U> f) { return create(Functional.seq.map(f, i)); }
            public <U> Iterable2<U> mapi(final BiFunction<Integer, T, ? extends U> f) { return create(Functional.seq.mapi(f, i)); }
            public final <U>Iterable2<U> choose(final Function<? super T, Option<U>> f) { return create(Functional.seq.choose(f, i)); }
            public final boolean exists(final Function<? super T, Boolean> f) { return Functional.exists(f, i); }
            public final boolean forAll(final Function<? super T, Boolean> f) { return Functional.forAll(f, i); }
            public <U> boolean forAll2(final BiFunction<? super U, ? super T, Boolean> f, final Iterable<U> j) { return Functional.forAll2(f,j,i); }
            public final <U>U fold(final BiFunction<? super U, ? super T, ? extends U> f, final U seed) { return Functional.fold(f, seed, i); }
            public final List<T> toList() { return Functional.toList(i); }
            public Object[] toArray() { return Functional.toArray(i); }
            public Set<T> toSet() { return Functional.toSet(i); }
            public <K, V> Map<K, V> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn) { return Functional.toDictionary(keyFn,valueFn,i); }
            public T last() { return Functional.last(i); }
            public final Iterable2<T> sortWith(final Comparator<T> f) { return create(Functional.sortWith(f,toList())); }
            public final Iterable2<T> concat(final Iterable2<T> list2) { return create(Functional.seq.concat(i,list2));}
            public final T find(final Function<? super T, Boolean> f) { return Functional.find(f,i);}
            public int findIndex(final Function<? super T, Boolean> f) { return Functional.findIndex(f,i);}
            public final <B>B pick(final Function<? super T, Option<B>> f){return Functional.pick(f,i);}
            public final Iterable2<T> take(final int howMany){return create(Functional.seq.take(howMany, i));}


            public Iterable2<T> takeWhile(final Function<? super T, Boolean> f) {
                return create(Functional.seq.takeWhile(f, i));
            }

            public Iterable2<T> skip(final int howMany) { return create(Functional.seq.skip(howMany, i)); }


            public Iterable2<T> skipWhile(final Function<? super T, Boolean> f) {
                return create(Functional.seq.skipWhile(f,i));
            }

            public String join(final String delimiter) { return Functional.join(delimiter,i); }
            public T findLast(final Function<? super T, Boolean> f) { return Functional.findLast(f,i); }
            public Pair<List<T>, List<T>> partition(final Function<? super T, Boolean> f) { return Functional.partition(f, i); }
            public final <U>Iterable2<Pair<T,U>> zip(final Iterable2<? extends U> l2) { return create(Functional.seq.zip(i, l2));}
            //public final <U>Pair<List<T>,List<U>> unzip(){return Functional.unzip(i);}
            public final <U,V>Iterable2<Triple<T,U,V>> zip3(final Iterable<? extends U> l2, final Iterable<? extends V> l3){return create(Functional.seq.zip3(i, l2, l3));}

            public final <U>Iterable2<U> collect(final Function<? super T, ? extends Iterable<U>> f){return create(Functional.seq.collect(f, i));}
            public <U>U in(final Function<Iterable2<T>, U> f){ return f.apply(this); }

            public <U> Map<U, List<T>> groupBy(final Function<? super T, ? extends U> keyFn) { return Functional.groupBy(keyFn,i); }
        };
    }

    public static <T>Iterable2<T> createEmpty()
    {
        return new EmptyList<>();
    }

    private static class EmptyList<T> implements Iterable2<T>
    {
        EmptyList() {}
        public Iterator<T> iterator() { return new Iterator<T>() {
             public boolean hasNext() { return false; }
             public T next() { throw new java.util.NoSuchElementException(); }
             public void remove() { throw new UnsupportedOperationException("Can't remove objects from this container"); }
        }; }

        public final Iterable2<T> filter(final Function<? super T, Boolean> f) { return this; }
        public final <U>Iterable2<U> map(final Function<? super T, ? extends U> f) { return new EmptyList<>(); }
        public <U> Iterable2<U> mapi(final BiFunction<Integer, T, ? extends U> f) { return new EmptyList<>();}
        public final <U>Iterable2<U> choose(final Function<? super T, Option<U>> f) { return new EmptyList<>(); }
        public final boolean exists(final Function<? super T, Boolean> f) { return false; }
        public final boolean forAll(final Function<? super T, Boolean> f) { return false; }
        public <U> boolean forAll2(final BiFunction<? super U, ? super T, Boolean> f, final Iterable<U> input1) { return false; }
        public final <U>U fold(final BiFunction<? super U, ? super T, ? extends U> f, final U seed) { return seed; }
        public final List<T> toList() { return Functional.toList(this); }
        public Object[] toArray() { return Functional.toArray(this); }
        public Set<T> toSet() { return Functional.toSet(this); }
        public <K, V> Map<K, V> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn) { return Functional.toDictionary(keyFn,valueFn,this); }
        public T last() { throw new NoSuchElementException(); }
        public final Iterable2<T> sortWith(final Comparator<T> f) { return this; }
        public final Iterable2<T> concat(final Iterable2<T> list2) { return list2;}
        public final T find(final Function<? super T, Boolean> f) { throw new NoSuchElementException();}
        public int findIndex(final Function<? super T, Boolean> f) { throw new NoSuchElementException();}
        public final <B>B pick(final Function<? super T, Option<B>> f){throw new NoSuchElementException();}
        public final Iterable2<T> take(final int howMany){return this;}

        public Iterable2<T> takeWhile(final Function<? super T, Boolean> f) { return this; }
        public Iterable2<T> skip(final int howMany) { return this; }

        public Iterable2<T> skipWhile(final Function<? super T, Boolean> f) { return this; }

        public String join(final String delimiter) { return Functional.join(delimiter,this); }
        public T findLast(final Function<? super T, Boolean> f) { throw new NoSuchElementException(); }
        public Pair<List<T>, List<T>> partition(final Function<? super T, Boolean> f) { return Functional.partition(f,this); }

        public final <U>Iterable2<Pair<T,U>> zip(final Iterable2<? extends U> l2) { throw new IllegalArgumentException("Iterable2.zip: It is not possible to zip an empty list with a non-empty list");}
        //public final <U>Pair<List<T>,List<U>> unzip(){return Functional.unzip(i);}
        public final <U,V>Iterable2<Triple<T,U,V>> zip3(final Iterable<? extends U> l2, final Iterable<? extends V> l3){throw new IllegalArgumentException("Iterable2.zip3: It is not possible to zip an empty list with a non-empty list");}

        public final <U>Iterable2<U> collect(final Function<? super T, ? extends Iterable<U>> f){return new EmptyList<>();}
        public <U>U in(final Function<Iterable2<T>, U> f){ return f.apply(this); }

        public <U> Map<U, List<T>> groupBy(final Function<? super T, ? extends U> keyFn) { return Collections.emptyMap(); }

        public boolean equals(final Object o) { return o instanceof EmptyList<?>; }


        public int hashCode() { return 0; }


        public String toString() { return "()"; }
    }

    public static <T>Iterable2<T> init(final Function<Integer, T> f, final int howMany) { return create(Functional.seq.init(f, howMany)); }
    public static <T>Iterable2<T> init(final Function<Integer,? extends T> f) { return create(Functional.seq.init(f));}
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T>Iterable2<T> asList(final T... a) { return create(Arrays.asList(a)); }
}