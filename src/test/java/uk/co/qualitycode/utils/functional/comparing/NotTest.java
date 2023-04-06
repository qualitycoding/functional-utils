package uk.co.qualitycode.utils.functional.comparing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static uk.co.qualitycode.utils.functional.comparing.Not.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotTest {
    @Test
    void factory(@Mock final Comparator<?> c) {
        assertThat(not(c)).isNotNull();
    }

    @Test
    void compareWith(@Mock final Comparator<Object> c) {
        final Object o = new Object();
        given(c.compareWith(o)).willReturn(true);
        assertThat(not(c).compareWith(o)).isFalse();
    }
}
