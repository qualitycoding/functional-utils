package me.shaftesbury.utils.functional;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class Functional_Partition_Test {
    public Functional_Partition_Test() {
    }

    @Test
    void partitionTest1() {
        final Collection<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isOdd, m);

        Assertions.assertThat(r.getLeft()).containsExactly(3, 9, 15);
        Assertions.assertThat(r.getRight()).containsExactly(6, 12);
    }

    @Test
    void curriedPartitionTest1() {
        final Collection<Integer> m = Functional.init(FunctionalTest.triplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isOdd).apply(m);

        Assertions.assertThat(r.getLeft()).containsExactly(3, 9, 15);
        Assertions.assertThat(r.getRight()).containsExactly(6, 12);
    }

    @Test
    void iterablePartitionTest1() {
        final Iterable<Integer> m = Functional.seq.init(FunctionalTest.triplingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isOdd, m);

        Assertions.assertThat(r.getLeft()).containsExactly(3, 9, 15);
        Assertions.assertThat(r.getRight()).containsExactly(6, 12);
    }

    @Test
    void partitionTest2() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isEven, l);
        Assertions.assertThat(r.getLeft().toArray()).isEqualTo(l.toArray());
        Assertions.assertThat(r.getRight()).containsExactly();
    }

    @Test
    void partitionTest3() {
        final Collection<Integer> l = Functional.init(FunctionalTest.doublingGenerator, 5);
        final Pair<List<Integer>, List<Integer>> r = Functional.partition(Functional.isEven, l);
        Assertions.assertThat(r.getLeft().toArray()).isEqualTo(Functional.filter(Functional.isEven, l).toArray());
    }
}