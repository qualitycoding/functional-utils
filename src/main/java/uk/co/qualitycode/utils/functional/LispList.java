package uk.co.qualitycode.utils.functional;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A minimal immutable cons list with Lisp-style accessors.
 * <p>
 * Java does not eliminate tail calls, so every traversal here is written iteratively (via {@link #fold}) rather than
 * recursively: recursion depth would otherwise grow with list length and overflow the stack on long lists.
 */
public final class LispList {
    private LispList() {
    }

    public interface List<T> {
        T head();

        List<T> tail();

        boolean isEmpty();
    }

    public static <T> List<T> reverse(final List<T> input) {
        return fold((reversed, t) -> list(t, reversed), LispList.<T>nil(), input);
    }

    public static <T> List<T> filter(final Predicate<T> f, final List<T> input) {
        return reverse(fold((kept, t) -> f.test(t) ? list(t, kept) : kept, LispList.<T>nil(), input));
    }

    public static <T, R> List<R> map(final Function<T, R> f, final List<T> input) {
        return reverse(fold((mapped, t) -> list(f.apply(t), mapped), LispList.<R>nil(), input));
    }

    public static <T, R> R fold(final BiFunction<R, T, R> f, final R initialValue, final List<T> input) {
        R accumulator = initialValue;
        for (List<T> remaining = input; !remaining.isEmpty(); remaining = remaining.tail())
            accumulator = f.apply(accumulator, remaining.head());
        return accumulator;
    }

    public static <T, R> R foldRight(final BiFunction<T, R, R> f, final R initialValue, final List<T> input) {
        return fold((accumulator, t) -> f.apply(t, accumulator), initialValue, reverse(input));
    }

    public static <T> List<T> cons(final T t, final List<T> l) {
        return list(t, l);
    }

    public static <T> T car(final List<T> l) {
        return l.head();
    }

    public static <T> List<T> cdr(final List<T> l) {
        return l.tail();
    }

    public static <T> T cadr(final List<T> l) {
        return car(cdr(l));
    }

    public static <T> List<T> compose(final T t1, final T t2) {
        return list(t1, list(t2, nil()));
    }

    public static final class EmptyListHasNoHead extends RuntimeException {
    }

    public static final class EmptyListHasNoTail extends RuntimeException {
    }

    public static final class NonEmptyList<T> implements List<T> {
        private final T head;
        private final List<T> tail;

        NonEmptyList(final T head, final List<T> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public T head() {
            return head;
        }

        @Override
        public List<T> tail() {
            return tail;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof NonEmptyList<?>)) return false;
            List<?> mine = this;
            List<?> theirs = (List<?>) o;
            while (!mine.isEmpty() && !theirs.isEmpty()) {
                if (!Objects.equals(mine.head(), theirs.head())) return false;
                mine = mine.tail();
                theirs = theirs.tail();
            }
            return mine.isEmpty() && theirs.isEmpty();
        }

        @Override
        public int hashCode() {
            return fold((hash, t) -> 31 * hash + Objects.hashCode(t), 1, this);
        }

        @Override
        public String toString() {
            final StringBuilder text = new StringBuilder();
            int depth = 0;
            for (List<?> remaining = this; !remaining.isEmpty(); remaining = remaining.tail(), depth++)
                text.append("( ").append(remaining.head()).append(", ");
            text.append("( )");
            for (int i = 0; i < depth; i++) text.append(" )");
            return text.toString();
        }
    }

    public static <T> List<T> nil() {
        return new List<T>() {
            @Override
            public T head() {
                throw new EmptyListHasNoHead();
            }

            @Override
            public List<T> tail() {
                throw new EmptyListHasNoTail();
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public boolean equals(final Object o) {
                return o instanceof List<?> && ((List<?>) o).isEmpty();
            }

            @Override
            public int hashCode() {
                return 1;
            }

            @Override
            public String toString() {
                return "( )";
            }
        };
    }

    public static <T> List<T> list(final T head, final List<T> tail) {
        return new NonEmptyList<>(head, tail);
    }
}
