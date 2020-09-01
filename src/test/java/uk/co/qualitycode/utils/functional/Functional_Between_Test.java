package uk.co.qualitycode.utils.functional;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static uk.co.qualitycode.utils.functional.IsBetween.is;
import static uk.co.qualitycode.utils.functional.IsBetween.isNot;

class Functional_Between_Test {
    @Nested
    class Preconditions {
        @Test
        void lowerBound() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.between(null, 4, 3))
                    .withMessage("between(T,T,T): lower bound must not be null");
        }

        @Test
        void upperBound() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.between(1, null, 3))
                    .withMessage("between(T,T,T): upper bound must not be null");
        }

        @Test
        void bounds() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.between(null, 3))
                    .withMessage("between(Tuple2<T,T>,T): bounds must not be null");
        }

        @Test
        void value() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.between(new Tuple2<>(1, 2), null))
                    .withMessage("between(Tuple2<T,T>,T): value must not be null");
        }

        @Test
        void lowerBoundShouldBeLessThanUpperBound() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.between(2, 1, 3))
                    .withMessage("between(T,T,T): lower bound must be less than upper bound");
        }
    }

    @Nested
    class Simple {
        @Test
        void betweenTest1() {
            final int lowerBound = 2, upperBound = 4;
            assertThat(Functional.between(lowerBound, upperBound, 3)).isTrue();
        }

        @Test
        void betweenTest2() {
            final int lowerBound = 2, upperBound = 4;
            assertThat(Functional.between(lowerBound, upperBound, 1)).isFalse();
        }

        @Test
        void betweenTest3() {
            final double lowerBound = 2.5, upperBound = 2.6;
            assertThat(Functional.between(lowerBound, upperBound, 2.55)).isTrue();
        }
    }

    @Nested
    class Tupled {
        @Test
        void betweenTest1() {
            final int lowerBound = 2, upperBound = 4;
            assertThat(Functional.between(new Tuple2<>(lowerBound, upperBound), 3)).isTrue();
        }

        @Test
        void betweenTest2() {
            final int lowerBound = 2, upperBound = 4;
            assertThat(Functional.between(new Tuple2<>(lowerBound, upperBound), 1)).isFalse();
        }

        @Test
        void betweenTest3() {
            final double lowerBound = 2.5, upperBound = 2.6;
            assertThat(Functional.between(new Tuple2<>(lowerBound, upperBound), 2.55)).isTrue();
        }
    }

    @Nested
    class Curried {
        @Test
        void betweenTest1() {
            final int lowerBound = 2, upperBound = 4;
            assertThat(Functional.between(new Tuple2<>(lowerBound, upperBound)).test(3)).isTrue();
        }

        @Test
        void betweenTest2() {
            final int lowerBound = 2, upperBound = 4;
            assertThat(Functional.between(new Tuple2<>(lowerBound, upperBound)).test(1)).isFalse();
        }

        @Test
        void betweenTest3() {
            final double lowerBound = 2.5, upperBound = 2.6;
            assertThat(Functional.between(new Tuple2<>(lowerBound, upperBound)).test(2.55)).isTrue();
        }
    }

    @Nested
    class Monadic {
        @Nested
        class Is {
            @Test
            void isBetween() {
                final boolean actual = is(1).between(0).and(2);
                assertThat(actual).isTrue();
            }

            @Test
            void isNotBetween() {
                final boolean actual = is(4).between(0).and(2);
                assertThat(actual).isFalse();
            }
        }

        @Nested
        class IsNot {
            @Test
            void isBetween() {
                final boolean actual = isNot(1).between(0).and(2);
                assertThat(actual).isFalse();
            }

            @Test
            void isNotBetween() {
                final boolean actual = isNot(4).between(0).and(2);
                assertThat(actual).isTrue();
            }
        }
    }
}
