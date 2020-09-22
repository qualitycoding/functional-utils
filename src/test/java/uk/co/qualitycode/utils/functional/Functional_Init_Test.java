package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.init;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;

class Functional_Init_Test {
    @Test
    void preconditions() {
        assertAll(
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> init(null, 1))
                        .withMessage("init(Function<Integer,T>,int): f must not be null"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> init(mock(Function.class), -1))
                        .withMessage("init(Function<Integer,T>,int): howMany must be non-negative"));
    }

    @Test
    void initTest() {
        final Collection<Integer> output = init(doublingGenerator, 5);
        assertThat(output).containsExactly(2, 4, 6, 8, 10);
    }

    @Test
    void rangeTest() {
        final Collection<Integer> output = init(Functional.range(0), 5);
        assertThat(output).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    void initReturnsImmutableList() {
        final List<Integer> integers = init(doublingGenerator, 5);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(()->integers.add(0));
    }

    @Nested
    class Seq {
        @Test
        void seqInitWithBound() {
            final Iterable<Integer> output = Functional.Lazy.init(doublingGenerator, 5);
            assertThat(output).containsExactly(2, 4, 6, 8, 10);
        }

        @Test
        void seqInitWithoutUpperBound() {
            final Iterable<Integer> output = Functional.Lazy.init(doublingGenerator);
            assertThat(Functional.take(11, output)).containsExactly(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22);
        }

        @Test
        void cantRemoveFromSeqInit() {
            final Iterable<Integer> output = Functional.Lazy.init(doublingGenerator, 5);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRemoveFromSeqInitWithoutUpperBound() {
            final Iterable<Integer> output = Functional.Lazy.init(doublingGenerator);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> output.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqInit() {
            final Iterable<Integer> output = Functional.Lazy.init(doublingGenerator);
            try {
                output.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(output::iterator);
        }
    }
}
