package uk.co.qualitycode.utils.functional.monad;

import java.util.Objects;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * https://typelevel.org/cats/datatypes/ior.html
 * <p>
 * IOr represents an inclusive-or relationship between two data types. This makes it very similar to the Either data type,
 * which represents an exclusive-or relationship. What this means is that an IOr&lt;A,B&gt; (also written as A IOr B) can
 * contain either an A, a B, or both an A and a B. Another similarity to Either is that IOr is right-biased, which means that
 * the map and flatMap functions will work on the right side of the IOr, which in our case is the B value.
 * </p>
 *
 * @param <L>
 * @param <R>
 */
public class IOr<L, R> implements Comparable<IOr<L, R>> {
    private final io.vavr.control.Option<L> left;
    private final io.vavr.control.Option<R> right;

    private IOr(final io.vavr.control.Option<L> left, final io.vavr.control.Option<R> right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> IOr<L, R> right(final R right) {
        requireNonNull(right, "right must not be null");
        return both_(null, right);
    }

    public static <L, R> IOr<L, R> left(final L left) {
        requireNonNull(left, "left must not be null");
        return both_(left, null);
    }

    public static <L, R> IOr<L, R> both(final L left, final R right) {
        if (isNull(left) && isNull(right)) throw new NullPointerException("left and right must not both be null");
        requireNonNull(left, "left must not be null");
        requireNonNull(right, "right must not be null");
        return both_(left, right);
    }

    public static <L, R> IOr<L, R> both_(final L left, final R right) {
        return new IOr<>(io.vavr.control.Option.of(left), io.vavr.control.Option.of(right));
    }

    public R get() {
        return right.get();
    }

    public L _1() {
        return left.get();
    }

    public <LL, RR> IOr<LL, RR> mapBoth(final Function<L, LL> leftTfm, final Function<R, RR> rightTfm) {
        requireNonNull(leftTfm, "leftTfm must not be null");
        requireNonNull(rightTfm, "rightTfm must not be null");

        return left
                .map(leftTfm)
                .map(l2 -> right
                        .map(rightTfm)
                        .map(r2 -> IOr.both(l2, r2))
                        .getOrElse(() -> IOr.<LL, RR>left(l2)))
                .getOrElse(() -> right
                        .map(rightTfm)
                        .map(IOr::<LL, RR>right)
                        .getOrElseThrow(() -> new RuntimeException("This should never happen")));
    }

    public <RR> IOr<L, RR> map(final Function<R, RR> tfm) {
        requireNonNull(tfm, "tfm must not be null");

        return right
                .map(tfm)
                .map(rr -> left
                        .map(l -> IOr.both(l, rr))
                        .getOrElse(() -> IOr.right(rr)))
                .getOrElse(() -> getOrElseThrowNPE(left.map(IOr::left)));
    }

    public <LL> IOr<LL, R> mapLeft(final Function<L, LL> tfm) {
        requireNonNull(tfm, "tfm must not be null");

        return left
                .map(tfm)
                .map(ll -> right
                        .map(r -> IOr.both(ll, r))
                        .getOrElse(() -> IOr.left(ll)))
                .getOrElse(() -> getOrElseThrowNPE(right.map(IOr::right)));
    }

    public IOr<R, L> swap() {
        return left
                .map(l -> right
                        .map(r -> IOr.both(r, l))
                        .getOrElse(() -> IOr.right(l)))
                .getOrElse(() -> getOrElseThrowNPE(right.map(IOr::left)));
    }

    @Override
    public int compareTo(final IOr<L, R> that) {
        requireNonNull(that, "that must not be null");

        return IOr.compareTo(this, that);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IOr<?, ?> iOr = (IOr<?, ?>) o;
        return Objects.equals(left, iOr.left) && Objects.equals(right, iOr.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    private <Y> Y getOrElseThrowNPE(final io.vavr.control.Option<Y> y) {
        return y.getOrElseThrow(() -> new NullPointerException("tfm must not produce null values"));
    }

    public boolean hasValue() {
        return right.isDefined();
    }

    public boolean hasLeft() {
        return left.isDefined();
    }

    public boolean hasBoth() {
        return hasValue() && hasLeft();
    }

    @SuppressWarnings("unchecked")
    private static <U1 extends Comparable<? super U1>, U2 extends Comparable<? super U2>> int compareTo(final IOr<?, ?> o1, final IOr<?, ?> o2) {
//        final IOr<U1, U2> t1 = (IOr<U1, U2>) o1;
//        final IOr<U1, U2> t2 = (IOr<U1, U2>) o2;
//
//        final int check1 = t1.left.map(l1 -> l1.compareTo(t2.left.getOrElseThrow(() -> new RuntimeException()))).getOrElseThrow(() -> new RuntimeException());
//        if (check1 != 0) return check1;
//
//        final int check2 = t1.right.map(r1 -> r1.compareTo(t2.right.getOrElseThrow(() -> new RuntimeException()))).getOrElseThrow(() -> new RuntimeException());
//        if (check2 != 0) return check2;

        // all components are equal
        return 0;
    }

}
