package me.shaftesbury.utils.functional;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Iterable2<T> extends java.lang.Iterable<T>
{
    Iterable2<T> filter(Function<? super T, Boolean> f);
    <U>Iterable2<U> map(Function<? super T, ? extends U> f);
    <U> Iterable2<U> mapi(final BiFunction<Integer, T, ? extends U> f);
    <U>Iterable2<U> choose(Function<? super T, Option<U>> f);
    boolean exists(Function<? super T, Boolean> f);
    boolean forAll(Function<? super T, Boolean> f);
    <U>boolean forAll2(final BiFunction<? super U, ? super T, Boolean> f, final Iterable<U> input1);
    <U>U fold(BiFunction<? super U, ? super T, ? extends U> f, U seed);
    List<T> toList();
    Object[] toArray();
    Set<T> toSet();
    <K,V>Map<K,V> toDictionary(final Function<? super T, ? extends K> keyFn, final Function<? super T, ? extends V> valueFn);
    T last();
    Iterable2<T> sortWith(final Comparator<T> f);
    Iterable2<T> concat(final Iterable2<T> list2);
    T find(Function<? super T, Boolean> f);
    int findIndex(Function<? super T, Boolean> f);
    <U>U pick(final Function<? super T, Option<U>> f);
    <U>Iterable2<U> collect(final Function<? super T, ? extends Iterable<U>> f);

    Iterable2<T> take(final int howMany);
    Iterable2<T> takeWhile(final Function<? super T, Boolean> f);
    Iterable2<T> skip(final int howMany);
    Iterable2<T> skipWhile(final Function<? super T, Boolean> f);

    String join(final String delimiter);
    T findLast(final Function<? super T, Boolean> f);
    Pair<List<T>,List<T>> partition(final Function<? super T, Boolean> f);

    <U>Iterable2<Pair<T,U>> zip(final Iterable2<? extends U> l2);
    // Pair<List<T1>,List<T2>> unzip();
    <U,V>Iterable2<Triple<T,U,V>> zip3(final Iterable<? extends U> l2, final Iterable<? extends V> l3);
    <U>U in(final Function<Iterable2<T>, U> f);

    <U>Map<U,List<T>> groupBy(final Function<? super T, ? extends U> keyFn);
}
