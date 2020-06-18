package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static me.shaftesbury.utils.functional.Functional.isEven;
import static me.shaftesbury.utils.functional.FunctionalTest.doublingGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;
public class Functional_Filter_Test {
    public Functional_Filter_Test() {
    }

    @Test
    void seqFilterTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void curriedSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd).apply(l);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void cantRemoveFromSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> oddElems.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);
        try {
            oddElems.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(oddElems::iterator);
    }

    @Test
    void seqFilterTest2() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> evenElems = Functional.seq.filter(Functional.isEven, l);

        final Collection<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(evenElems).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest3() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final int limit = 5;
        final Iterable<Integer> highElems = Functional.seq.filter(a -> a > limit, l);

        final Collection<Integer> expected = Arrays.asList(6, 8, 10);
        assertThat(highElems).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest4() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final int limit = 10;
        final Iterable<Integer> output = Functional.seq.filter(a -> a > limit, li);

        assertThat(output.iterator().hasNext()).isFalse();
    }

    @Test
    void seqFilterTest5() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 10);
        final Collection<Integer> expected = Arrays.asList(4, 8, 12, 16, 20);
        final Iterable<Integer> output = Functional.seq.filter(a -> a % 4 == 0, li);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest6() {
        final Collection<Integer> input = Functional.init(doublingGenerator, 10);
        final Iterable<Integer> output = Functional.seq.filter(a -> a % 4 == 0, input);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        int next = iterator.next();
        assertThat(next).isEqualTo(4);
        next = iterator.next();
        assertThat(next).isEqualTo(8);
        next = iterator.next();
        assertThat(next).isEqualTo(12);
        next = iterator.next();
        assertThat(next).isEqualTo(16);
        next = iterator.next();
        assertThat(next).isEqualTo(20);

        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void recFilterTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.rec.filter(Functional.isOdd, l);

        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void setFilterTest1() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> oddElems = Functional.set.filter(Functional.isOdd, sl);

        assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
    }

    @Test
    void setFilterTest2() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> evenElems = Functional.set.filter(isEven, sl);

        final Collection<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
        assertThat(expected.containsAll(evenElems)).isTrue();
        assertThat(evenElems.containsAll(expected)).isTrue();
    }

    @Test
    void setFilterTest3() {
        final Collection<Integer> l = Functional.init(doublingGenerator, 5);
        final int limit = 5;
        final Set<Integer> sl = new HashSet<>(l);
        final Set<Integer> highElems = Functional.set.filter(a -> a > limit, sl);

        final Collection<Integer> expected = Arrays.asList(6, 8, 10);
        assertThat(expected.containsAll(highElems)).isTrue();
        assertThat(highElems.containsAll(expected)).isTrue();
    }

    @Test
    void setFilterTest4() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final int limit = 10;
        final Set<Integer> sl = new HashSet<>(li);
        final Set<Integer> output = Functional.set.filter(a -> a > limit, sl);

        assertThat(output.iterator().hasNext()).isFalse();
    }

    @Test
    void setFilterTest5() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 10);
        final Collection<Integer> expected = Arrays.asList(4, 8, 12, 16, 20);
        final Set<Integer> sl = new HashSet<>(li);
        final Set<Integer> output = Functional.set.filter(a -> a % 4 == 0, sl);

        assertThat(expected.containsAll(output)).isTrue();
        assertThat(output.containsAll(expected)).isTrue();
    }
}