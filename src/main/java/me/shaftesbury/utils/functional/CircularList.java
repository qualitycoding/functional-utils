package me.shaftesbury.utils.functional;

import io.vavr.PartialFunction;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Iterator;
import io.vavr.collection.LinearSeq;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

// This 'inheritance' is wrong. The circular list can't implement a general linear sequence because there are
// functions in the general class that this can't do, eg append()
public class CircularList<T> implements LinearSeq<T> {
    private final List<T> buffer;

    private CircularList(final List<T> buffer, final int bufferSize) {
        this.buffer = requireNonNull(buffer, "buffer must not be null").take(bufferSize);
    }

    private static <T> CircularList<T> of(final List<T> list, final int bufferSize) {
        return new CircularList<>(list, bufferSize);
    }

    public static <T> CircularList<T> of(final List<T> list) {
        return of(list, list.size());
    }

    // does this function even make sense? Why not use a combination of head() and tail()?
//    public CircularList<T> withSize(final int bufferSize) {
//    }

    // it doesn't make sense to append to an infinite sequence ...
    @Override
    public LinearSeq<T> append(final T element) {
        throw new UnsupportedOperationException("append() is not supported");
    }

    // it doesn't make sense to append to an infinite sequence ...
    @Override
    public LinearSeq<T> appendAll(final Iterable<? extends T> elements) {
        throw new UnsupportedOperationException("appendAll() is not supported");
    }

    @Override
    public java.util.List<T> asJava() {
        throw new UnsupportedOperationException("asJava() is not supported");
    }

    @Override
    public LinearSeq<T> asJava(final Consumer<? super java.util.List<T>> action) {
        throw new UnsupportedOperationException("asJava() is not supported");
    }

    @Override
    public java.util.List<T> asJavaMutable() {
        throw new UnsupportedOperationException("asJavaMutable() is not supported");
    }

    @Override
    public LinearSeq<T> asJavaMutable(final Consumer<? super java.util.List<T>> action) {
        throw new UnsupportedOperationException("asJavaMutable() is not supported");
    }

    @Override
    public <R> LinearSeq<R> collect(final PartialFunction<? super T, ? extends R> partialFunction) {
        throw new UnsupportedOperationException("collect() is not supported");
    }

    @Override
    public LinearSeq<? extends LinearSeq<T>> combinations() {
        throw new UnsupportedOperationException("combinations() is not supported");
    }

    @Override
    public LinearSeq<? extends LinearSeq<T>> combinations(final int k) {
        throw new UnsupportedOperationException("combinations() is not supported");
    }

    @Override
    public io.vavr.collection.Iterator<? extends LinearSeq<T>> crossProduct(final int power) {
        throw new UnsupportedOperationException("crossProduct() is not supported");
    }

    @Override
    public T get(final int index) {
        throw new UnsupportedOperationException("get() is not supported");
    }

    @Override
    public int indexOf(final T element, final int from) {
        throw new UnsupportedOperationException("indexOf() is not supported");
    }

    @Override
    public LinearSeq<T> distinct() {
        throw new UnsupportedOperationException("distinct() is not supported");
    }

    @Override
    public LinearSeq<T> distinctBy(final Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("distinctBy() is not currently supported");
    }

    @Override
    public <U> LinearSeq<T> distinctBy(final Function<? super T, ? extends U> keyExtractor) {
        throw new UnsupportedOperationException("distinctBy() is not currently supported");
    }

    @Override
    public LinearSeq<T> drop(final int n) {
        throw new UnimplementedException("drop() isn't implemented yet. Please call back later");
        if (n > buffer.size()) {
            ;
        }
        final List<T> prefix = buffer.drop(n);
        return fixedList(prefix).followedBy(this);
    }

    @Override
    public LinearSeq<T> dropUntil(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("dropUntil() is not currently supported");
    }

    @Override
    public LinearSeq<T> dropWhile(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("dropWhile() is not currently supported");
    }

    @Override
    public LinearSeq<T> dropRight(final int n) {
        throw new UnsupportedOperationException("dropRight() is not currently supported");
    }

