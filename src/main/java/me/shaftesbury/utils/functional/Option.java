package me.shaftesbury.utils.functional;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Option is an implementation of the <tt>option monad</tt>.
 * See http://en.wikipedia.org/wiki/Option_type
 * {@see http://en.wikipedia.org/wiki/Monad_(functional_programming)}
 */
public final class Option<T>
{
    private final T _t;
    private Option()
    {
        _t=null;
    }
    private Option(final T t)
    {
        _t = t;
    }

    /**
     * If this Option has a value then return it otherwise throw an exception. It is not intended that this method is called
     * without a prior call to {@link #isSome()}
     * @throws me.shaftesbury.utils.functional.OptionNoValueAccessException
     * @return the underlying value of the Option
     */
    public T Some()
    {
        if(_t!=null) return _t;
        else throw new OptionNoValueAccessException();
    }

    /**
     * Factory method similar to {@link #toOption(Object)}. The difference between them is that this method throws an exception
     * if a null is passed as the argument. {@link #toOption(Object)} returns a None.
     * @param t the object which is to be wrapped in an Option
     * @return the Option containing <tt>t</tt>
     * @throws me.shaftesbury.utils.functional.OptionNoValueAccessException
     */
    public static <U>Option<U> Some(final U t)
    {
        if(t==null) throw new OptionNoValueAccessException();
        return toOption(t);
    }

    /**
     * Factory method which returns an Option with no underlying value, ie a None
     * @param <U> the type of the underlying data, had there been any.
     * @return the Option None
     */
    public static <U>Option<U> None()
    {
        return new Option<>();
    }

    /**
     * Predicate. Does this Option object have a value?
     * @return true if this Option has a value, false otherwise
     */
    public boolean isSome()
    {
        return _t!=null;
    }

    /**
     * Predicate. Does this Option have a value?
     * @return true if this Option does not have a value, false otherwise.
     */
    public boolean isNone()
    {
        return _t==null;
    }

    /**
     * Equality operator. If the two Options are of the same type, they are both {@link #isSome()} and {@link #Some()} are
     * both equal then the two Options are considered to be equal. If one or both is {@link #None()}, <tt>o</tt> is not an
     * Option, <tt>o</tt> is an Option of another type or <tt>o</tt> is an Option of the same type but having a non-equal
     * underlying value (as determined using the underlying value's {@link #equals(Object)} function) then the two are not
     * considered equal.
     * @param o the Option which we wish to compare for equality with <tt>this</tt>.
     * @return true if they compare equally, false otherwise
     */
    public boolean equals(final Object o)
    {
        if(o instanceof Option<?>)
        {
            final Option<?> other = (Option<?>)o;
            try
            {
                return isSome()==other.isSome() && Some().equals(other.Some());
            }
            catch(final OptionNoValueAccessException ex)
            {
                return isNone() && other.isNone(); // every None is considered to be the same
            }
        }
        else return false;
    }

    /**
     * Return the hashCode of this Option
     * @return an integer hashcode
     */
    public int hashCode()
    {
        return isNone() ? 0 : 31 * _t.hashCode();
    }

    /**
     * Return a rendering of this Option as a String. That is, "Option( value.toString() )" or "None"
     * @return the string representation of the Option.
     */
    public String toString()
    {
        return isSome()
                ? "Option( "+ Some().toString() + " )"
                : "None";
    }

    /**
     * Factory method similar to {@link #Some(Object)}. The difference between them is that this method returns a None
     * if a null is passed as the argument. {@link #toOption(Object)} throws an exception.
     * @param t the object which is to be wrapped in an Option
     * @return the Option containing <tt>t</tt>
     */
    public static <U>Option<U> toOption(final U t)
    {
        return new Option<>(t);
    }

    /**
     * Apply a function to the underlying data if {@link #isSome()} and return the result otherwise return {@link #None()}
     * {@see http://en.wikipedia.org/wiki/Monad_(functional_programming)}
     * @param <U> the type of the resulting Option type
     * @param f the function to be bound
     * @return an Option containing either {@link #None()} or the result of the function <tt>f</tt>
     */
    public <U>Option<U> bind(final Function<T, Option<U>> f)
    {
        if(isSome()) return f.apply(Some());
        else return Option.None();
    }

    /**
     * Given two monadic Options apply the supplied binary function to them if they are both {@link #isSome()} and return
     * a wrapped Option containing the result or {@link #None()}.
     * @param f the binary function to be lifted
     * @param o1 the first Option to be passed to the lift function <tt>f</tt>
     * @param o2 the second Option to be passed to the lift function <tt>f</tt>
     * @param <A> the type of the first Option
     * @param <B> the type of the second Option
     * @param <C> the type of the resulting Option
     * @return an Option containing the result of the lifted function as applied to <tt>o1</tt> and <tt>o2</tt> or {@link #None()}
     */
    public static <A,B,C>Option<C>lift(final BiFunction<A,B,C> f, final Option<A> o1, final Option<B> o2)
    {
        if(o1.isSome() && o2.isSome()) return toOption(f.apply(o1.Some(),o2.Some()));
        else return None();
    }
}
