package me.shaftesbury.utils.functional;

import java.util.function.Function;

public final class Case<A, B>
{
    private final Function<A, Boolean> check;
    private final Function<A, B> result;
    public Case(final Function<A, Boolean> chk, final Function<A, B> res) {this.check = chk; this.result=res;}
    public final Boolean predicate(final A a) { return check.apply(a); }
    public final B results(final A a) { return result.apply(a); }
}
