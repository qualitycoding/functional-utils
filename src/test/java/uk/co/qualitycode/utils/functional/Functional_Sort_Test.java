package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.co.qualitycode.utils.functional.Functional.sortWith;
import static uk.co.qualitycode.utils.functional.Functional.sorter;

class Functional_Sort_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sortWith(null, mock(Collection.class)))
                .withMessage("sortWith(Comparator<A>,Collection<B>): comparator must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sortWith(mock(Comparator.class), (Collection) null))
                .withMessage("sortWith(Comparator<A>,Collection<B>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> sortWith(null, mock(Iterable.class)))
                .withMessage("sortWith(Comparator<A>,Iterable<B>): comparator must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sortWith(mock(Comparator.class), (Iterable) null))
                .withMessage("sortWith(Comparator<A>,Iterable<B>): input must not be null");
    }

    @Test
    void sortWithTest() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final List<Integer> output = sortWith(Integer::compare, i);
        assertThat(output).containsExactly(1, 4, 6, 7, 23);
    }

    @Test
    void sortWithReturnsImmutableList() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final List<Integer> output = sortWith(Integer::compare, i);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(1));
    }

    @Test
    void sortWithIterableTest() {
        final Iterable<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final List<Integer> output = sortWith(Integer::compare, i);
        assertThat(output).containsExactly(1, 4, 6, 7, 23);
    }

    @Test
    void sortWithIterableReturnsImmutableList() {
        final Iterable<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final List<Integer> output = sortWith(Integer::compare, i);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(1));
    }

    @Test
    void sorterOrdersThing() {
        assertThat(sorter(Integer.valueOf(2), Integer.valueOf(10))).isEqualTo(-1);
        assertThat(sorter(Integer.valueOf(2), Integer.valueOf(2))).isEqualTo(0);
        assertThat(sorter(Integer.valueOf(10), Integer.valueOf(2))).isEqualTo(1);
    }

    @Test
    void sorterWithSortWith() {
        assertThat(sortWith(sorter, Arrays.asList(2, 10))).containsExactly(2, 10);
        assertThat(sortWith(sorter, Arrays.asList(2, 2))).containsExactly(2, 2);
        assertThat(sortWith(sorter, Arrays.asList(10, 2))).containsExactly(2, 10);
    }

    @Test
    void sorterCallsCompareTo() {
        final Comparable a = mock(Comparable.class);
        final Comparable b = mock(Comparable.class);
        sorter(a, b);

        verify(a).compareTo(b);
    }
}
