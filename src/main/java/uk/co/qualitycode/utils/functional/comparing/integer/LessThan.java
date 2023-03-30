package uk.co.qualitycode.utils.functional.comparing.integer;

import uk.co.qualitycode.utils.functional.comparing.Comparator;

public class LessThan implements Comparator<Integer> {
    private final int i;

    public LessThan(final int i) {
        this.i = i;
    }

    @Override
    public boolean compareWith(final Integer value) {
        return value < i;
    }

    public static Comparator<Integer> lessThan(final int i) {
        return new LessThan(i);
    }
}
