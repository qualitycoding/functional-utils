package me.shaftesbury.utils.functional;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.IntConsumer;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

public class AnyMTest {

    interface AnyM<T> {
        <U> AnyM<U> map(final Function<T, U> f);

        <U, M extends AnyM<U>> M flatMap(final Function<T, M> f);
    }

    @Test@Disabled("This test doesn't work yet - the types aren't matching")
    public void test() {
        final Map<String, String> map = HashMap.of("key", "value");
        final MyOption<String> vvv = $(map.get("vvv"));
//        final MyEither<Integer, String> strings = vvv.flatMap(s -> s.length() < 3 ? MyEither.right(s) : MyEither.left(s.length()));
    }

    private MyOption<String> $(final Option<String> a) {
        return MyOption.of(a);
    }

    private final Function<String, MyEither<Exception, Integer>> intValue = s -> {
        try {
            return MyEither.right(Integer.valueOf(s));
        } catch (final NumberFormatException e) {
            return MyEither.left(e);
        }
    };

    private final Function<Integer, OptionalInt> add20IfLessThan20 = i -> i == null || i > 20 ? OptionalInt.empty() : OptionalInt.of(i + 20);

    @Test void add20ToSmaller() {
        final OptionalIntT<Either<Exception, Integer>> result = intValue.apply("10").flatMapT(add20IfLessThan20::apply);

        assertThat(result.isPresent()).isTrue();
        assertThat(30).isEqualTo(result.getAsInt());
    }

    @Test void add20ToLarger() {
        final OptionalIntT<Either<Exception, Integer>> result = intValue.apply("100").flatMapT(add20IfLessThan20::apply);

        result.ifPresent(i -> fail("Expected empty but received " + i));
    }

    @Test void dontAdd() {
        final OptionalIntT<Either<Exception, Integer>> result = intValue.apply("string").flatMapT(add20IfLessThan20::apply);

        assertThat(result.isPresent()).isFalse();
        assertThat(result.liftM().isLeft()).isTrue();
        assertThat(result.liftM().swap().get() instanceof NumberFormatException).isTrue();
    }

    @Test void combineTwoOptionalLists() {
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

    @Test void tryIt() {
        final Try<Boolean> booleans = Try.of(() -> {
            System.out.println("First try");
//            throw new RuntimeException();
            return true;
        }).onFailure(h -> System.out.println("First failure")).
                flatMap(i -> Try.of(() -> {
                    throw new RuntimeException();
//                    return true;
                }).onFailure(j -> System.out.println("fn1 failure"))).
                flatMap(k -> Try.of(() -> true).onFailure(l -> System.out.println("second onFailure")));
    }

    @Test void currying() {
        final Function2<Integer, Integer, Integer> sum = Integer::sum;
        final Function1<Integer, Function1<Integer, Integer>> curriedSum = sum.curried();

        final Function3<String, Integer, Double, String> fn3 = (a, b, c) -> Integer.toString(Integer.parseInt(a) + b + c.intValue());
        final Function1<String, Function1<Integer, Function1<Double, String>>> curriedFn3 = fn3.curried();
    }
}

class MyOption<T> /*implements AnyM<T> */{
    private final T t;

    public MyOption(final T t) {
        this.t = requireNonNull(t, "t must not be null");
    }

    public MyOption() {
        t = null;
    }

    public static <T> MyOption<T> of(final Option<T> o) {
        return o.isDefined() ? new MyOption<>(o.get()) : MyOption.empty();
    }

    private static <T> MyOption<T> empty() {
        return new MyOption<>();
    }

//    @Override
    public <U> MyOption<U> map(final Function<T, U> f) {
        if (isNull(t)) return MyOption.empty();
        final U result = f.apply(t);
        return result == null ? MyOption.empty() : new MyOption<>(result);
    }

//    @Override
//    public <U, M extends AnyM<U>> M flatMap(final Function<T, M> f) {
//        if (isNull(t)) return M.empty();
//        return f.apply(t);
//    }
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

