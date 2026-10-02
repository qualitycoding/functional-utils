package uk.co.qualitycode.utils.functional;

import static java.util.Objects.requireNonNull;

/**
 * Argument checks shared by the functional operations. A null argument is reported as a NullPointerException
 * whose message names the operation and the parameter.
 */
final class Checks {
    private Checks() {
    }

    static <T> T notNull(final T t, final String functionName, final String parameterName) {
        return notNull(t, functionName + ": " + parameterName + " must not be null");
    }

    static <T> T notNull(final T t, final String fullMessage) {
        return requireNonNull(t, fullMessage);
    }
}
