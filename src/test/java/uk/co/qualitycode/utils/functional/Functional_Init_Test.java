package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Init_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> Functional.init(null, 1))
                        .withMessage("init(Function<Integer,T>,int): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> Functional.init(mock(Function.class), -1))
                        .withMessage("init(Function<Integer,T>,int): howMany must be non-negative"));
    }

    @Test
    void initTest() {
        final Collection<Integer> output = Functional.init(doublingGenerator, 5);
        assertThat(output).containsExactly(2, 4, 6, 8, 10);
    }

    @Test
    void rangeTest() {
        final Collection<Integer> output = Functional.init(Functional.range(0), 5);
        assertThat(output).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void initReturnsImmutableList() {
        final List<Integer> integers = Functional.init(doublingGenerator, 5);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> integers.add(0));
    }

    @Nested
    class Lazy_FiniteInit extends FiniteIterableTest<Function<Integer, Integer>, Integer, Integer> {
        @Test
        void preconditions() {
            assertAll(
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.Lazy.init(null, 1))
                            .withMessage("Lazy.init(Function<Integer,T>,int): f must not be null"),
                    () -> assertThatIllegalArgumentException()
                            .isThrownBy(() -> Functional.Lazy.init(mock(Function.class), -1))
                            .withMessage("Lazy.init(Function<Integer,T>,int): howMany must be non-negative"));
        }

        @Test
        void initReturnsSequence() {
            final Iterable<Integer> output = Functional.Lazy.init(doublingGenerator, 5);
            assertThat(output).containsExactly(2, 4, 6, 8, 10);
        }

        @Test
        void initReturnsUnboundedSequence() {
            final Iterable<Integer> output = Functional.Lazy.init(doublingGenerator);
            assertThat(Functional.take(6, output)).containsExactly(2, 4, 6, 8, 10, 12);
        }

        @Override
        public Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        public Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.init(doublingGenerator, 5);
        }

        @Override
        public String methodNameInExceptionMessage() {
            return "Lazy.init(Function<Integer,T>,int)";
        }

        @Override
        public int noOfElementsInOutput() {
            return 5;
        }
    }

    @Nested
    class Lazy_InfiniteInit extends InfiniteIterableTest<Function<Integer, Integer>, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.init(null))
                    .withMessage("Lazy.init(Function<Integer,T>): f must not be null");
        }

        @Test
        void initReturnsUnboundedSequence() {
            final Iterable<Integer> output = Functional.Lazy.init(doublingGenerator);
            assertThat(Functional.take(6, output)).containsExactly(2, 4, 6, 8, 10, 12);
        }

        @Override
        public Collection<Integer> initialValues() {
            return Collections.emptyList();
        }

        @Override
        public Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.init(doublingGenerator);
        }

        @Override
        public String methodNameInExceptionMessage() {
            return "Lazy.init(Function<Integer,T>)";
        }

        @Override
        public int noOfElementsInOutput() {
            return 5;
        }
    }
}
