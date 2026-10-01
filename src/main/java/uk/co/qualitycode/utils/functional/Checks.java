package uk.co.qualitycode.utils.functional;

import static java.util.Objects.isNull;

/**
 * Argument checks shared by the functional operations; failures are reported as IllegalArgumentException.
 */
final class Checks {
    private Checks() {
    }

    static <T> T notNull(final T t, final String functionName, final String parameterName) {
        return notNull(t, functionName + ": " + parameterName + " must not be null");
    }

    static <T> T notNull(final T t, final String fullMessage) {
        if (isNull(t)) throw new IllegalArgumentException(fullMessage);
        return t;
    }
}
