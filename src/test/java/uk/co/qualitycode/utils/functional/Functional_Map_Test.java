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
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.map;
import static uk.co.qualitycode.utils.functional.Functional.stringify;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Map_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(null, new ArrayList<>()))
                .withMessage("map(Function<A,B>,Collection<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(i -> i, null))
                .withMessage("map(Function<A,B>,Collection<A>): input must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(null, mock(Iterable.class)))
                .withMessage("map(Function<A,B>,Iterable<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(i -> i, (Iterable) null))
                .withMessage("map(Function<A,B>,Iterable<A>): input must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map(null))
                .withMessage("map(Function<A,B>): f must not be null");

    }

    @Test
    void mapIterableOfIntsToStrings() {
        final List<String> output = map(stringify(), Functional.Lazy.init(doublingGenerator, 5));
        assertThat(output).containsExactly("2", "4", "6", "8", "10");
    }

    @Test
    void mapIntsToStrings() {
        final List<String> output = map(stringify(), Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    void mapIterableReturnsImmutableList() {
        final List<String> output = map(stringify(), Functional.Lazy.init(doublingGenerator, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add("ighijh"));
    }

    @Test
    void mapIntsToStringsReturnsImmutableList() {
        final List<String> output = map(stringify(), Arrays.asList(1, 2, 3, 4, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add("ighijh"));
    }

    @Test
    void curriedMapIntsToStrings() {
        final Function<Iterable<Object>, Iterable<String>> mapFunc = map(stringify());
        final Iterable<String> output = mapFunc.apply(Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly("1", "2", "3", "4", "5");
    }

    @Nested
    class Seq {
        @Test
        void seqMapTest1() {
            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
            final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
            final Iterable<String> output = Functional.Lazy.map(Functional.stringify(), input);
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
            final Iterable<String> output = Functional.Lazy.map(Functional.<Integer>stringify()).apply(input);
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
            final Iterable<String> output = Functional.Lazy.map(Functional.stringify(), input);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqMapTest1() {
            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
            final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
            final Iterable<String> output = Functional.Lazy.map(Functional.stringify(), input);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }

    @Nested
    class RecMap {
        @Test
        void recMapTest1() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<String> output = Functional.rec.map(Functional.stringify(), input);
            assertThat(output).isEqualTo(Arrays.asList("1", "2", "3", "4", "5"));
        }
    }

    @Nested
    class SetMap {
        @Test
        void setMapTest1() {
            final Set<Integer> input = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            final Set<String> output = Functional.set.map(Functional.stringify(), input);
            final Set<String> expected = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));
            assertThat(expected.containsAll(output)).isTrue();
            assertThat(output.containsAll(expected)).isTrue();
        }
    }
}
