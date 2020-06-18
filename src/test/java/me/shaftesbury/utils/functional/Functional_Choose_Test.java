package me.shaftesbury.utils.functional;

import me.shaftesbury.utils.functional.monad.Option;
import me.shaftesbury.utils.functional.monad.OptionNoValueAccessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static me.shaftesbury.utils.functional.FunctionalTest.triplingGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

public class Functional_Choose_Test {
    public Functional_Choose_Test() {
    }

    @Test
    void chooseTest1B() throws OptionNoValueAccessException {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Collection<String> o = Functional.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);
        Assertions.assertThat(o).containsExactly("6", "12");
    }

    @Test
    void curriedChooseTest1B() throws OptionNoValueAccessException {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Collection<String> o = Functional.choose((Function<Integer, Option<String>>) i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None()).apply(li);
        Assertions.assertThat(o).containsExactly("6", "12");
    }

    @Test
    void chooseTest2A() //throws OptionNoValueAccessException
    {
        Map<Integer, String> o = null;
        try {
            final Collection<Integer> li = Functional.init(triplingGenerator, 5);
            o = Functional.toDictionary(Functional.identity(), Functional.dStringify(),
                    Functional.choose(i -> i % 2 == 0 ? Option.toOption(i) : Option.None(), li));
        } catch (final Exception e) {
        }
        final Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(6, "6");
        expected.put(12, "12");
        Assertions.assertThat(expected.size() == o.size()).isTrue();
        for (final Integer expectedKey : expected.keySet()) {
            Assertions.assertThat(o.containsKey(expectedKey)).isTrue();
            final String expectedValue = expected.get(expectedKey);
            //assertThat("Expected '"+expectedValue+"' but got '"+o.get(expectedKey)+"'").isEqualTo(expectedValue,o.get(expectedKey));
            Assertions.assertThat(o.get(expectedKey).equals(expectedValue)).isTrue();
        }
    }

    @Test
    void seqChooseTest1() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);

        final Collection<String> expected = Arrays.asList("6", "12");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void curriedSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose((Function<Integer, Option<String>>) i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None()).apply(li);

        final Collection<String> expected = Arrays.asList("6", "12");
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void cantRemoveFromSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
    }

    @Test
    void cantRestartIteratorFromSeqChooseTest1() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);
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
        final Iterable<String> output = Functional.seq.choose(i -> i % 2 == 0 ? Option.toOption(i.toString()) : Option.None(), li);
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
    void chooseTest1() throws OptionNoValueAccessException {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Integer> expected = Arrays.asList(1, 3, 5);
        final Collection<Integer> output =
                Functional.choose(i -> i % 2 != 0 ? Option.toOption(i) : Option.None(), input);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void chooseTest2() throws OptionNoValueAccessException {
        final Collection<String> input = Arrays.asList("abc", "def");
        final Collection<Character> expected = Arrays.asList('a');
        final Collection<Character> output =
                Functional.choose(str -> str.startsWith("a") ? Option.toOption('a') : Option.None()
                        , input
                );
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void chooseTest3() throws OptionNoValueAccessException {
        final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        final Collection<Integer> expected = Arrays.asList(1, 3, 5);
        final Collection<Integer> output = Functional.choose(
                i -> i % 2 != 0 ? Option.toOption(i) : Option.None(), input);

        assertThat(output).containsExactlyElementsOf(expected);
    }

}