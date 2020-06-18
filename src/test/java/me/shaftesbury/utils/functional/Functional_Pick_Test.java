package me.shaftesbury.utils.functional;

import me.shaftesbury.utils.functional.monad.Option;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class Functional_Pick_Test {
    public Functional_Pick_Test() {
    }

    @Test
    void pickTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.pick((Function<Integer, Option<String>>) a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None(), li)).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void curriedPickTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Function<Iterable<Integer>, String> pickFunc = Functional.pick(a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None());
        assertThat(pickFunc.apply(li)).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void pickTest2() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.pick(a -> a == falseMatch ? Option.toOption(a.toString()) : Option.None(), li));
    }

    @Test
    void pickNoExceptionTest1() {
        final int trueMatch = 6;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.noException.pick(a -> a == trueMatch ? Option.toOption(a.toString()) : Option.None(), li).Some()).isEqualTo(((Integer) trueMatch).toString());
    }

    @Test
    void pickNoExceptionTest2() {
        final int falseMatch = 7;
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        assertThat(Functional.noException.pick(a -> a == falseMatch ? Option.toOption(a.toString()) : Option.None(), li).isNone()).isTrue();
    }
}