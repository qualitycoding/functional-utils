package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.qualitycode.utils.functional.Functional.join;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Fold_Test {

    public static final BiFunction<String, Integer, String> csv = (state, a) -> state + "," + a;

    @Test
    void foldIntegers() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = "0,2,4,6,8,10";

        final String s2 = Functional.fold(csv, "0", li);

        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void curriedFoldIntegers() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = "0,2,4,6,8,10";

        final String s2 = Functional.fold(csv, "0").apply(li);

        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void curriedFoldIntegers2() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = "0,2,4,6,8,10";

        final String s2 = Functional.fold(csv).withInitialValue("0").apply(li);

        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void foldvsMapTest1() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = join(",", Functional.map(Functional.dStringify(), li));
        assertThat(s1).isEqualTo("2,4,6,8,10");
        final String s2 = Functional.fold(FunctionalTest::csv, "", li);
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void countTest() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final int howMany = Functional.fold(Functional.count, 0, input);
        assertThat(howMany).isEqualTo(input.size());
    }

    @Test
    void sumTest() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final int sum = Functional.fold(Functional.sum, 0, input);
        assertThat(sum).isEqualTo(15);
    }
}
