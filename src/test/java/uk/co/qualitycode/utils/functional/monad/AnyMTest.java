package uk.co.qualitycode.utils.functional.monad;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.IntConsumer;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class AnyMTest {

    private final Function<java.lang.String, MyEither<Exception, Integer>> intValue = s -> {
        try {
            return MyEither.right(Integer.valueOf(s));
        } catch (final NumberFormatException e) {
            return MyEither.left(e);
        }
    };

    private final Function<Integer, OptionalInt> add20IfLessThan20 = i -> i == null || i > 20 ? OptionalInt.empty() : OptionalInt.of(i + 20);

    @Test
    void add20ToSmaller() {
        final OptionalIntT<Either<Exception, Integer>> result = intValue.apply("10").flatMapT(add20IfLessThan20::apply);

        assertThat(result.toOptionalInt()).hasValue(30);
    }

    @Test
    void add20ToLarger() {
        final OptionalIntT<Either<Exception, Integer>> result = intValue.apply("100").flatMapT(add20IfLessThan20::apply);

        assertThat(result.toOptionalInt()).isEmpty();
    }

    @Test
    void dontAdd() {
        final OptionalIntT<Either<Exception, Integer>> result = intValue.apply("string").flatMapT(add20IfLessThan20::apply);

        assertThat(result.toOptionalInt()).isEmpty();
        assertThat(result.liftM().getLeft()).isInstanceOf(NumberFormatException.class);
    }

    @Test
    void combineTwoOptionalLists() {
        final io.vavr.collection.List<Integer> is = getIntegers3(true, true);

        final io.vavr.collection.List<Integer> expected = io.vavr.collection.List.of(1, 2);

        assertThat(is).isEqualTo(expected);
    }

    private io.vavr.collection.List<Integer> getIntegers3(final boolean first, final boolean second) {
        return Optional.of(first).map(x -> io.vavr.collection.List.of(1)).orElse(io.vavr.collection.List.empty()).appendAll(
                Optional.of(second).map(x -> io.vavr.collection.List.of(2)).orElse(io.vavr.collection.List.empty()));
    }

    private List<Integer> getIntegers1(final boolean first, final boolean second) {
        final List<Integer> is = new ArrayList<>();
        Optional.of(first).ifPresent(x -> is.addAll(Collections.singletonList(1)));
        Optional.of(second).ifPresent(x -> is.addAll(Collections.singletonList(2)));
        return is;
    }

}

class OptionalIntT<M /*extends Monad*/> {
    private final OptionalInt optionalInt;
    private final M underlyingMonad;

    OptionalIntT(final OptionalInt op, final M underlyingMonad) {
        this.optionalInt = requireNonNull(op, "op must not be null");
        this.underlyingMonad = requireNonNull(underlyingMonad, "underlyingMonad must not be null");
    }

    boolean isPresent() {
        return optionalInt.isPresent();
    }

    OptionalInt toOptionalInt() {
        return optionalInt;
    }

    int getAsInt() {
        return optionalInt.getAsInt();
    }

    void ifPresent(final IntConsumer consumer) {
        optionalInt.ifPresent(consumer);
    }

    M liftM() {
        return underlyingMonad;
    }
}

class MyEither<L, R> /*implements AnyM<R>*/ {
    private final Either<L, R> either;

    private MyEither(final Either<L, R> either) {
        this.either = either;
    }

    static <L, R> MyEither<L, R> left(final L l) {
        return new MyEither<>(Either.left(l));
    }

    static <L, R> MyEither<L, R> right(final R r) {
        return new MyEither<>(Either.right(r));
    }

    OptionalIntT<Either<L, R>> flatMapT(final Function<? super R, OptionalInt> f) {
        if (either.isLeft()) {
            return new OptionalIntT<>(OptionalInt.empty(), either);
        } else {
            final OptionalInt result = f.apply(either.get());
            return new OptionalIntT<>(result, either);
        }
    }

//    @Override
//    public <U> AnyM<U> map(final Function<R, U> f) {
//        if (either.isRight())
//            return right(f.apply(either.get()));
//        return left(either.swap().get());
//    }

//    @Override
//    public <U, M extends AnyM<U>> M flatMap(final Function<R, M> f) {
//        if (either.isRight())
//            return f.apply(either.get());
//        return M.empty(left(either.swap().get()));
//    }
}

