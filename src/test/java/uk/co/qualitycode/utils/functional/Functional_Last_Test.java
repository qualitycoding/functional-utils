package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class Functional_Last_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
            .isThrownBy(()->Functional.last((Iterable)null))
            .withMessage("last(Iterable<T>): input must not be null");
        assertThatIllegalArgumentException()
            .isThrownBy(()->Functional.last(new ArrayList<>()))
            .withMessage("last(Iterable<T>): input must not be empty");

        assertThatIllegalArgumentException()
            .isThrownBy(()->Functional.last((Object[])null))
            .withMessage("last(T[]): input must not be null");
        assertThatIllegalArgumentException()
            .isThrownBy(()->Functional.last(new Object[]{}))
            .withMessage("last(T[]): input must not be empty");
    }

    @Test
    void lastFromCollection() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        assertThat(Functional.last(input)).isEqualTo(5);
    }

    @Test
    void lastFromArray() {
        final Integer[] input = new Integer[]{1, 2, 3, 4, 5};
        assertThat(Functional.last(input)).isEqualTo(5);
    }
}
