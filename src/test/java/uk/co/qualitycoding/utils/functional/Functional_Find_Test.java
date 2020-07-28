package uk.co.qualitycoding.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class Functional_Find_Test {
    public Functional_Find_Test() {
    }

    @Test
    void findTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.find(s -> s.equals(trueMatch), ls)).isEqualTo(trueMatch);
    }

    @Test
    void curriedFindTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        final Function<Iterable<String>, String> findFunc = Functional.find(s -> s.equals(trueMatch));
        assertThat(findFunc.apply(ls)).isEqualTo(trueMatch);
    }

    @Test
    void findTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> Functional.find(s -> s.equals(falseMatch), ls));
    }

    @Test
    void findNoExceptionTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.find(s -> s.equals(trueMatch), ls).Some()).isEqualTo(trueMatch);
    }

    @Test
    void findNoExceptionTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.find(s -> s.equals(falseMatch), ls).isNone()).isTrue();
    }
}
