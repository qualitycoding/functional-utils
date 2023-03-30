package uk.co.qualitycode.utils.functional.comparing.integer;

import uk.co.qualitycode.utils.functional.comparing.Comparator;
import io.vavr.control.Either;

import java.util.Objects;
import java.util.function.Function;

import static java.util.Objects.isNull;

public class HasSize implements Comparator<Integer> {
    private final Either<Comparator<Integer>, Integer> i;

    private HasSize(final int i) {
        this.i = Either.right(i);
    }

    private HasSize(final Comparator<Integer> c) {
        this.i = Either.left(c);
    }

    public static Comparator<Integer> isEmpty() {
        return new HasSize(0);
    }

    public static Comparator<Integer> hasSize(final int i) {
        return new HasSize(i);
    }

    public static Comparator<Integer> hasSize(final Comparator<Integer> c) {
        if (isNull(c)) throw new IllegalArgumentException("comparator must not be null");
        return new HasSize(c);
    }

    @Override
    public boolean compareWith(final Integer value) {
        return i.map(v -> Objects.equals(value, v)).mapLeft(c -> c.compareWith(value)).fold(Function.identity(), Function.identity());
    }
}
