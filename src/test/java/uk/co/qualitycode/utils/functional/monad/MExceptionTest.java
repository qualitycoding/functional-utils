package uk.co.qualitycode.utils.functional.monad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.qualitycode.utils.functional.Functional;
import uk.co.qualitycode.utils.functional.Iterable2;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MExceptionTest {
    @Test
    void returnTest1() {
        final MException<Object> mex = MException.toMException(
                () -> null
        );

        assertThat(mex.hasException()).isFalse();
        assertThat(mex.read()).isNull();
    }

    @Test
    void readMExceptionInErrorTest() {
        final MException<Object> m = MException.toMException(() -> {
            throw new RuntimeException();
        });

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(m::read);
    }

    @Test
    void returnTest2() {
        final MException<Integer> mex = MException.toMException(() -> 10);

        assertThat(mex.hasException()).isFalse();
        assertThat(mex.read()).isEqualTo(Integer.valueOf(10));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 9})
    void returnWithFuncTest1(final int value) {
        final MException<Integer> mex = MException.toMException(() -> value);

        assertThat(mex.hasException()).isFalse();
        assertThat(mex.read()).isEqualTo(value);
    }

    private static final Function<Integer, Integer> doublingGenerator = a -> 2 * a;

    @Test
    void returnWithFuncTest2() {
        final java.util.List<MException<Integer>> l = Iterable2.init(doublingGenerator, 10).map(
                ii -> MException.toMException(() -> {
                    if (ii == 8 || ii == 10 || ii == 16) throw new IllegalArgumentException("value");
                    return ii;
                })).toList();

        assertThat(Functional.map(MException::hasException, l))
                .containsExactly(false, false, false, true, true, false, false, true, false, false);
    }

    @Test
    void bindTest1() {
        // i / j for i in 1..3 and j in -i..i; dividing by zero fails once for each i.
        final java.util.List<MException<Integer>> l = IntStream.rangeClosed(1, 3).boxed()
                .flatMap(i -> IntStream.rangeClosed(-i, i).mapToObj(
                        j -> MException.toMException(() -> i).bind(integer -> MException.toMException(() -> integer / j))))
                .collect(Collectors.toList());

        assertThat(l).filteredOn(MException::hasException).hasSize(3);
        assertThat(l).filteredOn(m -> !m.hasException()).extracting(MException::read)
                .containsExactly(-1, 1, -1, -2, 2, 1, -1, -1, -3, 3, 1, 1);
    }

    @Test
    void bindTestExceptionThrownInFunc() {
        final MException<Integer> m = MException.toMException(() -> 1);

        final MException<Integer> m1 = m.bind(integer -> {
            throw new RuntimeException("Argh");
        });

        assertThat(m1.hasException()).isTrue();
        assertThat(m1.getExceptionWithStackTrace().get()._1()).hasMessage("Argh");
    }

    @Test
    void bindTestThisMExceptionHasExceptionAlready() {
        final MException<Integer> m = MException.toMException(
                () -> {
                    throw new RuntimeException("Argh");
                }
        );

        final MException<Integer> m1 = m.bind(integer -> MException.toMException(() -> integer));

        assertThat(m1.hasException()).isTrue();
        assertThat(m.hasException()).isTrue();
        assertThat(m1.getExceptionWithStackTrace().get()._1()).hasMessage("Argh");
    }

    @Test
    void liftTest1() {
        final int value = 10;
        final MException<Integer> a = MException.toMException(() -> 10 / value);
        final MException<Integer> b = MException.toMException(() -> 20 / value);
        final MException<Integer> c = MException.lift(Integer::sum, a, b);
        assertThat(c.hasException()).isFalse();
        assertThat(c.read()).isEqualTo(Integer.valueOf(3));
    }

    @Test
    void evaluatesTheSupplierAtMostOnce() {
        final AtomicInteger evaluations = new AtomicInteger();
        final MException<Integer> m = MException.toMException(evaluations::incrementAndGet);

        m.hasException();
        m.read();
        m.read();

        assertThat(evaluations).hasValue(1);
    }

    @Test
    void remembersANullResultRatherThanReEvaluating() {
        final AtomicInteger evaluations = new AtomicInteger();
        final MException<Object> m = MException.toMException(() -> {
            evaluations.incrementAndGet();
            return null;
        });

        m.read();
        m.read();

        assertThat(evaluations).hasValue(1);
    }

    @Test
    void evaluatesOnceWhenReadConcurrently() {
        final AtomicInteger evaluations = new AtomicInteger();
        final MException<Integer> m = MException.toMException(evaluations::incrementAndGet);

        IntStream.range(0, 1_000).parallel().forEach(i -> m.read());

        assertThat(evaluations).hasValue(1);
    }

    @Test
    void hasNoExceptionWhenTheComputationSucceeds() {
        final MException<Integer> m = MException.toMException(() -> 1);

        assertThat(m.getException()).isEqualTo(Option.none());
        assertThat(m.getExceptionWithStackTrace()).isEqualTo(Option.none());
    }

    @Test
    void exposesTheExceptionWhenTheComputationFails() {
        final IllegalStateException failure = new IllegalStateException("broken");
        final MException<Integer> m = MException.toMException(() -> {
            throw failure;
        });

        assertThat(m.getException()).isEqualTo(Option.of(failure));
    }

    @Test
    void returnsACopyOfTheStackTrace() {
        final MException<Integer> m = MException.toMException(() -> {
            throw new IllegalStateException();
        });
        final StackTraceElement[] first = m.getExceptionWithStackTrace().get()._2();
        final StackTraceElement original = first[0];

        first[0] = null;

        assertThat(m.getExceptionWithStackTrace().get()._2()[0]).isSameAs(original);
    }
}
