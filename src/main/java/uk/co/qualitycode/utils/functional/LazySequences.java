package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import uk.co.qualitycode.utils.functional.monad.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static uk.co.qualitycode.utils.functional.Checks.notNull;
import static uk.co.qualitycode.utils.functional.Functional.*;

/**
 * Lazily evaluated sequence operations on containers of references.
 * <p>
 * Also reachable as {@code Functional.Lazy}, which is kept as an alias for existing code. The constructor is
 * package-private only so that alias can extend this class.
 *
 * Lazily-evaluated implementations of various of the algorithms
 * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
 * Note that these functions do not generally expose a restartable sequence. If you want to restart the iteration
 * then you should make the convert the sequence (the Iterable) to a concrete collection before accessing the
 * iterator.
 */
public class LazySequences {
    LazySequences() {
    }

    /**
     * append: given the input sequence and an item, return a new, lazily-evaluated sequence containing the input with the item
     * as the final element.
     *
     * @param value the item to be appended
     * @param input the input sequence
     * @param <T>   the type of the element in the input sequence
     * @return a sequence containing all the elements of 'input' followed by 'value'
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> append(final T value, final Iterable<T> input) {
        notNull(value, "Lazy.append(T,Iterable<T>)", "value");
        notNull(input, "Lazy.append(T,Iterable<T>)", "input");
        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private final Iterator<? extends T> iterator = input.iterator();
                        private boolean hasMoreInput = iterator.hasNext();
                        private boolean hasNotConsumedAppendee = true;

                        public boolean hasNext() {
                            hasMoreInput = iterator.hasNext();
                            return hasMoreInput || hasNotConsumedAppendee;
                        }

                        public T next() {
                            final T next;
                            if (hasMoreInput) {
                                next = iterator.next();
                                hasMoreInput = iterator.hasNext();
                            } else if (hasNotConsumedAppendee) {
                                next = value;
                                hasNotConsumedAppendee = false;
                            } else
                                throw new NoSuchElementException("Lazy.append(T,Iterable<T>): cannot seek beyond the end of the sequence");
                            return next;
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.append(T,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.append(T,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * append: given the input sequence and an item, return a new, lazily-evaluated sequence containing the input with the item
     * as the final element.
     *
     * @param value the item to be appended
     * @param <T>   the type of the element in the input sequence
     * @return a sequence containing all the elements of 'input' followed by 'value'
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Function<Iterable<T>, Iterable<T>> append(final T value) {
        notNull(value, "Lazy.append(T)", "value");
        return input -> append(value, input);
    }

    /**
     * See <A href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</A>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * map: (T -> U) -> T seq -> U seq
     *
     * @param <T>   the type of the element in the input sequence
     * @param <U>   the type of the element in the output sequence
     * @param f     a transformation function which takes a object of type A and returns an object, presumably related, of type B
     * @param input a sequence to be fed into f
     * @return a lazily-evaluated sequence of type B containing the transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T, U> Iterable<U> map(final Function<? super T, ? extends U> f, final Iterable<T> input) {
        notNull(f, "Lazy.map(Function<T,R>,Iterable<T>)", "f");
        notNull(input, "Lazy.map(Function<T,R>,Iterable<T>)", "input");

        return new Iterable<U>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<U> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<U>() {
                        private final Iterator<? extends T> iterator = input.iterator();
                        private boolean hasMoreInput = iterator.hasNext();

                        public boolean hasNext() {
                            hasMoreInput = iterator.hasNext();
                            return hasMoreInput;
                        }

                        public U next() {
                            final U next;
                            if (hasMoreInput) {
                                next = f.apply(iterator.next());
                                hasMoreInput = iterator.hasNext();
                            } else
                                throw new NoSuchElementException("Lazy.map(Function<T,R>,Iterable<T>): cannot seek beyond the end of the sequence");
                            return next;
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.map(Function<T,R>,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.map(Function<T,R>,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * map: (T -> U) -> T seq -> U seq
     *
     * @param <T> the type of the element in the input sequence
     * @param <U> the type of the element in the output sequence
     * @param f   a transformation function which takes a object of type A and returns an object, presumably related, of type B
     * @return a curried function that expects an input sequence which it feeds to the transformation f which returns a lazily-evaluated
     * sequence of type U containing the transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T, U> Function<Iterable<T>, Iterable<U>> map(final Function<? super T, ? extends U> f) {
        notNull(f, "Lazy.map(Function<T,R>)", "f");
        return input -> Lazy.map(f, input);
    }

    /**
     * See <A href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</A>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * mapi: (Integer -> T -> U) -> T seq -> U seq
     *
     * @param <T>   the type of the element in the input sequence
     * @param <U>   the type of the element in the output sequence
     * @param f     a transformation function which takes a object of type A and returns an object, presumably related, of type B
     * @param input a sequence to be fed into f
     * @return a lazily-evaluated sequence of type B containing the transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T, U> Iterable<U> mapi(final BiFunction<Integer, ? super T, ? extends U> f, final Iterable<T> input) {
        notNull(f, "Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>)", "f");
        notNull(input, "Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>)", "input");

        return new Iterable<U>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<U> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<U>() {
                        private final Iterator<? extends T> iterator = input.iterator();
                        private boolean hasMoreInput = iterator.hasNext();
                        private int counter;

                        public boolean hasNext() {
                            hasMoreInput = iterator.hasNext();
                            return hasMoreInput;
                        }

                        public U next() {
                            final U next;
                            if (hasMoreInput) {
                                next = f.apply(counter++, iterator.next());
                                hasMoreInput = iterator.hasNext();
                            } else
                                throw new NoSuchElementException("Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>): cannot seek beyond the end of the sequence");
                            return next;

                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.mapi(BiFunction<Integer,U,V>,Iterable<U>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into an element in the output sequence.
     * mapi: (int -> T -> U) -> T seq -> U seq
     *
     * @param <T> the type of the element in the input sequence
     * @param <U> the type of the element in the output sequence
     * @param f   a transformation function which takes a object of type A and returns an object, presumably related, of type B
     * @return a curried function that expects an input sequence which it feeds to the transformation f which returns a lazily-evaluated
     * sequence of type U containing the transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Currying">Currying</a>
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T, U> Function<Iterable<T>, Iterable<U>> mapi(final BiFunction<Integer, ? super T, ? extends U> f) {
        notNull(f, "Lazy.mapi(BiFunction<Integer,U,V>)", "f");
        return input -> Lazy.mapi(f, input);
    }

    /**
     * Concatenate two sequences and return a new sequence containing the concatenation.
     *
     * @param input1 first input sequence
     * @param input2 second input sequence
     * @param <T>    the type of the element in the input sequence
     * @return a lazily-evaluated sequence containing the elements of the first sequence followed by the elements of the second sequence
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> concat(final Iterable<? extends T> input1, final Iterable<? extends T> input2) {
        notNull(input1, "Lazy.concat(Iterable<T>,Iterable<T>)", "input1");
        notNull(input2, "Lazy.concat(Iterable<T>,Iterable<T>)", "input2");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private final Iterator<? extends T> _s1 = input1.iterator();
                        private final Iterator<? extends T> _s2 = input2.iterator();

                        public boolean hasNext() {
                            return _s1.hasNext() || _s2.hasNext();
                        }

                        public T next() {
                            if (_s1.hasNext()) return _s1.next();
                            if (_s2.hasNext()) return _s2.next();
                            throw new NoSuchElementException("Lazy.concat(Iterable<T>,Iterable<T>): cannot seek beyond the end of the sequence");
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.concat(Iterable<T>,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.concat(Iterable<T>,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
     *                  the input element is passed through to the output otherwise it is ignored.
     * @param input     a sequence of objects
     * @return a lazily-evaluated sequence which contains zero or more of the elements of the input sequence. Each element is included only if
     * the filter function returns true for the element.
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> filter(final Predicate<? super T> predicate, final Iterable<T> input) {
        notNull(predicate, "Lazy.filter(Predicate<T>,Iterable<T>)", "predicate");
        notNull(input, "Lazy.filter(Predicate<T>,Iterable<T>)", "input");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private final Iterator<T> _input = input.iterator();
                        private final Predicate<? super T> _f = predicate;
                        private T _next;

                        public boolean hasNext() {
                            while (isNull(_next) && // ie we haven't already read the next element
                                    _input.hasNext()) {
                                final T next = _input.next();
                                if (_f.test(next)) {
                                    _next = next;
                                    return true;
                                }
                            }
                            return _next != null;
                        }

                        public T next() {
                            if (hasNext()) {
                                final T next = _next;
                                _next = null;
                                return next;
                            }
                            throw new NoSuchElementException("Lazy.filter(Predicate<T>,Iterable<T>): cannot seek beyond the end of the sequence");
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.filter(Predicate<T>,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.filter(Predicate<T>,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Filter</a>
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate a filter function. This is passed each input element in turn and returns either true or false. If true then
     *                  the input element is passed through to the output otherwise it is ignored.
     * @return a lazily-evaluated sequence which contains zero or more of the elements of the input sequence. Each element is included only if
     * the filter function returns true for the element.
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Function<Iterable<T>, Iterable<T>> filter(final Predicate<? super T> predicate) {
        notNull(predicate, "Lazy.filter(Predicate<T>)", "predicate");
        return input -> Lazy.filter(predicate, input);
    }

    /**
     * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     *
     * @param <T>     the type of the element in the input sequence
     * @param <U>     the type of the element in the output sequence
     * @param chooser map function. This transforms the input element into an Option
     * @param input   input sequence
     * @return a lazily-evaluated sequence of transformed elements, numbering less than or equal to the number of input elements
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T, U> Iterable<U> choose(final Function<? super T, Option<U>> chooser, final Iterable<T> input) {
        notNull(chooser, "Lazy.choose(Function<A,Option<B>>,Iterable<A>)", "chooser");
        notNull(input, "Lazy.choose(Function<A,Option<B>>,Iterable<A>)", "input");

        return new Iterable<U>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<U> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<U>() {
                        private final Iterator<T> _input = input.iterator();
                        private final Function<? super T, Option<U>> _f = chooser;
                        private Option<U> _next = Option.none();

                        public boolean hasNext() {
                            while (_next.isNone() && // ie we haven't already read the next element
                                    _input.hasNext()) {
                                final Option<U> next = _f.apply(_input.next());
                                if (next.isSome()) {
                                    _next = next;
                                    return true;
                                }
                            }
                            return _next.isSome();
                        }

                        public U next() {
                            if (hasNext()) {
                                final Option<U> next = _next;
                                _next = Option.none();
                                return next.get();
                            }
                            throw new NoSuchElementException("Lazy.choose(Function<T,Option<U>>,Iterable<T>): cannot seek beyond the end of the sequence");
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.choose(Function<T,Option<U>>,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.choose(Function<T,Option<U>>,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * choose: this is a map transformation with the difference being that the number of elements in the output sequence may
     * be between zero and the number of elements in the input sequence.
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * choose: (A -> B option) -> A list -> B list
     *
     * @param <T>     the type of the element in the input sequence
     * @param <U>     the type of the element in the output sequence
     * @param chooser map function. This transforms the input element into an Option
     * @return a lazily-evaluated sequence of transformed elements, numbering less than or equal to the number of input elements
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T, U> Function<Iterable<T>, Iterable<U>> choose(final Function<? super T, Option<U>> chooser) {
        notNull(chooser, "Lazy.choose(Function<A,Option<B>>)", "chooser");
        return input -> Lazy.choose(chooser, input);
    }

    /**
     * The init function, not dissimilar to list comprehensions, which is used to return a new finite sequence whose contents are
     * determined by successive calls to the function f.
     * init: (int -> T) -> int -> T seq
     *
     * @param <T>     the type of the element in the output sequence
     * @param f       generator function used to produce the individual elements of the output sequence.
     *                This function is called by init with the unity-based position of the current element in the output sequence being
     *                produced. Therefore, the first time f is called it will receive a literal '1' as its argument; the second time
     *                '2'; etc.
     * @param howMany the number of elements in the output sequence
     * @return a lazily-evaluated sequence which will contain no more than 'howMany' elements of type 'T' which were generated by the function 'f'
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> init(final Function<Integer, ? extends T> f, final int howMany) {
        notNull(f, "Lazy.init(Function<Integer,T>,int)", "f");
        if (howMany < 1)
            throw new IllegalArgumentException("Lazy.init(Function<Integer,T>,int): howMany must be non-negative");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private int _counter = 1;
                        private final Function<Integer, ? extends T> _f = f;

                        public boolean hasNext() {
                            return _counter <= howMany;
                        }

                        public T next() {
                            if (!hasNext())
                                throw new NoSuchElementException("Lazy.init(Function<Integer,T>,int): cannot seek beyond the end of the sequence");
                            return _f.apply(_counter++);
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.init(Function<Integer,T>,int): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.init(Function<Integer,T>,int): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * The init function, not dissimilar to list comprehensions, which is used to return a new infinite sequence whose contents are
     * determined by successive calls to the function f.
     * init: (int -> T) -> T seq
     *
     * @param <T> the type of the element in the output sequence
     * @param f   generator function used to produce the individual elements of the output sequence.
     *            This function is called by init with the unity-based position of the current element in the output sequence being
     *            produced. Therefore, the first time f is called it will receive a literal '1' as its argument; the second time
     *            '2'; etc.
     * @return a potentially infinite sequence containing elements of type 'T' which were generated by the function 'f'
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> init(final Function<Integer, ? extends T> f) {
        notNull(f, "Lazy.init(Function<Integer,T>)", "f");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private int _counter = 1;
                        private final Function<Integer, ? extends T> _f = f;

                        public boolean hasNext() {
                            return true;
                        }

                        public T next() {
                            return _f.apply(_counter++);
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.init(Function<Integer,T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.init(Function<Integer,T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
     * These sequences are concatenated into one final output sequence at the end of the transformation.
     * map: (T -> U list) -> T list -> U list
     *
     * @param <T>   the type of the element in the input sequence
     * @param <U>   the type of the element in the output sequence
     * @param f     a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
     * @param input a sequence to be fed into f
     * @return a lazily-evaluated sequence of type U containing the concatenated sequences of transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T, U> Iterable<U> flatMap(final Function<? super T, ? extends Iterable<U>> f, final Iterable<T> input) {
        notNull(f, "Lazy.flatMap(Function<A,Iterable<B>>,Iterable<A>)", "f");
        notNull(input, "Lazy.flatMap(Function<A,Iterable<B>>,Iterable<A>)", "input");

        return new Iterable<U>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<U> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<U>() {
                        private final Iterator<T> it = input.iterator();
                        private List<U> cache = new ArrayList<>();
                        private Iterator<U> cacheIterator = cache.iterator();

                        public boolean hasNext() {
                            return it.hasNext() || cacheIterator.hasNext();
                        }

                        public U next() {
                            if (!hasNext())
                                throw new NoSuchElementException("Lazy.flatMap(Function<T,Iterable<U>>,Iterable<T>): cannot seek beyond the end of the sequence");
                            if (cacheIterator.hasNext()) return cacheIterator.next();
                            cache = StreamSupport.stream(f.apply(it.next()).spliterator(), false).collect(Collectors.toList());
                            cacheIterator = cache.iterator();
                            return cacheIterator.next();
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.flatMap(Function<T,Iterable<U>>,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.flatMap(Function<T,Iterable<U>>,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Map</a>
     * This is a 1-to-1 transformation. Every element in the input sequence will be transformed into a sequence of output elements.
     * These sequences are concatenated into one final output sequence at the end of the transformation.
     * map: (T -> U list) -> T list -> U list
     *
     * @param <T> the type of the element in the input sequence
     * @param <U> the type of the element in the output sequence
     * @param f   a transformation function which takes a object of type T and returns a sequence of objects, presumably related, of type U
     * @return a function returning a lazily-evaluated sequence of type U containing the concatenated sequences of transformed values.
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T, U> Function<Iterable<T>, Iterable<U>> flatMap(final Function<? super T, ? extends Iterable<U>> f) {
        notNull(f, "Lazy.flatMap(Function<A,Iterable<B>>)", "f");
        return input -> Lazy.flatMap(f, input);
    }

    /**
     * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
     * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
     *
     * @param howMany a non-negative number of elements to be discarded from the input sequence
     * @param input   the input sequence
     * @param <T>     the type of the element in the input sequence
     * @return a lazily-evaluated sequence containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
     * are skipped than are present in the 'list'
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> skip(final int howMany, final Iterable<T> input) {
        if (howMany < 0)
            throw new IllegalArgumentException("Lazy.skip(int,Iterable<T>): howMany must not be negative");
        notNull(input, "Lazy.skip(int,Iterable<T>)", "input");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private final Iterator<T> it = input.iterator();
                        private boolean haveWeSkipped;

                        public boolean hasNext() {
                            if (haveWeSkipped) return it.hasNext();
                            for (int i = 0; i < howMany; ++i)
                                if (it.hasNext()) it.next();
                                else return false;
                            haveWeSkipped = true;
                            return it.hasNext();
                        }

                        public T next() {
                            if (!hasNext())
                                throw new NoSuchElementException("Lazy.skip(int,Iterable<T>): cannot seek beyond the end of the sequence");
                            return it.next();
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.skip(int,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.skip(int,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * skip: the converse of <tt>take</tt>. Given a list return another list containing those elements that follow the
     * first 'howMany' elements. That is, if we skip(1,[1,2,3]) then we have [2,3]
     *
     * @param <T>     the type of the element in the input sequence
     * @param howMany a non-negative number of elements to be discarded from the input sequence
     * @return a lazily-evaluated sequence containing the remaining elements after the first 'howMany' elements of 'list' or an empty list if more elements
     * are skipped than are present in the 'list'
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Function<Iterable<T>, Iterable<T>> skip(final int howMany) {
        if (howMany < 0)
            throw new IllegalArgumentException("Lazy.skip(int): howMany must not be negative");
        return input -> Lazy.skip(howMany, input);
    }

    /**
     * skipWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate ignore elements in the input while the predicate is true.
     * @param input     the input sequence
     * @return a lazily-evaluated sequence containing the remaining elements after and including the first element for which
     * the predicate returns false
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> skipWhile(final Predicate<? super T> predicate, final Iterable<T> input) {
        notNull(predicate, "Lazy.skipWhile(Predicate<T>,Iterable<T>)", "predicate");
        notNull(input, "Lazy.skipWhile(Predicate<T>,Iterable<T>)", "input");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private final Iterator<T> it = input.iterator();
                        private boolean haveWeSkipped = false;
                        private T next;
                        private boolean haveWeConsumedNext = false;
                        private boolean nextIsSet = false;

                        public boolean hasNext() {
                            if (haveWeSkipped) return it.hasNext();
                            if (it.hasNext()) {
                                boolean hasNext;
                                while ((hasNext = it.hasNext()) && predicate.test(next = it.next())) ;
                                if (hasNext) {
                                    nextIsSet = true;
                                    haveWeSkipped = true;
                                    return true;
                                } else return false;
                            }
                            haveWeSkipped = true;
                            return false;
                        }

                        public T next() {
                            if (!hasNext())
                                throw new NoSuchElementException("Lazy.skipWhile(Predicate<T>,Iterable<T>): cannot seek beyond the end of the sequence");
                            if (haveWeConsumedNext && !nextIsSet) next = it.next();
                            haveWeConsumedNext = true;
                            nextIsSet = false;
                            return next;
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.skipWhile(Predicate<T>,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.skipWhile(Predicate<T>,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * skipWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate ignore elements in the input while the predicate is true.
     * @return a lazily-evaluated sequence containing the remaining elements after and including the first element for which
     * the predicate returns false
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Function<Iterable<T>, Iterable<T>> skipWhile(final Predicate<? super T> predicate) {
        notNull(predicate, "Lazy.skipWhile(Predicate<T>)", "predicate");
        return input -> Lazy.skipWhile(predicate, input);
    }

    /**
     * take: given a sequence return another sequence containing the first 'howMany' elements
     *
     * @param howMany a positive number of elements to be returned from the input sequence
     * @param input   the input sequence
     * @param <T>     the type of the element in the input sequence
     * @return a sequence containing the first 'howMany' elements of 'input'
     * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
     */
    public static <T> Iterable<T> take(final int howMany, final Iterable<? extends T> input) {
        if (howMany < 0)
            throw new IllegalArgumentException("Lazy.take(int,Iterable<T>): howMany must not be negative");
        notNull(input, "Lazy.take(int,Iterable<T>)", "input");

        if (howMany == 0) return Collections.emptyList();

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private final Iterator<? extends T> it = input.iterator();
                        private int howManyHaveWeRetrievedAlready;

                        public boolean hasNext() {
                            return howManyHaveWeRetrievedAlready < howMany && it.hasNext();
                        }

                        public T next() {
                            if (howManyHaveWeRetrievedAlready >= howMany)
                                throw new java.util.NoSuchElementException("Lazy.take(int,Iterable<T>): cannot seek beyond the end of the sequence");
                            final T next = it.next();
                            howManyHaveWeRetrievedAlready++;
                            return next;
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.take(int,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.take(int,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * take: given a sequence return another sequence containing the first 'howMany' elements
     *
     * @param <T>     the type of the element in the input sequence
     * @param howMany a positive number of elements to be returned from the input sequence
     * @return a sequence containing the first 'howMany' elements of 'list'
     * @throws java.util.NoSuchElementException if more elements are requested than are present in the input sequence
     */
    public static <T> Function<Iterable<T>, Iterable<T>> take(final int howMany) {
        if (howMany < 0)
            throw new IllegalArgumentException("Lazy.take(int): howMany must not be negative");
        return input -> Lazy.take(howMany, input);
    }

    /**
     * takeWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate ignore elements in the input while the predicate is true.
     * @param input     the input sequence
     * @return a lazily-evaluated sequence containing the remaining elements after and including the first element for which
     * the predicate returns false
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Iterable<T> takeWhile(final Predicate<? super T> predicate, final Iterable<T> input) {
        notNull(predicate, "Lazy.takeWhile(Predicate<T>,Iterable<T>)", "predicate");
        notNull(input, "Lazy.takeWhile(Predicate<T>,Iterable<T>)", "input");

        return new Iterable<T>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<T> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<T>() {
                        private final Iterator<T> it = input.iterator();
                        private boolean haveWeFinished;
                        private T next;
                        private boolean haveWeCheckedTheCurrentElement;

                        public boolean hasNext() {
                            if (!haveWeFinished) {
                                if (!haveWeCheckedTheCurrentElement) {
                                    if (it.hasNext()) {
                                        next = it.next();
                                        if (predicate.test(next)) {
                                            haveWeCheckedTheCurrentElement = true;
                                            return true;
                                        } else {
                                            haveWeCheckedTheCurrentElement = true;
                                            haveWeFinished = true;
                                            return false;
                                        }
                                    } else {
                                        haveWeFinished = true;
                                        return false;
                                    }
                                } else {
                                    return true;
                                }
                            } else {
                                return false;
                            }
                        }

                        public T next() {
                            if (!haveWeFinished) {
                                if (hasNext()) {
                                    haveWeCheckedTheCurrentElement = false;
                                    return next;
                                } else
                                    throw new NoSuchElementException("Lazy.takeWhile(Predicate<T>,Iterable<T>): cannot seek beyond the end of the sequence");
                            }
                            throw new NoSuchElementException();
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.takeWhile(Predicate<T>,Iterable<T>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.takeWhile(Predicate<T>,Iterable<T>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * takeWhile: the converse of <tt>takeWhile</tt>. Given a list return another list containing all those elements from,
     * and including, the first element for which the predicate returns false. That is, if we skip(isOdd,[1,2,3]) then we have [2,3]
     *
     * @param <T>       the type of the element in the input sequence
     * @param predicate ignore elements in the input while the predicate is true.
     * @return a lazily-evaluated sequence containing the remaining elements after and including the first element for which
     * the predicate returns false
     * @see <a href="http://en.wikipedia.org/wiki/Lazy_evaluation">Lazy evaluation</a>
     */
    public static <T> Function<Iterable<T>, Iterable<T>> takeWhile(final Predicate<? super T> predicate) {
        notNull(predicate, "Lazy.takeWhile(Predicate<T>)", "predicate");
        return input -> Lazy.takeWhile(predicate, input);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
     * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> Iterable<A> unfold(final Function<? super B, Tuple2<A, B>> unspool, final Predicate<? super B> finished, final B seed) {
        notNull(unspool, "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)", "unspooler");
        notNull(finished, "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B)", "finished");

        return new Iterable<A>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<A> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<A>() {
                        B next = seed;

                        public boolean hasNext() {
                            return !finished.test(next);
                        }

                        public A next() {
                            if (!hasNext()) throw new NoSuchElementException();
                            final Tuple2<A, B> t = unspool.apply(next);
                            next = t._2();
                            return t._1();
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>,B): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
     * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> WithSeed<A, B> unfold(final Function<? super B, Tuple2<A, B>> unspooler, final Predicate<? super B> finished) {
        notNull(unspooler, "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>)", "unspooler");
        notNull(finished, "Lazy.unfold(Function<B,Tuple2<A,B>>,Predicate<B>)", "finished");

        return seed -> unfold(unspooler, finished, seed);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a> and
     * <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> WithFinished<A, B> unfold(final Function<? super B, Tuple2<A, B>> unspooler) {
        notNull(unspooler, "Lazy.unfold(Function<B,Tuple2<A,B>>)", "unspooler");
        return finished -> seed -> unfold(unspooler, finished, seed);
    }

    public interface WithSeed<A, B> {
        Iterable<A> withSeed(B seed);
    }

    public interface WithFinished<A, B> {
        WithSeed<A, B> withFinished(Predicate<? super B> finished);
    }

    /**
     * See <a href="http://en.wikipedia.org/wiki/Unfold_(higher-order_function)">Unfold</a>
     * and <a href="http://en.wikipedia.org/wiki/Anamorphism">Anamorphism</a>
     * This is the converse of <tt>fold</tt>
     * unfold: (b -> (a, b)) -> (b -> Bool) -> b -> [a]
     */
    public static <A, B> Iterable<A> unfold(final Function<? super B, Option<Tuple2<A, B>>> unspooler, final B seed) {
        notNull(unspooler, "Lazy.unfold(Function<B,Option<Tuple2<A,B>>>,B)", "unspooler");

        return new Iterable<A>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<A> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<A>() {
                        B next = seed;

                        public boolean hasNext() {
                            return unspooler.apply(next).isSome();
                        }

                        public A next() {
                            final Option<Tuple2<A, B>> temp = unspooler.apply(next);
                            if (temp.isNone()) throw new NoSuchElementException();
                            next = temp.get()._2();
                            return temp.get()._1();
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.unfold(Func,B): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.unfold(Func,B): This Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * Lazily split a sequence into the elements that satisfy the predicate and those that do not, preserving order.
     * <p>
     * Both halves draw on a single traversal of {@code input}, which starts only when either half is first
     * iterated. The predicate is evaluated exactly once per element, in input order. An element destined for the
     * other half is held until that half asks for it, so consuming only one half buffers the other half's
     * elements. Infinite inputs work, provided the half being consumed keeps receiving elements.
     * <p>
     * Like the other lazy sequences, each half may be iterated only once.
     *
     * @param predicate decides which half each element belongs to
     * @param input     the sequence to split
     * @param <T>       the type of the elements
     * @return the elements satisfying the predicate, and the remaining elements
     */
    public static <T> Tuple2<Iterable<T>, Iterable<T>> partition(final Predicate<? super T> predicate, final Iterable<T> input) {
        final String op = "Lazy.partition(Predicate<T>,Iterable<T>)";
        notNull(predicate, op, "predicate");
        notNull(input, op, "input");
        final Partitioner<T> partitioner = new Partitioner<>(predicate, input);
        return new Tuple2<>(partitioner.half(true, op), partitioner.half(false, op));
    }

    public static <T> Function<Iterable<T>, Tuple2<Iterable<T>, Iterable<T>>> partition(final Predicate<? super T> predicate) {
        notNull(predicate, "Lazy.partition(Predicate<T>)", "predicate");
        return input -> partition(predicate, input);
    }

    /**
     * The shared state behind a lazy partition: one traversal of the input, and a queue of elements for each half
     * that have been read but not yet consumed. LinkedList, not ArrayDeque, because elements may be null.
     */
    private static final class Partitioner<T> {
        private final Predicate<? super T> predicate;
        private final Iterable<T> input;
        private final java.util.LinkedList<T> matching = new java.util.LinkedList<>();
        private final java.util.LinkedList<T> rest = new java.util.LinkedList<>();
        private Iterator<T> source;

        Partitioner(final Predicate<? super T> predicate, final Iterable<T> input) {
            this.predicate = predicate;
            this.input = input;
        }

        /**
         * Read from the input until the requested half has an element waiting, or the input is exhausted.
         */
        private boolean fill(final boolean wantMatching) {
            if (source == null) source = input.iterator();
            final java.util.LinkedList<T> wanted = wantMatching ? matching : rest;
            while (wanted.isEmpty() && source.hasNext()) {
                final T element = source.next();
                (predicate.test(element) ? matching : rest).add(element);
            }
            return !wanted.isEmpty();
        }

        Iterable<T> half(final boolean matchingHalf, final String op) {
            final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);
            return () -> {
                if (!haveCreatedIterator.compareAndSet(false, true))
                    throw new UnsupportedOperationException(op + ": this Iterable does not allow multiple Iterators");
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return fill(matchingHalf);
                    }

                    @Override
                    public T next() {
                        if (!fill(matchingHalf))
                            throw new NoSuchElementException(op + ": cannot seek beyond the end of the sequence");
                        return (matchingHalf ? matching : rest).removeFirst();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException(op + ": it is not possible to remove elements from this sequence");
                    }
                };
            };
        }
    }

    /**
     * This sequence generator returns a list of Range objects which split the interval [1-'howManyElements') into 'howManyPartitions' Range objects.
     * If the interval cannot be divided exactly then the remainder is allocated evenly across the first
     * 'howManyElements' % 'howManyPartitions' Range objects.
     *
     * @param howManyElements   defines the exclusive upper bound of the interval to be split
     * @param howManyPartitions defines the number of Range objects to generate to cover the interval
     * @return a list of Range objects
     */
    public static Iterable<Range<Integer>> partition(final int howManyElements, final int howManyPartitions) {
        if (howManyElements <= 0)
            throw new IllegalArgumentException("Lazy.partition(int,int): howManyElements must be positive");
        if (howManyPartitions <= 0)
            throw new IllegalArgumentException("Lazy.partition(int,int): howManyPartitions must be positive");
        return partition(Functional.range(1), howManyElements, howManyPartitions);
    }

    /**
     * This sequence generator returns a sequence of Range objects which split the interval [1-'howManyElements') into 'howManyPartitions'
     * Range objects. If the interval cannot be divided exactly then the remainder is allocated evenly across the first
     * 'howManyElements' % 'howManyPartitions' Range objects.
     *
     * @param generator         a function which generates members of the input sequence
     * @param howManyElements   defines the exclusive upper bound of the interval to be split
     * @param howManyPartitions defines the number of Range objects to generate to cover the interval
     * @return a list of Range objects
     */
    public static <T> Iterable<Range<T>> partition(final Function<Integer, T> generator, final int howManyElements, final int howManyPartitions) {
        notNull(generator, "Lazy.partition(Function<Integer,T>,int,int)", "generator");
        if (howManyElements <= 0)
            throw new IllegalArgumentException("Lazy.partition(Function<Integer,T>,int,int): howManyElements must be positive");
        if (howManyPartitions <= 0)
            throw new IllegalArgumentException("Lazy.partition(Function<Integer,T>,int,int): howManyPartitions must be positive");

        final int size = howManyElements / howManyPartitions;
        final int remainder = howManyElements % howManyPartitions;

        assert size * howManyPartitions + remainder == howManyElements;

        final Integer seed = 0;
        final Function<Integer, Tuple2<T, Integer>> boundsCalculator = integer -> new Tuple2<>(
                generator.apply(1 + (integer * size + (integer <= remainder ? integer : remainder))),
                integer + 1);
        final Predicate<Integer> finished = integer -> integer > howManyPartitions;

        final Iterable<T> output = Lazy.unfold(boundsCalculator, finished, seed);

        return new Iterable<Range<T>>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<Range<T>> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<Range<T>>() {
                        final Iterator<T> iterator = output.iterator();
                        T last = iterator.next();

                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        public Range<T> next() {
                            if (!iterator.hasNext())
                                throw new NoSuchElementException("Lazy.partition(Function<Integer,T>,int,int): cannot seek beyond the end of the sequence");
                            final T next = iterator.next();
                            final Range<T> retval = new Range<>(last, next);
                            last = next;
                            return retval;
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.partition(Function<Integer,T>,int,int): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.partition(Function<Integer,T>,int,int): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * Convolution of functions
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param <A>      the type of the input sequence
     * @param <B>      the result type of the first transformation
     * @param <C>      the result type of the second transformation
     * @param zipFunc1 the first transformation function
     * @param zipFunc2 the second transformation function
     * @param input    the input sequence
     * @return a sequence of pairs. The first value of the pair is the result of the first transformation and the second value of the
     * pair of the result of the second transformation.
     */
    public static <A, B, C> Iterable<Tuple2<B, C>> zip(final Function<? super A, B> zipFunc1, final Function<? super A, C> zipFunc2, final Iterable<? extends A> input) {
        notNull(zipFunc1, "Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>)", "zipFunc1");
        notNull(zipFunc2, "Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>)", "zipFunc2");
        notNull(input, "Lazy.zip(Function<A,B>,Function<A,B>,Iterable<A>)", "input");

        return new Iterable<Tuple2<B, C>>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<Tuple2<B, C>> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<Tuple2<B, C>>() {
                        private final Iterator<? extends A> iterator = input.iterator();

                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        public Tuple2<B, C> next() {
                            if (!iterator.hasNext())
                                throw new NoSuchElementException("Lazy.zip(Function<A,B>,Function<A,C>,Iterable<A>): cannot seek beyond the end of the sequence");
                            final A next = iterator.next();
                            return new Tuple2<>(zipFunc1.apply(next), zipFunc2.apply(next));
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.zip(Function<A,B>,Function<A,C>,Iterable<A>): it is not possible to remove elements from this sequence");
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.zip(Function<A,B>,Function<A,C>,Iterable<A>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * Convolution of functions. That is, apply two transformation functions 'simultaneously' and return a list of pairs,
     * each of which contains one part of the results.
     *
     * @param zipFunc1 the transformation function that generates the first value in the resultant pair
     * @param zipFunc2 the transformation function that generates the second value in the resultant pair
     * @param <A>      a type that all the elements in the input list extend and that both of the transformation functions accept as input
     * @param <B>      the resulting type of the first transformation
     * @param <C>      the resulting type of the second transformation
     * @return a list of pairs containing the two transformed sequences
     */
    public static <A, B, C> Function<Iterable<? extends A>, Iterable<Tuple2<B, C>>> zip(final Function<? super A, B> zipFunc1, final Function<? super A, C> zipFunc2) {
        notNull(zipFunc1, "Lazy.zip(Function<A,B>,Function<A,B>)", "zipFunc1");
        notNull(zipFunc2, "Lazy.zip(Function<A,B>,Function<A,B>)", "zipFunc2");
        return input -> Lazy.zip(zipFunc1, zipFunc2, input);
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param input1 input sequence
     * @param input2 input sequence
     * @param <A>    the type of the element in the first input sequence
     * @param <B>    the type of the element in the second input sequence
     * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
     * in order. If the sequences do not have the same number of elements then an exception is thrown.
     * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
     */
    public static <A, B> Iterable<Tuple2<A, B>> zip(final Iterable<? extends A> input1, final Iterable<? extends B> input2) {
        notNull(input1, "Lazy.zip(Iterable<A>,Iterable<B>)", "input1");
        notNull(input2, "Lazy.zip(Iterable<A>,Iterable<B>)", "input2");

        return new Iterable<Tuple2<A, B>>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<Tuple2<A, B>> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<Tuple2<A, B>>() {
                        private final Iterator<? extends A> l1_it = input1.iterator();
                        private final Iterator<? extends B> l2_it = input2.iterator();

                        public boolean hasNext() {
                            return bothHaveNext();
                        }

                        public Tuple2<A, B> next() {
                            if (!bothHaveNext())
                                throw new NoSuchElementException();
                            return new Tuple2<>(l1_it.next(), l2_it.next());
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.zip(Iterable<A>,Iterable<B>): it is not possible to remove elements from this sequence");
                        }

                        private boolean bothHaveNext() {
                            final boolean l1_it_hasNext = l1_it.hasNext();
                            final boolean l2_it_hasNext = l2_it.hasNext();
                            if (l1_it_hasNext != l2_it_hasNext)
                                throw new IllegalArgumentException("Lazy.zip(Iterable<A>,Iterable<B>): cannot zip two iterables with different lengths");
                            return l1_it_hasNext;
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.zip(Iterable<A>,Iterable<B>): this Iterable does not allow multiple Iterators");
            }
        };
    }

    /**
     * The Convolution operator
     * See <a href="http://en.wikipedia.org/wiki/Zip_(higher-order_function)">Zip</a>
     *
     * @param input1 input sequence
     * @param input2 input sequence
     * @param input3 input sequence
     * @param <A>    the type of the element in the first input sequence
     * @param <B>    the type of the element in the second input sequence
     * @param <C>    the type of the element in the third input sequence
     * @return list of pairs; the first element from each of the two input sequences is the first pair in the output sequence and so on,
     * in order. If the sequences do not have the same number of elements then an exception is thrown.
     * @throws java.lang.IllegalArgumentException if either input sequence is null or if the sequences have differing lengths.
     */
    public static <A, B, C> Iterable<Tuple3<A, B, C>> zip3(final Iterable<? extends A> input1, final Iterable<? extends B> input2, final Iterable<? extends C> input3) {
        notNull(input1, "Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input1");
        notNull(input2, "Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input2");
        notNull(input3, "Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>)", "input3");

        return new Iterable<Tuple3<A, B, C>>() {
            private final AtomicBoolean haveCreatedIterator = new AtomicBoolean(false);

            public Iterator<Tuple3<A, B, C>> iterator() {
                if (haveCreatedIterator.compareAndSet(false, true))
                    return new Iterator<Tuple3<A, B, C>>() {
                        private final Iterator<? extends A> l1_it = input1.iterator();
                        private final Iterator<? extends B> l2_it = input2.iterator();
                        private final Iterator<? extends C> l3_it = input3.iterator();

                        public boolean hasNext() {
                            return allHaveNext();
                        }

                        public Tuple3<A, B, C> next() {
                            if (!allHaveNext())
                                throw new NoSuchElementException("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot seek beyond the end of the sequence");
                            return new Tuple3<>(l1_it.next(), l2_it.next(), l3_it.next());
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): it is not possible to remove elements from this sequence");
                        }

                        private boolean allHaveNext() {
                            final boolean l1_it_hasNext = l1_it.hasNext();
                            final boolean l2_it_hasNext = l2_it.hasNext();
                            final boolean l3_it_hasNext = l3_it.hasNext();
                            if (l1_it_hasNext != l2_it_hasNext || l1_it_hasNext != l3_it_hasNext)
                                throw new IllegalArgumentException("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): cannot zip three iterables with different lengths");
                            return l1_it_hasNext;
                        }
                    };
                else
                    throw new UnsupportedOperationException("Lazy.zip3(Iterable<A>,Iterable<B>,Iterable<C>): this Iterable does not allow multiple Iterators");
            }
        };
    }
}
