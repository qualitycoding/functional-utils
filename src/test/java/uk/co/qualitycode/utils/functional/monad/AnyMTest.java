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

/**
 * Spike: monads over a common base type ({@link AnyM}) and a first monad transformer ({@link OptionalIntT}).
 */
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
    void anOptionIsBoundToAnEitherByConvertingItFirst() {
        // flatMap cannot cross from Option to MyEither (the witnesses differ, so it does not compile);
        // convert with a natural transformation, then bind within MyEither.
        final Function<java.lang.String, MyEither<java.lang.String, java.lang.String>> shortOnly =
                s -> s.length() < 3 ? MyEither.right(s) : MyEither.left("too long: " + s);

        assertThat(MyEither.fromOption(Option.<java.lang.String>none(), "missing").flatMap(shortOnly))
                .isEqualTo(MyEither.left("missing"));
        assertThat(MyEither.fromOption(Option.of("ok"), "missing").flatMap(shortOnly))
                .isEqualTo(MyEither.right("ok"));
        assertThat(MyEither.fromOption(Option.of("long"), "missing").flatMap(shortOnly))
                .isEqualTo(MyEither.left("too long: long"));
    }

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

/**
 * The spike's Either, completed as an {@link AnyM}. The left type is fixed by the witness, which is how a type with
 * two parameters becomes a monad in its right-hand parameter.
 */
final class MyEither<L, R> implements AnyM<MyEither.Witness<L>, R> {
    static final class Witness<L> {
        private Witness() {
        }
    }

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

    /**
     * A natural transformation from Option: the way to move between monads, since flatMap cannot.
     */
    static <L, R> MyEither<L, R> fromOption(final Option<R> option, final L ifNone) {
        return option.map(MyEither::<L, R>right).getOrElse(() -> left(ifNone));
    }

    static <L, R> MyEither<L, R> narrow(final AnyM<Witness<L>, R> m) {
        return (MyEither<L, R>) m;
    }

    @Override
    public <U> MyEither<L, U> unit(final U value) {
        return right(value);
    }

    @Override
    public <U> MyEither<L, U> flatMap(final Function<? super R, ? extends AnyM<Witness<L>, U>> f) {
        return either.fold(MyEither::left, r -> narrow(f.apply(r)));
    }

    @Override
    public <U> MyEither<L, U> map(final Function<? super R, ? extends U> f) {
        return narrow(AnyM.super.map(f));
    }

    OptionalIntT<Either<L, R>> flatMapT(final Function<? super R, OptionalInt> f) {
        return new OptionalIntT<>(either.fold(l -> OptionalInt.empty(), f::apply), either);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof MyEither && either.equals(((MyEither<?, ?>) o).either);
    }

    @Override
    public int hashCode() {
        return either.hashCode();
    }

    @Override
    public java.lang.String toString() {
        return "My" + either;
    }
}
