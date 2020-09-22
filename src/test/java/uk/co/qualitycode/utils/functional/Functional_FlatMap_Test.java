package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.flatMap;

class Functional_FlatMap_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(null, new ArrayList<>()))
                .withMessage("flatMap(Function<A,B>,Collection<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(i -> Collections.emptyList(), (List)null))
                .withMessage("flatMap(Function<A,B>,Collection<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(null, mock(Iterable.class)))
                .withMessage("flatMap(Function<A,B>,Iterable<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(i -> Collections.emptyList(), (Iterable) null))
                .withMessage("flatMap(Function<A,B>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(null))
                .withMessage("flatMap(Function<A,Iterable<B>>): f must not be null");

    }

    @Test
    void flatMapUsingIterable() {
        final Iterable<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> output = flatMap(Collections::singleton, expected);
        assertThat(output).containsExactlyElementsOf(expected);
    }

        @Test
    void flatMapUsingList() {
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> output = flatMap(Collections::singleton, expected);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void flatMapUsingIterableReturnsImmutableList() {
        final List<Integer> output = flatMap(Collections::singleton, (Iterable)Arrays.asList(1, 2, 3, 4, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(10));
    }

    @Test
    void flatMapUsingListReturnsImmutableList() {
        final List<Integer> output = flatMap(Collections::singleton, Arrays.asList(1, 2, 3, 4, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(10));
    }

    @Test
    void curriedFlatMap() {
        final Function<Iterable<Object>, List<Object>> flatMapFunc = flatMap(Collections::singleton);
        final List<Object> output = flatMapFunc.apply(Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly(1,2,3,4,5);
    }

//    @Nested
//    class Seq {
//        @Test
//        void seqMapTest1() {
//            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
//            final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
//            final Iterable<String> output = Functional.seq.flatMap(Collections::singleton, input);
//            final Iterator<String> it = output.iterator();
//            for (int i = 0; i < 20; ++i)
//                assertThat(it.hasNext()).isTrue();
//
//            for (int i = 0; i < expected.size(); ++i)
//                assertThat(it.next()).isEqualTo(expected.get(i));
//
//            assertThat(it.hasNext()).isFalse();
//            try {
//                it.next();
//            } catch (final NoSuchElementException e) {
//                return;
//            }
//
//            fail("Should not reach this point");
//        }
//
//        @Test
//        void curriedSeqMapTest1() {
//            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
//            final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
//            final Iterable<String> output = Functional.seq.flatMap(Collections::singleton).apply(input);
//            final Iterator<String> it = output.iterator();
//            for (int i = 0; i < 20; ++i)
//                assertThat(it.hasNext()).isTrue();
//
//            for (int i = 0; i < expected.size(); ++i)
//                assertThat(it.next()).isEqualTo(expected.get(i));
//
//            assertThat(it.hasNext()).isFalse();
//            try {
//                it.next();
//            } catch (final NoSuchElementException e) {
//                return;
//            }
//
//            fail("Should not reach this point");
//        }
//
//        @Test
//        void cantRemoveFromSeqMapTest1() {
//            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
//            final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
//            final Iterable<String> output = Functional.seq.flatMap(Collections::singleton, input);
//            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
//        }
//
//        @Test
//        void cantRestartIteratorFromSeqMapTest1() {
//            final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5); //Enumerable.Range(1, 5).ToList();
//            final List<String> expected = Arrays.asList("1", "2", "3", "4", "5");
//            final Iterable<String> output = Functional.seq.flatMap(Collections::singleton, input);
//            try {
//                output.iterator();
//            } catch (final UnsupportedOperationException e) {
//                fail("Shouldn't reach this point");
//            }
//            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
//        }
//    }

//    @Nested
//    class RecMap {
//        @Test
//        void recMapTest1() {
//            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
//            final Iterable<String> output = Functional.rec.flatMap(Collections::singleton, input);
//            assertThat(output).isEqualTo(Arrays.asList("1", "2", "3", "4", "5"));
//        }
//    }

//    @Nested
//    class SetMap {
//        @Test
//        void setMapTest1() {
//            final Set<Integer> input = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
//            final Set<String> output = Functional.set.flatMap(Collections::singleton, input);
//            final Set<String> expected = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));
//            assertThat(expected.containsAll(output)).isTrue();
//            assertThat(output.containsAll(expected)).isTrue();
//        }
//    }
}
