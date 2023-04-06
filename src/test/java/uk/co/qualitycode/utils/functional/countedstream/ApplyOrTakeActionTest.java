package uk.co.qualitycode.utils.functional.countedstream;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.Function;

import static uk.co.qualitycode.utils.functional.countedstream.ApplyOrTakeAction.apply;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ApplyOrTakeActionTest {

    @Test
    void applyReturnsSingleElement() {
        final Object input = new Object();
        assertThat(apply(Function.identity()).ifSingleElement(Collections.singleton(input)).orThrow(() -> new RuntimeException("Busted"))).isSameAs(input);
    }

    @Test
    void applyThrowsWithNoElements() {
        final RuntimeException busted = new RuntimeException("Busted");
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> apply(Function.identity()).ifSingleElement(Collections.emptyList()).orThrow(() -> busted)).isSameAs(busted);
    }

    @Test
    void applyThrowsWithMoreThanOneElement() {
        final RuntimeException busted = new RuntimeException("Busted");
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> apply(Function.identity()).ifSingleElement(Collections.nCopies(2, new Object())).orThrow(() -> busted)).isSameAs(busted);
    }

    @Test
    void applyOrElseWithASingleElement() {
        final Object expected = new Object();
        assertThat(apply(Function.identity()).ifSingleElement(Collections.singleton(expected)).orElse(() -> new Object())).isSameAs(expected);
    }

    @Test
    void applyOrElseWithNoElements() {
        final Object expected = new Object();
        assertThat(apply(Function.identity()).ifSingleElement(Collections.emptyList()).orElse(() -> expected)).isSameAs(expected);
    }

    @Test
    void applyOrElseWithMoreThanOneElement() {
        final Object expected = new Object();
        assertThat(apply(Function.identity()).ifSingleElement(Collections.nCopies(2, new Object())).orElse(() -> expected)).isSameAs(expected);
    }

    @Test
    void applyOrElseWithASingleElementReturningOptional() {
        final Object expected = new Object();
        assertThat(apply(Function.identity()).ifSingleElement(Collections.singleton(expected)).orElseOptional(() -> new Object())).hasValue(expected);
    }

    @Test
    void applyOrElseOptionallyWithNoElements() {
        final Object expected = new Object();
        assertThat(apply(Function.identity()).ifSingleElement(Collections.emptyList()).orElseOptional(() -> expected)).hasValue(expected);
    }

    @Test
    void applyOrElseOptionallyWithMoreThanOneElement() {
        final Object expected = new Object();
        assertThat(apply(Function.identity()).ifSingleElement(Collections.nCopies(2, new Object())).orElseOptional(() -> expected)).hasValue(expected);
    }
}
