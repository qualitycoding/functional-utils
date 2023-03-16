package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
    class Lazy extends FiniteIterableTest<Function<Integer, Option<String>>, Integer, String> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.choose(null, mock(Iterable.class)))
                    .withMessage("Lazy.choose(Function<A,Option<B>>,Iterable<A>): chooser must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.choose(mock(Function.class), (Iterable) null))
                    .withMessage("Lazy.choose(Function<A,Option<B>>,Iterable<A>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.choose(null))
                    .withMessage("Lazy.choose(Function<A,Option<B>>): chooser must not be null");
        }

        @Test
        void chooseEvenNumsAsStringsUsingIterable() {
            final Iterable<Integer> li = Functional.init(triplingGenerator, 5);
            final Iterable<String> o = Functional.Lazy.choose(
                    Functional.ConvertFlatMapOptionalToFlatMapOption.convert(
                            i -> Optional.of(i).filter(Functional::isEven).map(Object::toString)),
                    li);
            assertThat(o).containsExactly("6", "12");
        }

        @Test
        void curriedChooseEvenNumsAsStringsUsingIterable() {
            final Iterable<Integer> li = Functional.init(triplingGenerator, 5);
            final Iterable<String> o = Functional.Lazy.choose(
                    Functional.ConvertFlatMapOptionalToFlatMapOption.convert(
                            (Integer i) -> Optional.of(i).filter(Functional::isEven).map(Object::toString))).apply(li);
            assertThat(o).containsExactly("6", "12");
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Functional.init(triplingGenerator, 5);
        }

        @Override
        protected Iterable<String> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.choose(
                    Functional.ConvertFlatMapOptionalToFlatMapOption.convert(
                            i -> Optional.of(i).filter(Functional::isEven).map(Object::toString)),
                    l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.choose(Function<T,Option<U>>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 2;
        }
    }
}
