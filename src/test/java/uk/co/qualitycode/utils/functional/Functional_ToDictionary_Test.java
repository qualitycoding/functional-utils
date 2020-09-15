package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.vavr.collection.HashMap.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.FunctionalTest.triplingGenerator;

class Functional_ToDictionary_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary(null, mock(Function.class), mock(Iterable.class)))
                .withMessage("toDictionary(Function<T,K>,Function<T,V>,Iterable<T>): keyFn must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary(mock(Function.class), null, mock(Iterable.class)))
                .withMessage("toDictionary(Function<T,K>,Function<T,V>,Iterable<T>): valueFn must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary(mock(Function.class), mock(Function.class), (Iterable)null))
                .withMessage("toDictionary(Function<T,K>,Function<T,V>,Iterable<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary(null, mock(Function.class), mock(Collection.class)))
                .withMessage("toDictionary(Function<T,K>,Function<T,V>,Collection<T>): keyFn must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary(mock(Function.class), null, mock(Collection.class)))
                .withMessage("toDictionary(Function<T,K>,Function<T,V>,Collection<T>): valueFn must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary(mock(Function.class), mock(Function.class), (Collection)null))
                .withMessage("toDictionary(Function<T,K>,Function<T,V>,Collection<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary(null, mock(Function.class)))
                .withMessage("toDictionary(Function<T,K>,Function<T,V>): keyFn must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary(mock(Function.class), null))
                .withMessage("toDictionary(Function<T,K>,Function<T,V>): valueFn must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary((Iterable)null))
                .withMessage("toDictionary(Iterable<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toDictionary((Collection)null))
                .withMessage("toDictionary(Collection<T>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.toMutableDictionary(null))
                .withMessage("toMutableDictionary(Map<K,V>): input must not be null");
    }

    @Test
    void toDictionaryUsingIterable() {
        final List<Integer> li = Functional.init(triplingGenerator, 5);
        final Map<Integer, String> output = Functional.toDictionary(
                Function.identity(),
                Functional.stringify(),
                Functional.seq.filter(Functional::isEven, li));
        final Map<Integer, String> expected = of(6, "6", 12, "12").toJavaMap();
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void curriedToDictionaryUsingIterable() {
        final List<Integer> li = Functional.init(triplingGenerator, 5);
        final Map<Integer, String> output = Functional.<Integer, Integer, String>toDictionary(Functional.seq.filter(Functional::isEven, li))
                .withKeyFn(Function.identity())
                .withValueFn(Functional.stringify());
        final Map<Integer, String> expected = of(6, "6", 12, "12").toJavaMap();
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void toDictionaryUsingCollection() {
        final List<Integer> li = Functional.init(triplingGenerator, 5);
        final Map<Integer, String> output = Functional.toDictionary(
                Function.identity(),
                Functional.stringify(),
                Functional.filter(Functional::isEven, li));
        final Map<Integer, String> expected = of(6, "6", 12, "12").toJavaMap();
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void curriedToDictionaryUsingCollection() {
        final List<Integer> li = Functional.init(triplingGenerator, 5);
        final Map<Integer, String> output = Functional.<Integer, Integer, String>toDictionary(Functional.filter(Functional::isEven, li))
                .withKeyFn(Function.identity())
                .withValueFn(Functional.stringify());
        final Map<Integer, String> expected = of(6, "6", 12, "12").toJavaMap();
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void toMutableDictionary() {
        final Map<Integer, String> expected = of(6, "6", 12, "12").toJavaMap();
        final Map<Integer, String> output = Functional.toMutableDictionary(expected);
        assertThat(output).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
