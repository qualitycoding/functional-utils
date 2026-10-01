package uk.co.qualitycode.utils.functional;

import java.util.Collections;
import java.util.HashSet;

import static uk.co.qualitycode.utils.functional.Checks.notNull;
import static uk.co.qualitycode.utils.functional.Functional.*;

/**
 * Set operations on sequences.
 * <p>
 * Also reachable as {@code Functional.Set}, which is kept as an alias for existing code. The constructor is
 * package-private only so that alias can extend this class.
 *
 * Implementations of the algorithms contained herein which return sets
 * See <a href="http://en.wikipedia.org/wiki/Set_(computer_science)">Set</a>
 */
public class SetOperations {
    SetOperations() {
    }

    /**
     * Non-destructive wrapper for set intersection
     *
     * @param e1  input set
     * @param e2  input set
     * @param <E> the underlying base type of the two input sets
     * @return a set containing those elements which are contained within both sets 'e1' and 'e2'
     */
    public static <E> java.util.Set<E> intersection(final java.util.Set<? extends E> e1, final java.util.Set<? extends E> e2) {
        final java.util.Set<E> i = new HashSet<>(e1);
        i.retainAll(e2);
        return Collections.unmodifiableSet(i);
    }

    /**
     * Non-destructive wrapper for set difference
     *
     * @param inSet    input set
     * @param notInSet input set
     * @param <E>      the underlying base type of the two input sets
     * @return a set of those elements which are in 'inSet' and not in 'notInSet'
     */
    public static <E> java.util.Set<E> asymmetricDifference(final java.util.Set<? extends E> inSet, final java.util.Set<? extends E> notInSet) {
        notNull(inSet, "Set.asymmetricDifference(Set<A>,Set<A>): input1 must not be null");
        notNull(notInSet, "Set.asymmetricDifference(Set<A>,Set<A>): input2 must not be null");
        final java.util.Set<E> i = new HashSet<>(inSet);
        i.removeAll(notInSet);
        return Collections.unmodifiableSet(i);
    }
}
