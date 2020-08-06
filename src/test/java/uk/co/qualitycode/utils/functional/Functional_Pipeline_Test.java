package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Pipeline_Test {
    private FunctionalTest functionalTest;

    @BeforeEach
    public void setup() {
        functionalTest = new FunctionalTest();
    }

    @Test
    void fwdPipelineTest1() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = Functional.in(li, functionalTest.concatenate);
        assertThat(s1).isEqualTo("2,4,6,8,10");
    }

    @Test
    void fwdPipelineTest2() {
        final Collection<Integer> li = Functional.init(FunctionalTest.triplingGenerator, 5);
        final Collection<Integer> evens = Functional.in(li, functionalTest.evens_f);
        final String s1 = Functional.in(evens, functionalTest.concatenate);
        final String s2 = Functional.in(li, Functional.then(functionalTest.evens_f, functionalTest.concatenate));
        assertThat(s1).isEqualTo("6,12");
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void fwdPipelineTest3() {
        final Collection<Integer> input = Functional.init(doublingGenerator, 5);
        final Collection<String> output = Functional.in(input, (Function<Collection<Integer>, Collection<String>>) integers -> Functional.map(Functional.<Integer>dStringify(), integers));

        final Collection<String> expected = Arrays.asList("2", "4", "6", "8", "10");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void fwdPipelineTest4() {
        final Iterable<Integer> input = Functional.seq.init(doublingGenerator, 5);
        final Iterable<String> output = Functional.in(input,
                integers -> {
                    try {
                        return Functional.seq.map(Functional.dStringify(), integers);
                    } catch (final Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        return null; // Argh!!!
                    }
                });

        final Collection<String> expected = Arrays.asList("2", "4", "6", "8", "10");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void fwdPipelineTest5() {
        final Iterable<Integer> l = Functional.seq.init(doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.in(l, (Function<Iterable<Integer>, Iterable<Integer>>) ints -> Functional.filter(Functional.isOdd, ints));

        assertThat(oddElems.iterator().hasNext()).isFalse();
    }
}
