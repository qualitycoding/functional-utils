package me.shaftesbury.utils.functional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;
public class Functional_Mapi_Test {
    public Functional_Mapi_Test() {
    }

    @Test
    void mapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        assertThat(Functional.map(Pair::getRight, output)).containsExactly("1", "2", "3", "4", "5");
        assertThat(Functional.map(Pair::getLeft, output)).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void curriedMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Iterable<Integer>, List<Pair<Integer, String>>> mapiFunc = Functional.mapi((pos, i) -> Pair.of(pos, i.toString()));
        final Collection<Pair<Integer, String>> output = mapiFunc.apply(input);
        assertThat(Functional.map(Pair::getRight, output)).containsExactly("1", "2", "3", "4", "5");
        assertThat(Functional.map(Pair::getLeft, output)).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void seqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.toList(Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input));
        assertThat(Functional.map(Pair::getRight, output)).containsExactly("1", "2", "3", "4", "5");
        assertThat(Functional.map(Pair::getLeft, output)).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void cantRestartIteratorInSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> output = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void curriedSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.toList(Functional.seq.mapi((BiFunction<Integer, Integer, Pair<Integer, String>>) (pos, i) -> Pair.of(pos, i.toString())).apply(input));
        assertThat(Functional.map(Pair::getRight, output)).containsExactly("1", "2", "3", "4", "5");
        assertThat(Functional.map(Pair::getLeft, output)).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void cantRemoveFromSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> output = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void seqMapiTest2() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> mapi = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        final Iterator<Pair<Integer, String>> iterator = mapi.iterator();

        for (int i = 0; i < 10; ++i)
            assertThat(iterator.hasNext()).isTrue();

        Pair<Integer, String> next = iterator.next();
        assertThat(next.getRight()).isEqualTo("1");
        next = iterator.next();
        assertThat(next.getRight()).isEqualTo("2");
        next = iterator.next();
        assertThat(next.getRight()).isEqualTo("3");
        next = iterator.next();
        assertThat(next.getRight()).isEqualTo("4");
        next = iterator.next();
        assertThat(next.getRight()).isEqualTo("5");

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }
}