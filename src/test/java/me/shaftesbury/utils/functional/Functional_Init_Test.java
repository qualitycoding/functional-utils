package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static me.shaftesbury.utils.functional.FunctionalTest.doublingGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;

class Functional_Init_Test {
    @Test
    void initTest() {
        final Collection<Integer> output = Functional.init(doublingGenerator, 5);
        assertThat(output).containsExactly(2, 4, 6, 8, 10);
    }

    @Test
    void rangeTest() {
        final Collection<Integer> output = Functional.init(Functional.range(0), 5);
        assertThat(output).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void seqInitTest1() {
        final Iterable<Integer> output = Functional.seq.init(doublingGenerator, 5);
        assertThat(output).containsExactly(2, 4, 6, 8, 10);
    }

    @Test
    void cantRemoveFromSeqInitTest1() {
        final Iterable<Integer> output = Functional.seq.init(doublingGenerator, 5);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void seqInitTest3() {
        final Iterable<Integer> output = Functional.seq.init(doublingGenerator, 5);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        int next = iterator.next();
        assertThat(next).isEqualTo(2);
        next = iterator.next();
        assertThat(next).isEqualTo(4);
        next = iterator.next();
        assertThat(next).isEqualTo(6);
        next = iterator.next();
        assertThat(next).isEqualTo(8);
        next = iterator.next();
        assertThat(next).isEqualTo(10);
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void seqInitTest2() {
        final Iterable<Integer> output = Functional.seq.init(doublingGenerator);
        assertThat(Functional.take(11, output)).containsExactly(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22);
    }

    @Test
    void cantRemoveFromSeqInitTest2() {
        final Iterable<Integer> output = Functional.seq.init(doublingGenerator);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqInitTest2() {
        final Iterable<Integer> output = Functional.seq.init(doublingGenerator);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

}