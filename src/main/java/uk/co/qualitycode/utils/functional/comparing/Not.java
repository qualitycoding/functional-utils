package uk.co.qualitycode.utils.functional.comparing;

public final class Not {
    private Not() {
    }

    public static <T> Comparator<T> not(final Comparator<T> check) {
        return value -> !(check.compareWith(value));
    }
}
