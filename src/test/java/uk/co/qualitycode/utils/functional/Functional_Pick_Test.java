package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class Functional_Pick_Test {
    @Test
    void pickTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.pick((Function<Integer, Option<String>>) a -> a == trueMatch ? Option.of(a.toString()) : Option.none(), li)).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void curriedPickTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Function<Iterable<Integer>, String> pickFunc = Functional.pick(a -> a == trueMatch ? Option.of(a.toString()) : Option.none());
        assertThat(pickFunc.apply(li)).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void pickTest2() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.pick(a -> a == falseMatch ? Option.of(a.toString()) : Option.none(), li));
    }

    @Test
    void pickNoExceptionTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.noException.pick(a -> a == trueMatch ? Option.of(a.toString()) : Option.none(), li).get()).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void pickNoExceptionTest2() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.noException.pick(a -> a == falseMatch ? Option.of(a.toString()) : Option.none(), li).isNone()).isTrue();
    }
}
