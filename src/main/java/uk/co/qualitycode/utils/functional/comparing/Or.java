package uk.co.qualitycode.utils.functional.comparing;

import static java.util.Objects.isNull;

public class Or<T> implements Comparator<T> {
    private final Comparator<T> c1;
    private final Comparator<T> c2;

    private Or(final Comparator<T> c1, final Comparator<T> c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public boolean compareWith(final T value) {
        return c1.compareWith(value) || c2.compareWith(value);
    }

    public static <T> Or<T> or(final Comparator<T> c1, final Comparator<T> c2) {
        if (isNull(c1)) throw new IllegalArgumentException("c1 must not be null");
        if (isNull(c2)) throw new IllegalArgumentException("c2 must not be null");
        return new Or<>(c1, c2);
    }
}
