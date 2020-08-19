package uk.co.qualitycode.utils.functional;

public class IsBetween<T extends Comparable<T>> {
    private enum Not {IS, NOT;}

    private final T value;
    private final Not notValue;

    private IsBetween(final T value) {
        this(value, Not.IS);
    }

    private IsBetween(final T value, final Not notValue) {
        this.value = value;
        this.notValue = notValue;
    }

    public static <A extends Comparable<A>> IsBetween<A> is(final A value) {
        return new IsBetween<>(value);
    }

    public static <A extends Comparable<A>> IsBetween<A> isNot(final A value) {
        return new IsBetween<>(value, Not.NOT);
    }

    public IsIt<T> between(final T lower) {
        return upper -> isOrIsNot(Functional.between(lower, upper, value));
    }

    public interface IsIt<T> {
        boolean and(final T upper);
    }

    private boolean isOrIsNot(final boolean value) {
        return (notValue == Not.IS) == value;
    }
}

