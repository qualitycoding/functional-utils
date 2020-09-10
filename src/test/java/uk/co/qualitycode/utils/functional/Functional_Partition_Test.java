package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class Functional_Partition_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.partition(null, mock(Iterable.class)))
                .withMessage("partition(Predicate<A>,Iterable<A>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.partition(mock(Predicate.class), (Iterable) null))
                .withMessage("partition(Predicate<A>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.partition(null))
                .withMessage("partition(Predicate<A>): predicate must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.partition(0, 1))
                .withMessage("partition(int,int): howManyElements must be positive");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.partition(1, 0))
                .withMessage("partition(int,int): howManyPartitions must be positive");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.partition(null, 1, 1))
                .withMessage("partition(Function<Integer,A>,int,int): generator must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.partition(mock(Function.class), 0, 1))
                .withMessage("partition(Function<Integer,A>,int,int): howManyElements must be positive");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.partition(mock(Function.class), 1, 0))
                .withMessage("partition(Function<Integer,A>,int,int): howManyPartitions must be positive");
    }

    @Test
    void partitionUsingPredicate() {
        final Collection<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);
        final Tuple2<List<Integer>, List<Integer>> r = Functional.partition(Functional::isOdd, m);

        assertThat(r._1()).containsExactly(3, 9, 15);
        assertThat(r._2()).containsExactly(6, 12);
    }

    @Test
    void curriedPartitionUsingPredicate() {
        final Collection<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);
        final Tuple2<List<Integer>, List<Integer>> r = Functional.partition(Functional::isOdd).apply(m);

        assertThat(r._1()).containsExactly(3, 9, 15);
        assertThat(r._2()).containsExactly(6, 12);
    }

    @Test
    void exactlyEvenPartitionRangesOfInt() {
        final int noElems = 10;
        final int noPartitions = 5;
        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems, noPartitions);

        final List<Functional.Range<Integer>> expected = Arrays.asList(
                new Functional.Range<>(1, 3),
                new Functional.Range<>(3, 5),
                new Functional.Range<>(5, 7),
                new Functional.Range<>(7, 9),
                new Functional.Range<>(9, 11));

        assertThat(partitions).containsExactlyElementsOf(expected);
    }

    @Test
    void partitionWhereNumberOfElementsDoesNotSplitEqually() {
        final int noElems = 13;
        final int noPartitions = 5;
        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems, noPartitions);

        final List<Functional.Range<Integer>> expected = Arrays.asList(
                new Functional.Range<>(1, 4),
                new Functional.Range<>(4, 7),
                new Functional.Range<>(7, 10),
                new Functional.Range<>(10, 12),
                new Functional.Range<>(12, 14));

        assertThat(partitions).containsExactlyElementsOf(expected);
    }

    @Test
    void partitionFewerIntsThanPartitionsRequested() {
        final int noElems = 7;
        final int noPartitions = 10;
        final List<Functional.Range<Integer>> partitions = Functional.partition(noElems, noPartitions);

        final List<Functional.Range<Integer>> expected = Arrays.asList(
                new Functional.Range<>(1, 2),
                new Functional.Range<>(2, 3),
                new Functional.Range<>(3, 4),
                new Functional.Range<>(4, 5),
                new Functional.Range<>(5, 6),
                new Functional.Range<>(6, 7),
                new Functional.Range<>(7, 8),
                new Functional.Range<>(8, 8),
                new Functional.Range<>(8, 8),
                new Functional.Range<>(8, 8));

        assertThat(partitions).containsExactlyElementsOf(expected);
    }

    @Test
    void partitionSupplyingAGenerator() {
        final int noElems = 13;
        final int noPartitions = 5;
        final List<Functional.Range<String>> partitions = Functional.partition(i -> Integer.toString(i), noElems, noPartitions);

        final List<Functional.Range<String>> expected = Arrays.asList(
                new Functional.Range<>("1", "4"),
                new Functional.Range<>("4", "7"),
                new Functional.Range<>("7", "10"),
                new Functional.Range<>("10", "12"),
                new Functional.Range<>("12", "14"));

        assertThat(partitions).containsExactlyElementsOf(expected);
    }

    @Nested
    class Seq {
        @Test
        void cantRemoveFromSeqPartitionRanges() {
            final int noElems = 13;
            final int noPartitions = 5;
            final Iterable<Functional.Range<Integer>> partitions = Functional.seq.partition(noElems, noPartitions);
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> partitions.iterator().remove());
        }

        @Test
        void cantRestartIteratorFromSeqPartition() {
            final int noElems = 13;
            final int noPartitions = 5;
            final Iterable<Functional.Range<Integer>> partitions = Functional.seq.partition(noElems, noPartitions);
            try {
                partitions.iterator();
            } catch (final UnsupportedOperationException e) {
                fail("Shouldn't reach this point");
            }
            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(partitions::iterator);
        }
    }
}