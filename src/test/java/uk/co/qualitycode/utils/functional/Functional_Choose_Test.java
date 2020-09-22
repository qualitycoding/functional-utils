package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
                .isThrownBy(() -> Functional.choose(null, mock(Iterable.class)))
                .withMessage("choose(Function<A,Option<B>>,Iterable<A>): chooser must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.choose(mock(Function.class), (Iterable) null))
                .withMessage("choose(Function<A,Option<B>>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.choose(null, mock(Collection.class)))
                .withMessage("choose(Function<A,Option<B>>,Collection<A>): chooser must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.choose(mock(Function.class), (Collection) null))
                .withMessage("choose(Function<A,Option<B>>,Collection<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.choose(null))
                .withMessage("choose(Function<A,Option<B>>): chooser must not be null");
    }

    @Test
    void chooseEvenNumsAsStringsUsingIterable() {
        final Iterable<Integer> li = Functional.init(triplingGenerator, 5);
        final Collection<String> o = Functional.choose(
                Functional.ConvertFlatMapOptionalToFlatMapOption.convert(
                        i -> Optional.of(i).filter(Functional::isEven).map(Object::toString)),
                li);
        assertThat(o).containsExactly("6", "12");
    }

    @Test
    void chooseEvenNumsAsStringsUsingCollection() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Collection<String> o = Functional.choose(
                Functional.ConvertFlatMapOptionalToFlatMapOption.convert(
                        i -> Optional.of(i).filter(Functional::isEven).map(Object::toString)),
                li);
        assertThat(o).containsExactly("6", "12");
    }

    @Test
    void curriedChooseEvenNumsAsStringsUsingCollection() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final Collection<String> o = Functional.choose(
                Functional.ConvertFlatMapOptionalToFlatMapOption.convert(
                        (Integer i) -> Optional.of(i).filter(Functional::isEven).map(Object::toString)))
                .apply(li);
        assertThat(o).containsExactly("6", "12");
    }

    @Test
    void cantRemoveFromChooseResultsUsingCollection() {
        final Collection<Integer> li = Functional.init(triplingGenerator, 5);
        final List<String> output = Functional.choose(
                Functional.ConvertFlatMapOptionalToFlatMapOption.convert(
                        i -> Optional.of(i).filter(Functional::isEven).map(Object::toString)),
                li);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::clear);
    }

    @Test
    void cantRemoveFromChooseResultsUsingIterable() {
        final Iterable<Integer> li = Functional.init(triplingGenerator, 5);
        final List<String> output = Functional.choose(
                Functional.ConvertFlatMapOptionalToFlatMapOption.convert(
                        i -> Optional.of(i).filter(Functional::isEven).map(Object::toString)),
                li);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::clear);
    }

    @Nested
    class Seq {
        @Test
        void seqChooseTest1() {
            final Collection<Integer> li = Functional.init(triplingGenerator, 5);
            final Iterable<String> output = Functional.Lazy.choose(i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none(), li);

            final Collection<String> expected = Arrays.asList("6", "12");
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedSeqChooseTest1() {
            final Collection<Integer> li = Functional.init(triplingGenerator, 5);
            final Iterable<String> output = Functional.Lazy.choose((Function<Integer, Option<String>>) i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none()).apply(li);

            final Collection<String> expected = Arrays.asList("6", "12");
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void cantRemoveFromSeqChooseTest1() {
            final Collection<Integer> li = Functional.init(triplingGenerator, 5);
            final Iterable<String> output = Functional.Lazy.choose(i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none(), li);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqChooseTest1() {
            final Collection<Integer> li = Functional.init(triplingGenerator, 5);
            final Iterable<String> output = Functional.Lazy.choose(i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none(), li);
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
            final Iterable<String> output = Functional.Lazy.choose(i -> i % 2 == 0 ? Option.of(i.toString()) : Option.none(), li);
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
    }
}
