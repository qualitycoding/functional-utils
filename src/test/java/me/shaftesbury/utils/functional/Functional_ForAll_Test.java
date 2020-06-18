package me.shaftesbury.utils.functional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class Functional_ForAll_Test {
    public Functional_ForAll_Test() {
    }

    @Test
    void forAll2Test1() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(FunctionalTest.quadruplingGenerator, 5);
        Assertions.assertThat(Functional.forAll2(FunctionalTest::bothAreEven, l, m)).isTrue();
    }

    @Test
    void forAll2NoExceptionTest1() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(FunctionalTest.quadruplingGenerator, 5);
        Assertions.assertThat(Functional.noException.forAll2(FunctionalTest::bothAreEven, l, m).Some()).isTrue();
    }

    @Test
    void forAll2Test2() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);

        Assertions.assertThat(Functional.forAll2(FunctionalTest.dBothAreLessThan10, l, m)).isFalse();
    }

    @Test
    void forAll2Test3() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(FunctionalTest.quadruplingGenerator, 7);

        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Functional.forAll2(FunctionalTest::bothAreEven, l, m));
    }

    @Test
    void forAll2NoExceptionTest2() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);

        Assertions.assertThat(Functional.noException.forAll2(FunctionalTest.dBothAreLessThan10, l, m).Some()).isFalse();
    }

    @Test
    void forAll2NoExceptionTest3() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Collection<Integer> m = Functional.init(FunctionalTest.quadruplingGenerator, 7);

        Assertions.assertThat(Functional.noException.forAll2(FunctionalTest::bothAreEven, l, m).isNone()).isTrue();
    }
}