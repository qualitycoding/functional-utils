package uk.co.qualitycode.utils.functional.comparing.integer;

import uk.co.qualitycode.utils.functional.comparing.Comparator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static uk.co.qualitycode.utils.functional.comparing.integer.HasSize.hasSize;
import static uk.co.qualitycode.utils.functional.comparing.integer.HasSize.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HasSizeTest {

    @Test
    void hasSizePrecondition() {
        assertThatIllegalArgumentException().isThrownBy(() -> hasSize(null)).withMessage("comparator must not be null");
    }

    @Nested
    class Factory {
        @Test
        void hasSizeFactory__int() {
            assertThat(hasSize(10)).isNotNull();
        }

        @Test
        void hasSizeFactory__cmp(@Mock final Comparator<Integer> c) {
            assertThat(hasSize(c)).isNotNull();
        }

        @Test
        void isEmptyFactory() {
            assertThat(isEmpty()).isNotNull();
        }
    }

    @Nested
    class CompareWith {
        @Test
        void hasSize__int() {
            assertThat(hasSize(33).compareWith(33)).isTrue();
        }

        @Test
        void hasSize__cmp(@Mock final Comparator c) {
            given(c.compareWith(33)).willReturn(true);
            assertThat(hasSize(c).compareWith(33)).isTrue();
        }

        @Test
        void isEmpty() {
            assertThat(HasSize.isEmpty().compareWith(0)).isTrue();
        }
    }
}