    @Override
    public LinearSeq<T> dropRightUntil(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("dropRightUntil() is not currently supported");
    }

    @Override
    public LinearSeq<T> dropRightWhile(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("dropRightWhile() is not currently supported");
    }

    @Override
    public LinearSeq<T> filter(final Predicate<? super T> predicate) {
        return of(buffer.filter(predicate));
    }

    @Override
    public LinearSeq<T> filterNot(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("filterNot() is not currently supported");
    }

    @Override
    public LinearSeq<T> reject(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("reject() is not currently supported");
    }

    @Override
    public <U> LinearSeq<U> flatMap(final Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return of(buffer.flatMap(mapper));
    }

    @Override
    public <C> Map<C, ? extends LinearSeq<T>> groupBy(final Function<? super T, ? extends C> classifier) {
        throw new UnsupportedOperationException("groupBy() is not currently supported");
    }

    @Override
    public io.vavr.collection.Iterator<? extends LinearSeq<T>> grouped(final int size) {
        throw new UnsupportedOperationException("grouped() is not currently supported");
    }

    @Override
    public boolean hasDefiniteSize() {
        return true;
    }

    @Override
    public T head() {
        return buffer.head();
    }

    @Override
    public LinearSeq<T> init() {
        throw new UnsupportedOperationException("init() is not currently supported");
    }

    @Override
    public Option<? extends LinearSeq<T>> initOption() {
        throw new UnsupportedOperationException("initOption() is not currently supported");
    }

    @Override
    public boolean isTraversableAgain() {
        throw new UnsupportedOperationException("isTraversableAgain() is not currently supported");
    }

    @Override
    public T last() {
        throw new UnsupportedOperationException("last() is not currently supported");
    }

    @Override
    public int length() {
        return buffer.length();
    }

    @Override
    public LinearSeq<T> insert(final int index, final T element) {
        throw new UnsupportedOperationException("insert() is not currently supported");
    }

    @Override
    public LinearSeq<T> insertAll(final int index, final Iterable<? extends T> elements) {
        throw new UnsupportedOperationException("insertAll() is not currently supported");
    }

    @Override
    public LinearSeq<T> intersperse(final T element) {
        throw new UnsupportedOperationException("intersperse() is not currently supported");
    }

    @Override
    public int lastIndexOf(final T element, final int end) {
        throw new UnsupportedOperationException("lastIndexOf() is not currently supported");
    }

    @Override
    public boolean isAsync() {
        throw new UnsupportedOperationException("isAsync() is not currently supported");
    }

    @Override
    public boolean isLazy() {
        throw new UnsupportedOperationException("isLazy() is not currently supported");
    }

    @Override
    public <U> LinearSeq<U> map(final Function<? super T, ? extends U> mapper) {
        return of(buffer.map(mapper));
    }

    @Override
    public LinearSeq<T> orElse(final Iterable<? extends T> other) {
        throw new UnsupportedOperationException("orElse() is not currently supported");
    }

    @Override
    public LinearSeq<T> orElse(final Supplier<? extends Iterable<? extends T>> supplier) {
        throw new UnsupportedOperationException("orElse() is not currently supported");
    }

    @Override
    public LinearSeq<T> padTo(final int length, final T element) {
        throw new UnsupportedOperationException("padTo() is not currently supported");
    }

    @Override
    public Seq<T> leftPadTo(final int length, final T element) {
        throw new UnsupportedOperationException("leftPadTo() is not currently supported");
    }

    @Override
    public LinearSeq<T> patch(final int from, final Iterable<? extends T> that, final int replaced) {
        throw new UnsupportedOperationException("patch() is not currently supported");
    }

    @Override
    public Tuple2<? extends LinearSeq<T>, ? extends LinearSeq<T>> partition(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("partition() is not currently supported");
    }

    @Override
    public LinearSeq<T> peek(final Consumer<? super T> action) {
        throw new UnsupportedOperationException("peek() is not currently supported");
    }

    @Override
    public String stringPrefix() {
        throw new UnsupportedOperationException("stringPrefix() is not currently supported");
    }

