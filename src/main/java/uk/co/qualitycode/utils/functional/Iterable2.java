package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import uk.co.qualitycode.utils.functional.monad.Option;

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
import java.util.function.Predicate;

public interface Iterable2<T> extends java.lang.Iterable<T> {
    Iterable2<T> filter(Predicate<? super T> f);

    <U> Iterable2<U> map(Function<? super T, ? extends U> f);

    <U> Iterable2<U> mapi(final BiFunction<Integer, T, ? extends U> f);

    <U> Iterable2<U> choose(Function<? super T, Option<U>> f);

    boolean exists(Predicate<? super T> f);

    boolean forAll(Predicate<? super T> f);

    <U> boolean forAll2(final BiPredicate<? super U, ? super T> f, final Iterable<U> input1);

    <U> U fold(BiFunction<? super U, ? super T, ? extends U> f, U seed);

    List<T> toList();

    Object[] toArray();

    Set<T> toSet();

    <K, V> Map<K, V> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn);

    T last();

    Iterable2<T> sortWith(final Comparator<T> f);

    Iterable2<T> concat(final Iterable2<T> list2);

    Option<T> find(Predicate<? super T> f);

    int findIndex(Predicate<? super T> f);

    <U> Option<U> pick(final Function<? super T, Option<U>> f);

    <U> Iterable2<U> collect(final Function<? super T, ? extends Iterable<U>> f);

    Iterable2<T> take(final int howMany);

    Iterable2<T> takeWhile(final Predicate<? super T> f);

    Iterable2<T> skip(final int howMany);

    Iterable2<T> skipWhile(final Predicate<? super T> f);

    String join(final String delimiter);

    Option<T> findLast(final Predicate<T> f);

    Tuple2<List<T>, List<T>> partition(final Predicate<? super T> f);

    <U> Iterable2<Tuple2<T, U>> zip(final Iterable2<? extends U> l2);

    // Tuple2<List<T1>,List<T2>> unzip();
    <U, V> Iterable2<Tuple3<T, U, V>> zip3(final Iterable<? extends U> l2, final Iterable<? extends V> l3);

    <U> U in(final Function<Iterable2<T>, U> f);

    <U> Map<U, List<T>> groupBy(final Function<? super T, ? extends U> keyFn);

    /**
     * Note this is not intended to be a wrapper for a restartable sequence. If you want a restartable sequence turn
     * the underlying container into a concrete collection first.
     *
     * @param it  the singly-traversable input sequence
     * @param <T> the type of the underlying data in the input sequence
     * @return an Iterable2
     */
    public static <T> Iterable2<T> of(final java.lang.Iterable<T> it) {
        return new Iterable2<T>() {
            private final java.lang.Iterable<T> i = it;

            public Iterator<T> iterator() {
                return i.iterator();
            }

            public Iterable2<T> filter(final Predicate<? super T> f) {
                return of(Functional.seq.filter(f, i));
            }

            public <U> Iterable2<U> map(final Function<? super T, ? extends U> f) {
                return of(Functional.seq.map(f, i));
            }

            public <U> Iterable2<U> mapi(final BiFunction<Integer, T, ? extends U> f) {
                return of(Functional.seq.mapi(f, i));
            }

            public <U> Iterable2<U> choose(final Function<? super T, Option<U>> f) {
                return of(Functional.seq.choose(f, i));
            }

            public boolean exists(final Predicate<? super T> f) {
                return Functional.exists(f, i);
            }

            public boolean forAll(final Predicate<? super T> f) {
                return Functional.forAll(f, i);
            }

            public <U> boolean forAll2(final BiPredicate<? super U, ? super T> f, final Iterable<U> j) {
                return Functional.forAll2(f, j, i);
            }

            public <U> U fold(final BiFunction<? super U, ? super T, ? extends U> f, final U seed) {
                return Functional.fold(f, seed, i);
            }

            public List<T> toList() {
                return Functional.toList(i);
            }

            public Object[] toArray() {
                return Functional.toArray(i);
            }

            public Set<T> toSet() {
                return Functional.toSet(i);
            }

            public <K, V> Map<K, V> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn) {
                return Functional.toDictionary(keyFn, valueFn, i);
            }

            public T last() {
                return Functional.last(i);
            }

            public Iterable2<T> sortWith(final Comparator<T> f) {
                return of(Functional.sortWith(f, toList()));
            }

            public Iterable2<T> concat(final Iterable2<T> list2) {
                return of(Functional.seq.concat(i, list2));
            }

            public Option<T> find(final Predicate<? super T> f) {
                return Functional.find(f, i);
            }

            public int findIndex(final Predicate<? super T> f) {
                return Functional.findIndex(f, i);
            }

            public <B> Option<B> pick(final Function<? super T, Option<B>> f) {
                return Functional.pick(f, i);
            }

            public Iterable2<T> take(final int howMany) {
                return of(Functional.seq.take(howMany, i));
            }


            public Iterable2<T> takeWhile(final Predicate<? super T> f) {
                return of(Functional.seq.takeWhile(f, i));
            }

            public Iterable2<T> skip(final int howMany) {
                return of(Functional.seq.skip(howMany, i));
            }


            public Iterable2<T> skipWhile(final Predicate<? super T> f) {
                return of(Functional.seq.skipWhile(f, i));
            }

            public String join(final String delimiter) {
                return Functional.join(delimiter, i);
            }

            public Option<T> findLast(final Predicate<T> f) {
                return Functional.findLast(f, i);
            }

            public Tuple2<List<T>, List<T>> partition(final Predicate<? super T> f) {
                return Functional.partition(f, i);
            }

            public <U> Iterable2<Tuple2<T, U>> zip(final Iterable2<? extends U> l2) {
                return of(Functional.seq.zip(i, l2));
            }

            //public <U>Tuple2<List<T>,List<U>> unzip(){return Functional.unzip(i);}
            public <U, V> Iterable2<Tuple3<T, U, V>> zip3(final Iterable<? extends U> l2, final Iterable<? extends V> l3) {
                return of(Functional.seq.zip3(i, l2, l3));
            }

            public <U> Iterable2<U> collect(final Function<? super T, ? extends Iterable<U>> f) {
                return of(Functional.seq.collect(f, i));
            }

            public <U> U in(final Function<Iterable2<T>, U> f) {
                return f.apply(this);
            }

            public <U> Map<U, List<T>> groupBy(final Function<? super T, ? extends U> keyFn) {
                return Functional.groupBy(keyFn, i);
            }
        };
    }

    static <T> Iterable2<T> empty() {
        return new uk.co.qualitycode.utils.functional.Iterable2.EmptyList<>();
    }

    class EmptyList<T> implements Iterable2<T> {
        private EmptyList() {
        }

        public Iterator<T> iterator() {
            return new Iterator<T>() {
                public boolean hasNext() {
                    return false;
                }

                public T next() {
                    throw new java.util.NoSuchElementException();
                }

                public void remove() {
                    throw new UnsupportedOperationException("Can't remove objects from this container");
                }
            };
        }

        public Iterable2<T> filter(final Predicate<? super T> f) {
            return this;
        }

        public <U> Iterable2<U> map(final Function<? super T, ? extends U> f) {
            return new uk.co.qualitycode.utils.functional.Iterable2.EmptyList<>();
        }

        public <U> Iterable2<U> mapi(final BiFunction<Integer, T, ? extends U> f) {
            return new uk.co.qualitycode.utils.functional.Iterable2.EmptyList<>();
        }

        public <U> Iterable2<U> choose(final Function<? super T, Option<U>> f) {
            return new uk.co.qualitycode.utils.functional.Iterable2.EmptyList<>();
        }

        public boolean exists(final Predicate<? super T> f) {
            return false;
        }

        public boolean forAll(final Predicate<? super T> f) {
            return false;
        }

        public <U> boolean forAll2(final BiPredicate<? super U, ? super T> f, final Iterable<U> input1) {
            return false;
        }

        public <U> U fold(final BiFunction<? super U, ? super T, ? extends U> f, final U seed) {
            return seed;
        }

        public List<T> toList() {
            return Functional.toList(this);
        }

        public Object[] toArray() {
            return Functional.toArray(this);
        }

        public Set<T> toSet() {
            return Functional.toSet(this);
        }

        public <K, V> Map<K, V> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn) {
            return Functional.toDictionary(keyFn, valueFn, this);
        }

        public T last() {
            throw new NoSuchElementException();
        }

        public Iterable2<T> sortWith(final Comparator<T> f) {
            return this;
        }

        public Iterable2<T> concat(final Iterable2<T> list2) {
            return list2;
        }

        public Option<T> find(final Predicate<? super T> f) {
            throw new NoSuchElementException();
        }

        public int findIndex(final Predicate<? super T> f) {
            throw new NoSuchElementException();
        }

        public <B> Option<B> pick(final Function<? super T, Option<B>> f) {
            throw new NoSuchElementException();
        }

        public Iterable2<T> take(final int howMany) {
            return this;
        }

        public Iterable2<T> takeWhile(final Predicate<? super T> f) {
            return this;
        }

        public Iterable2<T> skip(final int howMany) {
            return this;
        }

        public Iterable2<T> skipWhile(final Predicate<? super T> f) {
            return this;
        }

        public String join(final String delimiter) {
            return Functional.join(delimiter, this);
        }

        public Option<T> findLast(final Predicate<T> f) {
            throw new NoSuchElementException();
        }

        public Tuple2<List<T>, List<T>> partition(final Predicate<? super T> f) {
            return Functional.partition(f, this);
        }

        public <U> Iterable2<Tuple2<T, U>> zip(final Iterable2<? extends U> l2) {
            throw new IllegalArgumentException("Iterable2.zip: It is not possible to zip an empty list with a non-empty list");
        }

        //public <U>Tuple2<List<T>,List<U>> unzip(){return Functional.unzip(i);}
        public <U, V> Iterable2<Tuple3<T, U, V>> zip3(final Iterable<? extends U> l2, final Iterable<? extends V> l3) {
            throw new IllegalArgumentException("Iterable2.zip3: It is not possible to zip an empty list with a non-empty list");
        }

        public <U> Iterable2<U> collect(final Function<? super T, ? extends Iterable<U>> f) {
            return new uk.co.qualitycode.utils.functional.Iterable2.EmptyList<>();
        }

        public <U> U in(final Function<Iterable2<T>, U> f) {
            return f.apply(this);
        }

        public <U> Map<U, List<T>> groupBy(final Function<? super T, ? extends U> keyFn) {
            return Collections.emptyMap();
        }

        public boolean equals(final Object o) {
            return o instanceof uk.co.qualitycode.utils.functional.Iterable2.EmptyList<?>;
        }

        public int hashCode() {
            return 0;
        }

        public String toString() {
            return "()";
        }
    }

    static <T> Iterable2<T> init(final Function<Integer, T> f, final int howMany) {
        return of(Functional.seq.init(f, howMany));
    }

    static <T> Iterable2<T> init(final Function<Integer, ? extends T> f) {
        return of(Functional.seq.init(f));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Iterable2<T> asList(final T... a) {
        return of(Arrays.asList(a));
    }
}
