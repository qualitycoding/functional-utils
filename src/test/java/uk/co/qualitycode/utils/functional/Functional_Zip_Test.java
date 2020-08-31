package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class Functional_Zip_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(null, Function.identity(), mock(Iterable.class)))
                .withMessage("Functional.zip(Function<A,B>,Function<A,B>,Iterable<A>): zipFunc1 must not be null");
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(Function.identity(), null, mock(Iterable.class)))
                .withMessage("Functional.zip(Function<A,B>,Function<A,B>,Iterable<A>): zipFunc2 must not be null");
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(Function.identity(), Function.identity(), null))
                .withMessage("Functional.zip(Function<A,B>,Function<A,B>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(null, mock(Iterable.class)))
                .withMessage("Functional.zip(Iterable<A>,Iterable<B>): input1 must not be null");
        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(mock(Iterable.class), null))
                .withMessage("Functional.zip(Iterable<A>,Iterable<B>): input2 must not be null");
    }

    @Test
    void zipTwoFuncs() {
        final List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

        final List<Tuple2<Integer, String>> expected = new ArrayList<>();
        expected.add(new Tuple2<>(2, "1"));
        expected.add(new Tuple2<>(3, "2"));
        expected.add(new Tuple2<>(4, "3"));
        expected.add(new Tuple2<>(5, "4"));
        expected.add(new Tuple2<>(6, "5"));

        final List<Tuple2<Integer, String>> output = Functional.zip(i -> i + 1, Functional.dStringify(), ints);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedZipTwoFuncs() {
        final List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

        final List<Tuple2<Integer, String>> expected = new ArrayList<>();
        expected.add(new Tuple2<>(1, "1"));
        expected.add(new Tuple2<>(2, "2"));
        expected.add(new Tuple2<>(3, "3"));
        expected.add(new Tuple2<>(4, "4"));
        expected.add(new Tuple2<>(5, "5"));

        final List<Tuple2<Integer, String>> output = Functional.zip(Function.<Integer>identity(), Functional.dStringify()).apply(ints);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zipTwoIterables() {
        final Iterable<Integer> input1 = Functional.seq.map(Function.identity(), Arrays.asList(1, 2, 3, 4, 5));
        final Iterable<Character> input2 = Functional.seq.map(Function.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));

        final List<Tuple2<Integer, Character>> expected = new ArrayList<>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final List<Tuple2<Integer, Character>> output = Functional.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cannotZipTwoIterablesWithUnequalLengths() {
        final Iterable<Integer> input1 = Functional.seq.map(Function.identity(), Arrays.asList(1, 2));
        final Iterable<Character> input2 = Functional.seq.map(Function.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));

        assertThatIllegalArgumentException()
                .isThrownBy(()->Functional.zip(input1, input2))
                .withMessage("Functional.zip(Iterable<A>,Iterable<B>): Cannot zip two iterables with different lengths");
    }

    @Test
    void zipTwoCollections() {
        final List<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final List<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final List<Tuple2<Integer, Character>> expected = new ArrayList<>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final List<Tuple2<Integer, Character>> output = Functional.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cannotZipTwoCollectionsWithUnequalLengths() {
        final List<Integer> input1 = Arrays.asList(1, 2);
        final Collection<Character> input2 = Collections.singleton('a');

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.zip(input1, input2))
                .withMessage("Functional.zip(Collection<A>,Collection<B>): The input sequences must have the same number of elements");
    }

    @Nested
    class Seq {
        @Test
        void seqZipTwoCollections() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

            final Collection<Tuple2<Integer, Character>> expected = new ArrayList<>();
            expected.add(new Tuple2<>(1, 'a'));
            expected.add(new Tuple2<>(2, 'b'));
            expected.add(new Tuple2<>(3, 'c'));
            expected.add(new Tuple2<>(4, 'd'));
            expected.add(new Tuple2<>(5, 'e'));

            final Collection<Tuple2<Integer, Character>> output = Functional.toList(Functional.seq.zip(input1, input2));

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedSeqZipTwoCollections() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

            final Collection<Tuple2<Integer, Character>> expected = new ArrayList<>();
            expected.add(new Tuple2<>(1, 'a'));
            expected.add(new Tuple2<>(2, 'b'));
            expected.add(new Tuple2<>(3, 'c'));
            expected.add(new Tuple2<>(4, 'd'));
            expected.add(new Tuple2<>(5, 'e'));

            final Collection<Tuple2<Integer, Character>> output = Functional.toList(Functional.seq.<Integer, Character>zip(input1).apply(input2));

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cantRemoveFromSeqZip() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

            final Iterable<Tuple2<Integer, Character>> zip = Functional.seq.zip(input1, input2);
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> zip.iterator().remove())
                    .withMessage("Functional.seq.zip(Iterable,Iterable): it is not possible to remove elements from this sequence");
        }

        @Test
        void cantRestartIteratorFromSeqZipTest1() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

            final Iterable<Tuple2<Integer, Character>> zip = Functional.seq.zip(input1, input2);
            try {
                zip.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(zip::iterator).withMessage("This Iterable does not allow multiple Iterators");
        }

        @Test
        void seqZipTest2() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

            final Collection<Tuple2<Integer, Character>> expected = new ArrayList<>();
            expected.add(new Tuple2<>(1, 'a'));
            expected.add(new Tuple2<>(2, 'b'));
            expected.add(new Tuple2<>(3, 'c'));
            expected.add(new Tuple2<>(4, 'd'));
            expected.add(new Tuple2<>(5, 'e'));

            final Collection<Tuple2<Integer, Character>> output = Functional.toList(Functional.seq.zip(input1, input2));
            final Iterator<Tuple2<Integer, Character>> iterator = output.iterator();

            for (int i = 0; i < 20; ++i)
                assertThat(iterator.hasNext()).isTrue();

            for (final Tuple2<Integer, Character> element : expected) {
                final Tuple2<Integer, Character> next = iterator.next();
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

        @Test
        void seqZipFnTest1() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

            final Collection<Tuple2<Integer, String>> expected = new ArrayList<>();
            expected.add(new Tuple2<>(1, "1"));
            expected.add(new Tuple2<>(2, "2"));
            expected.add(new Tuple2<>(3, "3"));
            expected.add(new Tuple2<>(4, "4"));
            expected.add(new Tuple2<>(5, "5"));

            final List<Tuple2<Integer, String>> output = Functional.toList(Functional.seq.zip(Function.identity(), Functional.dStringify(), input));

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cantRemoveFromSeqZipFnTest1() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

            final Iterable<Tuple2<Integer, String>> output = Functional.seq.zip(Function.identity(), Functional.dStringify(), input);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqZipFnTest1() {
            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

            final Iterable<Tuple2<Integer, String>> output = Functional.seq.zip(Function.identity(), Functional.dStringify(), input);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }
}
