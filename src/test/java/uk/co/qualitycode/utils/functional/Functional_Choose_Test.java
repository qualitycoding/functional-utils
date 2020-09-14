package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.FunctionalTest.triplingGenerator;

class Functional_Choose_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(()->Functional.choose(null, mock(Iterable.class)))
                .withMessage("choose(Function<A,Option<B>>,Iterable<A>): chooser must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(()->Functional.choose(mock(Function.class), (Iterable)null))
                .withMessage("choose(Function<A,Option<B>>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(()->Functional.choose(null, mock(Collection.class)))
                .withMessage("choose(Function<A,Option<B>>,Collection<A>): chooser must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(()->Functional.choose(mock(Function.class), (Collection)null))
                .withMessage("choose(Function<A,Option<B>>,Collection<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(()->Functional.choose(null))
                .withMessage("choose(Function<A,Option<B>>): chooser must not be null");
    }

    @Test
    void chooseTest1BUsingOption() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Collection<String> o = Functional.choose(i -> Option.of(Optional.of(i).filter(Functional::isEven).map(Object::toString)), li);
        assertThat(o).containsExactly("6", "12");
    }

    @Test
    void curriedChooseTest1B() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Collection<String> o = Functional.choose((Function<Integer, Option<String>>) i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none()).apply(li);
        assertThat(o).containsExactly("6", "12");
    }

    @Test
    void chooseTest2A() {
        Map<Integer, String> o = null;
        try {
            final Collection<Integer> li = Functional.init(triplingGenerator, 5);
            o = Functional.toDictionary(Function.identity(), Functional.stringify(),
                    Functional.choose(i -> i % 2 == 0 ? Option.of(i) : Option.none(), li));
        } catch (final Exception e) {
        }
        final Map<Integer, String> expected = new HashMap<>();
        expected.put(6, "6");
        expected.put(12, "12");
        assertThat(expected.size() == o.size()).isTrue();
        for (final int expectedKey : expected.keySet()) {
            assertThat(o.containsKey(expectedKey)).isTrue();
            final String expectedValue = expected.get(expectedKey);
            //assertThat("Expected '"+expectedValue+"' but got '"+o.get(expectedKey)+"'").isEqualTo(expectedValue,o.get(expectedKey));
            assertThat(o.get(expectedKey).equals(expectedValue)).isTrue();
        }
    }

    @Test
    void seqChooseTest1() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none(), li);

        final Collection<String> expected = Arrays.asList("6", "12");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose((Function<Integer, Option<String>>) i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none()).apply(li);

        final Collection<String> expected = Arrays.asList("6", "12");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none(), li);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none(), li);
        try {
            output.iterator();
        } catch (final UnsupportedOperationException e) {
            fail("Shouldn't reach this point");
        }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
    }

    @Test
    void seqChooseTest2() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none(), li);
        final Iterator<String> iterator = output.iterator();

        for (int i = 0; i < 20; ++i)
            assertThat(iterator.hasNext()).isTrue();

        String next = iterator.next();
        assertThat(next).isEqualTo("6");
        next = iterator.next();
        assertThat(next).isEqualTo("12");
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
        } catch (final NoSuchElementException e) {
            return;
        }

        fail("Should not reach this point");
    }

    @Test
    void chooseTest1() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Integer> expected = Arrays.asList(1, 3, 5);
        final Collection<Integer> output = Functional.choose(i -> i % 2 != 0 ? Option.of(i) : Option.none(), input);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void chooseTest2() {
        final Collection<String> input = Arrays.asList("abc", "def");
        final Collection<Character> expected = Arrays.asList('a');
        final Collection<Character> output = Functional.choose(str -> str.startsWith("a") ? Option.of('a') : Option.none(), input);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void chooseTest3() {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Integer> expected = Arrays.asList(1, 3, 5);
        final Collection<Integer> output = Functional.choose(i -> i % 2 != 0 ? Option.of(i) : Option.none(), input);

        assertThat(output).containsExactlyElementsOf(expected);
    }
}
