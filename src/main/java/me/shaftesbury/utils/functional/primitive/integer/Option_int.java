package me.shaftesbury.utils.functional.primitive.integer;

import me.shaftesbury.utils.functional.OptionNoValueAccessException;

/**
 * Option is an implementation of the <tt>option monad</tt>.
 * See http://en.wikipedia.org/wiki/Option_type
 * {@see http://en.wikipedia.org/wiki/Monad_(functional_programming)}
 */
public final class Option_int
{
    private final int _t;
    private final boolean isSet;
    private Option_int()
    {
        isSet=false; _t=Integer.MIN_VALUE;
    }
    private Option_int(final int t)
    {
        isSet = true; _t = t;
    }

    /**
     * If this Option has a value then return it otherwise throw an exception. It is not intended that this method is called
     * without a prior call to {@link #isSome()}
     * @throws me.shaftesbury.utils.functional.OptionNoValueAccessException
     * @return the underlying value of the Option
     */
    public int Some()
    {
        if(isSet) return _t;
        else throw new OptionNoValueAccessException();
    }

    /**
     * Factory method similar to {@link #toOption(int)}. The difference between them is that this method throws an exception
     * if a null is passed as the argument. {@link #toOption(int)} returns a None.
     * @param t the object which is to be wrapped in an Option
     * @return the Option containing <tt>t</tt>
     * @throws me.shaftesbury.utils.functional.OptionNoValueAccessException
     */
    public static Option_int Some(final int t)
    {
        return toOption(t);
    }

    /**
     * Factory method which returns an Option with no underlying value, ie a None
     * @return the Option None
     */
    public static Option_int None()
    {
        return new Option_int();
    }

    /**
     * Predicate. Does this Option object have a value?
     * @return true if this Option has a value, false otherwise
     */
    public boolean isSome()
    {
        return isSet;
    }

    /**
     * Predicate. Does this Option have a value?
     * @return true if this Option does not have a value, false otherwise.
     */
    public boolean isNone()
    {
        return !isSet;
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
        if(o instanceof Option_int)
        {
            final Option_int other = (Option_int)o;
            try
            {
                return isSome()==other.isSome() && Some()==other.Some();
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
        return isNone() ? -1 : 3 * _t;
    }

    /**
     * Return a rendering of this Option as a String. That is, "Option( value.toString() )" or "None"
     * @return the string representation of the Option.
     */
    public String toString()
    {
        return isSome()
                ? "Option( "+ Some() + " )"
                : "None";
    }

    /**
     * Factory method similar to {@link #Some(int)}. The difference between them is that this method returns a None
     * if a null is passed as the argument. {@link #toOption(int)} throws an exception.
     * @param t the object which is to be wrapped in an Option
     * @return the Option containing <tt>t</tt>
     */
    public static Option_int toOption(final int t)
    {
        return new Option_int(t);
    }

    /**
     * Apply a function to the underlying data if {@link #isSome()} and return the result otherwise return {@link #None()}
     * {@see http://en.wikipedia.org/wiki/Monad_(functional_programming)}
     * @param f the function to be bound
     * @return an Option containing either {@link #None()} or the result of the function <tt>f</tt>
     */
    public Option_int bind(final Func_int_T<Option_int> f)
    {
        if(isSome()) return f.apply(Some());
        else return Option_int.None();
    }

    /**
     * Given two monadic Options apply the supplied binary function to them if they are both {@link #isSome()} and return
     * a wrapped Option containing the result or {@link #None()}.
     * @param f the binary function to be lifted
     * @param o1 the first Option to be passed to the lift function <tt>f</tt>
     * @param o2 the second Option to be passed to the lift function <tt>f</tt>
     * @return an Option containing the result of the lifted function as applied to <tt>o1</tt> and <tt>o2</tt> or {@link #None()}
     */
    public static Option_int lift(final Func2_int_int_int f, final Option_int o1, final Option_int o2)
    {
        if(o1.isSome() && o2.isSome()) return toOption(f.apply(o1.Some(),o2.Some()));
        else return None();
    }
}
