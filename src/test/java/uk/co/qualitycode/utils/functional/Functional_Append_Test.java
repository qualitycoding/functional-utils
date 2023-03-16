package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_Append_Test {
    @Nested
    class Lazy extends FiniteIterableTest<Integer, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.append(null, mock(Iterable.class)))
                    .withMessage("Lazy.append(T,Iterable<T>): value must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.append(new Object(), null))
                    .withMessage("Lazy.append(T,Iterable<T>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.append(null))
                    .withMessage("Lazy.append(T): value must not be null");
        }

        @Test
        void appendShouldPlaceNewValueAtTheEndOfTheSequence() {
            final int i = 6;
            final Collection<Integer> l = Functional.init(Function.identity(), 5);
            final Iterable<Integer> output = Functional.Lazy.append(i, l);
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedAppendShouldPlaceNewValueAtTheEndOfTheSequence() {
            final int i = 6;
            final Collection<Integer> l = Functional.init(Function.identity(), 5);
            final Iterable<Integer> output = Functional.Lazy.append(i).apply(l);
            final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6);

            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Functional.init(Function.identity(), 5);
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.append(10, l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.append(T,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 6;
        }
    }
}