package me.shaftesbury.utils.functional;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Functional_Mapi_Test {
    public Functional_Mapi_Test() {
    }

    @Test
    void mapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        Assertions.assertThat(Functional.map(Pair::getRight, output)).containsExactly("1", "2", "3", "4", "5");
        Assertions.assertThat(Functional.map(Pair::getLeft, output)).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void curriedMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Function<Iterable<Integer>, List<Pair<Integer, String>>> mapiFunc = Functional.mapi((pos, i) -> Pair.of(pos, i.toString()));
        final Collection<Pair<Integer, String>> output = mapiFunc.apply(input);
        Assertions.assertThat(Functional.map(Pair::getRight, output)).containsExactly("1", "2", "3", "4", "5");
        Assertions.assertThat(Functional.map(Pair::getLeft, output)).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void seqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.toList(Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input));
        Assertions.assertThat(Functional.map(Pair::getRight, output)).containsExactly("1", "2", "3", "4", "5");
        Assertions.assertThat(Functional.map(Pair::getLeft, output)).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void cantRestartIteratorInSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> output = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            Assertions.fail("Shouldn't reach this point");
        }
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void curriedSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Pair<Integer, String>> output = Functional.toList(Functional.seq.mapi((BiFunction<Integer, Integer, Pair<Integer, String>>) (pos, i) -> Pair.of(pos, i.toString())).apply(input));
        Assertions.assertThat(Functional.map(Pair::getRight, output)).containsExactly("1", "2", "3", "4", "5");
        Assertions.assertThat(Functional.map(Pair::getLeft, output)).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void cantRemoveFromSeqMapiTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> output = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void seqMapiTest2() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Pair<Integer, String>> mapi = Functional.seq.mapi((pos, i) -> Pair.of(pos, i.toString()), input);
        final Iterator<Pair<Integer, String>> iterator = mapi.iterator();

        for (int i = 0; i < 10; ++i)
            Assertions.assertThat(iterator.hasNext()).isTrue();

        Pair<Integer, String> next = iterator.next();
        Assertions.assertThat(next.getRight()).isEqualTo("1");
        next = iterator.next();
        Assertions.assertThat(next.getRight()).isEqualTo("2");
        next = iterator.next();
        Assertions.assertThat(next.getRight()).isEqualTo("3");
        next = iterator.next();
        Assertions.assertThat(next.getRight()).isEqualTo("4");
        next = iterator.next();
        Assertions.assertThat(next.getRight()).isEqualTo("5");

        Assertions.assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        Assertions.fail("Should not reach this point");
    }
}