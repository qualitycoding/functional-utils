package me.shaftesbury.utils.functional;

import io.vavr.Function0;
import io.vavr.Function1;

import static java.util.Objects.requireNonNull;
import static me.shaftesbury.utils.functional.UsingWrapper.using;

public class Using<T> {
    private final Function0<T> value;

    Using(final Function0<T> value) {
        this.value = value;
    }

    public <R> R in(final Function1<T, R> f) {
        requireNonNull(f, "f must not be null");
        return f.apply(value.get());
    }

    public <R> Using<R> map(final Function1<T, R> f) {
        requireNonNull(f, "f must not be null");
        return using(in(f));
    }

    public <R> Using<R> flatMap(final Function1<T, Using<R>> f) {
        requireNonNull(f, "f must not be null");
        return using(in(f).value);
    }

    Function0<T> getValue() {
        return value;
    }
}
