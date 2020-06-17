package me.shaftesbury.utils.functional;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class LispList
{
    public interface List<T>
    {
        T head();
        List<T> tail();

        boolean isEmpty();
    }

    private static <T>List<T> reverse(final List<T> input, final List<T> accumulator)
    {
        return input.isEmpty() ? accumulator : reverse(input.tail(),list(input.head(),accumulator));
    }

    public static <T>List<T> reverse(final List<T> input)
    {
        return reverse(input, nil());
    }

    private static <T>List<T> filter(final Function<T, Boolean> f, final List<T> input, final List<T> accumulator)
    {
        return input.isEmpty()
                ? accumulator
                : filter(f,input.tail(),f.apply(input.head())?list(input.head(), accumulator):accumulator);
    }

    public static <T>List<T> filter(final Function<T, Boolean> f, final List<T> input)
    {
        return reverse(filter(f, input, nil()));
    }

    public static <T,R>List<R> map(final Function<T, R> f, final List<T> input)
    {
        return input.isEmpty()
               ? (List<R>)nil()
               : list(f.apply(input.head()),map(f,input.tail()));
    }

    public static <T,R>R fold(final BiFunction<R,T,R> f, final R initialValue, final List<T> input)
    {
        return input.isEmpty()
                ? initialValue
                : fold(f,f.apply(initialValue,input.head()),input.tail());
    }
    public static <T,R>R foldRight(final BiFunction<T,R,R> f, final R initialValue, final List<T> input)
    {
        return input.isEmpty()
                ? initialValue
                : f.apply(input.head(),foldRight(f,initialValue,input.tail()));
    }

    public static <T>List<T> cons(final T t, final List<T> l) { return list(t,l); }
    public static <T>T car(final List<T> l) { return l.head(); }
    public static <T>List<T> cdr(final List<T> l) { return l.tail(); }
    public static <T>T cadr(final List<T> l) { return car(cdr(l)); }
    public static <T>List<T> compose(final T t1, final T t2) { return list(t1, list(t2, nil())); }

    public static class EmptyListHasNoHead extends RuntimeException {}
    public static class EmptyListHasNoTail extends RuntimeException {}

    private static LispList ll = new LispList();

    public final class NonEmptyList<T> implements List<T>
    {
        final private T _head;
        final private List<T> _tail;

        NonEmptyList(final T head, final List<T> tail)
        {
            _head = head;
            _tail = tail;
        }


        public T head() {
            return _head;
        }


        public List<T> tail() {
            return _tail;
        }


        public boolean isEmpty() {
            return false;
        }

        public boolean equals(final Object o)
        {
            if(o==null) return false;
            if(o instanceof NonEmptyList<?>)
                return ((NonEmptyList<?>)o).head().equals(head()) && ((NonEmptyList<?>)o).tail().equals(tail());
            else return false;
        }

        public String toString()
        {
            return "( "+head()+", "+tail().toString()+" )";
        }
    }

    public static <T>List<T> nil()
    {
        return new List<T>(){

            public T head() {
                throw new EmptyListHasNoHead();
            }


            public List<T> tail() {
                throw new EmptyListHasNoTail();
            }


            public boolean isEmpty() {
                return true;
            }

            public boolean equals(final Object o)
            {
                if(o==null) return false;
                return o instanceof List<?> && ((List<?>)o).isEmpty();
            }

            public String toString() { return "( )";}
        };
    }

    public static <T>List<T> list(final T head, final List<T> tail)
    {
        return ll.new NonEmptyList<>(head, tail);
    }
}
