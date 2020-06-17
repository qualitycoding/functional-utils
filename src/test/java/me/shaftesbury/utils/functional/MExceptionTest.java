package me.shaftesbury.utils.functional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MExceptionTest {
    @Test void returnTest1() {
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

    @Test void returnTest2() {
        final MException<Integer> mex = MException.toMException(
                () -> 10
        );

        assertThat(mex.hasException()).isFalse();
        assertThat(mex.read()).isEqualTo(Integer.valueOf(10));
    }

    @Test void returnWithFuncTest1() {
        for (int i = 0; i < 10; ++i) {
            final int ii = i;
            final Supplier<Integer> f = () -> ii;
            final MException<Integer> mex = MException.toMException(f);
            assertThat(mex.hasException()).isFalse();
            assertThat(mex.read()).isEqualTo(ii);
        }
    }

    private static Function<Integer, Integer> DoublingGenerator =
            a -> 2 * a;

    @Test void returnWithFuncTest2() {
        final Iterable2<Integer> it = IterableHelper.init(DoublingGenerator, 10);
        final java.util.List<MException<Integer>> l = it.map(
                ii -> {
                    final Supplier<Integer> f = () -> {
                        if (ii == 8 || ii == 10 || ii == 16) throw new IllegalArgumentException("value");
                        return ii;
                    };
                    return MException.toMException(f);
                }).toList();
        assertThat(l.size()).isEqualTo(10);
        for (int i = 1; i <= 10; ++i)
            if (i != 4 && i != 5 && i != 8) assertThat(l.get(i - 1).hasException()).isFalse();
            else assertThat(l.get(i - 1).hasException()).isTrue();
    }

    @Test void bindTest1() {
        final java.util.List<MException<Integer>> l = new ArrayList<>();
        for (int i = 1; i < 4; ++i) {
            final int ii = i;
            final MException<Integer> m = MException.toMException(() -> ii);

            for (int j = -i; j <= i; ++j) {
                final Integer jj = j;
                final MException<Integer> m1 = m.bind(integer -> MException.toMException(() -> integer / jj));
                l.add(m1);
            }
        }

        final java.util.List<MException<Integer>> l1 = Functional.filter(
                m -> !m.hasException(), l);

        assertThat(Functional.forAll(MException::hasException, l1)).isFalse();

        assertThat(IterableHelper.create(l).filter(MException::hasException).toList().size()).isEqualTo(3);
    }

    @Test void bindTestExceptionThrownInFunc() {
        final MException<Integer> m = MException.toMException(() -> 1);

        final MException<Integer> m1 = m.bind(integer -> {
            throw new RuntimeException("Argh");
        });

        assertThat(m1.hasException()).isTrue();
        final Pair<RuntimeException, StackTraceElement[]> exceptionWithStackTrace = m1.getExceptionWithStackTrace();
        assertThat(exceptionWithStackTrace.getLeft().getMessage()).isEqualTo("Argh");
    }

    @Test void bindTestThisMExceptionHasExceptionAlready() {
        final MException<Integer> m = MException.toMException(
                () -> {
                    throw new RuntimeException("Argh");
                }
        );

        final MException<Integer> m1 = m.bind(integer -> MException.toMException(() -> integer));

        assertThat(m1.hasException()).isTrue();
        assertThat(m.hasException()).isTrue();
        final Pair<RuntimeException, StackTraceElement[]> exceptionWithStackTrace = m1.getExceptionWithStackTrace();
        assertThat(exceptionWithStackTrace.getLeft().getMessage()).isEqualTo("Argh");
    }

    @Test void liftTest1() {
        final int value = 10;
        final MException<Integer> a = MException.toMException(() -> 10 / value);
        final MException<Integer> b = MException.toMException(() -> 20 / value);
        final MException<Integer> c = MException.lift(Integer::sum, a, b);
        assertThat(c.hasException()).isFalse();
        assertThat(c.read()).isEqualTo(Integer.valueOf(3));
    }
}
