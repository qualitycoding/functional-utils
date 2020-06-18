package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static me.shaftesbury.utils.functional.Functional.dStringify;
import static me.shaftesbury.utils.functional.Functional.map;
import static me.shaftesbury.utils.functional.FunctionalTest.doublingGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

class Functional_Map_Test {
    @Test
    void mapTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<String> output = map(dStringify(), input);
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void curriedMapTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Iterable<Integer>, List<String>> mapFunc = map(dStringify());
        final Collection<String> output = mapFunc.apply(input);
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void toStringTest1() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Collection<String> ls = Functional.map(Functional.dStringify(), li);
        assertThat(ls).containsExactly("2", "4", "6", "8", "10");
    }

    @Test
    void seqMapTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
        final Iterable<String> output = Functional.seq.map(Functional.dStringify(), input);
        final Iterator<String> it = output.iterator();
        for (int i = 0; i < 20; ++i)
            assertThat(it.hasNext()).isTrue();

        for (int i = 0; i < expected.size(); ++i)
            assertThat(it.next()).isEqualTo(expected.get(i));

        assertThat(it.hasNext()).isFalse();
        try {
            it.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void curriedSeqMapTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
        final Iterable<String> output = Functional.seq.map(Functional.<Integer>dStringify()).apply(input);
        final Iterator<String> it = output.iterator();
        for (int i = 0; i < 20; ++i)
            assertThat(it.hasNext()).isTrue();

        for (int i = 0; i < expected.size(); ++i)
            assertThat(it.next()).isEqualTo(expected.get(i));

        assertThat(it.hasNext()).isFalse();
        try {
            it.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void cantRemoveFromSeqMapTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
        final Iterable<String> output = Functional.seq.map(Functional.dStringify(), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqMapTest1() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
        final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
        final Iterable<String> output = Functional.seq.map(Functional.dStringify(), input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

}