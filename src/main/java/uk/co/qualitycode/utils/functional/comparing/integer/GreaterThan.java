package uk.co.qualitycode.utils.functional.comparing.integer;

import uk.co.qualitycode.utils.functional.comparing.Comparator;

public class GreaterThan implements Comparator<Integer> {
    private final int i;

    public GreaterThan(final int i) {
        this.i = i;
    }

    @Override
    public boolean compareWith(final Integer value) {
        return value > i;
    }

    public static Comparator<Integer> greaterThan(final int i) {
        return new GreaterThan(i);
    }
}