    @Override
    public LinearSeq<? extends LinearSeq<T>> permutations() {
        throw new UnsupportedOperationException("permutations() is not currently supported");
    }

    @Override
    public LinearSeq<T> prepend(final T element) {
        return of(buffer.prepend(element));
    }

    @Override
    public LinearSeq<T> prependAll(final Iterable<? extends T> elements) {
        return of(buffer.prependAll(elements));
    }

    @Override
    public LinearSeq<T> remove(final T element) {
        throw new UnsupportedOperationException("remove() is not currently supported");
    }

    @Override
    public LinearSeq<T> removeFirst(final Predicate<T> predicate) {
        throw new UnsupportedOperationException("removeFirst() is not currently supported");
    }

    @Override
    public LinearSeq<T> removeLast(final Predicate<T> predicate) {
        throw new UnsupportedOperationException("removeLast() is not currently supported");
    }

    @Override
    public LinearSeq<T> removeAt(final int index) {
        throw new UnsupportedOperationException("removeAt() is not currently supported");
    }

    @Override
    public LinearSeq<T> removeAll(final T element) {
        throw new UnsupportedOperationException("removeAll() is not currently supported");
    }

    @Override
    public LinearSeq<T> removeAll(final Iterable<? extends T> elements) {
        throw new UnsupportedOperationException("removeAll() is not currently supported");
    }

    @Override
    public LinearSeq<T> removeAll(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("removeAll() is not currently supported");
    }

    @Override
    public LinearSeq<T> replace(final T currentElement, final T newElement) {
        throw new UnsupportedOperationException("replace() is not currently supported");
    }

    @Override
    public LinearSeq<T> replaceAll(final T currentElement, final T newElement) {
        throw new UnsupportedOperationException("replaceAll() is not currently supported");
    }

    @Override
    public LinearSeq<T> retainAll(final Iterable<? extends T> elements) {
        throw new UnsupportedOperationException("retainAll() is not currently supported");
    }

    @Override
    public LinearSeq<T> reverse() {
        throw new UnsupportedOperationException("reverse() is not currently supported");
    }

    @Override
    public LinearSeq<T> rotateLeft(final int n) {
        throw new UnsupportedOperationException("rotateLeft() is not currently supported");
    }

    @Override
    public LinearSeq<T> rotateRight(final int n) {
        throw new UnsupportedOperationException("rotateRight() is not currently supported");
    }

    @Override
    public LinearSeq<T> shuffle() {
        throw new UnsupportedOperationException("shuffle() is not currently supported");
    }

    @Override
    public LinearSeq<T> shuffle(final Random random) {
        throw new UnsupportedOperationException("shuffle() is not currently supported");
    }

    @Override
    public LinearSeq<T> scan(final T zero, final BiFunction<? super T, ? super T, ? extends T> operation) {
        throw new UnsupportedOperationException("scan() is not currently supported");
    }

    @Override
    public <U> LinearSeq<U> scanLeft(final U zero, final BiFunction<? super U, ? super T, ? extends U> operation) {
        throw new UnsupportedOperationException("scanLeft() is not currently supported");
    }

    @Override
    public <U> LinearSeq<U> scanRight(final U zero, final BiFunction<? super T, ? super U, ? extends U> operation) {
        throw new UnsupportedOperationException("scanRight() is not currently supported");
    }

    @Override
    public LinearSeq<T> slice(final int beginIndex, final int endIndex) {
        throw new UnsupportedOperationException("slice() is not currently supported");
    }

    @Override
    public io.vavr.collection.Iterator<? extends LinearSeq<T>> slideBy(final Function<? super T, ?> classifier) {
        throw new UnsupportedOperationException("slideBy() is not currently supported");
    }

    @Override
    public io.vavr.collection.Iterator<? extends LinearSeq<T>> sliding(final int size) {
        throw new UnsupportedOperationException("sliding() is not currently supported");
    }

    @Override
    public io.vavr.collection.Iterator<? extends LinearSeq<T>> sliding(final int size, final int step) {
        throw new UnsupportedOperationException("sliding() is not currently supported");
    }

