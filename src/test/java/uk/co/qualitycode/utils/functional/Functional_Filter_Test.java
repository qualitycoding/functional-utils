package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Filter_Test {

    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(null, mock(Iterable.class)))
                .withMessage("filter(Predicate<A>,Iterable<A>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(mock(Predicate.class), (Iterable) null))
                .withMessage("filter(Predicate<A>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(null, mock(Collection.class)))
                .withMessage("filter(Predicate<A>,Collection<A>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(mock(Predicate.class), null))
                .withMessage("filter(Predicate<A>,Collection<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.filter(null))
                .withMessage("filter(Predicate<A>): predicate must not be null");
    }

   @Test
    void filterCollectionForOddElements() {
        final List<Integer> l = Functional.init(doublingGenerator, 5);
        final List<Integer> oddElems = Functional.filter(Functional::isOdd, l);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void filterIterableForOddElements() {
        final Iterable<Integer> l = Functional.Lazy.init(doublingGenerator, 5);
        final List<Integer> oddElems = Functional.filter(Functional::isOdd, l);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void curriedFilterForOddElements() {
        final List<Integer> l = Functional.init(doublingGenerator, 5);
        final List<Integer> oddElems = Functional.filter(Functional::isOdd).apply(l);

        assertThat(oddElems).isEmpty();
    }

    @Test
    void filterCollectionForEvenElements() {
        final List<Integer> l = Functional.init(doublingGenerator, 5);
        final List<Integer> evenElems = Functional.filter(Functional::isEven, l);

        assertThat(evenElems).containsExactlyElementsOf(l);
    }

    @Test
    void filterIterableForEvenElements() {
        final List<Integer> evenElems = Functional.filter(Functional::isEven, Functional.Lazy.init(doublingGenerator, 5));

        assertThat(evenElems).containsExactlyElementsOf(Functional.Lazy.init(doublingGenerator, 5));
    }

    @Test
    void curriedFilterForEvenElements() {
        final List<Integer> l = Functional.init(doublingGenerator, 5);
        final List<Integer> evenElems = Functional.filter(Functional::isEven).apply(l);

        assertThat(evenElems).containsExactlyElementsOf(l);
    }

    @Nested
    class Lazy {
        @Test
        void seqFilterTest1() {
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> oddElems = Functional.Lazy.filter(Functional::isOdd, l);

            assertThat(oddElems).isEmpty();
        }

        @Test
        void curriedSeqFilterTest1() {
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> oddElems = Functional.Lazy.filter(Functional::isOdd).apply(l);

            assertThat(oddElems).isEmpty();
        }

        @Test
        void cantRemoveFromSeqFilterTest1() {
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> oddElems = Functional.Lazy.filter(Functional::isOdd, l);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> oddElems.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqFilterTest1() {
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> oddElems = Functional.Lazy.filter(Functional::isOdd, l);
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
            final Iterable<Integer> evenElems = Functional.Lazy.filter(Functional::isEven, l);

            final Collection<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
            assertThat(evenElems).containsExactlyElementsOf(expected);
        }

        @Test
        void seqFilterTest3() {
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final int limit = 5;
            final Iterable<Integer> highElems = Functional.Lazy.filter(a -> a > limit, l);

            final Collection<Integer> expected = Arrays.asList(6, 8, 10);
            assertThat(highElems).containsExactlyElementsOf(expected);
        }

        @Test
        void seqFilterTest4() {
            final Collection<Integer> li = Functional.init(doublingGenerator, 5);
            final int limit = 10;
            final Iterable<Integer> output = Functional.Lazy.filter(a -> a > limit, li);

            assertThat(output.iterator().hasNext()).isFalse();
        }

        @Test
        void seqFilterTest5() {
            final Collection<Integer> li = Functional.init(doublingGenerator, 10);
            final Collection<Integer> expected = Arrays.asList(4, 8, 12, 16, 20);
            final Iterable<Integer> output = Functional.Lazy.filter(a -> a % 4 == 0, li);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void seqFilterTest6() {
            final Collection<Integer> input = Functional.init(doublingGenerator, 10);
            final Iterable<Integer> output = Functional.Lazy.filter(a -> a % 4 == 0, input);
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
    }

    @Nested
    class Rec {
        @Test
        void recFilterTest1() {
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Iterable<Integer> oddElems = Functional.rec.filter(Functional::isOdd, l);

            assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
        }
    }

    @Nested
    class Set_ {
        @Test
        void setFilterTest1() {
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Set<Integer> sl = new HashSet<>(l);
            final Set<Integer> oddElems = Functional.set.filter(Functional::isOdd, sl);

            assertThat(oddElems).containsExactlyElementsOf(new ArrayList<>());
        }

        @Test
        void setFilterTest2() {
            final Collection<Integer> l = Functional.init(doublingGenerator, 5);
            final Set<Integer> sl = new HashSet<>(l);
            final Set<Integer> evenElems = Functional.set.filter(Functional::isEven, sl);

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
}