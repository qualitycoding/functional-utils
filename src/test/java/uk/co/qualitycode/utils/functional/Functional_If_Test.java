package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class Functional_If_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.if_(new Object(), null, mock(Function.class), mock(Function.class)))
                .withMessage("if_(A,Predicate<A>,Function<A,B>,Function<A,B>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.if_(new Object(), mock(Predicate.class), null, mock(Function.class)))
                .withMessage("if_(A,Predicate<A>,Function<A,B>,Function<A,B>): thenClause must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.if_(new Object(), mock(Predicate.class), mock(Function.class), null))
                .withMessage("if_(A,Predicate<A>,Function<A,B>,Function<A,B>): elseClause must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.if_(new Object()).withPredicate(null).then(mock(Function.class)).orElse(mock(Function.class)))
                .withMessage("if_(A,Predicate<A>,Function<A,B>,Function<A,B>): predicate must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.if_(new Object()).withPredicate(mock(Predicate.class)).then(null).orElse(mock(Function.class)))
                .withMessage("if_(A,Predicate<A>,Function<A,B>,Function<A,B>): thenClause must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Functional.if_(new Object()).withPredicate(mock(Predicate.class)).then(mock(Function.class)).orElse(null))
                .withMessage("if_(A,Predicate<A>,Function<A,B>,Function<A,B>): elseClause must not be null");

    }

    @Test
    void ifA() {
        final int input = 1;
        final Iterable2<Integer> i = Iterable2.asList(0, 1, 2);
        final Iterable2<Integer> result = i.map(ii -> Functional.if_(input, Functional.greaterThan(ii), FunctionalTest.doublingGenerator, FunctionalTest.triplingGenerator));
        final List<Integer> expected = Arrays.asList(2, 3, 3);
        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void ifAWithPredicateThenElse() {
        final int input = 1;
        final Iterable2<Integer> i = Iterable2.asList(0, 1, 2);
        final Iterable2<Integer> result = i.map(ii -> Functional
                .<Integer, Integer>if_(input)
                .withPredicate(Functional.greaterThan(ii))
                .then(FunctionalTest.doublingGenerator)
                .orElse(FunctionalTest.triplingGenerator));
        final List<Integer> expected = Arrays.asList(2, 3, 3);
        assertThat(result).containsExactlyElementsOf(expected);
    }
}