    @Override
    public LinearSeq<T> sorted() {
        throw new UnsupportedOperationException("sorted() is not currently supported");
    }

    @Override
    public LinearSeq<T> sorted(final Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("sorted() is not currently supported");
    }

    @Override
    public <U extends Comparable<? super U>> LinearSeq<T> sortBy(final Function<? super T, ? extends U> mapper) {
        throw new UnsupportedOperationException("sortBy() is not currently supported");
    }

    @Override
    public <U> LinearSeq<T> sortBy(final Comparator<? super U> comparator, final Function<? super T, ? extends U> mapper) {
        throw new UnsupportedOperationException("sortBy() is not currently supported");
    }

    @Override
    public Tuple2<? extends Seq<T>, ? extends Seq<T>> splitAt(final int n) {
        throw new UnsupportedOperationException("splitAt() is not currently supported");
    }

    @Override
    public Tuple2<? extends Seq<T>, ? extends Seq<T>> splitAt(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("splitAt() is not currently supported");
    }

    @Override
    public Tuple2<? extends Seq<T>, ? extends Seq<T>> splitAtInclusive(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("splitAtInclusive() is not currently supported");
    }

    @Override
    public Tuple2<? extends LinearSeq<T>, ? extends LinearSeq<T>> span(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("span() is not currently supported");
    }

    @Override
    public LinearSeq<T> subSequence(final int beginIndex) {
        throw new UnsupportedOperationException("subSequence() is not currently supported");
    }

    @Override
    public LinearSeq<T> subSequence(final int beginIndex, final int endIndex) {
        throw new UnsupportedOperationException("subSequence() is not currently supported");
    }

    @Override
    public LinearSeq<T> tail() {
        return of(buffer.tail());
    }

    @Override
    public Option<? extends LinearSeq<T>> tailOption() {
        throw new UnsupportedOperationException("tailOption() is not currently supported");
    }

    @Override
    public LinearSeq<T> take(final int n) {
        if (n > buffer.size()) {
            final java.util.List<T> reduced = Collections.nCopies(n / buffer.size(), buffer.toJavaList()).stream()
                    .reduce(new ArrayList<>(), (a, b) -> {
                        a.addAll(b);
                        return a;
                    });
            reduced.addAll(buffer.take(n % buffer.size()).toJavaList());
            return List.ofAll(reduced);
        } else
            return buffer.take(n);
    }

    @Override
    public LinearSeq<T> takeUntil(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("takeUntil() is not currently supported");
    }

    @Override
    public LinearSeq<T> takeWhile(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("takeWhile() is not currently supported");
    }

    @Override
    public LinearSeq<T> takeRight(final int n) {
        throw new UnsupportedOperationException("takeRight() is not currently supported");
    }

    @Override
    public LinearSeq<T> takeRightUntil(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("takeRightUntil() is not currently supported");
    }

    @Override
    public LinearSeq<T> takeRightWhile(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("takeRightWhile() is not currently supported");
    }

    @Override
    public <T1, T2> Tuple2<? extends LinearSeq<T1>, ? extends LinearSeq<T2>> unzip(final Function<? super T, Tuple2<? extends T1, ? extends T2>> unzipper) {
        throw new UnsupportedOperationException("unzip() is not currently supported");
    }

    @Override
    public <T1, T2, T3> Tuple3<? extends Seq<T1>, ? extends Seq<T2>, ? extends Seq<T3>> unzip3(final Function<? super T, Tuple3<? extends T1, ? extends T2, ? extends T3>> unzipper) {
        throw new UnsupportedOperationException("unzip3() is not currently supported");
    }

    @Override
    public LinearSeq<T> update(final int index, final T element) {
        throw new UnsupportedOperationException("update() is not currently supported");
    }

    @Override
    public LinearSeq<T> update(final int index, final Function<? super T, ? extends T> updater) {
        throw new UnsupportedOperationException("update() is not currently supported");
    }

    @Override
    public <U> LinearSeq<Tuple2<T, U>> zip(final Iterable<? extends U> that) {
        throw new UnsupportedOperationException("zip() is not currently supported");
    }

