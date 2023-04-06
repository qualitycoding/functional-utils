package uk.co.qualitycode.utils.functional.comparing;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static uk.co.qualitycode.utils.functional.comparing.Or.or;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrTest {
    @Nested
    class Preconditions {
        @Test
        void c1MustNotBeNull(@Mock final Comparator<?> c2) {
            assertThatIllegalArgumentException().isThrownBy(() -> or(null, c2)).withMessage("c1 must not be null");
        }

        @Test
        void c2MustNotBeNull(@Mock final Comparator<?> c1) {
            assertThatIllegalArgumentException().isThrownBy(() -> or(c1, null)).withMessage("c2 must not be null");
        }
    }

    @Test
    void factory(@Mock final Comparator<Object> c1, @Mock final Comparator<Object> c2) {
        assertThat(or(c1, c2)).isNotNull();
    }

    @Test
    void compareWith__1stIsFalse(@Mock final Comparator<Object> c1, @Mock final Comparator<Object> c2) {
        given(c1.compareWith(51)).willReturn(false);
        given(c2.compareWith(51)).willReturn(true);
        assertThat(or(c1, c2).compareWith(51)).isTrue();
    }

    @Test
    void compareWith__BothFalse(@Mock final Comparator<Object> c1, @Mock final Comparator<Object> c2) {
        given(c1.compareWith(51)).willReturn(false);
        given(c2.compareWith(51)).willReturn(false);
        assertThat(or(c1, c2).compareWith(51)).isFalse();
    }

    @Test
    void compareWith__1stIsTrue(@Mock final Comparator<Object> c1, @Mock final Comparator<Object> c2) {
        given(c1.compareWith(51)).willReturn(true);
        assertThat(or(c1, c2).compareWith(51)).isTrue();
    }
}
