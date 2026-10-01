package uk.co.qualitycode.utils.functional.primitive.integer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class IntListTest {
    private final IntList unsorted = new IntList(new int[]{5, 1, 9, 3});

    @ParameterizedTest
    @ValueSource(ints = {5, 1, 9, 3})
    void containsEveryElementOfAnUnsortedList(final int element) {
        assertThat(unsorted.contains(element)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4, 10})
    void doesNotContainValuesThatAreAbsent(final int absent) {
        assertThat(unsorted.contains(absent)).isFalse();
    }

    @Test
    void toArrayBoxesIntoAnArrayThatIsLargeEnough() {
        final Integer[] target = new Integer[4];

        assertThat(unsorted.toArray(target)).isSameAs(target).containsExactly(5, 1, 9, 3);
    }

    @Test
    void toArrayAllocatesWhenTheArrayIsTooSmall() {
        assertThat(unsorted.toArray(new Integer[0])).containsExactly(5, 1, 9, 3);
    }

    @Test
    void toArrayTerminatesWithNullWhenTheArrayIsLarger() {
        assertThat(unsorted.toArray(new Integer[6])).containsExactly(5, 1, 9, 3, null, null);
    }

    @Test
    void isIsolatedFromLaterChangesToTheSourceArray() {
        final int[] source = {1, 2, 3};
        final IntList list = new IntList(source);

        source[0] = 99;

        assertThat(list.toArray()).containsExactly(1, 2, 3);
    }

    @Test
    void toArrayReturnsACopy() {
        unsorted.toArray()[0] = 99;

        assertThat(unsorted.get(0)).isEqualTo(5);
    }
}
