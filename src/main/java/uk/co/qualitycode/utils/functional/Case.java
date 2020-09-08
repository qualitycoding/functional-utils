package uk.co.qualitycode.utils.functional;

import java.util.function.Function;
import java.util.function.Predicate;

public final class Case<A, B> {
    private final Predicate<A> check;
    private final Function<A, B> result;

    public Case(final Predicate<A> chk, final Function<A, B> res) {
        this.check = chk;
        this.result = res;
    }

    public Boolean predicate(final A a) {
        return check.test(a);
    }

    public B results(final A a) {
        return result.apply(a);
    }
}
