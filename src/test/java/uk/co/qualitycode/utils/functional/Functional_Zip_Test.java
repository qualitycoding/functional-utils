package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;

class Functional_Zip_Test {
    @Test
    void zipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Tuple2<Integer, Character>> expected = new ArrayList<Tuple2<Integer, Character>>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final Collection<Tuple2<Integer, Character>> output = Functional.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void iterableZipTest1() {
        final Iterable<Integer> input1 = Functional.seq.map(Functional.identity(), Arrays.asList(1, 2, 3, 4, 5));
        final Iterable<Character> input2 = Functional.seq.map(Functional.identity(), Arrays.asList('a', 'b', 'c', 'd', 'e'));

        final Collection<Tuple2<Integer, Character>> expected = new ArrayList<Tuple2<Integer, Character>>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final Collection<Tuple2<Integer, Character>> output = Functional.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void failingZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd');

        assertThatIllegalArgumentException().isThrownBy(() -> Functional.zip(input1, input2));
    }

    @Test
    void zipTwoFuncsTest1() {
        final Collection<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

        final Collection<Tuple2<Integer, String>> expected = new ArrayList<Tuple2<Integer, String>>();
        expected.add(new Tuple2<>(1, "1"));
        expected.add(new Tuple2<>(2, "2"));
        expected.add(new Tuple2<>(3, "3"));
        expected.add(new Tuple2<>(4, "4"));
        expected.add(new Tuple2<>(5, "5"));

        final List<Tuple2<Integer, String>> output = Functional.zip(Functional.identity(), Functional.dStringify(), ints);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zipNoExceptionTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Tuple2<Integer, Character>> expected = new ArrayList<Tuple2<Integer, Character>>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final Collection<Tuple2<Integer, Character>> output = Functional.noException.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void zipNoExceptionTest2() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5, 6);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Tuple2<Integer, Character>> expected = new ArrayList<Tuple2<Integer, Character>>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final Collection<Tuple2<Integer, Character>> output = Functional.noException.zip(input1, input2);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void seqZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Tuple2<Integer, Character>> expected = new ArrayList<Tuple2<Integer, Character>>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final Collection<Tuple2<Integer, Character>> output = Functional.toList(Functional.seq.zip(input1, input2));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Tuple2<Integer, Character>> expected = new ArrayList<Tuple2<Integer, Character>>();
        expected.add(new Tuple2<>(1, 'a'));
        expected.add(new Tuple2<>(2, 'b'));
        expected.add(new Tuple2<>(3, 'c'));
        expected.add(new Tuple2<>(4, 'd'));
        expected.add(new Tuple2<>(5, 'e'));

        final Collection<Tuple2<Integer, Character>> output = Functional.toList(Functional.seq.<Integer, Character>zip(input1).apply(input2));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqZipTest1() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Iterable<Tuple2<Integer, Character>> zip = Functional.seq.zip(input1, input2);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> zip.iterator().remove());
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
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(zip::iterator);
    }

    @Test
    void seqZipTest2() {
        final Collection<Integer> input1 = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Character> input2 = Arrays.asList('a', 'b', 'c', 'd', 'e');

        final Collection<Tuple2<Integer, Character>> expected = new ArrayList<Tuple2<Integer, Character>>();
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

        final Collection<Tuple2<Integer, String>> expected = new ArrayList<Tuple2<Integer, String>>();
        expected.add(new Tuple2<>(1, "1"));
        expected.add(new Tuple2<>(2, "2"));
        expected.add(new Tuple2<>(3, "3"));
        expected.add(new Tuple2<>(4, "4"));
        expected.add(new Tuple2<>(5, "5"));

        final List<Tuple2<Integer, String>> output = Functional.toList(Functional.seq.zip(Functional.identity(), Functional.dStringify(), input));

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqZipFnTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

        final Iterable<Tuple2<Integer, String>> output = Functional.seq.zip(Functional.identity(), Functional.dStringify(), input);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqZipFnTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

        final Iterable<Tuple2<Integer, String>> output = Functional.seq.zip(Functional.identity(), Functional.dStringify(), input);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }
}
