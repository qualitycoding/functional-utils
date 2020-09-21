package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple3;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class Functional_Zip3_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(null, mock(Iterable.class), mock(Iterable.class)))
                        .withMessage("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): input1 must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(mock(Iterable.class), null, mock(Iterable.class)))
                        .withMessage("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): input2 must not be null"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip3(mock(Iterable.class), mock(Iterable.class), null))
                        .withMessage("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): input3 must not be null"));
    }

    @Test
    void zip3WithThreeIterables() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Iterable<Character> input2 = Functional.seq.map(Function.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));
        final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

        final Collection<Tuple3<Integer, Character, Double>> expected = new ArrayList<>();
        expected.add(new Tuple3<>(1, 'a', 1.0));
        expected.add(new Tuple3<>(2, 'b', 2.0));
        expected.add(new Tuple3<>(3, 'c', 2.5));
        expected.add(new Tuple3<>(4, 'd', 3.0));
        expected.add(new Tuple3<>(5, 'e', 3.5));

        final List<Tuple3<Integer, Character, Double>> output = Functional.zip3(input1, input2, input3);

        assertThat(output).containsExactlyElementsOf(expected);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(()->output.add(new Tuple3<>(1,'l',2.0)));
    }

    @Test
    void cannotZip3UsingIterablesWithUnequalLengths() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> {
                            final Iterable<Integer> input1 = Functional.seq.map(Function.identity(), Arrays.asList(1, 2));
                            final Iterable<Character> input2 = Functional.seq.map(Function.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));
                            final Iterable<Character> input3 = Functional.seq.map(Function.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));
                            Functional.zip3(input1, input2, input3);
                        })
                        .withMessage("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): Cannot zip three iterables with different lengths"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> {
                            final Iterable<Integer> input1 = Functional.seq.map(Function.identity(), Arrays.asList(1, 2));
                            final Iterable<Character> input2 = Functional.seq.map(Function.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));
                            final Iterable<Integer> input3 = Functional.seq.map(Function.identity(), Arrays.asList(1, 2));
                            Functional.zip3(input1, input2, input3);
                        })
                        .withMessage("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): Cannot zip three iterables with different lengths"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> {
                            final Iterable<Integer> input1 = Functional.seq.map(Function.identity(), Arrays.asList(1, 2));
                            final Iterable<Integer> input2 = Functional.seq.map(Function.identity(), Arrays.asList(1, 2));
                            final Iterable<Character> input3 = Functional.seq.map(Function.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));
                            Functional.zip3(input1, input2, input3);
                        })
                        .withMessage("Functional.zip3(Iterable<A>,Iterable<B>,Iterable<C>): Cannot zip three iterables with different lengths"));
    }

    @Nested
    class Seq {
        @Test
        void seqZip3Test1() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
            final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

            final Collection<Tuple3<Integer, Character, Double>> expected = new ArrayList<>();
            expected.add(new Tuple3<>(1, 'a', 1.0));
            expected.add(new Tuple3<>(2, 'b', 2.0));
            expected.add(new Tuple3<>(3, 'c', 2.5));
            expected.add(new Tuple3<>(4, 'd', 3.0));
            expected.add(new Tuple3<>(5, 'e', 3.5));

            final Iterable<Tuple3<Integer, Character, Double>> output = Functional.seq.zip3(input1, input2, input3);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedSeqZip3Test1() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
            final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

            final Collection<Tuple3<Integer, Character, Double>> expected = new ArrayList<>();
            expected.add(new Tuple3<>(1, 'a', 1.0));
            expected.add(new Tuple3<>(2, 'b', 2.0));
            expected.add(new Tuple3<>(3, 'c', 2.5));
            expected.add(new Tuple3<>(4, 'd', 3.0));
            expected.add(new Tuple3<>(5, 'e', 3.5));

            final Iterable<Tuple3<Integer, Character, Double>> output = Functional.seq.<Integer, Character, Double>zip3(input1, input2).apply(input3);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cantRemoveFromSeqZip3Test1() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
            final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

            final Iterable<Tuple3<Integer, Character, Double>> output = Functional.seq.zip3(input1, input2, input3);

            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqZip3Test1() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
            final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

            final Iterable<Tuple3<Integer, Character, Double>> output = Functional.seq.zip3(input1, input2, input3);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }

        @Test
        void seqZip3Test2() {
            final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
            final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');
            final Collection<Double> input3 = Arrays.asList(1.0, 2.0, 2.5, 3.0, 3.5);

            final Collection<Tuple3<Integer, Character, Double>> expected = new ArrayList<>();
            expected.add(new Tuple3<>(1, 'a', 1.0));
            expected.add(new Tuple3<>(2, 'b', 2.0));
            expected.add(new Tuple3<>(3, 'c', 2.5));
            expected.add(new Tuple3<>(4, 'd', 3.0));
            expected.add(new Tuple3<>(5, 'e', 3.5));

            final Iterable<Tuple3<Integer, Character, Double>> output = Functional.seq.zip3(input1, input2, input3);
            final Iterator<Tuple3<Integer, Character, Double>> iterator = output.iterator();

            for (int i = 0; i < 20; ++i)
                assertThat(iterator.hasNext()).isTrue();

            for (final Tuple3<Integer, Character, Double> element : expected) {
                final Tuple3<Integer, Character, Double> next = iterator.next();
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
    }
}