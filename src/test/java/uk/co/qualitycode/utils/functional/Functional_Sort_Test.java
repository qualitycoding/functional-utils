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

class Functional_Sort_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.sortWith(null, mock(Collection.class)))
                .withMessage("sortWith(Comparator<A>,Collection<B>): comparator must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.sortWith(mock(Comparator.class), (Collection)null))
                .withMessage("sortWith(Comparator<A>,Collection<B>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.sortWith(null, mock(Iterable.class)))
                .withMessage("sortWith(Comparator<A>,Iterable<B>): comparator must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.sortWith(mock(Comparator.class), (Iterable)null))
                .withMessage("sortWith(Comparator<A>,Iterable<B>): input must not be null");
    }

    @Test
    void sortWithTest() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final List<Integer> output = Functional.sortWith(Integer::compare, i);
        assertThat(output).containsExactly(1, 4, 6, 7, 23);
    }

    @Test
    void sortWithReturnsImmutableList() {
        final List<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final List<Integer> output = Functional.sortWith(Integer::compare, i);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(1));
    }

    @Test
    void sortWithIterableTest() {
        final Iterable<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final List<Integer> output = Functional.sortWith(Integer::compare, i);
        assertThat(output).containsExactly(1, 4, 6, 7, 23);
    }

    @Test
    void sortWithIterableReturnsImmutableList() {
        final Iterable<Integer> i = Arrays.asList(1, 6, 23, 7, 4);
        final List<Integer> output = Functional.sortWith(Integer::compare, i);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(1));
    }
}
