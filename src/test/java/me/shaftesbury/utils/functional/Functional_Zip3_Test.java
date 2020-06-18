package me.shaftesbury.utils.functional;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;

public class Functional_Zip3_Test {
    public Functional_Zip3_Test() {
    }

    @Test
    void zip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void failingZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(input1, input2, input3));
    }

    @Test
    void iterableZip3Test1() {
        final Iterable<Integer> input1 = Functional.seq.map(Functional.identity(), Arrays.asList(1, 2, 3, 4, 5));
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void failingIterableZip3Test1() {
        final Iterable<Integer> input1 = Functional.seq.map(Functional.identity(), Arrays.asList(1, 2, 3, 4, 5));
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(input1, input2, input3));
    }

    @Test
    void zip3NoExceptionTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.noException.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zip3NoExceptionTest2() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5, 6);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.noException.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.toList(Functional.seq.zip3(input1, input2, input3));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.toList(Functional.seq.<Integer, Character, Double>zip3(input1, input2).apply(input3));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Iterable<Triple<Integer, Character, Double>> output = Functional.seq.zip3(input1, input2, input3);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqZip3Test1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Iterable<Triple<Integer, Character, Double>> output = Functional.seq.zip3(input1, input2, input3);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqZip3Test2() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Triple<Integer, Character, Double>> expected = new ArrayList<Triple<Integer, Character, Double>>();
        expected.add(Triple.of(1, 'a', 1.0));
        expected.add(Triple.of(2, 'b', 2.0));
        expected.add(Triple.of(3, 'c', 2.5));
        expected.add(Triple.of(4, 'd', 3.0));
        expected.add(Triple.of(5, 'e', 3.5));

        final Collection<Triple<Integer, Character, Double>> output = Functional.toList(Functional.seq.zip3(input1, input2, input3));
        final Iterator<Triple<Integer, Character, Double>> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        for (final Triple<Integer, Character, Double> element : expected) {
            final Triple<Integer, Character, Double> next = iterator.next();
            assertThat(next).isEqualTo(element);
        }

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }
}