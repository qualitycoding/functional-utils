package me.shaftesbury.utils.functional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Functional_Filter_Test {
    public Functional_Filter_Test() {
    }

    @Test
    void seqFilterTest1() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);

        Assertions.assertThat(oddElems).isEmpty();
    }

    @Test
    void curriedSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd).apply(l);

        Assertions.assertThat(oddElems).isEmpty();
    }

    @Test
    void cantRemoveFromSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> oddElems.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqFilterTest1() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Iterable<Integer> oddElems = Functional.seq.filter(Functional.isOdd, l);
        try {
            oddElems.iterator();
        } catch (final UnsupportedOperationException e) {
            Assertions.fail("Shouldn't reach this point");
        }
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(oddElems::iterator);
    }

    @Test
    void seqFilterTest2() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Iterable<Integer> evenElems = Functional.seq.filter(Functional.isEven, l);

        final Collection<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
        Assertions.assertThat(evenElems).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest3() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Integer limit = 5;
        final Iterable<Integer> highElems = Functional.seq.filter(a -> a > limit, l);

        final Collection<Integer> expected = Arrays.asList(6, 8, 10);
        Assertions.assertThat(highElems).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest4() {
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Integer limit = 10;
        final Iterable<Integer> output = Functional.seq.filter(a -> a > limit, li);

        Assertions.assertThat(output.iterator().hasNext()).isFalse();
    }

    @Test
    void seqFilterTest5() {
        final Collection<Integer> li = Functional.init(FunctionalTest.doublingGenerator, 10);
        final Collection<Integer> expected = Arrays.asList(4, 8, 12, 16, 20);
        final Iterable<Integer> output = Functional.seq.filter(a -> a % 4 == 0, li);

        Assertions.assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqFilterTest6() {
        final Collection<Integer> input = Functional.init(FunctionalTest.doublingGenerator, 10);
        final Iterable<Integer> output = Functional.seq.filter(a -> a % 4 == 0, input);
        final Iterator<Integer> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            Assertions.assertThat(iterator.hasNext()).isTrue();

        int next = iterator.next();
        Assertions.assertThat(next).isEqualTo(4);
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo(8);
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo(12);
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo(16);
        next = iterator.next();
        Assertions.assertThat(next).isEqualTo(20);

        Assertions.assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        Assertions.fail("Should not reach this point");
    }
}