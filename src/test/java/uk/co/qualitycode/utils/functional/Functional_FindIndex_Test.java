package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class Functional_FindIndex_Test {
    @Test
    void findIndexTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.findIndex(s -> s.equals(trueMatch), ls)).isEqualTo(2);
    }

    @Test
    void findIndexTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.findIndex(s -> s.equals(falseMatch), ls));
    }

    @Test
    void findIndexNoExceptionTest1() {
        final String trueMatch = "6";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.findIndex(s -> s.equals(trueMatch), ls).get()).isEqualTo((Integer) 2);
    }

    @Test
    void findIndexNoExceptionTest2() {
        final String falseMatch = "7";
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(Functional.noException.findIndex(s -> s.equals(falseMatch), ls).isNone()).isTrue();
    }
}
