package me.shaftesbury.utils.functional;

import me.shaftesbury.utils.functional.monad.Option;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
public class Functional_FindLast_Test {
    public Functional_FindLast_Test() {
    }

    @Test
    void findLastTest1() {
        final List<Integer> l = new ArrayList<Integer>(Functional.init(FunctionalTest.doublingGenerator, 5));
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.findLast(Functional.isOdd, l));
    }

    @Test
    void findLastTest2() {
        final List<Integer> l = new ArrayList<Integer>(Functional.init(FunctionalTest.doublingGenerator, 5));
        assertThat(Functional.findLast(Functional.isEven, l)).isEqualTo((Integer) 10);
    }

    @Test
    void curriedFindLastTest2() {
        final List<Integer> l = new ArrayList<Integer>(Functional.init(FunctionalTest.doublingGenerator, 5));
        final Function<List<Integer>, Integer> lastFunc = Functional.findLast(Functional.isEven);
        assertThat(lastFunc.apply(l)).isEqualTo((Integer) 10);
    }

    @Test
    void findLastNoExceptionTest1() {
        final List<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.noException.findLast(Functional.isOdd, l).isNone()).isTrue();
    }

    @Test
    void findLastNoExceptionTest2() {
        final List<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.noException.findLast(Functional.isEven, l).Some()).isEqualTo((Integer) 10);
    }

    @Test
    void iterableFindLastNoExceptionTest1() {
        final Iterable<Integer> l = Functional.seq.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.noException.findLast(Functional.isOdd, l).isNone()).isTrue();
    }

    @Test
    void iterableFindLastNoExceptionTest2() {
        final Iterable<Integer> l = Functional.seq.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.noException.findLast(Functional.isEven, l).Some()).isEqualTo((Integer) 10);
    }

    @Test
    void lastNoExceptionTest1() {
        final List<Integer> l = new ArrayList<Integer>(Functional.init(FunctionalTest.doublingGenerator, 5));
        final Option<Integer> last = Functional.noException.last(l);
        assertThat(last.isSome()).isTrue();
        assertThat(last.Some()).isEqualTo((Integer) 10);
    }

    @Test
    void findLastIterableTest1() {
        final Iterable<Integer> l = new ArrayList<Integer>(Functional.init(FunctionalTest.doublingGenerator, 5));
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.findLast(Functional.isOdd, l));
    }

    @Test
    void findLastIterableTest2() {
        final Iterable<Integer> l = new ArrayList<Integer>(Functional.init(FunctionalTest.doublingGenerator, 5));
        assertThat(Functional.findLast(Functional.isEven, l)).isEqualTo((Integer) 10);
    }
}