    @Override
    public <U, R> LinearSeq<R> zipWith(final Iterable<? extends U> that, final BiFunction<? super T, ? super U, ? extends R> mapper) {
        throw new UnsupportedOperationException("zipWith() is not currently supported");
    }

    @Override
    public <U> LinearSeq<Tuple2<T, U>> zipAll(final Iterable<? extends U> that, final T thisElem, final U thatElem) {
        throw new UnsupportedOperationException("zipAll() is not currently supported");
    }

    @Override
    public LinearSeq<Tuple2<T, Integer>> zipWithIndex() {
        throw new UnsupportedOperationException("zipWithIndex() is not currently supported");
    }

    @Override
    public <U> LinearSeq<U> zipWithIndex(final BiFunction<? super T, ? super Integer, ? extends U> mapper) {
        throw new UnsupportedOperationException("zipWithIndex() is not currently supported");
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentPos = 0;

            @Override
            public boolean hasNext() {
                return !buffer.isEmpty();
            }

            @Override
            public T next() {
                if (hasNext())
                    return buffer.get(currentPos++ % buffer.size());
                else
                    throw new NoSuchElementException();
            }
        };
    }

    private LSeq<T> fixedList(final List<T> prefix) {
        return circ -> new LinearSeq<T>() {
            @Override
            public LinearSeq<T> append(final T element) {
                return null;
            }

            @Override
            public LinearSeq<T> appendAll(final Iterable<? extends T> elements) {
                return null;
            }

            @Override
            public LinearSeq<T> asJava(final Consumer<? super java.util.List<T>> action) {
                return null;
            }

            @Override
            public LinearSeq<T> asJavaMutable(final Consumer<? super java.util.List<T>> action) {
                return null;
            }

            @Override
            public <R> LinearSeq<R> collect(final PartialFunction<? super T, ? extends R> partialFunction) {
                return null;
            }

            @Override
            public LinearSeq<? extends LinearSeq<T>> combinations() {
                return null;
            }

            @Override
            public LinearSeq<? extends LinearSeq<T>> combinations(final int k) {
                return null;
            }

            @Override
            public Iterator<? extends LinearSeq<T>> crossProduct(final int power) {
                return null;
            }

            @Override
            public LinearSeq<T> distinct() {
                return null;
            }

            @Override
            public LinearSeq<T> distinctBy(final Comparator<? super T> comparator) {
                return null;
            }

            @Override
            public <U> LinearSeq<T> distinctBy(final Function<? super T, ? extends U> keyExtractor) {
                return null;
            }

            @Override
            public LinearSeq<T> drop(final int n) {
                return null;
            }

            @Override
            public LinearSeq<T> dropUntil(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> dropWhile(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> dropRight(final int n) {
                return null;
            }

            @Override
            public LinearSeq<T> dropRightUntil(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> dropRightWhile(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> filter(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> filterNot(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> reject(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public <U> LinearSeq<U> flatMap(final Function<? super T, ? extends Iterable<? extends U>> mapper) {
                return null;
            }

            @Override
            public <C> Map<C, ? extends LinearSeq<T>> groupBy(final Function<? super T, ? extends C> classifier) {
                return null;
            }

            @Override
            public Iterator<? extends LinearSeq<T>> grouped(final int size) {
                return null;
            }

            @Override
            public LinearSeq<T> init() {
                return null;
            }

            @Override
            public Option<? extends LinearSeq<T>> initOption() {
                return null;
            }

            @Override
            public LinearSeq<T> insert(final int index, final T element) {
                return null;
            }

            @Override
            public LinearSeq<T> insertAll(final int index, final Iterable<? extends T> elements) {
                return null;
            }

            @Override
            public LinearSeq<T> intersperse(final T element) {
                return null;
            }

            @Override
            public <U> LinearSeq<U> map(final Function<? super T, ? extends U> mapper) {
                return null;
            }

            @Override
            public LinearSeq<T> orElse(final Iterable<? extends T> other) {
                return null;
            }

            @Override
            public LinearSeq<T> orElse(final Supplier<? extends Iterable<? extends T>> supplier) {
                return null;
            }

            @Override
            public LinearSeq<T> padTo(final int length, final T element) {
                return null;
            }

            @Override
            public LinearSeq<T> patch(final int from, final Iterable<? extends T> that, final int replaced) {
                return null;
            }

            @Override
            public Tuple2<? extends LinearSeq<T>, ? extends LinearSeq<T>> partition(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> peek(final Consumer<? super T> action) {
                return null;
            }

            @Override
            public LinearSeq<? extends LinearSeq<T>> permutations() {
                return null;
            }

            @Override
            public LinearSeq<T> prepend(final T element) {
                return null;
            }

            @Override
            public LinearSeq<T> prependAll(final Iterable<? extends T> elements) {
                return null;
            }

            @Override
            public LinearSeq<T> remove(final T element) {
                return null;
            }

            @Override
            public LinearSeq<T> removeFirst(final Predicate<T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> removeLast(final Predicate<T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> removeAt(final int index) {
                return null;
            }

            @Override
            public LinearSeq<T> removeAll(final T element) {
                return null;
            }

            @Override
            public LinearSeq<T> removeAll(final Iterable<? extends T> elements) {
                return null;
            }

            @Override
            public LinearSeq<T> removeAll(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> replace(final T currentElement, final T newElement) {
                return null;
            }

            @Override
            public LinearSeq<T> replaceAll(final T currentElement, final T newElement) {
                return null;
            }

            @Override
            public LinearSeq<T> retainAll(final Iterable<? extends T> elements) {
                return null;
            }

            @Override
            public LinearSeq<T> reverse() {
                return null;
            }

            @Override
            public LinearSeq<T> rotateLeft(final int n) {
                return null;
            }

            @Override
            public LinearSeq<T> rotateRight(final int n) {
                return null;
            }

            @Override
            public LinearSeq<T> shuffle() {
                return null;
            }

            @Override
            public LinearSeq<T> shuffle(final Random random) {
                return null;
            }

            @Override
            public LinearSeq<T> scan(final T zero, final BiFunction<? super T, ? super T, ? extends T> operation) {
                return null;
            }

            @Override
            public <U> LinearSeq<U> scanLeft(final U zero, final BiFunction<? super U, ? super T, ? extends U> operation) {
                return null;
            }

            @Override
            public <U> LinearSeq<U> scanRight(final U zero, final BiFunction<? super T, ? super U, ? extends U> operation) {
                return null;
            }

            @Override
            public LinearSeq<T> slice(final int beginIndex, final int endIndex) {
                return null;
            }

            @Override
            public Iterator<? extends LinearSeq<T>> slideBy(final Function<? super T, ?> classifier) {
                return null;
            }

            @Override
            public Iterator<? extends LinearSeq<T>> sliding(final int size) {
                return null;
            }

            @Override
            public Iterator<? extends LinearSeq<T>> sliding(final int size, final int step) {
                return null;
            }

            @Override
            public LinearSeq<T> sorted() {
                return null;
            }

            @Override
            public LinearSeq<T> sorted(final Comparator<? super T> comparator) {
                return null;
            }

            @Override
            public <U extends Comparable<? super U>> LinearSeq<T> sortBy(final Function<? super T, ? extends U> mapper) {
                return null;
            }

            @Override
            public <U> LinearSeq<T> sortBy(final Comparator<? super U> comparator, final Function<? super T, ? extends U> mapper) {
                return null;
            }

            @Override
            public Tuple2<? extends LinearSeq<T>, ? extends LinearSeq<T>> span(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> subSequence(final int beginIndex) {
                return null;
            }

            @Override
            public LinearSeq<T> subSequence(final int beginIndex, final int endIndex) {
                return null;
            }

            @Override
            public LinearSeq<T> tail() {
                return null;
            }

            @Override
            public Option<? extends LinearSeq<T>> tailOption() {
                return null;
            }

            @Override
            public LinearSeq<T> take(final int n) {
                return null;
            }

            @Override
            public LinearSeq<T> takeUntil(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> takeWhile(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> takeRight(final int n) {
                return null;
            }

            @Override
            public LinearSeq<T> takeRightUntil(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public LinearSeq<T> takeRightWhile(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public <T1, T2> Tuple2<? extends LinearSeq<T1>, ? extends LinearSeq<T2>> unzip(final Function<? super T, Tuple2<? extends T1, ? extends T2>> unzipper) {
                return null;
            }

            @Override
            public LinearSeq<T> update(final int index, final T element) {
                return null;
            }

            @Override
            public LinearSeq<T> update(final int index, final Function<? super T, ? extends T> updater) {
                return null;
            }

            @Override
            public <U> LinearSeq<Tuple2<T, U>> zip(final Iterable<? extends U> that) {
                return null;
            }

            @Override
            public <U, R> LinearSeq<R> zipWith(final Iterable<? extends U> that, final BiFunction<? super T, ? super U, ? extends R> mapper) {
                return null;
            }

            @Override
            public <U> LinearSeq<Tuple2<T, U>> zipAll(final Iterable<? extends U> that, final T thisElem, final U thatElem) {
                return null;
            }

            @Override
            public LinearSeq<Tuple2<T, Integer>> zipWithIndex() {
                return null;
            }

            @Override
            public <U> LinearSeq<U> zipWithIndex(final BiFunction<? super T, ? super Integer, ? extends U> mapper) {
                return null;
            }

            @Override
            public java.util.List<T> asJava() {
                return null;
            }

            @Override
            public java.util.List<T> asJavaMutable() {
                return null;
            }

            @Override
            public T get(final int index) {
                return null;
            }

            @Override
            public int indexOf(final T element, final int from) {
                return 0;
            }

            @Override
            public int lastIndexOf(final T element, final int end) {
                return 0;
            }

            @Override
            public Seq<T> leftPadTo(final int length, final T element) {
                return null;
            }

            @Override
            public Tuple2<? extends Seq<T>, ? extends Seq<T>> splitAt(final int n) {
                return null;
            }

            @Override
            public Tuple2<? extends Seq<T>, ? extends Seq<T>> splitAt(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public Tuple2<? extends Seq<T>, ? extends Seq<T>> splitAtInclusive(final Predicate<? super T> predicate) {
                return null;
            }

            @Override
            public <T1, T2, T3> Tuple3<? extends Seq<T1>, ? extends Seq<T2>, ? extends Seq<T3>> unzip3(final Function<? super T, Tuple3<? extends T1, ? extends T2, ? extends T3>> unzipper) {
                return null;
            }

            @Override
            public boolean hasDefiniteSize() {
                return false;
            }

            @Override
            public T head() {
                return null;
            }

            @Override
            public boolean isTraversableAgain() {
                return false;
            }

            @Override
            public T last() {
                return null;
            }

            @Override
            public int length() {
                return 0;
            }

            @Override
            public boolean isAsync() {
                return false;
            }

            @Override
            public boolean isLazy() {
                return false;
            }

            @Override
            public String stringPrefix() {
                return null;
            }
        };
    }

    public interface LSeq<T> {
        LinearSeq<T> followedBy(CircularList<T> circularList);
    }


    // this provides the maximum size of the list but if the the input data is fewer elements then the size of the input data
    // will be used instead
//    @FunctionalInterface
//    public interface WithSize<B> {
//        B withSize(int size);
//    }

    private static int requirePositive(final int bufferSize, final String message) {
        if (bufferSize > 0)
            return bufferSize;
        else
            throw new IllegalArgumentException(message);
    }

//    public CircularList<T> create(final Iterable<T> input, final int howMany) {
//        this.bufferSize = howMany;
//        this.buffer = new ArrayList<>(howMany);
//        int counter = 0;
//        final Iterator<T> iterator = input.iterator();
//        while (counter < howMany && iterator.hasNext()) {
//            buffer.add(iterator.next());
//            ++counter;
//        }
//        while (counter < howMany) {
//            final Iterator<T> iterator1 = buffer.iterator();
//            while (counter < howMany && iterator1.hasNext()) {
//                buffer.add(iterator1.next());
//                ++counter;
//            }
//        }
//    }

}
