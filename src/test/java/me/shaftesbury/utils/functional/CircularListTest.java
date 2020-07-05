package me.shaftesbury.utils.functional;

import io.vavr.collection.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.vavr.api.VavrAssertions.assertThat;

class CircularListTest {
    @Nested
    class Initialise {
        @Test
        void circularArrayListInitialiseTest1() {
            final CircularList<Integer> output = CircularList.of(List.of(1, 2, 3, 4));
            assertThat(output)
                    .containsSequence(1, 2, 3, 4)
                    .containsSequence(2, 3, 4, 1)
                    .containsSequence(3, 4, 1, 2)
                    .containsSequence(4, 1, 2, 3);
        }
    }

    @Nested
    class PrependOrAppend {
        @Test
        void prependAnElement() {
            final CircularList<Integer> output = CircularList.of(List.of(1, 2, 3, 4));
            assertThat(output.prepend(0))
                    .containsSequence(0, 1, 2, 3, 4)
                    .containsSequence(1, 2, 3, 4, 0)
                    .containsSequence(2, 3, 4, 0, 1)
                    .containsSequence(3, 4, 0, 1, 2)
                    .containsSequence(4, 0, 1, 2, 3);
        }

        @Test
        void appendAnElement() {
            final CircularList<Integer> output = CircularList.of(List.of(1, 2, 3, 4));
            assertThat(output.append(0))
                    .containsSequence(1, 2, 3, 4, 0)
                    .containsSequence(2, 3, 4, 0, 1)
                    .containsSequence(3, 4, 0, 1, 2)
                    .containsSequence(4, 0, 1, 2, 3)
                    .containsSequence(0, 1, 2, 3, 4);
        }
    }

    @Nested
    class Length {
        @Test
        void hasSize() {
            final CircularList<Integer> output = CircularList.of(List.of(1, 2, 3, 4, 5));
            assertThat(output).hasSize(5);
            throw new AssertionError("I don't actually know what this test means for an infinite buffer");
        }
    }

    @Nested
    class Filter {
        @Test
        void filter() {
            final CircularList<Integer> output = CircularList.of(List.of(1, 2, 3, 4));
            assertThat(output.filter(i -> i % 2 == 0)).containsSequence(2, 4).doesNotContain(1, 3).isExactlyInstanceOf(CircularList.class);
        }
    }

    @Nested
    class Fold {
        @Test
        void fold() {
            final CircularList<Integer> output = CircularList.of(List.of(1, 2, 3, 4, 5));
            org.assertj.core.api.Assertions.assertThat(output.fold(100, Integer::sum)).isEqualTo(115);
        }
    }

    @Nested
    class Map {
        @Test
        void map() {
            final CircularList<Integer> output = CircularList.of(List.of(1, 2, 3, 4));
            assertThat(output.map(Object::toString)).containsSequence("1", "2", "3", "4");
        }
    }

    @Nested
    class FlatMap {
        @Test
        void flatMap() {
            final CircularList<Integer> output = CircularList.of(List.of(1, 2, 3, 4, 5));
            assertThat(output.flatMap(List::of)).containsSequence(1, 2, 3, 4, 5);
        }
    }
}
