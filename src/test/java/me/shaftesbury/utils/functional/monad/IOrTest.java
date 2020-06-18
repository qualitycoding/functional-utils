package me.shaftesbury.utils.functional.monad;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class IOrTest {
    @Nested
    class Creation {
        @Test
        void right() {
            final Object input = new Object();
            final IOr<Object, Object> right = IOr.right(input);
            assertThat(right).isInstanceOf(IOr.class);
            assertThat(right).isNotNull();
        }

        @Test
        void left() {
            final Object input = new Object();
            final IOr<Object, Object> left = IOr.left(input);
            assertThat(left).isInstanceOf(IOr.class);
            assertThat(left).isNotNull();
        }

        @Test
        void both() {
            final Object leftInput = new Object();
            final Object rightInput = new Object();
            final IOr<Object, Object> both = IOr.both(leftInput, rightInput);
            assertThat(both).isInstanceOf(IOr.class);
            assertThat(both).isNotNull();
        }
    }

    @Nested
    class LeftThrows {
        @Test
        void whenArgsAreNull() {
            assertThatNullPointerException().isThrownBy(() -> IOr.left(null)).withMessage("left must not be null");
        }
    }

    @Nested
    class RightThrows {
        @Test
        void whenArgsAreNull() {
            assertThatNullPointerException().isThrownBy(() -> IOr.right(null)).withMessage("right must not be null");
        }
    }

    @Nested
    class BothThrows {
        @Nested
        class Null {
            @Test
            void leftArgIsNull() {
                assertThatNullPointerException().isThrownBy(() -> IOr.both(null, new Object())).withMessage("left must not be null");
            }

            @Test
            void rightArgIsNull() {
                assertThatNullPointerException().isThrownBy(() -> IOr.both(new Object(), null)).withMessage("right must not be null");
            }

            @Test
            void bothArgsAreNull() {
                assertThatNullPointerException().isThrownBy(() -> IOr.both(null, null)).withMessage("left and right must not both be null");
            }
        }
    }

    @Nested
    class Get {
        @Test
        void getIsRightBiased() {
            final Object leftInput = new Object();
            final Object rightInput = new Object();
            final IOr<Object, Object> both = IOr.both(leftInput, rightInput);

            assertThat(both.get()).isSameAs(rightInput);
        }

        @Test
        void getThrowsWhenRightEntryIsMissing() {
            final Object input = new Object();
            final IOr<Object, Object> left = IOr.left(input);

            assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(left::get).withMessage("No value present");
        }
    }

    @Nested
    class GetLeft {
        @Test
        void getLeft() {
            final Object leftInput = new Object();
            final Object rightInput = new Object();
            final IOr<Object, Object> both = IOr.both(leftInput, rightInput);

            assertThat(both.getLeft()).isSameAs(leftInput);
        }

        @Test
        void getLeftThrowsWhenLeftEntryIsMissing() {
            final Object input = new Object();
            final IOr<Object, Object> right = IOr.right(input);

            assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(right::getLeft).withMessage("No value present");
        }
    }

    @Nested
    class Equals {
        @Test
        void succeeds() {
            final Object left = new Object();
            final Object right = new Object();
            final IOr<Object, Object> both1 = IOr.both(left, right);
            final IOr<Object, Object> both2 = IOr.both(left, right);
            assertThat(both1).isEqualTo(both2);
        }

        @Test
        void notEqualWhenRightDiffers() {
            final Object left = new Object();
            final Object right1 = new Object();
            final Object right2 = new Object();
            final IOr<Object, Object> both1 = IOr.both(left, right1);
            final IOr<Object, Object> both2 = IOr.both(left, right2);
            assertThat(both1).isNotEqualTo(both2);
        }

        @Test
        void notEqualWhenLeftDiffers() {
            final Object left1 = new Object();
            final Object left2 = new Object();
            final Object right = new Object();
            final IOr<Object, Object> both1 = IOr.both(left1, right);
            final IOr<Object, Object> both2 = IOr.both(left2, right);
            assertThat(both1).isNotEqualTo(both2);
        }


        @Test
        void notEqualWhenBothDiffer() {
            final Object left1 = new Object();
            final Object left2 = new Object();
            final Object right1 = new Object();
            final Object right2 = new Object();
            final IOr<Object, Object> both1 = IOr.both(left1, right1);
            final IOr<Object, Object> both2 = IOr.both(left2, right2);
            assertThat(both1).isNotEqualTo(both2);
        }
    }

    @Nested
    class Map {
        @Test
        void isRightBiased() {
            final Object left = new Object();
            final Object right = new Object();
            final IOr<Object, Object> both = IOr.both(left, right);
            final Function<Object, Object> tfm = mock(Function.class);
            final Object expectedRight = new Object();
            final IOr<Object, Object> expected = IOr.both(left, expectedRight);

            when(tfm.apply(right)).thenReturn(expectedRight);

            assertThat(both.map(tfm)).isEqualTo(expected);
            verify(tfm).apply(right);
        }

        @Test
        void mapWhenValueIsMissing() {
            final IOr<Object, Object> left = IOr.left(new Object());
            final Function<Object, Object> tfm = mock(Function.class);
            assertThat(left.map(tfm)).isEqualTo(left);
            verifyNoInteractions(tfm);
        }

        @Test
        void mapWhenLeftIsMissing() {
            final Object input = new Object();
            final IOr<Object, Object> right = IOr.right(new Object());
            final Function<Object, Object> tfm = mock(Function.class);
            final Object expectedRight = new Object();
            final IOr<Object, Object> expected = IOr.right(expectedRight);

            when(tfm.apply(input)).thenReturn(expectedRight);

            assertThat(right.map(tfm)).isEqualTo(expected);
            verify(tfm).apply(right);
        }

        @Test
        void mapThrowsWhenSuppliedNullFunction() {
            assertThatNullPointerException().isThrownBy(() -> IOr.both(new Object(), new Object()).map((Function) null)).withMessage("tfm must not be null");
        }

        @Test
        void tfmShouldNotReturnNull() {
            assertThatNullPointerException().isThrownBy(() -> IOr.right(new Object()).map(x -> null)).withMessage("right must not be null");
        }
    }

    @Nested
    class MapLeft {
        @Test
        void mapLeft() {
            final Object left = new Object();
            final Object right = new Object();
            final IOr<Object, Object> both = IOr.both(left, right);
            final Function<Object, Object> tfm = mock(Function.class);
            final Object expectedLeft = new Object();
            final IOr<Object, Object> expected = IOr.both(expectedLeft, right);

            when(tfm.apply(left)).thenReturn(expectedLeft);

            assertThat(both.mapLeft(tfm)).isEqualTo(expected);
            verify(tfm).apply(left);
        }

        @Test
        void whenValueIsMissing() {
            final IOr<Object, Object> right = IOr.right(new Object());
            final Function<Object, Object> tfm = mock(Function.class);
            assertThat(right.mapLeft(tfm)).isEqualTo(right);
            verifyNoInteractions(tfm);
        }

        @Test
        void whenRightIsMissing() {
            final Object input = new Object();
            final IOr<Object, Object> left = IOr.left(new Object());
            final Function<Object, Object> tfm = mock(Function.class);
            final Object expectedLeft = new Object();
            final IOr<Object, Object> expected = IOr.left(expectedLeft);

            when(tfm.apply(input)).thenReturn(expectedLeft);

            assertThat(left.mapLeft(tfm)).isEqualTo(expected);
            verify(tfm).apply(left);
        }

        @Test
        void throwsWhenSuppliedNullFunction() {
            assertThatNullPointerException().isThrownBy(() -> IOr.both(new Object(), new Object()).mapLeft((Function) null)).withMessage("tfm must not be null");
        }

        @Test
        void tfmShouldNotReturnNull() {
            assertThatNullPointerException().isThrownBy(() -> IOr.left(new Object()).map(x -> null)).withMessage("left must not be null");
        }
    }

    @Nested
    class MapBoth {
        @Nested
        class TwoFunctions {
            @Test
            void mapBoth() {
                final Object left = new Object();
                final Object right = new Object();
                final Function<Object, Object> leftTfm = mock(Function.class);
                final Function<Object, Object> rightTfm = mock(Function.class);
                final Object expectedLeft = new Object();
                final Object expectedRight = new Object();
                final IOr<Object, Object> expected = IOr.both(expectedLeft, expectedRight);

                when(leftTfm.apply(left)).thenReturn(expectedLeft);
                when(rightTfm.apply(right)).thenReturn(expectedRight);

                assertThat(IOr.both(left, right).mapBoth(leftTfm, rightTfm)).isEqualTo(expected);

                verify(leftTfm).apply(left);
                verify(rightTfm).apply(right);
            }

            @Test
            void mapBothWhenLeftIsMissing() {
                final Object right = new Object();
                final Function<Object, Object> leftTfm = mock(Function.class);
                final Function<Object, Object> rightTfm = mock(Function.class);
                final Object expectedRight = new Object();
                final IOr<Object, Object> expected = IOr.right(expectedRight);

                when(rightTfm.apply(right)).thenReturn(expectedRight);

                assertThat(IOr.right(right).mapBoth(leftTfm, rightTfm)).isEqualTo(expected);

                verifyNoInteractions(leftTfm);
                verify(rightTfm).apply(right);
            }

            @Test
            void mapBothWhenRightIsMissing() {
                final Object left = new Object();
                final Function<Object, Object> leftTfm = mock(Function.class);
                final Function<Object, Object> rightTfm = mock(Function.class);
                final Object expectedLeft = new Object();
                final IOr<Object, Object> expected = IOr.left(expectedLeft);

                when(rightTfm.apply(left)).thenReturn(expectedLeft);

                assertThat(IOr.left(left).mapBoth(leftTfm, rightTfm)).isEqualTo(expected);

                verify(leftTfm).apply(left);
                verifyNoInteractions(rightTfm);
            }

            @Test
            void mapBothThrowsWhenSuppliedNullAsLeft() {
                assertThatNullPointerException().isThrownBy(() -> IOr.both(new Object(), new Object()).mapBoth(null, Function.identity())).withMessage("leftTfm must not be null");
            }

            @Test
            void mapBothThrowsWhenSuppliedNullAsRight() {
                assertThatNullPointerException().isThrownBy(() -> IOr.both(new Object(), new Object()).mapBoth(Function.identity(), null)).withMessage("leftTfm must not be null");
            }

            @Test
            void leftTfmShouldNotReturnNull() {
                assertThatNullPointerException().isThrownBy(() -> IOr.both(new Object(), new Object()).mapBoth(x -> null, Function.identity())).withMessage("left must not be null");
            }

            @Test
            void rightTfmShouldNotReturnNull() {
                assertThatNullPointerException().isThrownBy(() -> IOr.both(new Object(), new Object()).mapBoth(Function.identity(), x -> null)).withMessage("right must not be null");
            }
        }
    }

    @Nested
    class Swap {
        @Test
        void swap() {
            final Object left = new Object();
            final Object right = new Object();

            assertThat(IOr.both(left, right).swap()).isEqualTo(IOr.both(right, left));
        }

        @Test
        void swapWhentRightIsMissing() {
            final Object left = new Object();

            assertThat(IOr.left(left).swap()).isEqualTo(IOr.right(left));
        }

        @Test
        void swapWhentLeftIsMissing() {
            final Object right = new Object();

            assertThat(IOr.right(right).swap()).isEqualTo(IOr.left(right));
        }
    }

    @Nested
    @Disabled
    class Comparable {
    }

    @Nested
    class HasValue {
        @Test
        void isRightBiased() {
            assertThat(IOr.right(new Object()).hasValue()).isTrue();
        }

        @Test
        void both() {
            assertThat(IOr.both(new Object(), new Object()).hasValue()).isTrue();
        }

        @Test
        void left() {
            assertThat(IOr.left(new Object()).hasValue()).isFalse();
        }
    }

    @Nested
    class HasLeft {
        @Test
        void right() {
            assertThat(IOr.right(new Object()).hasLeft()).isFalse();
        }

        @Test
        void both() {
            assertThat(IOr.both(new Object(), new Object()).hasLeft()).isTrue();
        }

        @Test
        void left() {
            assertThat(IOr.left(new Object()).hasLeft()).isTrue();
        }
    }

    @Nested
    class HasBoth {
        @Test
        void right() {
            assertThat(IOr.right(new Object()).hasBoth()).isTrue();
        }

        @Test
        void both() {
            assertThat(IOr.both(new Object(), new Object()).hasBoth()).isTrue();
        }

        @Test
        void left() {
            assertThat(IOr.left(new Object()).hasBoth()).isTrue();
        }
    }
}
